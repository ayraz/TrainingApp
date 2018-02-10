package cz.nudz.www.trainingapp.tutorial;


import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.TutorialImageFragmentBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TutorialImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialImageFragment extends Fragment {

    private static final String TUTORIAL_TOP_TEXT_ID = "TUTORIAL_TOP_TEXT_ID";
    private static final String TUTORIAL_BOTTOM_TEXT_ID = "TUTORIAL_BOTTOM_TEXT_ID";
    private static final String TUTORIAL_DRAWABLE_ID = "TUTORIAL_DRAWABLE_ID";

    private int topTextId;
    private Integer drawableId;
    private TutorialImageFragmentBinding binding;

    public TutorialImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param topTextId Must be a valid string id.
     * @param drawableId Optional center image drawable resource id.
     * @return A new instance of fragment TutorialFragment.
     */
    public static TutorialImageFragment newInstance(int topTextId, @Nullable Integer drawableId) {

        TutorialImageFragment fragment = new TutorialImageFragment();
        Bundle args = new Bundle();
        args.putInt(TUTORIAL_TOP_TEXT_ID, topTextId);
        if (drawableId != null) args.putInt(TUTORIAL_DRAWABLE_ID, drawableId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topTextId = getArguments().getInt(TUTORIAL_TOP_TEXT_ID);
        if (getArguments().containsKey(TUTORIAL_DRAWABLE_ID)){
            drawableId = getArguments().getInt(TUTORIAL_DRAWABLE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.tutorial_image_fragment, container, false);

        binding.tutorialFragmentTopText.setText(Html.fromHtml(getString(topTextId)));
        if (drawableId != null && !drawableId.equals(0)) {
            Drawable drawable = getResources().getDrawable(drawableId);
            binding.tutorialFragmentImage.setImageDrawable(drawable);
        } else {
            binding.tutorialFragmentImage.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

}
