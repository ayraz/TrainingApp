package cz.nudz.www.trainingapp.training;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.TrainingApp;
import cz.nudz.www.trainingapp.database.TrainingAppDbHelper;
import cz.nudz.www.trainingapp.database.tables.User;
import cz.nudz.www.trainingapp.databinding.LoginActivityBinding;
import cz.nudz.www.trainingapp.utils.TrainingUtils;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private TrainingApp applicationContext;
    private LoginActivityBinding binding;
    private TrainingAppDbHelper dbHelper;

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

        binding.loginActivityLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        final String username = binding.loginActivityUserName.getText().toString();
        if (TrainingUtils.isNullOrEmpty(username)) {
            new AlertDialog.Builder(this)
                    .setMessage("The username field mustn't be empty!")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            try {
                final RuntimeExceptionDao<User, String> userDao = dbHelper.getUserDao();
                final User user = userDao.queryForId(username);
                if (user == null) {
                    TrainingUtils.showYesNoDialog(this, getString(R.string.createUserTitle), getString(R.string.createUserMessage),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    User newUser = new User();
                                    newUser.setUsername(username);
                                    userDao.create(newUser);
                                }
                            }, null);
                }
            } catch (SQLException e) {
                Log.e(TAG, "Couldn't query for user", e);
                TrainingUtils.showError(this, e.getMessage());
            }

        }
    }
}
