package cz.nudz.www.trainingapp.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.nudz.www.trainingapp.ParadigmSet;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.enums.ParadigmType;

/**
 * Created by P8P67 on 11/1/2017.
 */

public class ChartRowAdapter extends RecyclerView.Adapter<ChartRowAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chart_selection_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = "";
        Context context = holder.getChartTitle().getContext();
        switch (ParadigmSet.getAt(position)) {
            case COLOR:
                title = context.getString(R.string.colorParadigmPerformanceSummary);
                break;
            case POSITION:
                title = context.getString(R.string.positionParadigmPerformanceSummary);
                break;
            case SHAPE:
                title = context.getString(R.string.shapeParadigmPerformanceSummary);
                break;
        }
        holder.getChartTitle().setText(title);
    }

    @Override
    public int getItemCount() {
        return ParadigmSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView chartTitle;

        public ViewHolder(View v) {
            super(v);
            this.chartTitle = (TextView) v.findViewById(R.id.chartTitle);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("ChartRow", ParadigmSet.getAt(getAdapterPosition()).toString());
                }
            });
        }

        public TextView getChartTitle() {
            return chartTitle;
        }
    }
}
