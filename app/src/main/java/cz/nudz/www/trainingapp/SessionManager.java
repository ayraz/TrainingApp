package cz.nudz.www.trainingapp;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import cz.nudz.www.trainingapp.training.LoginActivity;

/**
 * Created by artem on 21-Sep-17.
 */

public class SessionManager {

    private SharedPreferences pref;
    private Editor editor;
    private Context context;

    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "TrainingAppPref";

    private static final String KEY_IS_LOGGED_IN = "KEY_IS_LOGGED_IN";
    public static final String KEY_NAME = "KEY_NAME";

    public SessionManager(Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createSession(String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_NAME, name);

        editor.commit();
    }

    /**
     * Check if user is logged in. If user is not logged in, redirect to LoginActivity
     */
    public void checkLogin() {
        if (!this.isLoggedIn()) {
            // user is not logged in, redirect him to Login Activity
            redirectToLogin();
        }

    }

    /**
     * Get stored session data. Use public keys on this class to query for details.
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

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
        Intent i = new Intent(context, LoginActivity.class);
        // Closing all activities on top this one
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Start new app task history
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
