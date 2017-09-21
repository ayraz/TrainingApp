package cz.nudz.www.trainingapp.database.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by artem on 20-Sep-17.
 */

@DatabaseTable
public class User {

    @DatabaseField(id = true)
    private String username;

    public User() {};

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
