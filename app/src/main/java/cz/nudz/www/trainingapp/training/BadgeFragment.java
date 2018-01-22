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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BadgeFragmentListener} interface
 * to handle interaction events.
 * Use the {@link BadgeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BadgeFragment extends Fragment {

    private BadgeFragmentListener listener;
    private ViewDataBinding binding;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.badge_fragment, container, false);

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BadgeFragmentListener) {
            listener = (BadgeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BadgeFragmentListener");
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
