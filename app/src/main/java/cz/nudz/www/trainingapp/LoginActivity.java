package cz.nudz.www.trainingapp;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import cz.nudz.www.trainingapp.data.tables.User;
import cz.nudz.www.trainingapp.databinding.LoginActivityBinding;
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
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        binding.lastLoginAsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.username.setText(binding.lastLoginAsBtn.getText());
            }
        });

        // set last user btn help
        try {
            List<User> lastLoginUser = getHelper().getUserDao().queryBuilder()
                    .orderBy("lastLoginDate", false)
                    .limit(1L)
                    .query();
            if (!lastLoginUser.isEmpty()) {
                binding.lastLoginAsBtn.setText(lastLoginUser.get(0).getUsername());
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
            Utils.showErrorDialog(this, null, e.getMessage());
        }
    }

    private void login() {
        enteredUsername = binding.username.getText().toString();
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
        // update login date
        user.setLastLoginDate(new Date());
        getHelper().getUserDao().update(user);

        getSessionManager().createSession(user.getUsername());
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onYes() {
        // create and store new user
        User newUser = new User();
        newUser.setUsername(enteredUsername);
        Date now = new Date();
        newUser.setRegistrationDate(now);
        newUser.setLastLoginDate(now);
        getHelper().getUserDao().create(newUser);
        startUserSession(newUser);
    }
}
