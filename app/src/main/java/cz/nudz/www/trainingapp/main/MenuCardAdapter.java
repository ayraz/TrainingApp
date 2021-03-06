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

public class MenuCardAdapter extends RecyclerView.Adapter<MenuCardAdapter.ViewHolder> {

    // order here defines order of cards in recycler
    private static final int[] CARD_TITLES_IDS = {
            R.string.mainCardTitle,
            R.string.modesCardTitle,
            R.string.resultsCardTitle
    };
    private static final Map<Integer, List<Pair<Integer, Integer>>> CARD_OPTIONS_MAP = new HashMap<>();

    private final Context context;
    private final OnMenuOptionSelectedListener listener;
    private View home;
    private List<Integer> activeOptionPosition;
    private RecyclerView menuCardRecycler;

    public MenuCardAdapter(BaseActivity context, OnMenuOptionSelectedListener listener) {
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        menuCardRecycler = recyclerView;
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

        MenuOptionAdapter menuOptionAdapter = new MenuOptionAdapter(CARD_OPTIONS_MAP.get(cardTitleId), position);
        holder.optionsList.setAdapter(menuOptionAdapter);
        holder.optionsList.setLayoutManager(new LinearLayoutManager(context, VERTICAL, false));

        // HACK: this makes recycler view recreate all holders and thus we lose out-of-reach highlighting
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return CARD_TITLES_IDS.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView cardTitle;
        private final RecyclerView optionsList;

        public ViewHolder(View itemView) {
            super(itemView);
            this.cardTitle = itemView.findViewById(R.id.cardTitle);
            this.optionsList = itemView.findViewById(R.id.optionList);
        }
    }

    public interface OnMenuOptionSelectedListener {

        void onSelection(int optionStringId);
    }

    public List<Integer> getActiveOptionPosition() {
        return activeOptionPosition;
    }

    public void setActiveOptionPosition(List<Integer> activeOptionPosition) {
        this.activeOptionPosition = activeOptionPosition;
    }

    protected class MenuOptionAdapter extends RecyclerView.Adapter<MenuOptionAdapter.ViewHolder> {

        private final List<Pair<Integer, Integer>> options;
        private final int positionInParent;

        public MenuOptionAdapter(List<Pair<Integer, Integer>> options, int positionInParent) {
            this.options = options;
            this.positionInParent = positionInParent;
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
            if (titleId == R.string.sideMenuOptionIntro) home = holder.itemView;
            // re-highlight last active view if activity was recreated
            if (activeOptionPosition != null
                    && activeOptionPosition.get(0) == positionInParent
                    && activeOptionPosition.get(1) == position) {
                setActiveOptionColor(holder.itemView);
            }
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        public void setActiveOptionColor(View option) {
            Utils.setBackgroundAndKeepPadding(option, context.getResources().getColor(R.color.menuOptionSelected));
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final ImageView icon;
            private final TextView title;

            public ViewHolder(View v) {
                super(v);
                icon = v.findViewById(R.id.optionIcon);
                title = v.findViewById(R.id.optionTitle);

                v.setOnClickListener((View view) -> {
                    // remove filter from previously active option
                    if (activeOptionPosition != null) {
                        final MenuCardAdapter.ViewHolder cardViewHolder = (MenuCardAdapter.ViewHolder)
                            menuCardRecycler.findViewHolderForAdapterPosition(activeOptionPosition.get(0));
                        // If findViewHolderForAdapterPosition returns null, it means last selected view was recreated anyway
                        if (cardViewHolder != null) {
                            final MenuOptionAdapter.ViewHolder optionsViewHolder = (ViewHolder)
                                cardViewHolder.optionsList.findViewHolderForAdapterPosition(activeOptionPosition.get(1));
                            optionsViewHolder.itemView.setBackgroundColor(android.R.color.transparent);
                        }
                    }
                    setActiveOptionPosition(Arrays.asList(positionInParent, getAdapterPosition()));
                    setActiveOptionColor(view);

                    listener.onSelection(options.get(getAdapterPosition()).first);
                });
            }
        }
    }
}
