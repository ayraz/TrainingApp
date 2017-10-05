package cz.nudz.www.trainingapp.data.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by artem on 21-Sep-17.
 */

@DatabaseTable
public class TrialAnswer {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private Sequence sequence;

    @DatabaseField()
    private Boolean isCorrect;

    @DatabaseField(canBeNull = false)
    private boolean isChangingTrial;

    @DatabaseField(canBeNull = false)
    private long responseTimeMillis;

    public TrialAnswer() {};

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    public Boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public long getResponseTimeMillis() {
        return responseTimeMillis;
    }

    public void setResponseTimeMillis(long responseTimeMillis) {
        this.responseTimeMillis = responseTimeMillis;
    }

    public boolean isChangingTrial() {
        return isChangingTrial;
    }

    public void setChangingTrial(boolean changingTrial) {
        isChangingTrial = changingTrial;
    }
}
