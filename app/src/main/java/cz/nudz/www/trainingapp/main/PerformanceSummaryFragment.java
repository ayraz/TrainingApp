package cz.nudz.www.trainingapp.main;


import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import cz.nudz.www.trainingapp.data.TrainingRepository;
import cz.nudz.www.trainingapp.databinding.PerformanceSummaryFragmentBinding;
import cz.nudz.www.trainingapp.enums.ParadigmType;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerformanceSummaryFragment extends Fragment {

    public static final String TAG = PerformanceSummaryFragment.class.getSimpleName();

    private PerformanceSummaryFragmentBinding binding;
    private BaseActivity activity;
    private TrainingRepository trainingRepository;
    private GetLastSessionTask colorTask;

    private static final int CHART_FONT_SIZE = 14;

    public PerformanceSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.performance_summary_fragment, container, false);
        activity = (BaseActivity) getActivity();
        trainingRepository = new TrainingRepository(activity, activity.getHelper());

        colorTask = new GetLastSessionTask(results -> {
            if (results.isEmpty()) return;

            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < results.size(); ++i) {
                entries.add(new Entry(i, results.get(i).second));
            }
            final XAxis xAxis = binding.colorChart.getXAxis();
            xAxis.setValueFormatter(((value, axis) -> results.get((int) value).first));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(CHART_FONT_SIZE);
            final YAxis axisLeft = binding.colorChart.getAxisLeft();
            axisLeft.setGranularity(1f);
            axisLeft.setTextSize(CHART_FONT_SIZE);
            binding.colorChart.getAxisRight().setEnabled(false);

            LineDataSet lineDataSet = new LineDataSet(entries, "Progress during last session; COLOR");
            lineDataSet.setValueFormatter(new DefaultValueFormatter(0));
            lineDataSet.setValueTextSize(CHART_FONT_SIZE);
            binding.colorChart.setData(new LineData(lineDataSet));
            binding.colorChart.invalidate();
        });
        colorTask.execute(activity.getSessionManager().getUsername(), ParadigmType.COLOR.toString());

//        final List<SessionData> sessionData = trainingRepository.getAllSessionParadigmData(activity.getSessionManager().getUsername(), ParadigmType.COLOR);
//        if (sessionData.isEmpty()) {
//            Toast.makeText(activity, "NO DATA", Toast.LENGTH_LONG).show();
//        } else {
//            List<Entry> entries = new ArrayList<>();
//            for (int i = 0; i < sessionData.size(); ++i) {
//                entries.add(new Entry(i, sessionData.get(i).maxDifficulty));
//            }
//
//            binding.lineChart.getXAxis().setValueFormatter((value, axis) -> new SimpleDateFormat("dd/MM/yyyy").format(sessionData.get((int) value).sessionDate));
//            binding.lineChart.getAxisLeft().setGranularity(1);
////            binding.lineChart.getAxisRight().setGranularity(1);
//            LineDataSet lineDataSet = new LineDataSet(entries, "Label");
//            binding.lineChart.setData(new LineData(lineDataSet));
//            binding.lineChart.invalidate();
//        }

        return binding.getRoot();
    }

    @Override
    public void onStop() {
        colorTask.cancel(true);

        super.onStop();
    }

    private class GetLastSessionTask extends AsyncTask<String, Integer, List<Pair<String, Integer>>> {

        private final AsyncListener listener;

        GetLastSessionTask(AsyncListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<Pair<String, Integer>> doInBackground(String... strings) {
            return trainingRepository.getLastSessionParadigmData(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(List<Pair<String, Integer>> pairs) {
            super.onPostExecute(pairs);
            listener.onComplete(pairs);
        }

    }

    private interface AsyncListener {

        void onComplete(List<Pair<String, Integer>> results);
    }
}
