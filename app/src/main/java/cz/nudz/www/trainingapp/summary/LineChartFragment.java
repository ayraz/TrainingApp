package cz.nudz.www.trainingapp.summary;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;

import java.util.ArrayList;
import java.util.List;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.Repository;
import cz.nudz.www.trainingapp.databinding.ChartFragmentBinding;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.utils.Utils;

/**
 * Created by P8P67 on 11/5/2017.
 */

public abstract class LineChartFragment<X extends String, Y extends Integer> extends Fragment {

    private static final int CHART_FONT_SIZE = 12;

    protected final List<AsyncTask> tasks = new ArrayList<>();
    protected ChartFragmentBinding binding;
    protected BaseActivity activity;
    protected Repository repository;
    private Toast toast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.chart_fragment, container, false);
        activity = (BaseActivity) getActivity();
        repository = new Repository(activity);

        setChartNoData(binding.colorChart, binding.positionChart);
//        setChartNoData(binding.colorChart, binding.positionChart, binding.shapeChart);

        return binding.getRoot();
    }

    private void setChartNoData(LineChart... charts) {
        for (LineChart c : charts) {
            c.setNoDataText(getString(R.string.noChartDataAvailableText));
            c.setNoDataTextColor(R.color.red);
        }
    }

    @Override
    public void onStop() {
        for (AsyncTask t : tasks) {
            t.cancel(true);
        }
        super.onStop();
    }

    protected void setChartData(List<Pair<X, Y>> data, LineChart chart, ParadigmType paradigmType) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < data.size(); ++i) {
            entries.add(new Entry(i, data.get(i).second));
        }

        LineDataSet lineDataSet = configureLineDataSet(entries, paradigmType);
        // disable zooming
        chart.setScaleEnabled(false);
        chart.setData(new LineData(lineDataSet));
        configureAxis(data, chart, entries, paradigmType);
        chart.invalidate();
    }

    @NonNull
    protected LineDataSet configureLineDataSet(List<Entry> entries, ParadigmType paradigmType) {
        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setValueFormatter(new DefaultValueFormatter(0));
        lineDataSet.setValueTextSize(CHART_FONT_SIZE);
        lineDataSet.setLineWidth(4f);
        lineDataSet.setCircleRadius(8f);
        return lineDataSet;
    }

    @NonNull
    protected void configureAxis(List<Pair<X, Y>> data, LineChart chart, List<Entry> entries, ParadigmType paradigmType) {
        chart.setExtraOffsets(24, 0, 40, 0);
        final Description desc = new Description();
        desc.setEnabled(false);
        chart.setDescription(desc);

        final XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(0f);
        // setting minimum on xAxis does not help fucking mpchart to pass humanly value to formatter
        // so beware of the index hack in the formatter...
        xAxis.setValueFormatter(((value, axis) -> {
            value = value < 0 ? 0 : value % data.size();
            return data.get((int) value).first;
        }));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(CHART_FONT_SIZE);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        final YAxis axisLeft = chart.getAxisLeft();
        axisLeft.setGranularity(1f);
        axisLeft.setTextSize(CHART_FONT_SIZE);
        chart.getAxisRight().setEnabled(false);
    }

    protected interface AsyncListener<X extends String, Y extends Integer> {

        void onComplete(List<Pair<X, Y>> results);
    }

    protected abstract class ChartLoadTask extends AsyncTask<String, Integer, List<Pair<X, Y>>> {

        private final AsyncListener listener;

        ChartLoadTask(AsyncListener listener) {
            this.listener = listener;
        }

        @Override
        protected abstract List<Pair<X, Y>> doInBackground(String... strings);

        @Override
        protected void onPostExecute(List<Pair<X, Y>> results) {
            super.onPostExecute(results);
            if (results.isEmpty()) {
                if (toast == null) {
                    toast = Toast.makeText(activity,
                            R.string.notEnoughTrainingDataMessage,
                            Toast.LENGTH_LONG);
                    Utils.adjustToastPosition(activity, binding.getRoot(), toast);
                    toast.show();
                }
                return;
            }
            listener.onComplete(results);
        }
    }
}
