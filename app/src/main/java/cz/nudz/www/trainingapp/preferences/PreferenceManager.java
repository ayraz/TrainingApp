package cz.nudz.www.trainingapp.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.training.TrainingFragment;

import static cz.nudz.www.trainingapp.trial.TrialSelectionFragment.TEST_TRIAL_COUNT;

public class PreferenceManager {

    private static final String KEY_IS_LOGGED_IN = "KEY_IS_LOGGED_IN";
    private static final String KEY_USERNAME = "KEY_USERNAME";
    private static final String KEY_ADMIN_SESSION = "KEY_ADMIN_SESSION";

    private final Context context;
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        this.context = context;
        this.pref = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = pref.edit();
    }

    public void setIsValidSession(final boolean value) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public boolean getIsValidSession() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setUsername(final String name) {
        editor.putString(KEY_USERNAME, name);
        editor.commit();
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public void clearSession() {
        editor.remove(KEY_ADMIN_SESSION);
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USERNAME);
        editor.commit();
    }

    public void setIsAdminSession(final boolean isAdminSession) {
        editor.putBoolean(KEY_ADMIN_SESSION, isAdminSession);
        editor.commit();
    }

    public boolean getIsAdminSession() {
        return pref.getBoolean(KEY_ADMIN_SESSION, false);
    }

    public int getTrialCount() {
        String key = context.getString(R.string.pref_trial_number);
        return pref.getInt(key, TEST_TRIAL_COUNT);
    }

    public int getPresentationTime() {
        String key = context.getString(R.string.pref_trial_presentation_time);
        return pref.getInt(key, TrainingFragment.MEMORIZATION_INTERVAL);
    }
}
