package util;

/**
 * Created by philipp on 16.01.15.
 */
public class Motor {

    private int motorNr;
    private double position;
    private long minPosition;
    private long maxPosition;

    private double oneRotation = 1600;



    public Motor(int motorNr) {
        this.motorNr    = motorNr;
    }



    public double getPosition() {
        return position;

    }

    public void setPosition(long position) {
        this.position = position;
    }

    public void setPosition(float position) {
        this.position = position * oneRotation;
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
