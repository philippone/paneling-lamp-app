package Util;

import net.philippschardt.panelinlamp.MainActivity;

/**
 * Created by philipp on 16.01.15.
 */
public class Motor {

    public interface MotorInterface {

        public void updateMotorPosGUI(int motorNr, float position);

    }

    private int motorNr;
    private double position;
    private long minPosition;
    private long maxPosition;
    private MainActivity observer;

    private double oneRotation = 1600;



    public Motor(int motorNr, MainActivity observer) {
        this.motorNr    = motorNr;
        this.observer   = observer;
    }



    public double getPosition() {
        return position;

    }

    public void setPosition(long position) {
        this.position = position;
    }

    public void setPosition(float position) {
        this.position = position * oneRotation;
        this.observer.updateMotorPosGUI(motorNr, position);
    }

    public long getMinPosition() {
        return minPosition;
    }

    public void setMinPosition(long minPosition) {
        this.minPosition = minPosition;
    }

    public long getMaxPosition() {
        return maxPosition;
    }

    public void setMaxPosition(long maxPosition) {
        this.maxPosition = maxPosition;
    }


}
