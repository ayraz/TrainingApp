package cz.nudz.www.trainingapp.training;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.Repository;
import cz.nudz.www.trainingapp.databinding.BadgeFragmentBinding;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BadgeFragmentListener} interface
 * to handle interaction events.
 * Use the {@link BadgeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BadgeFragment extends DialogFragment {

    public static final String TAG = BadgeFragment.class.getSimpleName();

    private BadgeFragmentListener listener;
    private BadgeFragmentBinding binding;

    public BadgeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BadgeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BadgeFragment newInstance() {
        BadgeFragment fragment = new BadgeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.badge_fragment, container, false);
        Repository tr = new Repository((BaseActivity) getActivity());

        int rating = 0;
        StringBuilder msg = new StringBuilder();
        boolean pauses = tr.hasOnlyShortPauses();
        boolean sameOrBetter = tr.isCurrentSessionSameOrBetter();
        boolean diffInAllParadigms = tr.hasRaisedDiffInAllParadigms();
        if (pauses) {
            rating += 1;
            msg.append(getString(R.string.ratingPauseConditionText));
            if (sameOrBetter || diffInAllParadigms) {
                msg.append("\n\n");
            }
        }
        if (sameOrBetter) {
            rating += 1;
            msg.append(getString(R.string.ratingSumConditionText));
            if (diffInAllParadigms) {
                msg.append("\n\n");
            }
        }
        if (diffInAllParadigms) {
            rating += 1;
            msg.append(getString(R.string.ratingAllParadigmsRaisedText));
        }
        if (!pauses && !sameOrBetter && !diffInAllParadigms) {
            msg.append(getString(R.string.ratingNoConditionReachedText));
        }

        binding.ratingBar.setRating(rating);
        binding.ratingMessage.setText(msg.toString());

        binding.okBtn.setOnClickListener((View v) -> {
            if (listener != null) {
                listener.onOkClick();
            }
            dismiss();
        });

        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BadgeFragmentListener) {
            listener = (BadgeFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface BadgeFragmentListener {

        void onOkClick();
    }
}
