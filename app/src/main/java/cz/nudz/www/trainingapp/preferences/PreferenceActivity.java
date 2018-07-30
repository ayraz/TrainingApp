package cz.nudz.www.trainingapp.preferences;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;

public class PreferenceActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent i = new Intent(context, PreferenceActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_activity);

        setTitle(R.string.settings_option);

        getFragmentManager().beginTransaction()
                .replace(R.id.containerId, new SettingsFragment(), SettingsFragment.TAG)
                .commit();
    }
}
