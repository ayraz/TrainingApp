package cz.nudz.www.trainingapp.data.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by artem on 20-Sep-17.
 */

@DatabaseTable
public class TrainingSession {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private Date startDate;

    @DatabaseField()
    private Date endDate;

    @DatabaseField(canBeNull = false)
    private boolean isFinished;

    @DatabaseField(canBeNull = false)
    private boolean isTest;

    @DatabaseField(canBeNull = false, foreign = true)
    private User user;

    @DatabaseField()
    private int effortAnswer;

    @DatabaseField()
    private int difficultyAnswer;

    public TrainingSession() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getEffortAnswer() {
        return effortAnswer;
    }

    public void setEffortAnswer(int effortAnswer) {
        this.effortAnswer = effortAnswer;
    }

    public int getDifficultyAnswer() {
        return difficultyAnswer;
    }

    public void setDifficultyAnswer(int difficultyAnswer) {
        this.difficultyAnswer = difficultyAnswer;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }
}
