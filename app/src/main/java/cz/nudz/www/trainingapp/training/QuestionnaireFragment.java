package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.QuestionnaireFragmentBinding;
import cz.nudz.www.trainingapp.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuestionnaireListener} interface
 * to handle interaction events.
 */
public class QuestionnaireFragment extends Fragment {

    public static final String TAG = QuestionnaireFragment.class.getSimpleName();

    private QuestionnaireListener listener;
    private QuestionnaireFragmentBinding binding;

    public QuestionnaireFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.questionnaire_fragment, container, false);

        binding.submitBtn.setOnClickListener(view -> {
            final int checkedDiffRadio = binding.difficultyGrp.getCheckedRadioButtonId();
            final int checkedEffortRadio = binding.effortGrp.getCheckedRadioButtonId();
            if (checkedDiffRadio != -1 && checkedEffortRadio != -1) {
                if (listener != null) {
                    final int diffAnswer = Integer.parseInt(((TextView) binding.difficultyGrp.findViewById(checkedDiffRadio)).getText().toString());
                    final int effortAnswer = Integer.parseInt(((TextView) binding.effortGrp.findViewById(checkedEffortRadio)).getText().toString());
                    listener.onQuestionnairSubmission(effortAnswer, diffAnswer);
                }
            } else {
                Utils.showErrorDialog((BaseActivity) getActivity(),
                        getString(R.string.answerQuestionnaireErrorTitle),
                        getString(R.string.answerQuestionnaireErrorText));
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof QuestionnaireListener) {
            listener = (QuestionnaireListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement QuestionnaireListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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
    public interface QuestionnaireListener {
        void onQuestionnairSubmission(int effort, int difficulty);
    }
}
