package cz.nudz.www.trainingapp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import java.util.HashMap;

import cz.nudz.www.trainingapp.main.BaseActivity;
import cz.nudz.www.trainingapp.utils.Utils;

/**
 * Created by artem on 21-Sep-17.
 */

public class SessionManager {

    private SharedPreferences pref;
    private Editor editor;
    private BaseActivity context;

    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "TrainingAppPref";

    private static final String KEY_IS_LOGGED_IN = "KEY_IS_LOGGED_IN";
    public static final String KEY_USERNAME = "KEY_USERNAME";

    public SessionManager(BaseActivity context) {
        this.context = context;
        this.pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.editor = pref.edit();
    }

    public void createSession(String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, name);
        editor.commit();
    }

    /**
     * Check if user is logged in. If user is not logged in, redirect to LoginActivity
     */
    public boolean checkLogin() {
        if (!this.isLoggedIn()) {
            // user is not logged in, redirect him to Login Activity
            Utils.showErrorDialog(context, null, context.getString(R.string.errorNotLoggedIn));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    redirectToLogin();
                }
            }, 3000);
            return false;
        }
        return true;
    }

    /**
     * Get stored session data. Use public keys on this class to query for details.
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        return user;
    }

    /**
     * Clear session data.
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to LoginActivity
        redirectToLogin();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void redirectToLogin() {
        LoginActivity.startActivity(context);
    }
}
