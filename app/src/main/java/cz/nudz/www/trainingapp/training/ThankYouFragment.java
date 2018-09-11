package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.ThankYouFragmentBinding;

public class ThankYouFragment extends Fragment {

    public static final String TAG = ThankYouFragment.class.getSimpleName();
    private static final String KEY_IS_TEST_MODE = "KEY_IS_TEST_MODE";

    private ThankYouFragmentListener mListener;
    private ThankYouFragmentBinding binding;

    public ThankYouFragment() {
        // Required empty public constructor
    }

    public static ThankYouFragment newInstance(final boolean isTestMode) {
        Bundle args = new Bundle();
        args.putBoolean(KEY_IS_TEST_MODE, isTestMode);

        ThankYouFragment fragment = new ThankYouFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.thank_you_fragment, container, false);

        final boolean isTestMode = getArguments().getBoolean(KEY_IS_TEST_MODE);

        binding.finishBtn.setOnClickListener(v -> {
            if (mListener != null) {
                if (isTestMode) {
                    mListener.returnToTraining();
                } else {
                    mListener.proceedToResults();
                }
            }
        });

        binding.message.setText(isTestMode ? R.string.thankYouForTestMessage : R.string.thankYouForTrainingMessage);

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ThankYouFragmentListener) {
            mListener = (ThankYouFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ThankYouFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ThankYouFragmentListener {

        void returnToTraining();

        void proceedToResults();
    }
}
