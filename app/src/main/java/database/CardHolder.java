package database;

import android.database.Cursor;

import net.philippschardt.panelinglamp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 20.01.15.
 */
public class CardHolder {


    private long id;
    private int status;
    private String name;
    private String thumbnail;
    private int posInView;
    private float[] motorPos;
    private int[] ledValues;
    private boolean standard;


    public CardHolder(long id, String cardName, String thumb, int posinCategory, int status, boolean isStandard, float[] motorPos, int[] ledValues) {
        this.id = id;
        this.name = cardName;
        this.thumbnail = thumb;
        this.posInView = posinCategory;
        this.motorPos = motorPos;
        this.ledValues = ledValues;
        this.status = status;
        this.standard = isStandard;

    }

    public boolean isStandard() {
        return standard;
    }

    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    public long getId() {
        return id;
    }


    public float[] getMotorPos() {
        return motorPos;
    }

    public void setMotorPos(float[] motorPos) {
        this.motorPos = motorPos;
    }

    public int[] getLedValues() {
        return ledValues;
    }

    public void setLedValues(int[] ledValues) {
        this.ledValues = ledValues;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPosInView() {
        return posInView;
    }

    public void setPosInView(int posInView) {
        this.posInView = posInView;
    }


    public static List<CardHolder> createListfrom(Cursor c, String mCategory, String mPos) {


        ArrayList<CardHolder> list = new ArrayList<CardHolder>();

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {

            long id = c.getLong(c.getColumnIndex(PanelingLampContract.FormEntry._ID));
            String cardName = c.getString(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_NAME_TITLE));
            String thumb = c.getString(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_PATH_THUMBNAIL));
            int pos = c.getInt(c.getColumnIndex(mPos));
            int status = c.getInt(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_ACTIVE));

            float m0 = c.getFloat(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_POS_MOTOR_0));
            float m1 = c.getFloat(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_POS_MOTOR_1));
            float m2 = c.getFloat(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_POS_MOTOR_2));
            float m3 = c.getFloat(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_POS_MOTOR_3));
            float m4 = c.getFloat(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_POS_MOTOR_4));

            int l0 = c.getInt(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_LED_0));
            int l1 = c.getInt(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_LED_1));
            int l2 = c.getInt(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_LED_2));
            int l3 = c.getInt(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_LED_3));

            boolean isStandard = c.getInt(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_IS_STANDARD)) == 1 ? true : false;

            CardHolder card = new CardHolder(id, cardName, thumb, pos, status, isStandard, new float[] {m0,m1,m2,m3,m4}, new int[] {l0,l1,l2,l3});
            list.add(card);


            c.moveToNext();
        }

        return list;

    }

}
