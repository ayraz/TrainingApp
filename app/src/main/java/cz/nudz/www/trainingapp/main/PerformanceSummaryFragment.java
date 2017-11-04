package cz.nudz.www.trainingapp.main;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
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


    private PerformanceSummaryFragmentBinding binding;
    private BaseActivity activity;

    public PerformanceSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.performance_summary_fragment, container, false);
        activity = (BaseActivity) getActivity();

        TrainingRepository trainingRepository = new TrainingRepository(activity, activity.getHelper());
        final List<TrainingRepository.SessionData> sessionData = trainingRepository.getParadigmSessionData(activity.getSessionManager().getUsername(), ParadigmType.COLOR);
        if (sessionData.isEmpty()) {
            Toast.makeText(activity, "NO DATA", Toast.LENGTH_LONG).show();
        } else {
            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < sessionData.size(); ++i) {
                entries.add(new Entry(i, sessionData.get(i).maxDifficulty));
            }

            binding.lineChart.getXAxis().setValueFormatter((value, axis) -> new SimpleDateFormat("dd/MM/yyyy").format(sessionData.get((int) value).sessionDate));
            binding.lineChart.getAxisLeft().setGranularity(1);
//            binding.lineChart.getAxisRight().setGranularity(1);
            LineDataSet lineDataSet = new LineDataSet(entries, "Label");
            binding.lineChart.setData(new LineData(lineDataSet));
            binding.lineChart.invalidate();
        }

        return binding.getRoot();
    }

}
