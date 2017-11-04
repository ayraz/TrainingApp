package cz.nudz.www.trainingapp.utils;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.TrainingApp;

/**
 * Created by artem on 21-Sep-17.
 */

public class ErrorDialogFragment extends AlertDialogFragment {

    public static final String TAG = ErrorDialogFragment.class.getSimpleName();

    public static ErrorDialogFragment newInstance(@Nullable String title, @NonNull String message) {
        Bundle args = AlertDialogFragment.bundleArguments(
                title != null ? title : TrainingApp.getContext().get().getString(R.string.errorTitle),
                message);
        ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
        errorDialogFragment.setArguments(args);
        return errorDialogFragment;
    }

    @Override
    protected void init(AlertDialog.Builder builder) {
        super.init(builder);
        builder.setNeutralButton(R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            ErrorDialogFragment.this.dismiss();
        });
    }
}
