package cz.nudz.www.trainingapp.database.tables;

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

    @DatabaseField(canBeNull = false)
    private boolean isCorrect;

    @DatabaseField(canBeNull = false)
    private int responseTimeMillis;

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

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public int getResponseTimeMillis() {
        return responseTimeMillis;
    }

    public void setResponseTimeMillis(int responseTimeMillis) {
        this.responseTimeMillis = responseTimeMillis;
    }
}
