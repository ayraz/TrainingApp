package cz.nudz.www.trainingapp.training;

<<<<<<< HEAD
=======
import android.app.Dialog;
>>>>>>> ba2787d3db4a87ef3949c2204a9155c645dbab3c
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
<<<<<<< HEAD
=======
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
>>>>>>> ba2787d3db4a87ef3949c2204a9155c645dbab3c
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

<<<<<<< HEAD
import cz.nudz.www.trainingapp.R;
=======
import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.TrainingRepository;
import cz.nudz.www.trainingapp.data.tables.TrainingSession;
import cz.nudz.www.trainingapp.databinding.BadgeFragmentBinding;
>>>>>>> ba2787d3db4a87ef3949c2204a9155c645dbab3c

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BadgeFragmentListener} interface
 * to handle interaction events.
 * Use the {@link BadgeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
<<<<<<< HEAD
public class BadgeFragment extends Fragment {

    private BadgeFragmentListener listener;
    private ViewDataBinding binding;
=======
public class BadgeFragment extends DialogFragment {

    public static final String TAG = BadgeFragment.class.getSimpleName();

    private BadgeFragmentListener listener;
    private BadgeFragmentBinding binding;
>>>>>>> ba2787d3db4a87ef3949c2204a9155c645dbab3c

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
<<<<<<< HEAD
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.badge_fragment, container, false);
=======
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.badge_fragment, container, false);
        TrainingRepository tr = new TrainingRepository((BaseActivity) getActivity());

        int rating = 0;
        StringBuilder msg = new StringBuilder();
        boolean pauses = tr.hasOnlyShortPauses();
        if (pauses) {
            rating += 1;
            msg.append("- ");
            msg.append(getString(R.string.ratingPauseConditionText));
            msg.append("\n\n");
        }
        boolean sameOrBetter = tr.isCurrentSessionSameOrBetter();
        if (sameOrBetter) {
            rating += 1;
            msg.append("- ");
            msg.append(getString(R.string.ratingSumConditionText));
            msg.append("\n\n");
        }
        boolean diffInAllParadigms = tr.hasRaisedDiffInAllParadigms();
        if (diffInAllParadigms) {
            rating += 1;
            msg.append("- ");
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
>>>>>>> ba2787d3db4a87ef3949c2204a9155c645dbab3c

        return binding.getRoot();
    }

<<<<<<< HEAD
=======
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        return dialog;
    }

>>>>>>> ba2787d3db4a87ef3949c2204a9155c645dbab3c
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BadgeFragmentListener) {
            listener = (BadgeFragmentListener) context;
<<<<<<< HEAD
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BadgeFragmentListener");
=======
>>>>>>> ba2787d3db4a87ef3949c2204a9155c645dbab3c
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
