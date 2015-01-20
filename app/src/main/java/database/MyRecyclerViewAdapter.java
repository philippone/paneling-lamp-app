package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makeramen.dragsortadapter.DragSortAdapter;

import net.philippschardt.panelinglamp.R;

import java.util.List;

/**
 * Created by philipp on 20.01.15.
 */
public class MyRecyclerViewAdapter extends DragSortAdapter<MyRecyclerViewAdapter.MainViewHolder> {

    private final String TAG = this.getClass().getName();
    private final Context mContext;
    private final RecyclerView mRecyclerView;
    private  List<CardHolder> mCards;
    private final SQLiteDatabase mDB;
    private final String mCategory;
    private final String mPos;
    private Cursor mCursor;


    class MainViewHolder extends DragSortAdapter.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        private final ProgressBar progressBar;
        private final ImageView activeIndic;
        public ViewGroup container;
        public TextView name;
        public ImageView thumbnail;
        public ImageView options;

        public MainViewHolder(View v) {
            super(v);

            container = (ViewGroup) v.findViewById(R.id.card_form_card_view);
            name = (TextView) v.findViewById(R.id.card_form_name);
            thumbnail = (ImageView) v.findViewById(R.id.card_from_thumbnail);
            options = (ImageView) v.findViewById(R.id.card_form_options);
            progressBar = (ProgressBar) v.findViewById(R.id.card_form_progressBar);
            activeIndic = (ImageView) v.findViewById(R.id.card_form_active_indicator);

            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "OnClick Popup");
                    PopupMenu popup = new PopupMenu(mContext, v);

                    popup.setOnMenuItemClickListener(optionItemListener);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_card_options_fav, popup.getMenu());
                    popup.show();
                }
            });
            container.setOnClickListener(this);
            container.setOnLongClickListener(this);
        }


        PopupMenu.OnMenuItemClickListener optionItemListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO
                switch (item.getItemId()) {
                    case R.id.fav_removeform_fav:
                        mCards.remove(getPosition());
                        notifyDataSetChanged();
                        break;
                    case R.id.fav_moveinform:
                        ViewHolder vh = (ViewHolder) mRecyclerView.findViewHolderForItemId(getPosition());
                        Log.d(TAG, "get " +  vh.getItemId());
                        break;
                    case R.id.fav_editform:
                        changeName(getPosition());
                        break;

                }

                return false;
            }
        };

        private void changeName(int position) {


            long id = mCards.get(position).getId();

            // New value for one column
            ContentValues values = new ContentValues();
            values.put(PanelingLampContract.FormEntry.COLUMN_NAME_TITLE, "noob");

            // Which row to update, based on the ID
            String selection = PanelingLampContract.FormEntry._ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(id) };

            int count = mDB.update(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

            Log.d(TAG, "count " + count);

            mCards.get(position).setName("noob_text");

            notifyDataSetChanged();

        }


        @Override
        public void onClick(@NonNull View v) {


            Log.d(TAG, "OnClick");


            // TODO




        }

        @Override
        public boolean onLongClick(@NonNull View v) {
            startDrag();
            return true;
        }


    }


    public MyRecyclerViewAdapter(Context ctx, RecyclerView recyclerView, SQLiteDatabase db, String column_Category, String column_Position) {
        super(recyclerView);

        mRecyclerView = recyclerView;
        mContext = ctx;
        mDB = db;
        mCategory = column_Category;
        mPos = column_Position;
        getCards();

    }

    protected void getCards() {

        String query = "SELECT * FROM " + PanelingLampContract.FormEntry.TABLE_NAME;

        // rawQuery must not include a trailing ';'
        Cursor cursor = mDB.rawQuery(query, null);


        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                PanelingLampContract.FormEntry._ID,
                PanelingLampContract.FormEntry.COLUMN_NAME_TITLE,
                mCategory,
                mPos,
                PanelingLampContract.FormEntry.COLUMN_ACTIVE,
                PanelingLampContract.FormEntry.COLUMN_PATH_THUMBNAIL
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = mPos + " ASC";

        // Which row to update, based on the ID
        String selection = mCategory + " LIKE ?";
        // is true
        String[] selectionArgs = { String.valueOf(1) };




        mCursor = mDB.query(
                PanelingLampContract.FormEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        mCursor.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //mCards = CardHolder.createListfrom(mCursor, mCategory, mPos);
                Log.d("observer", "datasetobserver onChange");
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
            }
        });



        mCards = CardHolder.createListfrom(mCursor, mCategory, mPos);
    }

    @Override
    public MyRecyclerViewAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.form_card, parent, false);

        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyRecyclerViewAdapter.MainViewHolder holder, int position) {

        holder.thumbnail.setImageDrawable(mContext.getResources().getDrawable(Integer.parseInt(mCards.get(position).getThumbnail())));

        Log.d(TAG, "onBinderView - Card Name: " + mCards.get(position).getName());
        holder.name.setText(mCards.get(position).getName()
                + " - " + mCards.get(position).getPosInView());

        long itemId = mCards.get(position).getId();
        // NOTE: check for getDraggingId() match to set an "invisible space" while dragging
        holder.container.setVisibility(getDraggingId() == itemId ? View.INVISIBLE : View.VISIBLE);
        holder.container.postInvalidate();

    }

    @Override
    public int getItemCount() {

        return mCards.size();
    }

    @Override
    public long getItemId(int position) {

        return mCards.get(position).getId();
    }


    @Override
    public int getPositionForId(long id) {

        for (int i = 0; i < mCards.size(); i++) {
            if (mCards.get(i).getId() == id)
                return mCards.indexOf(mCards.get(i));
        }
        return 0;
    }

    @Override
    public void move(int fromPosition, int toPosition) {

        Log.d(TAG, "move ");
        mCards.add(toPosition, mCards.remove(fromPosition));

        updateModel(fromPosition, toPosition);
        // update in database
        //updataePosInDB(fromPosition, toPosition)
    }

    private void updateModel(int fromPosition, int toPosition) {

        for (int i = fromPosition; i < toPosition; i++) {

            mCards.get(i).setPosInView(i);
        }

    }
/*
    private void dbWrite(int newValue, int oldValue) {
        ContentValues values = new ContentValues();
        values.put(PanelingLampContract.FormEntry.COLUMN_FAV_POS, newValue);

        // Which row to update, based on the ID
        String selection = PanelingLampContract.FormEntry.COLUMN_FAV_POS + " LIKE ?";
        String[] selectionArgs = { String.valueOf(oldValue) };

        int count = mDB.update(
                PanelingLampContract.FormEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
*/

}