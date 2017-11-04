package cz.nudz.www.trainingapp.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import cz.nudz.www.trainingapp.R;

/**
 * Created by artem on 21-Sep-17.
 */

public class YesNoDialogFragment extends AlertDialogFragment {

    public static final String TAG = YesNoDialogFragment.class.getSimpleName();
    private YesNoDialogFragmentListener listener;

    public static YesNoDialogFragment newInstance(@Nullable String title, @NonNull String message) {
        Bundle args = AlertDialogFragment.bundleArguments(title, message);
        YesNoDialogFragment yesNoDialogFragment = new YesNoDialogFragment();
        yesNoDialogFragment.setArguments(args);
        return yesNoDialogFragment;
    }

    @Override
    protected void init(AlertDialog.Builder builder) {
        super.init(builder);
        builder.setNegativeButton(R.string.no, (dialog, which) -> {
            dialog.dismiss();
            YesNoDialogFragment.this.dismiss();
        });
        builder.setPositiveButton(R.string.yes, (dialog, which) -> listener.onYes());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof YesNoDialogFragmentListener) {
            listener = (YesNoDialogFragmentListener) context;
        } else {
            throw new ClassCastException("Activity must implement YesNoDialogFragmentListener.");
        }
    }

    public interface YesNoDialogFragmentListener {

        void onYes();
    }
}
