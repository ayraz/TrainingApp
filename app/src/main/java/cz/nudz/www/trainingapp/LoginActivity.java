package cz.nudz.www.trainingapp;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import cz.nudz.www.trainingapp.data.tables.User;
import cz.nudz.www.trainingapp.databinding.LoginActivityBinding;
import cz.nudz.www.trainingapp.main.BaseActivity;
import cz.nudz.www.trainingapp.main.MainActivity;
import cz.nudz.www.trainingapp.utils.Utils;
import cz.nudz.www.trainingapp.utils.YesNoDialogFragment;

public class LoginActivity extends BaseActivity implements YesNoDialogFragment.YesNoDialogFragmentListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginActivityBinding binding;
    private String enteredUsername;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        // Closing all activities on top this one
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Start new app task history
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        binding.loginActivityLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        enteredUsername = binding.loginActivityUserName.getText().toString();
        if (Utils.isNullOrEmpty(enteredUsername)) {
            Utils.showErrorDialog(this, null, getString(R.string.createUserEmptyUsernameMessage));
        } else {
            try {
                final RuntimeExceptionDao<User, String> userDao = getHelper().getUserDao();
                final User user = userDao.queryForId(enteredUsername);
                if (user == null) {
                    Utils.showYesNoDialog(this, getString(R.string.createUserTitle), getString(R.string.createUserMessage));
                } else {
                    startUserSession(user);
                }
            } catch (RuntimeException e) {
                Log.e(TAG, "Couldn't query for user", e);
                Utils.showErrorDialog(this, null, e.getMessage());
            }

        }
    }

    private void startUserSession(User user) {
        getSessionManager().createSession(user.getUsername());
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onYes() {
        User newUser = new User();
        newUser.setUsername(enteredUsername);
        getHelper().getUserDao().create(newUser);
        startUserSession(newUser);
    }
}
