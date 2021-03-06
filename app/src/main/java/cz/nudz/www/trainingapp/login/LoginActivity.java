package cz.nudz.www.trainingapp.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.tables.User;
import cz.nudz.www.trainingapp.databinding.LoginActivityBinding;
import cz.nudz.www.trainingapp.main.MainActivity;
import cz.nudz.www.trainingapp.utils.CollectionUtils;
import cz.nudz.www.trainingapp.utils.Utils;

public class LoginActivity extends BaseActivity {

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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        binding.btnLogin.setOnClickListener(v -> login());

        binding.existingUserList.setOnItemClickListener((parent, view, position, id) ->
                binding.inputName.setText(((TextView) view).getText()));

        // set last user btn help
        try {
            List<User> existingUsers = getHelper().getUserDao().queryBuilder()
                    .orderBy("lastLoginDate", false)
                    .query();
            List<String> names = CollectionUtils.map(existingUsers, User::getUsername);
            // remove admin from suggestions
            names = CollectionUtils.filter(names, name -> !name.equals("##"));
            if (!names.isEmpty()) {
                binding.existingUserList.setAdapter(new ArrayAdapter<>(this, R.layout.simple_text_line, names));
            } else {
                binding.lastLoginListLabel.setVisibility(View.GONE);
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
        } else {
            try {
                final RuntimeExceptionDao<User, String> userDao = getHelper().getUserDao();
                final User user = userDao.queryForId(username);
                if (user == null) {
                    Utils.showAlertDialog(this, null,
                            getString(R.string.userDoesNotExistError));
                } else {
                    if (username.equals("##")) {
                        // mark this session as admin
                        getPreferenceManager().setIsAdminSession(true);
                    }
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
}
