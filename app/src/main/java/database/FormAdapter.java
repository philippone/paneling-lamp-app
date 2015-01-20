package database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.philippschardt.panelinglamp.R;

/**
 * Created by philipp on 19.01.15.
 */
public class FormAdapter extends CursorAdapter {


    private final LayoutInflater cursorInflater;
    private final int layout;

    public FormAdapter(Context context, int layout, Cursor c) {
        super(context, c, 0);

        this.layout = layout;
        cursorInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return  cursorInflater.inflate(layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView thumbnail = (ImageView) view.findViewById(R.id.card_from_thumbnail);
        TextView name = (TextView) view.findViewById(R.id.card_form_name);


        cursor.moveToFirst();
        String nameS = cursor.getString(cursor.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_NAME_TITLE));
        name.setText(nameS);


    }
}
