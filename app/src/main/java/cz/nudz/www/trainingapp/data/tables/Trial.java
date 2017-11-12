package cz.nudz.www.trainingapp.data.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.nudz.www.trainingapp.enums.Side;

/**
 * Created by artem on 21-Sep-17.
 */

@DatabaseTable
public class Trial {

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

    @DatabaseField(canBeNull = false)
    private Side cuedSide;

    /**
     * JSON serialized string with information about trial's stimuli.
     */
    @DatabaseField(canBeNull = false)
    private String stimuliJSON;

    /**
     * JSON serialized string with information about trial's changed stimuli if one exits.
     */
    @DatabaseField()
    private String changedStimulusJSON;

    /**
     * Proxy field for serialized database field.
     */
    private JSONArray stimuli;
    private JSONObject changedStimulus;

    public Trial() {}

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

    public Side getCuedSide() {
        return cuedSide;
    }

    public void setCuedSide(Side cuedSide) {
        this.cuedSide = cuedSide;
    }

    public JSONArray getStimuli() {
        return stimuli;
    }

    public void setStimuli(JSONArray stimuli) {
        this.stimuli = stimuli;
        this.stimuliJSON = stimuli.toString();
    }

    public JSONObject getChangedStimulus() {
        return changedStimulus;
    }

    public void setChangedStimulus(JSONObject changedStimulus) {
        this.changedStimulus = changedStimulus;
        this.changedStimulusJSON = changedStimulus.toString();
    }
}
