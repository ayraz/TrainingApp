package cz.nudz.www.trainingapp.dialogs;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class AlertDialogFragment extends DialogFragment {

    public static final String KEY_TITLE ="KEY_TITLE";
    public static final String KEY_MESSAGE ="KEY_MESSAGE";

    public static Bundle bundleArguments(@Nullable String title, @NonNull String message) {
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        return args;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        this.init(builder);
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.alert_dialog_fragment, container, false);
    }

    protected void init(AlertDialog.Builder builder) {
        String title = getArguments().getString(KEY_TITLE);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setMessage(getArguments().getString(KEY_MESSAGE));
    }
}
