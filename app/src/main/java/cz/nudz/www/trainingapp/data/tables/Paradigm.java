package cz.nudz.www.trainingapp.data.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import cz.nudz.www.trainingapp.training.ParadigmType;

/**
 * Created by artem on 21-Sep-17.
 */

@DatabaseTable
public class Paradigm {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private TrainingSession trainingSession;

    @DatabaseField(canBeNull = false)
    private ParadigmType paradigmType;

    @DatabaseField(canBeNull = false)
    private Date startDate;

    @DatabaseField()
    private Date endDate;

    @DatabaseField
    private long pauseDurationMillis;

    public Paradigm() {};

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TrainingSession getTrainingSession() {
        return trainingSession;
    }

    public void setTrainingSession(TrainingSession trainingSession) {
        this.trainingSession = trainingSession;
    }

    public ParadigmType getParadigmType() {
        return paradigmType;
    }

    public void setParadigmType(ParadigmType paradigmType) {
        this.paradigmType = paradigmType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public long getPauseDurationMillis() {
        return pauseDurationMillis;
    }

    public void setPauseDurationMillis(long pauseDurationMillis) {
        this.pauseDurationMillis = pauseDurationMillis;
    }

}
