package cz.nudz.www.trainingapp;


import android.database.DataSetObserver;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cz.nudz.www.trainingapp.databinding.TrialSelectionFragmentBinding;
import cz.nudz.www.trainingapp.training.ParadigmType;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrialSelectionFragment extends DialogFragment {

    private TrialSelectionFragmentBinding binding;

    public TrialSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.trial_selection_fragment, container, false);

        ParadigmListFragment listFragment = new ParadigmListFragment();
        listFragment.setListAdapter(new ParadigmListAdapter());

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(binding.paradigmTypeListFragment.getId() , listFragment, "");
        fragmentTransaction.commit();

        return binding.getRoot();
    }

    // TODO do i really have to override to get onitemclick lol?
    public static class ParadigmListFragment extends ListFragment {
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            ParadigmType paradigmType = (ParadigmType) getListView().getItemAtPosition(position);
            TrialActivity.startActivity(getActivity(), paradigmType);
        }
    }

    private class ParadigmListAdapter implements ListAdapter {

        private List<ParadigmType> paradigmTypes = ParadigmSet.getParadigmTypes();

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return paradigmTypes.size();
        }

        @Override
        public Object getItem(int position) {
            return paradigmTypes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getActivity());
            textView.setText(paradigmTypes.get(position).toString());
            return textView;
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return paradigmTypes.isEmpty();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }
    }
}
