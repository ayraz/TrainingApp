package cz.nudz.www.trainingapp.login;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.tables.User;
import cz.nudz.www.trainingapp.databinding.SignupFragmentBinding;
import cz.nudz.www.trainingapp.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupListener} interface
 * to handle interaction events.
 */
public class SignupFragment extends DialogFragment {

    public static final String TAG = SignupFragment.class.getSimpleName();

    private SignupListener listner;
    private BaseActivity activity;
    private SignupFragmentBinding binding;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.signup_fragment,
                container, false);

        binding.btnSignup.setOnClickListener(view -> {
            final String username = binding.inputName.getText().toString();
            if (Utils.isNullOrEmpty(username)) {
                Utils.showErrorDialog(activity, null, getString(R.string.emptyUsernameError));
            } else {
                final RuntimeExceptionDao<User, String> userDao = activity.getHelper().getUserDao();
                final User user = userDao.queryForId(username);
                if (user != null) {
                    Utils.showErrorDialog(activity, null,
                            getString(R.string.userAlreadyExistsError));
                } else if (listner != null) {
                    listner.onUserCreated(username);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (BaseActivity) context;
        if (context instanceof SignupListener) {
            listner = (SignupListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SignupListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listner = null;
        activity = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface SignupListener {
        void onUserCreated(String username);
    }
}
