package database;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 20.01.15.
 */
public class CardHolder {


    private long id;
    private boolean active;
    private boolean processing;
    private String name;
    private String thumbnail;
    private int posInView;


    public CardHolder(long id, String cardName, String thumb, int posinCategory, boolean active) {
        this.id = id;
        this.name = cardName;
        this.thumbnail = thumb;
        this.posInView = posinCategory;
        this.active = active;
        this.processing = false;
    }

    public long getId() {
        return id;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
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
            int active = c.getInt(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_ACTIVE));


            CardHolder card = new CardHolder(id, cardName, thumb, pos, active == 1 ? true : false);
            list.add(card);


            c.moveToNext();
        }

        return list;

    }
}
