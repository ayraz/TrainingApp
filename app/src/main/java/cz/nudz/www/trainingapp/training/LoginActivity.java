package cz.nudz.www.trainingapp.training;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.view.View;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.LoginActivityBinding;
import cz.nudz.www.trainingapp.utils.TrainingUtils;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityBinding binding;

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
        Editable text = binding.loginActivityUserName.getText();
        if (TrainingUtils.isNullOrEmpty(text.toString())) {
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

        }
    }
}
