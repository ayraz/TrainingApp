package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.CountDownFragmentBinding;

public class CountDownFragment extends DialogFragment {

    private static final int SEQUENCE_TIMEOUT = 10000;

    private OnCountDownListener listener;
    private CountDownFragmentBinding binding;
    private CountDownTimer countDownTimer;

    public CountDownFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.count_down_fragment, container, false);

        // Start countdown
        // TODO: if this fragment is every reused in activity which is not locked to landscape, handle fragment rotation.
        countDownTimer = new CountDownTimer(SEQUENCE_TIMEOUT, 1000) {
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
