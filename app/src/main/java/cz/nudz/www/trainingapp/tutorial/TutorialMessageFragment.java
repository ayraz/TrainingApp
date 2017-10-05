package cz.nudz.www.trainingapp.tutorial;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.TutorialMessageFragmentBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialMessageFragment extends Fragment {

    private TutorialMessageFragmentBinding binding;

    public TutorialMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.tutorial_message_fragment, container, false);

        return binding.getRoot();
    }

}
