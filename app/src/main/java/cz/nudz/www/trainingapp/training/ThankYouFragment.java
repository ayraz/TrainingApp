package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.ThankYouFragmentBinding;

public class ThankYouFragment extends Fragment {

    public static final String TAG = ThankYouFragment.class.getSimpleName();

    private ThankYouFragmentListener mListener;
    private ThankYouFragmentBinding binding;

    public ThankYouFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.thank_you_fragment, container, false);

        binding.finishBtn.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.proceedToResults();
            }
        });

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

        void proceedToResults();
    }
}
