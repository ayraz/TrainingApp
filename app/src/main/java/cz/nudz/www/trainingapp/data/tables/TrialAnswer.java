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

    /**
     * Null when left unanswered by user.
     */
    @DatabaseField()
    private Long responseTimeMillis;

    public TrialAnswer() {}

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

    public Long getResponseTimeMillis() {
        return responseTimeMillis;
    }

    public void setResponseTimeMillis(Long responseTimeMillis) {
        this.responseTimeMillis = responseTimeMillis;
    }

    public boolean isChangingTrial() {
        return isChangingTrial;
    }

    public void setChangingTrial(boolean changingTrial) {
        isChangingTrial = changingTrial;
    }
}
