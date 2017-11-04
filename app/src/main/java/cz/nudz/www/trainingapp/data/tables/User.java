package cz.nudz.www.trainingapp.data.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by artem on 20-Sep-17.
 */

@DatabaseTable
public class User {

    @DatabaseField(id = true)
    private String username;

    @DatabaseField(canBeNull = false)
    private Date registrationDate;

    @DatabaseField(canBeNull = false)
    private Date lastLoginDate;

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public User() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
