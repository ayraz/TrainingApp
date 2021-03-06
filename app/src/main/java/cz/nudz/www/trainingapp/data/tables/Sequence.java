package cz.nudz.www.trainingapp.data.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by artem on 21-Sep-17.
 */

@DatabaseTable
public class Sequence {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private Paradigm paradigm;

    @DatabaseField(canBeNull = false)
    private int difficulty;

    @DatabaseField(canBeNull = false)
    private Date startDate;

    @DatabaseField()
    private Date endDate;

    public Sequence() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Paradigm getParadigm() {
        return paradigm;
    }

    public void setParadigm(Paradigm paradigm) {
        this.paradigm = paradigm;
    }

    /**
     *
     * @return One-based*
     * difficulty integer.
     */
    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
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
}
