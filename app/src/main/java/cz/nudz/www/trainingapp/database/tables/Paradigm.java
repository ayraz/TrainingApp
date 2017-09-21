package cz.nudz.www.trainingapp.database.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

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
    private cz.nudz.www.trainingapp.training.Paradigm paradigmType;

    @DatabaseField(canBeNull = false)
    private Date startDate;

    @DatabaseField(canBeNull = false)
    private Date endDate;

    @DatabaseField
    private Date pauseStartDate;

    @DatabaseField
    private Date pauseEndDate;

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

    public cz.nudz.www.trainingapp.training.Paradigm getParadigmType() {
        return paradigmType;
    }

    public void setParadigmType(cz.nudz.www.trainingapp.training.Paradigm paradigmType) {
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

    public Date getPauseStartDate() {
        return pauseStartDate;
    }

    public void setPauseStartDate(Date pauseStartDate) {
        this.pauseStartDate = pauseStartDate;
    }

    public Date getPauseEndDate() {
        return pauseEndDate;
    }

    public void setPauseEndDate(Date pauseEndDate) {
        this.pauseEndDate = pauseEndDate;
    }
}
