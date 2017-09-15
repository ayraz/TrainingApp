package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.CountDownFragmentBinding;

public class CountDownFragment extends DialogFragment {

    public static final String TAG = CountDownFragment.class.getSimpleName();

    private static final String KEY_COUNT_DOWN_TYPE = "KEY_COUNT_DOWN_TYPE";

    private static final int SEQUENCE_TIMEOUT = 10000; // 10 sec
    private static final int PARADIGM_TIMEOUT = 3000 * 60; // 3 min

    private OnCountDownListener listener;
    private CountDownFragmentBinding binding;
    private CountDownTimer countDownTimer;

    public CountDownFragment() {
        // Required empty public constructor
    }

    public static CountDownFragment newInstance(boolean isSequenceCountDown) {
        CountDownFragment countDownFragment = new CountDownFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_COUNT_DOWN_TYPE, isSequenceCountDown);
        countDownFragment.setArguments(bundle);
        return countDownFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.count_down_fragment, container, false);

        if (getArguments() == null || getArguments().isEmpty())
            throw new IllegalStateException("Count down type must be set.");
        boolean isSequenceCountDown = getArguments().getBoolean(KEY_COUNT_DOWN_TYPE);

        // Start countdown
        // TODO: if this fragment is every reused in activity which is not locked to landscape, handle fragment rotation.
        countDownTimer = new CountDownTimer(isSequenceCountDown ? SEQUENCE_TIMEOUT : PARADIGM_TIMEOUT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / (60 * 1000);
                long seconds = (millisUntilFinished / 1000) % 60;
                binding.countDownFragmentCountDownText.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                listener.onCountDownExpired();
                dismiss();
            }
        };
        countDownTimer.start();

        binding.countDownFragmentContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onContinueClicked();
                dismiss();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        countDownTimer.cancel();
        listener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnCountDownListener) {
            listener = (OnCountDownListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + "must implement OnCountDownListener interface.");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCountDownListener {
        void onCountDownExpired();
        void onContinueClicked();
    }
}
