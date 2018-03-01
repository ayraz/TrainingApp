package cz.nudz.www.trainingapp.trial;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import cz.nudz.www.trainingapp.ParadigmSet;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.tutorial.TutorialFragmentFactory;

/**
 * Created by P8P67 on 11/1/2017.
 */

public class TrialRowAdapter extends RecyclerView.Adapter<TrialRowAdapter.ViewHolder> {

    private final OnParadigmRowClick callback;

    public TrialRowAdapter(OnParadigmRowClick callback) {
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trial_selection_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = "";
        Context context = holder.getTryBtn().getContext();
        final ParadigmType type = ParadigmSet.getAt(position);
        switch (type) {
            case COLOR:
                title = context.getString(R.string.tryColorParadigmBtn);
                break;
            case POSITION:
                title = context.getString(R.string.tryPositionParadigmBtn);
                break;
            case SHAPE:
                title = context.getString(R.string.tryShapeParadigmBtn);
                break;
        }
        holder.getTryBtn().setText(title);

        int drawableId = TutorialFragmentFactory.getIconByParadigm(type);
        holder.getIcon().setImageDrawable(context.getResources().getDrawable(drawableId));

        String[] difficulties = new String[Difficulty.values().length];
        for (int i = 0; i < difficulties.length; ++i) {
            difficulties[i] = Integer.toString(i+1);
        }
        holder.getDifficultyPicker().setMinValue(1);
        holder.getDifficultyPicker().setMaxValue(difficulties.length);
        holder.getDifficultyPicker().setWrapSelectorWheel(false);
        holder.getDifficultyPicker().setDisplayedValues(difficulties);
    }

    @Override
    public int getItemCount() {
        return ParadigmSet.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView icon;
        private final TextView tryBtn;
        private final NumberPicker difficultyPicker;

        public ViewHolder(View v) {
            super(v);
            this.tryBtn = v.findViewById(R.id.tryBtn);
            this.difficultyPicker = v.findViewById(R.id.difficultyPicker);
            this.icon = v.findViewById(R.id.icon);

            tryBtn.setOnClickListener(view -> TrialRowAdapter.this.callback.onClick(view,
                    ParadigmSet.getAt(getAdapterPosition()),
                    Difficulty.values()[ViewHolder.this.difficultyPicker.getValue() - 1]));
        }

        public TextView getTryBtn() {
            return tryBtn;
        }

        public NumberPicker getDifficultyPicker() {
            return difficultyPicker;
        }

        public ImageView getIcon() {
            return icon;
        }
    }

    public interface OnParadigmRowClick {

        void onClick(View v, ParadigmType paradigmType, Difficulty difficulty);
    }
}
