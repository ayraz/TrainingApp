package cz.nudz.www.trainingapp.preferences;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.tables.User;
import cz.nudz.www.trainingapp.login.SignupFragment;

public class PreferenceActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent i = new Intent(context, PreferenceActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_activity);

        setTitle(R.string.settingsOption);

        getFragmentManager().beginTransaction()
                .replace(R.id.containerId, new PreferenceFragment(), PreferenceFragment.TAG)
                .commit();
    }
}
