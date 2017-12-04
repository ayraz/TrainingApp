package cz.nudz.www.trainingapp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

import java.util.HashMap;

import cz.nudz.www.trainingapp.login.LoginActivity;

/**
 * Created by artem on 21-Sep-17.
 */

public class SessionManager {

    public static final String KEY_USERNAME = "KEY_USERNAME";

    private SharedPreferences pref;
    private Editor editor;
    private BaseActivity context;

    private static int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "TrainingAppPref";
    private static final String KEY_IS_LOGGED_IN = "KEY_IS_LOGGED_IN";

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
            Toast.makeText(context, R.string.errorNotLoggedIn, Toast.LENGTH_LONG);
            redirectToLogin();
            return false;
        }
        return true;
    }

    /**
     * Get stored session data. Use public keys on this class to query for details.
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        return user;
    }

    /**
     *
     * @return Username of currently logged user; null if no user is logged.
     */
    public String getUsername() {
        return getUserDetails().get(SessionManager.KEY_USERNAME);
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
