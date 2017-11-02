package cz.nudz.www.trainingapp.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import cz.nudz.www.trainingapp.ParadigmSet;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;

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
        Context context = holder.getChartTitleBtn().getContext();
        switch (ParadigmSet.getAt(position)) {
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
        holder.getChartTitleBtn().setText(title);

        String[] difficulties = new String[Difficulty.values().length];
        for (int i = 0; i < difficulties.length; ++i) {
            difficulties[i] = Integer.toString(i+1);
        }

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                difficulties);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.getDifficultySpinner().setAdapter(stringArrayAdapter);
        holder.getDifficultySpinner().setSelection(0);
    }

    @Override
    public int getItemCount() {
        return ParadigmSet.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView chartTitleBtn;
        private final Spinner difficultySpinner;

        public ViewHolder(View v) {
            super(v);
            this.chartTitleBtn = (TextView) v.findViewById(R.id.chartTitleBtn);
            this.difficultySpinner = (Spinner) v.findViewById(R.id.difficultySpinner);

            chartTitleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int difficultyIndex = Integer.parseInt((String) ViewHolder.this.difficultySpinner.getSelectedItem()) - 1;
                    TrialRowAdapter.this.callback.onClick(view,
                            ParadigmSet.getAt(getAdapterPosition()),
                            Difficulty.values()[difficultyIndex]);
                }
            });
        }

        public TextView getChartTitleBtn() {
            return chartTitleBtn;
        }

        public Spinner getDifficultySpinner() {
            return difficultySpinner;
        }
    }

    public interface OnParadigmRowClick {

        void onClick(View v, ParadigmType paradigmType, Difficulty difficulty);
    }
}
