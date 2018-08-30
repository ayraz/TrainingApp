package cz.nudz.www.trainingapp.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.utils.Utils;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by P8P67 on 11/4/2017.
 */

public class MenuCardAdapter extends ArrayAdapter<Integer> {

    // order here defines order of cards in recycler
    private static final Integer[] CARD_TITLES_IDS = {
            R.string.mainCardTitle,
            R.string.modesCardTitle,
            R.string.resultsCardTitle
    };
    private static final Map<Integer, List<Pair<Integer, Integer>>> CARD_OPTIONS_MAP = new HashMap<>();

    private final Context context;
    private final OnMenuOptionSelectedListener listener;
    private final List<ListView> optionLists = new ArrayList<>();

    public MenuCardAdapter(BaseActivity context, OnMenuOptionSelectedListener listener) {
        super(context, R.layout.menu_card, CARD_TITLES_IDS);
        this.context = context;
        this.listener = listener;
        boolean isAdmin = context.getPreferenceManager().getIsAdminSession();

        CARD_OPTIONS_MAP.put(R.string.mainCardTitle, Arrays.asList(
                new Pair<>(R.string.sideMenuOptionIntro, R.drawable.icons8_home),
                new Pair<>(R.string.sideMenuOptionAbout, R.drawable.icons8_about)
        ));
        List<Pair<Integer, Integer>> modeCardList = new LinkedList<>(Arrays.asList(
                new Pair<>(R.string.sideMenuOptionTutorial, R.drawable.icons8_classroom),
                new Pair<>(R.string.sideMenuOptionTrial, R.drawable.icons8_test_tube),
                new Pair<>(R.string.sideMenuOptionTraining, R.drawable.icons8_barbell)));
        if (isAdmin) {
            modeCardList.add(new Pair<>(R.string.sideMenuOptionTest, R.drawable.icons8_survey));
        }
        CARD_OPTIONS_MAP.put(R.string.modesCardTitle, modeCardList);
        CARD_OPTIONS_MAP.put(R.string.resultsCardTitle, Arrays.asList(
                new Pair<>(R.string.lastSessionPerformanceOptionTitle, R.drawable.icons8_area_chart),
                new Pair<>(R.string.allSessionsPerformanceOptionTitle, R.drawable.icons8_measure)
        ));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CardView view = (CardView) inflate(R.layout.menu_card, parent);

        TextView title = view.findViewById(R.id.cardTitle);
        Integer cardTitleId = CARD_TITLES_IDS[position];
        title.setText(cardTitleId);

        ListView list = view.findViewById(R.id.optionList);
        MenuOptionAdapter menuOptionAdapter = new MenuOptionAdapter(CARD_OPTIONS_MAP.get(cardTitleId), list);
        list.setAdapter(menuOptionAdapter);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        optionLists.add(list);

        return view;
    }

    private View inflate(@LayoutRes int layout, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(layout, parent, false);
    }

    public interface OnMenuOptionSelectedListener {

        void onSelection(int optionStringId);
    }

    protected class MenuOptionAdapter extends ArrayAdapter<Pair<Integer, Integer>> {

        private final List<Pair<Integer, Integer>> options;
        private final ListView list;

        public MenuOptionAdapter(List<Pair<Integer, Integer>> options, ListView list) {
            super(context, R.layout.menu_option, options);
            this.options = options;
            this.list = list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewGroup view = (ViewGroup) inflate(R.layout.menu_option, parent);

            Integer titleId = options.get(position).first;
            Integer iconId = options.get(position).second;
            ImageView icon = view.findViewById(R.id.optionIcon);
            TextView title = view.findViewById(R.id.optionTitle);
            title.setText(titleId);
            icon.setImageDrawable(context.getResources().getDrawable(iconId));

            view.setOnClickListener(v -> {
                // remove filter from previously active option
                for (ListView lv : optionLists) {
                    if (lv.getCheckedItemCount() != 0) {
                        View childAt = lv.getChildAt(lv.getCheckedItemPosition());
                        childAt.setBackgroundColor(android.R.color.transparent);
                    }
                }
                setActiveOptionColor(v);

                listener.onSelection(options.get(list.getPositionForView(v)).first);
            });
            return view;
        }

        void setActiveOptionColor(View option) {
            Utils.setBackgroundAndKeepPadding(option, context.getResources().getColor(R.color.menuOptionSelected));
        }
    }
}
