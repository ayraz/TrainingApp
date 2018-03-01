package cz.nudz.www.trainingapp.summary;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.tables.Paradigm;
import cz.nudz.www.trainingapp.enums.ParadigmType;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerformanceSummaryFragment extends LineChartFragment<String, Integer> {

    public static final String TAG = PerformanceSummaryFragment.class.getSimpleName();

    public PerformanceSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = super.onCreateView(inflater, container, savedInstanceState);

        tasks.add(executeTask(binding.colorChart, ParadigmType.COLOR));
        tasks.add(executeTask(binding.positionChart, ParadigmType.POSITION));
        tasks.add(executeTask(binding.shapeChart, ParadigmType.SHAPE));

        binding.colorChartTitle.setText(R.string.allSessionsColorResultsTitle);
        binding.positionChartTitle.setText(R.string.allSessionsPositionResultsTitle);
        binding.shapeChartTitle.setText(R.string.allSessionsShapeResultsTitle);

        return root;
    }

    @NonNull
    @Override
    protected void configureAxis(List<Pair<String, Integer>> data, LineChart chart, List<Entry> entries, ParadigmType paradigmType) {
        super.configureAxis(data, chart, entries, paradigmType);
        chart.getXAxis().setDrawGridLines(true);
        chart.getAxisLeft().setDrawGridLines(false);
    }

    @NonNull
    @Override
    protected LineDataSet configureLineDataSet(List<Entry> entries, ParadigmType paradigmType) {
        final LineDataSet lineDataSet = super.configureLineDataSet(entries, paradigmType);

        lineDataSet.setLabel(
                String.format("%s \"%s\"",
                        getString(R.string.maxLevelReachedLabel),
                        getString(ParadigmType.getLocalizedStringId(paradigmType)))
        );

        return lineDataSet;
    }

    private AsyncTask<String, Integer, List<Pair<String, Integer>>> executeTask(LineChart chart, ParadigmType paradigmType) {
        return new SummaryTask(results -> setChartData(results, chart, paradigmType)).execute(activity.getSessionManager().getUsername(), paradigmType.toString());
    }

    private class SummaryTask extends ChartLoadTask {

        SummaryTask(AsyncListener<String, Integer> listener) {
            super(listener);
        }

        @Override
        protected List<Pair<String, Integer>> doInBackground(String... strings) {
            return trainingRepository.getAllSessionParadigmData(ParadigmType.COLOR.toString());
        }
    }
}
