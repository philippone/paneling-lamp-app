package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makeramen.dragsortadapter.DragSortAdapter;

import net.philippschardt.panelinglamp.R;


/**
 * Created by philipp on 20.01.15.
 */
public class MyRecyclerViewAdapter extends DragSortAdapter<MyRecyclerViewAdapter.MainViewHolder> implements Filterable, CursorFilter.CursorFilterClient, LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = this.getClass().getName();
    private final Context mContext;
    private final RecyclerView mRecyclerView;

    private final SQLiteDatabase mDB;
    private final String mCategory;
    private final String mPos;
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIDColumn;
    private ChangeObserver mChangeObserver;
    private DataSetObserver mDataSetObserver;
    private FilterQueryProvider mFilterQueryProvider;
    private CursorFilter mCursorFilter;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


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
                        //mCards.remove(getPosition());
                        // TODO remove from databasae: update fav value
                        notifyDataSetChanged();
                        mChangeObserver.onChange(false);
                        break;
                    case R.id.fav_moveinform:
                        runQueryOnBackgroundThread("UPDATE forms SET " + PanelingLampContract.FormEntry.COLUMN_NAME_TITLE + " = neu  where _id = " + getPosition());
                        notifyDataSetChanged();
                        break;
                    case R.id.fav_editform:
                        changeName(getPosition());
                        MyRecyclerViewAdapter.this.notifyDataSetChanged();
                        break;

                }

                return false;
            }
        };

        private void changeName(int position) {


            //long id = mCards.get(position).getId();

            // New value for one column
            ContentValues values = new ContentValues();
            values.put(PanelingLampContract.FormEntry.COLUMN_NAME_TITLE, "noob");

            // Which row to update, based on the ID
            String selection = mPos + " LIKE ?";
            String[] selectionArgs = {String.valueOf(position)};

            int count = mDB.update(
                    PanelingLampContract.FormEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

            Log.d(TAG, "count " + count);

            notifyDataSetChanged();
            notifyItemChanged(position);

            swapCursor(getCur());

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


        init(db, column_Category, column_Position);
    }


    void init(SQLiteDatabase db, String column_Category, String column_Position) {


        Cursor c = getCur();


        boolean cursorPresent = c != null;
        mCursor = c;
        mDataValid = cursorPresent;

        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;

        mChangeObserver = new ChangeObserver();
        mDataSetObserver = new MyDataSetObserver();

        if (cursorPresent) {
            if (mChangeObserver != null) c.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) c.registerDataSetObserver(mDataSetObserver);
        }
    }

    public Cursor getCur() {
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
        String[] selectionArgs = {String.valueOf(1)};

        Cursor c = mDB.query(
                PanelingLampContract.FormEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return c;
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

        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        onBindViewHolderCursor(holder, mCursor, position);
    }


    private void onBindViewHolderCursor(MainViewHolder holder, Cursor c, int position) {
        holder.thumbnail.setImageDrawable(mContext.getResources().getDrawable(Integer.parseInt(c.getString(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_PATH_THUMBNAIL)))));

        String formName = c.getString(c.getColumnIndex(PanelingLampContract.FormEntry.COLUMN_NAME_TITLE));

        //Log.d(TAG, "onBinderView - Card Name: " + mCards.get(position).getName());
        holder.name.setText(formName);


        //     mCards.get(position).getName()
        //   + " - " + mCards.get(position).getPosInView());

        long itemId = c.getLong(mRowIDColumn);
        // NOTE: check for getDraggingId() match to set an "invisible space" while dragging
        holder.container.setVisibility(getDraggingId() == itemId ? View.INVISIBLE : View.VISIBLE);
        holder.container.postInvalidate();
    }


    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    /**
     * @see android.widget.ListAdapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }


    @Override
    public int getPositionForId(long id) {


        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                PanelingLampContract.FormEntry._ID,
                mCategory,
                mPos,
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = mPos + " ASC";

        // Which row to update, based on the ID
        String selection = mCategory + " LIKE ? AND _ID = ?";
        // is true
        String[] selectionArgs = {String.valueOf(1), String.valueOf(id)};

        Cursor c = mDB.query(
                PanelingLampContract.FormEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        if (c.moveToFirst())
            return c.getInt(c.getColumnIndex(mPos));
        else
            return 0;

    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param cursor The new cursor to be used
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there wasa not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            if (mChangeObserver != null) oldCursor.unregisterContentObserver(mChangeObserver);
            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if (mChangeObserver != null) newCursor.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            // notifyDataSetInvalidated();
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    /**
     * <p>Converts the cursor into a CharSequence. Subclasses should override this
     * method to convert their results. The default implementation returns an
     * empty String for null values or the default String representation of
     * the value.</p>
     *
     * @param cursor the cursor to convert to a CharSequence
     * @return a CharSequence representing the value
     */
    public CharSequence convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }

    /**
     * Runs a query with the specified constraint. This query is requested
     * by the filter attached to this adapter.
     * <p/>
     * The query is provided by a
     * {@link android.widget.FilterQueryProvider}.
     * If no provider is specified, the current cursor is not filtered and returned.
     * <p/>
     * After this method returns the resulting cursor is passed to {@link #changeCursor(Cursor)}
     * and the previous cursor is closed.
     * <p/>
     * This method is always executed on a background thread, not on the
     * application's main thread (or UI thread.)
     * <p/>
     * Contract: when constraint is null or empty, the original results,
     * prior to any filtering, must be returned.
     *
     * @param constraint the constraint with which the query must be filtered
     * @return a Cursor representing the results of the new query
     * @see #getFilter()
     * @see #getFilterQueryProvider()
     * @see #setFilterQueryProvider(android.widget.FilterQueryProvider)
     */
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (mFilterQueryProvider != null) {
            return mFilterQueryProvider.runQuery(constraint);
        }

        return mCursor;
    }

    public Filter getFilter() {
        if (mCursorFilter == null) {
            mCursorFilter = new CursorFilter(this);
        }
        return mCursorFilter;
    }

    /**
     * Returns the query filter provider used for filtering. When the
     * provider is null, no filtering occurs.
     *
     * @return the current filter query provider or null if it does not exist
     * @see #setFilterQueryProvider(android.widget.FilterQueryProvider)
     * @see #runQueryOnBackgroundThread(CharSequence)
     */
    public FilterQueryProvider getFilterQueryProvider() {
        return mFilterQueryProvider;
    }

    /**
     * Sets the query filter provider used to filter the current Cursor.
     * The provider's
     * {@link android.widget.FilterQueryProvider#runQuery(CharSequence)}
     * method is invoked when filtering is requested by a client of
     * this adapter.
     *
     * @param filterQueryProvider the filter query provider or null to remove it
     * @see #getFilterQueryProvider()
     * @see #runQueryOnBackgroundThread(CharSequence)
     */
    public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {
        mFilterQueryProvider = filterQueryProvider;
    }

    /**
     * Called when the {@link ContentObserver} on the cursor receives a change notification.
     * Can be implemented by sub-class.
     *
     * @see ContentObserver#onChange(boolean)
     */
    protected void onContentChanged() {
        notifyDataSetChanged();

    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            Log.d(TAG, "deliverSelfNotification");
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "onChange Changeobserver");
            onContentChanged();
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            Log.d(TAG, "onChange MyDataSetObserver");
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            Log.d(TAG, "onInvalidated");
            mDataValid = false;
            // notifyDataSetInvalidated();
            notifyItemRangeRemoved(0, getItemCount());
        }
    }

    /**
     * <p>The CursorFilter delegates most of the work to the CursorAdapter.
     * Subclasses should override these delegate methods to run the queries
     * and convert the results into String that can be used by auto-completion
     * widgets.</p>
     */

    @Override
    public void move(int fromPosition, int toPosition) {

        Log.d(TAG, "move ");
        //mCards.add(toPosition, mCards.remove(fromPosition));

        // update in database
        //updataePosInDB(fromPosition, toPosition)
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

class CursorFilter extends Filter {

    CursorFilterClient mClient;

    interface CursorFilterClient {
        CharSequence convertToString(Cursor cursor);

        Cursor runQueryOnBackgroundThread(CharSequence constraint);

        Cursor getCursor();

        void changeCursor(Cursor cursor);
    }

    CursorFilter(CursorFilterClient client) {
        mClient = client;
    }

    @Override
    public CharSequence convertResultToString(Object resultValue) {
        return mClient.convertToString((Cursor) resultValue);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        Cursor cursor = mClient.runQueryOnBackgroundThread(constraint);

        FilterResults results = new FilterResults();
        if (cursor != null) {
            results.count = cursor.getCount();
            results.values = cursor;
        } else {
            results.count = 0;
            results.values = null;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        Cursor oldCursor = mClient.getCursor();

        if (results.values != null && results.values != oldCursor) {
            mClient.changeCursor((Cursor) results.values);
        }
    }
}