package Util;

/**
 * Created by philipp on 14.01.15.
 */
public class MsgCreator {



    /**
     * move stepper to absolute positon (1rotation * position)
     * */
    public static String move(int stepper, float position) {
        return "sa;" + stepper + ";" + position + ",\n";
    }


    /**
     * move stepper relative position (x * rotations)
     * */
    public static String moveTo(int stepper, float rotations) {
        return "sr;" + stepper + ";" + rotations + ",\n";
    }

}
