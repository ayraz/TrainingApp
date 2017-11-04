package cz.nudz.www.trainingapp.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.nudz.www.trainingapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = HomeFragment.class.getSimpleName();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);
        TextView welcomeText = root.findViewById(R.id.welcomeText);
        welcomeText.setText(Html.fromHtml(getString(R.string.welcomeText)));
        // Inflate the layout for this fragment
        return root;
    }

}
