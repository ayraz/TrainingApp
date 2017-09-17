package cz.nudz.www.trainingapp.training;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.WarningParadigmFragmentBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class WarningFragment extends Fragment {

    private static final String KEY_HELP_TEXT_ID = "param1";
    private WarningParadigmFragmentBinding binding;
    private int helpTextId;

    public WarningFragment() {
        // Required empty public constructor
    }

    public static WarningFragment newInstance(@NonNull int helpTextId) {
        WarningFragment fragment = new WarningFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_HELP_TEXT_ID, helpTextId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            helpTextId = arguments.getInt(KEY_HELP_TEXT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.warning_paradigm_fragment, container, false);

        if (helpTextId != 0) {
            binding.warningFragmentExplanation.setText(Html.fromHtml(getString(helpTextId)));
        }
        return binding.getRoot();
    }
}
