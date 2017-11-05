package cz.nudz.www.trainingapp.main;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.nudz.www.trainingapp.R;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by P8P67 on 11/4/2017.
 */

public class MenuCardAdapter extends RecyclerView.Adapter<MenuCardAdapter.ViewHolder> {

    // order here defines order of cards in recycler
    private static final int[] CARD_TITLES_IDS = {
            R.string.mainCardTitle,
            R.string.modesCardTitle,
            R.string.resultsCardTitle
    };

    private static final Map<Integer, List<Pair<Integer, Integer>>> CARD_OPTIONS_MAP = new HashMap<>();
    static {
        CARD_OPTIONS_MAP.put(R.string.mainCardTitle, Arrays.asList(
                new Pair<>(R.string.introOptionTitle, R.drawable.icons8_home)
        ));
        CARD_OPTIONS_MAP.put(R.string.modesCardTitle, Arrays.asList(
            new Pair<>(R.string.trainingOptionTitle, R.drawable.icons8_barbell),
            new Pair<>(R.string.tutorialOptionTitle, R.drawable.icons8_course),
            new Pair<>(R.string.trialOptionTitle, R.drawable.icons8_survey)
        ));
        CARD_OPTIONS_MAP.put(R.string.resultsCardTitle, Arrays.asList(
            new Pair<>(R.string.lastSessionPerformanceOptionTitle, R.drawable.icons8_area_chart),
            new Pair<>(R.string.allSessionsPerformanceOptionTitle, R.drawable.icons8_measure)
        ));
    }

    private final Context context;
    private final OnMenuOptionSelectedListener listener;
    private View selected;
    private View home;

    public MenuCardAdapter(Context context, OnMenuOptionSelectedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int cardTitleId = CARD_TITLES_IDS[position];
        holder.cardTitle.setText(cardTitleId);

        MenuOptionAdapter menuOptionAdapter = new MenuOptionAdapter(CARD_OPTIONS_MAP.get(cardTitleId));
        holder.optionsList.setAdapter(menuOptionAdapter);
        holder.optionsList.setLayoutManager(new LinearLayoutManager(context, VERTICAL, false));
    }

    @Override
    public int getItemCount() {
        return CARD_TITLES_IDS.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView cardTitle;
        private final RecyclerView optionsList;

        public TextView getCardTitle() {
            return cardTitle;
        }

        public RecyclerView getOptionsList() {
            return optionsList;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            this.cardTitle = (TextView) itemView.findViewById(R.id.cardTitle);
            this.optionsList = (RecyclerView) itemView.findViewById(R.id.optionList);
        }
    }

    public void clickHome() {
        home.performClick();
    }

    public interface OnMenuOptionSelectedListener {

        void onSelection(int optionStringId);
    }

    /**
     * Created by P8P67 on 11/4/2017.
     */

    protected class MenuOptionAdapter extends RecyclerView.Adapter<MenuOptionAdapter.ViewHolder> {

        private final List<Pair<Integer, Integer>> options;

        public MenuOptionAdapter(List<Pair<Integer, Integer>> options) {
            this.options = options;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_option, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Integer titleId = options.get(position).first;
            Integer iconId = options.get(position).second;
            holder.title.setText(titleId);
            holder.icon.setImageDrawable(context.getResources().getDrawable(iconId));

            // catch home btn for later
            if (titleId == R.string.introOptionTitle) home = holder.itemView;
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final ImageView icon;
            private final TextView title;

            public ViewHolder(View v) {
                super(v);
                icon = (ImageView) v.findViewById(R.id.optionIcon);
                title = (TextView) v.findViewById(R.id.optionTitle);

                v.setOnClickListener((View view) -> {
                    if (selected != null) {
                        // remove filter from previously click btn
                        selected.setBackgroundColor(android.R.color.transparent);
                    }
                    view.setBackgroundColor(context.getResources().getColor(R.color.menuOptionSelected));
                    selected = view;

                    listener.onSelection(options.get(getAdapterPosition()).first);
                });
            }
        }
    }
}
