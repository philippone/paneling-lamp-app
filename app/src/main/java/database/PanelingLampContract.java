package database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by philipp on 19.01.15.
 */
public class PanelingLampContract {


    public PanelingLampContract() {
    }

    public static abstract class FormEntry implements BaseColumns {
        public static final String TABLE_NAME = "forms";
        public static final String COLUMN_NAME_NULLABLE = "";

        public static final String COLUMN_NAME_ENTRY_ID = "formid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_PATH_THUMBNAIL = "path_thumbnail";
        // position
        public static final String COLUMN_POS_MOTOR_0 = "pos_motor0";
        public static final String COLUMN_POS_MOTOR_1 = "pos_motor1";
        public static final String COLUMN_POS_MOTOR_2 = "pos_motor2";
        public static final String COLUMN_POS_MOTOR_3 = "pos_motor3";
        public static final String COLUMN_POS_MOTOR_4 = "pos_motor4";
        // leds
        public static final String COLUMN_LED_0 = "led_0";
        public static final String COLUMN_LED_1 = "led_1";
        public static final String COLUMN_LED_2 = "led_2";
        public static final String COLUMN_LED_3 = "led_3";

        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_FAV = "in_favs";
        public static final String COLUMN_FAV_POS = "fav_position";
        public static final String COLUMN_IS_STANDARD = "is_standard";
        public static final String COLUMN_STANDARD_OWN_POS = "standard_position";

        public static ContentValues createContentValues(String name, String thumbnail, float pos0, float pos1, float pos2, float pos3, float pos4, int led0, float led1, float led2, float led3, boolean active, boolean inFavs, int favPos, boolean isStandard, int standardOwnPos) {
            ContentValues values = new ContentValues();

            //values.put(FormEntry.COLUMN_NAME_ENTRY_ID, id);
            values.put(FormEntry.COLUMN_NAME_TITLE, name);
            values.put(FormEntry.COLUMN_PATH_THUMBNAIL, thumbnail);
            values.put(FormEntry.COLUMN_POS_MOTOR_0, pos0);
            values.put(FormEntry.COLUMN_POS_MOTOR_1, pos1);
            values.put(FormEntry.COLUMN_POS_MOTOR_2, pos2);
            values.put(FormEntry.COLUMN_POS_MOTOR_3, pos3);
            values.put(FormEntry.COLUMN_POS_MOTOR_4, pos4);
            values.put(FormEntry.COLUMN_LED_0, led0);
            values.put(FormEntry.COLUMN_LED_1, led1);
            values.put(FormEntry.COLUMN_LED_2, led2);
            values.put(FormEntry.COLUMN_LED_3, led3);
            values.put(FormEntry.COLUMN_ACTIVE, active ? 1 : 0);
            values.put(FormEntry.COLUMN_FAV, inFavs ? 1: 0);
            values.put(FormEntry.COLUMN_FAV_POS, favPos);
            values.put(COLUMN_IS_STANDARD, isStandard ? 1 : 0);
            values.put(COLUMN_STANDARD_OWN_POS, standardOwnPos);

            return values;
        }

        private static final String TEXT_TYPE = " TEXT";
        private static final String FLOAT_TYPE = " REAL";
        private static final String INT_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + PanelingLampContract.FormEntry.TABLE_NAME + " (" +
                        FormEntry._ID + " INTEGER PRIMARY KEY," +
                        FormEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                        FormEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        FormEntry.COLUMN_PATH_THUMBNAIL + TEXT_TYPE + COMMA_SEP +
                        FormEntry.COLUMN_POS_MOTOR_0 + FLOAT_TYPE + COMMA_SEP +
                        FormEntry.COLUMN_POS_MOTOR_1 + FLOAT_TYPE + COMMA_SEP +
                        FormEntry.COLUMN_POS_MOTOR_2 + FLOAT_TYPE + COMMA_SEP +
                        FormEntry.COLUMN_POS_MOTOR_3 + FLOAT_TYPE + COMMA_SEP +
                        FormEntry.COLUMN_POS_MOTOR_4 + FLOAT_TYPE + COMMA_SEP +
                        FormEntry.COLUMN_LED_0 + INT_TYPE + COMMA_SEP +
                        FormEntry.COLUMN_LED_1 + INT_TYPE + COMMA_SEP +
                        FormEntry.COLUMN_LED_2 + INT_TYPE + COMMA_SEP +
                        FormEntry.COLUMN_LED_3 + INT_TYPE + COMMA_SEP +
                        FormEntry.COLUMN_ACTIVE + INT_TYPE  + COMMA_SEP +
                        FormEntry.COLUMN_FAV + INT_TYPE  + COMMA_SEP +
                        FormEntry.COLUMN_FAV_POS + INT_TYPE  + COMMA_SEP +
                        FormEntry.COLUMN_IS_STANDARD + INT_TYPE  + COMMA_SEP +
                        FormEntry.COLUMN_STANDARD_OWN_POS + INT_TYPE   +
                        " )";

    }


    public static int updateStatus(SQLiteDatabase db, long id, int status) {
        unsetStatusAll(db);

        // New value for one column
        ContentValues values = new ContentValues();
        values.put( FormEntry.COLUMN_ACTIVE, status);

        // Which row to update, based on the ID
        String selection = FormEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(
                FormEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }

    public static int unsetStatusAll(SQLiteDatabase db) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put( FormEntry.COLUMN_ACTIVE, 0);

        // Which row to update, based on the ID
        String selection = FormEntry.COLUMN_ACTIVE + " != ?";
        String[] selectionArgs = { String.valueOf(0) };

        int count = db.update(
                FormEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        return count;
    }
}
