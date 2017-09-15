package cz.nudz.www.trainingapp.tutorial;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.TutorialFragmentBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TutorialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialFragment extends Fragment {

    private static final String TUTORIAL_TOP_TEXT_ID = "param1";
    private static final String TUTORIAL_BOTTOM_TEXT_ID = "param2";

    private Integer topTextId;
    private Integer bottomTextId;
    private TutorialFragmentBinding binding;


    public TutorialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param topTextId Parameter 1.
     * @param bottomTextId Parameter 2.
     * @return A new instance of fragment TutorialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TutorialFragment newInstance(@Nullable Integer topTextId, @Nullable Integer bottomTextId) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();

        args.putInt(TUTORIAL_TOP_TEXT_ID, topTextId != null ? topTextId : 0);
        args.putInt(TUTORIAL_BOTTOM_TEXT_ID, bottomTextId != null ? bottomTextId : 0);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topTextId = getArguments().getInt(TUTORIAL_TOP_TEXT_ID);
            bottomTextId = getArguments().getInt(TUTORIAL_BOTTOM_TEXT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.tutorial_fragment, container, false);

        if (topTextId != null && !topTextId.equals(0))
            binding.tutorialFragmentTopText.setText(Html.fromHtml(getString(topTextId)));
        if (bottomTextId != null && !bottomTextId.equals(0))
            binding.tutorialFragmentBottomText.setText(Html.fromHtml(getString(bottomTextId)));

        return binding.getRoot();
    }

}
