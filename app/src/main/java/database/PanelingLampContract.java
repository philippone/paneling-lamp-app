package database;

import android.provider.BaseColumns;

/**
 * Created by philipp on 19.01.15.
 */
public class PanelingLampContract {


    public PanelingLampContract() {
    }

    public static abstract class FormEntry implements BaseColumns {
        public static final String TABLE_NAME = "forms";
        public static final String COLUMN_NAME_ENTRY_ID = "formid";
        public static final String COLUMN_NAME_TITLE = "title";
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
    }
}
