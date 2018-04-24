package cz.nudz.www.trainingapp.main;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import cz.nudz.www.trainingapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = HomeFragment.class.getSimpleName();
    private static final String WWW_PATH = "file:///android_asset/www/";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);

        final WebView webView = root.findViewById(R.id.webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        // pick home file based on current locale
        webView.loadUrl(WWW_PATH + getString(R.string.homeFile));

        // Inflate the layout for this fragment
        return root;
    }

}
