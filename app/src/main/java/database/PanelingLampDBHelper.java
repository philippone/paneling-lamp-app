package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.philippschardt.panelinglamp.R;

/**
 * Created by philipp on 19.01.15.
 */
public class PanelingLampDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 17;
    public static final String DATABASE_NAME = "PanelingLamp.db";



    public PanelingLampDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PanelingLampContract.FormEntry.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }




    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PanelingLampContract.FormEntry.TABLE_NAME;



    public static void setUpShapes(SQLiteDatabase db) {

        if (!PanelingLampContract.hasStandardShapes(db)) {

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("1", R.drawable.f1 + "", 8,3,0,0,0, 255, 100, 200, 255, 80, 80, 200, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("2", R.drawable.f2 + "", 0,20,0,9,5,			255, 20, 240, 255, 255, 125, 90, false, true, 0, true, 0));


            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("3", R.drawable.f3 + "", 22, 28, 0, 0, 8, 255, 255, 255, 255, 255, 255, 255, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("4", R.drawable.f4 + "", 22, 28, 0, 0, 20,		20, 20, 20, 255, 10, 255, 255, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("5", R.drawable.f5 + "", 17, 1, 1, 12, 5,		130, 130 ,20, 255, 130, 130, 255, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("6", R.drawable.f6 + "", 0, 17, 25.5f,0, 15,		255, 255, 255, 255, 255, 255, 255, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("7", R.drawable.f7 + "", 0, 35.5f, 42,0,20, 255, 255, 255, 255, 255, 255, 255, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("8", R.drawable.f8 + "", 0, 7.8f, 0,10.5f,0,		130,130,130,130,130,130,130, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("9", R.drawable.f9 + "", 0,0,0, 21, 3, 255, 255, 255, 255, 255, 255, 255, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("10", R.drawable.f10 + "", 0,0,10,21, 12,			190, 190, 190, 190, 190, 190, 190, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("11", R.drawable.f11 + "", 3,0,15,0,0,			130,130,130,130,130,130,130, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("12", R.drawable.f12 + "", 4,15.5f,18.5f,0,12,		255, 255, 255, 255, 255, 255, 255, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("13", R.drawable.f13 + "", 0,0,6.5f,15.5f,9.5f,		255, 255, 255, 255, 255, 255, 255, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("14", R.drawable.f14 + "", 0,0,10,21, 12,		130,130,130,130,130,130,130, false, true, 0, true, 0));

            db.insert(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    null,
                    PanelingLampContract.FormEntry.createContentValues("15", R.drawable.f14 + "", 0,0,10,21, 12,		255, 255, 255, 180, 20, 20, 120, false, true, 0, true, 0));

        }
    }
}
