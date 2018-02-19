package cz.nudz.www.trainingapp.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.tables.User;
import cz.nudz.www.trainingapp.databinding.LoginActivityBinding;
import cz.nudz.www.trainingapp.main.MainActivity;
import cz.nudz.www.trainingapp.utils.Utils;

public class LoginActivity extends BaseActivity implements
        SignupFragment.SignupListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginActivityBinding binding;
    private String username;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        // Start new app task history
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if there is existing session, spare user's time..
//        if (getSessionManager().isLoggedIn()) {
//            startActivity(new Intent(this, MainActivity.class));
//        }

        binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        binding.btnLogin.setOnClickListener(v -> login());

        binding.lastLoginAsBtn.setBackgroundColor(android.R.color.transparent);
        binding.lastLoginAsBtn.setOnClickListener(v ->
                binding.inputName.setText(binding.lastLoginAsBtn.getText()));

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
            Utils.showAlertDialog(this, null, e.getMessage());
        }
    }

    private void login() {
        username = binding.inputName.getText().toString();
        if (Utils.isNullOrEmpty(username)) {
            Utils.showAlertDialog(this, null,
                    getString(R.string.emptyUsernameError));
        } else if (username.equals("##")) {
            // special user creation mode
            final SignupFragment signupFragment = new SignupFragment();
            signupFragment.show(getSupportFragmentManager(), SignupFragment.TAG);
        } else {
            try {
                final RuntimeExceptionDao<User, String> userDao = getHelper().getUserDao();
                final User user = userDao.queryForId(username);
                if (user == null) {
                    Utils.showAlertDialog(this, null,
                            getString(R.string.userDoesNotExistError));
                } else {
                    startUserSession(user);
                }
            } catch (RuntimeException e) {
                Log.e(TAG, "Couldn't get user", e);
                Utils.showAlertDialog(this, null, e.getMessage());
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
    public void onUserCreated(String username) {
        // create and store new user
        User newUser = new User();
        newUser.setUsername(username);
        Date now = new Date();
        newUser.setRegistrationDate(now);
        newUser.setLastLoginDate(now);
        getHelper().getUserDao().create(newUser);
        startUserSession(newUser);
    }
}
