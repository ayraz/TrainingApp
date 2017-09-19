package cz.nudz.www.trainingapp.store;

import android.provider.BaseColumns;
import android.support.annotation.NonNull;

/**
 * Created by artem on 19-Sep-17.
 */

public class TrainingAppTables {

    private static final String PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";
    private static final String NOT_NULL = "NOT NULL";
    private static final String UNIQUE = "UNIQUE";
    private static final String DATETIME = "DATETIME";
    private static final String INTEGER = "INTEGER";

    private TrainingAppTables() { };

    public static class UserTable implements BaseColumns {

        public static final String TABLE_NAME = "User";
        public static final String COLUMN_NAME_USERNAME = "Username";

        public static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " ("
                + addColumn(_ID, INTEGER, PRIMARY_KEY)
                + addColumn(false, COLUMN_NAME_USERNAME, "NVARCHAR(255)", UNIQUE, NOT_NULL) + ")";
    }

    public static class TrainingSessionTable implements BaseColumns {

        public static final String TABLE_NAME = "TrainingSession";
        public static final String COLUMN_NAME_START_DATETIME = "StartDateTime";
        public static final String COLUMN_NAME_END_DATETIME = "EndDateTime";
        public static final String COLUMN_NAME_IS_FINISHED = "IsFinished";

        public static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " ("
                + addColumn(_ID, INTEGER, PRIMARY_KEY)
                + addColumn(COLUMN_NAME_START_DATETIME, DATETIME, NOT_NULL)
                + addColumn(COLUMN_NAME_END_DATETIME, DATETIME, NOT_NULL)
                + addColumn(false, COLUMN_NAME_IS_FINISHED, INTEGER, NOT_NULL) + ")";
    }

    public static class ParadigmTable implements BaseColumns {

        public static final String TABLE_NAME = "Paradigm";
        public static final String COLUMN_NAME_SESSION_ID = "SessionId";
        public static final String COLUMN_NAME_PARADIGM_TYPE_ID = "ParadigmTypeId";
        public static final String COLUMN_NAME_START_DATETIME = "StartDateTime";
        public static final String COLUMN_NAME_END_DATETIME = "EndDateTime";
        public static final String COLUMN_NAME_PAUSE_START_TIME = "PauseStartTime";
        public static final String COLUMN_NAME_PAUSE_END_TIME = "PauseEndTime";

        public static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " ("
                + addColumn(_ID, INTEGER, PRIMARY_KEY)
                + addColumn(COLUMN_NAME_SESSION_ID, INTEGER, NOT_NULL)
                + addColumn(COLUMN_NAME_PARADIGM_TYPE_ID, INTEGER, NOT_NULL)
                + addColumn(COLUMN_NAME_START_DATETIME, DATETIME, NOT_NULL)
                + addColumn(COLUMN_NAME_END_DATETIME, DATETIME, NOT_NULL)
                + addColumn(COLUMN_NAME_PAUSE_START_TIME, DATETIME)
                + addColumn(COLUMN_NAME_PAUSE_END_TIME, DATETIME)
                + foreignKey(COLUMN_NAME_SESSION_ID, TrainingSessionTable.TABLE_NAME) + ","
                + foreignKey(COLUMN_NAME_PARADIGM_TYPE_ID, ParadigmTypeTable.TABLE_NAME) + ")";
    }

    public static class ParadigmTypeTable implements BaseColumns {

        public static final String TABLE_NAME = "ParadigmType";
        public static final String COLUMN_NAME_TYPE = "ParadigmType";

        public static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " ("
                + addColumn(_ID, INTEGER, PRIMARY_KEY)
                + addColumn(false, COLUMN_NAME_TYPE, "VARCHAR(55)", UNIQUE, NOT_NULL) + ")";
    }

    public static class SequenceTable implements BaseColumns {

        public static final String TABLE_NAME = "Sequence";
        public static final String COLUMN_NAME_PARADIGM_ID = "ParadigmId";
        public static final String COLUMN_NAME_DIFFICULTY_ID = "DifficultyId";
        public static final String COLUMN_NAME_START_DATETIME = "StartDateTime";
        public static final String COLUMN_NAME_END_DATETIME = "EndDateTime";

        public static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " ("
                + addColumn(_ID, INTEGER, PRIMARY_KEY)
                + addColumn(COLUMN_NAME_PARADIGM_ID, INTEGER, NOT_NULL)
                + addColumn(COLUMN_NAME_DIFFICULTY_ID, INTEGER, NOT_NULL)
                + addColumn(COLUMN_NAME_START_DATETIME, DATETIME, NOT_NULL)
                + addColumn(COLUMN_NAME_END_DATETIME, DATETIME, NOT_NULL)
                + foreignKey(COLUMN_NAME_PARADIGM_ID, ParadigmTable.TABLE_NAME) + ","
                + foreignKey(COLUMN_NAME_DIFFICULTY_ID, DifficultyTable.TABLE_NAME) + ")";
    }

    public static class DifficultyTable implements BaseColumns {

        public static final String TABLE_NAME = "Difficulty";
        public static final String COLUMN_NAME_TYPE = "DifficultyCode";

        public static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " ("
                + addColumn(_ID, INTEGER, PRIMARY_KEY)
                + addColumn(false, COLUMN_NAME_TYPE, "VARCHAR(55)", UNIQUE, NOT_NULL) + ")";
    }

    public static class TrialAnswerTable implements BaseColumns {

        public static final String TABLE_NAME = "TrialAnswer";
        public static final String COLUMN_NAME_SEQUENCE_ID = "SequenceId";
        public static final String COLUMN_NAME_IS_CORRECT = "IsCorrect";
        public static final String COLUMN_NAME_RESPONSE_TIME = "ResponseTime";

        public static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " ("
                + addColumn(_ID, INTEGER, PRIMARY_KEY)
                + addColumn(COLUMN_NAME_SEQUENCE_ID, INTEGER, NOT_NULL)
                + addColumn(COLUMN_NAME_RESPONSE_TIME, DATETIME, NOT_NULL)
                + addColumn(COLUMN_NAME_IS_CORRECT, INTEGER, NOT_NULL)
                + foreignKey(COLUMN_NAME_SEQUENCE_ID, SequenceTable.TABLE_NAME) + ")";
    }

    @NonNull
    private static String foreignKey(String fkColumn, String fkTable) {
        return "FOREIGN KEY (" + fkColumn + ")" + " REFERENCES " + fkTable + " (_id)";
    }

    @NonNull
    private static String addColumn(String... parts) {
        return addColumn(true, parts);
    }

    @NonNull
    private static String addColumn(boolean appendComma, String... parts) {
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            result.append(part).append(" ");
        }
        // remove last whitespace
        result.deleteCharAt(result.length() - 1);
        if (appendComma) {
            result.append(", ");
        }
        return result.toString();
    }

}
