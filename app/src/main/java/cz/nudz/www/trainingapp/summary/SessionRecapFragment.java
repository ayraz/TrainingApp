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
import cz.nudz.www.trainingapp.enums.ParadigmType;

/**
 * A simple {@link Fragment} subclass.
 */
public class SessionRecapFragment extends LineChartFragment<String, Integer> {

    public static final String TAG = SessionRecapFragment.class.getSimpleName();

    public SessionRecapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = super.onCreateView(inflater, container, savedInstanceState);

        tasks.add(executeTask(binding.colorChart, ParadigmType.COLOR));
        tasks.add(executeTask(binding.positionChart, ParadigmType.POSITION));
        tasks.add(executeTask(binding.shapeChart, ParadigmType.SHAPE));

        return root;
    }

    @NonNull
    @Override
    protected LineDataSet configureLineDataSet(List<Entry> entries, ParadigmType paradigmType) {
        final LineDataSet lineDataSet = super.configureLineDataSet(entries, paradigmType);

        lineDataSet.setLabel(
                String.format("%s \"%s\"",
                        getString(R.string.levelProgressionLabel),
                        getString(ParadigmType.getLocalizedStringId(paradigmType)))
        );

        return lineDataSet;
    }

    private AsyncTask<String, Integer, List<Pair<String, Integer>>> executeTask(LineChart chart, ParadigmType paradigmType) {
        return new RecapTask(results -> setChartData(results, chart, paradigmType)).execute(paradigmType.toString());
    }

    private class RecapTask extends ChartLoadTask {

        RecapTask(AsyncListener<String, Integer> listener) {
            super(listener);
        }

        @Override
        protected List<Pair<String, Integer>> doInBackground(String... strings) {
            return trainingRepository.getLastSessionParadigmData(strings[1]);
        }
    }
}
