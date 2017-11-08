package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.CountDownFragmentBinding;

public class CountDownFragment extends DialogFragment {

    public static final String TAG = CountDownFragment.class.getSimpleName();

    private static final String KEY_COUNT_DOWN_MILLIS = "KEY_COUNT_DOWN_MILLIS";
    private static final String KEY_COUNT_DOWN_TITLE = "KEY_COUNT_DOWN_TITLE";
    private static final String KEY_COUNT_DOWN_BTN_TEXT = "KEY_COUNT_DOWN_BTN_TEXT";

    private CountDownListener listener;
    private CountDownFragmentBinding binding;
    private CountDownTimer countDownTimer;

    public CountDownFragment() {
        // Required empty public constructor
    }

    public static CountDownFragment newInstance(int millisCountDown) {
        CountDownFragment countDownFragment = new CountDownFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_COUNT_DOWN_MILLIS, millisCountDown);
        countDownFragment.setArguments(bundle);
        return countDownFragment;
    }

    public static CountDownFragment newInstance(int millisCountDown, @NonNull String title, @NonNull String btnText) {
        final CountDownFragment countDownFragment = CountDownFragment.newInstance(millisCountDown);
        final Bundle arguments = countDownFragment.getArguments();
        arguments.putString(KEY_COUNT_DOWN_TITLE, title);
        arguments.putString(KEY_COUNT_DOWN_BTN_TEXT, btnText);
        return countDownFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.count_down_fragment, container, false);

        if (getArguments() == null || !getArguments().containsKey(KEY_COUNT_DOWN_MILLIS))
            throw new IllegalStateException("Count down type must be set.");
        int millisToCountDown = getArguments().getInt(KEY_COUNT_DOWN_MILLIS);

        if (getArguments().containsKey(KEY_COUNT_DOWN_TITLE)) {
            binding.message.setText(getArguments().getString(KEY_COUNT_DOWN_TITLE));
        }
        if (getArguments().containsKey(KEY_COUNT_DOWN_BTN_TEXT)) {
            binding.continueBtn.setText(getArguments().getString(KEY_COUNT_DOWN_BTN_TEXT));
        }

        // TODO: if this fragment is ever reused in activity which is not locked to landscape, handle fragment rotation.
        // Start countdown
        countDownTimer = new CountDownTimer(millisToCountDown, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / (60 * 1000);
                long seconds = (millisUntilFinished / 1000) % 60;
                binding.countDownText.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                listener.onExpired();
                dismiss();
            }
        };
        countDownTimer.start();

        binding.continueBtn.setOnClickListener(v -> {
            listener.onContinue();
            dismiss();
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
        if (context instanceof CountDownListener) {
            listener = (CountDownListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + "must implement CountDownListener interface.");
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
    public interface CountDownListener {
        void onExpired();

        void onContinue();
    }
}
