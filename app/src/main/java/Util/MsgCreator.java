package Util;

/**
 * Created by philipp on 14.01.15.
 */
public class MsgCreator {


    /*
    *  Message Types
    *   msg have to end with NEWLINE

    STEPPER MSGs
    sr;     stepper#;   +- rotations    =   move relative rotations
    sa;     stepper#;   +- rotations    =   move absolute rotations:
    sp;     stepper#;   currentPosition =   set current position (as rotations * 1600)
    fs;     stepper#;                   =   force stop stepper #
    fr;     stepper#;                   =   force reset stepper # to postion 0  (stepper# = -1 > reset all steppers)


    LED MSGs
    l;      LED#;   value;              =   set value to LED#

    OTHERS
    p;      on/off (0,1);               =   lamp on/off

    * */


    public static String initConnection() {
        return "c;\n";
    }

    /**
     * move stepper to relvative positon (1rotation * position)
     * */
    public static String move(int stepper, float position) {
        return "sr;" + stepper + ";" + position + ";\n";
    }

    /**
     * move stepper absolute position (x * rotations)
     * */
    public static String moveTo(int stepper, float rotations) {
        return "sa;" + stepper + ";" + rotations + ";\n";
    }

    public static String moveToForm(long id, float m0, float m1, float m2, float m3, float m4, int l0, int l1, int l2, int l3) {
        return "mf;" + id + ";" + m0 + ";" + m1 + ";" + m2 + ";"+ m3 + ";"+ m4 + ";" + l0 + ";" + l1 + ";" + l2 + ";" + l3 + ";\n";
    }

    public static String moveToForm(long id, float[] m, int[] l) {
        String msg = "mf;" + id + ";";
        for(float p : m) {
            msg += p + ";";
        }
        for (int i : l) {
            msg += i + ";";
        }

        return msg + "\n";
    }

    public static String setCurrPos(int stepper, float rotations) {
        return "sp;" + stepper + ";" + rotations + ";\n";
    }

    public static String forceStop(int stepper) {
        return "fs;" + stepper + ";\n";
    }

    public static String forceReset(int stepper) {
        return "fr;" + stepper + ";\n";
    }

    public static String overridePos(int stepper, float position) {return "op;" + stepper + ";" + position+ ";\n";}

    public static String setLED(int led, int value) {
        return "l;" + led + ";" + value + ";\n";
    }


    public static String turn(boolean power) {
        int v = power ? 1 : 0;
        return "p;" + v  +  ";\n";
    }


}
