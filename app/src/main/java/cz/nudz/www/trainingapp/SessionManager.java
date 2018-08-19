package cz.nudz.www.trainingapp;

import android.content.Context;
import android.widget.Toast;

import cz.nudz.www.trainingapp.login.LoginActivity;
import cz.nudz.www.trainingapp.preferences.PreferenceManager;

/**
 * Created by artem on 21-Sep-17.
 */

public class SessionManager {

    private final PreferenceManager preferenceManager;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
        this.preferenceManager = new PreferenceManager(context);
    }

    public void createSession(String name) {
        preferenceManager.setIsValidSession(true);
        preferenceManager.setUsername(name);
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
     *
     * @return Username of currently logged user; null if no user is logged.
     */
    public String getUsername() {
        return preferenceManager.getUsername();
    }

    /**
     * Clear session data.
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        preferenceManager.clearSession();
        // After logout redirect user to LoginActivity
        redirectToLogin();
    }

    public boolean isLoggedIn() {
        return preferenceManager.getIsValidSession();
    }

    private void redirectToLogin() {
        LoginActivity.startActivity(context);
    }
}
