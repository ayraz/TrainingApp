package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;

import cz.nudz.www.trainingapp.MainActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.SessionManager;
import cz.nudz.www.trainingapp.TrainingApp;
import cz.nudz.www.trainingapp.database.TrainingAppDbHelper;
import cz.nudz.www.trainingapp.database.tables.User;
import cz.nudz.www.trainingapp.databinding.LoginActivityBinding;
import cz.nudz.www.trainingapp.utils.TrainingUtils;
import cz.nudz.www.trainingapp.utils.YesNoDialogFragment;

public class LoginActivity extends AppCompatActivity implements YesNoDialogFragment.YesNoDialogFragmentListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private TrainingApp applicationContext;
    private LoginActivityBinding binding;
    private TrainingAppDbHelper dbHelper;
    private SessionManager sessionManager;
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
    protected void onDestroy() {
        applicationContext.releaseHelper();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        applicationContext = (TrainingApp) getApplicationContext();
        dbHelper = applicationContext.getDbHelper();
        sessionManager = new SessionManager(applicationContext);
        binding.loginActivityLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        enteredUsername = binding.loginActivityUserName.getText().toString();
        if (TrainingUtils.isNullOrEmpty(enteredUsername)) {
            TrainingUtils.showErrorDialog(this, null, getString(R.string.createUserEmptyUsernameMessage));
        } else {
            try {
                final RuntimeExceptionDao<User, String> userDao = dbHelper.getUserDao();
                final User user = userDao.queryForId(enteredUsername);
                if (user == null) {
                    TrainingUtils.showYesNoDialog(this, getString(R.string.createUserTitle), getString(R.string.createUserMessage));
                } else {
                    startUserSession(user);
                }
            } catch (RuntimeException e) {
                Log.e(TAG, "Couldn't query for user", e);
                TrainingUtils.showErrorDialog(this, null, e.getMessage());
            }

        }
    }

    private void startUserSession(User user) {
        sessionManager.createSession(user.getUsername());
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onYes() {
        User newUser = new User();
        newUser.setUsername(enteredUsername);
        dbHelper.getUserDao().create(newUser);
        startUserSession(newUser);
    }
}
