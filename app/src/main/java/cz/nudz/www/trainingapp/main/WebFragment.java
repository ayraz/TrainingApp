package cz.nudz.www.trainingapp.main;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import cz.nudz.www.trainingapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebFragment extends Fragment {

    public static final String TAG = WebFragment.class.getSimpleName();

    private static final String WWW_PATH = "file:///android_asset/www/";
    private static final String KEY_FILE_NAME = "KEY_FILE_NAME";

    private WebFragment() {

    }

    public static WebFragment newInstance(final @StringRes int fileName) {
        Bundle args = new Bundle();
        args.putInt(KEY_FILE_NAME, fileName);

        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);

        final WebView webView = root.findViewById(R.id.webView);
        webView.setBackgroundColor(Color.TRANSPARENT);

        // pick file based on current locale
        webView.loadUrl(WWW_PATH + getString(getArguments().getInt(KEY_FILE_NAME)));

        // Inflate the layout for this fragment
        return root;
    }

}
