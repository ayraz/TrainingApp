package cz.nudz.www.trainingapp;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.databinding.TutorialFragmentBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TutorialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TUTORIAL_TOP_TEXT = "param1";
    private static final String TUTORIAL_BOTTOM_TEXT = "param2";

    // TODO: Rename and change types of parameters
    private String topText;
    private String bottomText;
    private TutorialFragmentBinding binding;


    public TutorialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param topText Parameter 1.
     * @param bottomText Parameter 2.
     * @return A new instance of fragment TutorialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TutorialFragment newInstance(String topText, String bottomText) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putString(TUTORIAL_TOP_TEXT, topText);
        args.putString(TUTORIAL_BOTTOM_TEXT, bottomText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topText = getArguments().getString(TUTORIAL_TOP_TEXT);
            bottomText = getArguments().getString(TUTORIAL_BOTTOM_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.tutorial_fragment, container, false);

        binding.tutorialFragmentTopText.setText(topText);
        binding.tutorialFragmentBottomText.setText(bottomText);

        return binding.getRoot();
    }

}
