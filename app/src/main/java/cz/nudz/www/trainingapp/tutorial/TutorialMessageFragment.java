package cz.nudz.www.trainingapp.tutorial;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.TutorialMessageFragmentBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialMessageFragment extends Fragment {

    public static final String KEY_TUTORIAL_MESSAGES = "KEY_TUTORIAL_MESSAGES";

    private TutorialMessageFragmentBinding binding;
    private ArrayList<Integer> messagesIds;

    public TutorialMessageFragment() {
        // Required empty public constructor
    }

    public static TutorialMessageFragment newInstance(@NonNull Integer... messageIds) {

        TutorialMessageFragment fragment = new TutorialMessageFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList(KEY_TUTORIAL_MESSAGES, new ArrayList<>(Arrays.asList(messageIds)));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messagesIds = getArguments().getIntegerArrayList(KEY_TUTORIAL_MESSAGES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.tutorial_message_fragment, container, false);

        for (Integer id : messagesIds) {
            if (id != null) {
                binding.messageContainer.addView(createMessageView(Html.fromHtml(getString(id))));
            }
        }

        return binding.getRoot();
    }

    private TextView createMessageView(Spanned message) {
        final TextView tv = new TextView(getContext());
        tv.setText(message);
        tv.setTextSize(20);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setPadding(64, 0, 64, 0);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 32;
        tv.setLayoutParams(params);
        return tv;
    }

}
