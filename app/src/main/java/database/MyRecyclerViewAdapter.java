package database;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.philippschardt.panelinglamp.EditFormActivity;
import net.philippschardt.panelinglamp.PanelingLamp;
import net.philippschardt.panelinglamp.R;

import java.util.List;

import Util.DragSortAdapter;
import Util.MsgCreator;
import fragments.OnFragmentInteractionListener;

/**
 * Created by philipp on 20.01.15.
 */
public class MyRecyclerViewAdapter extends DragSortAdapter<MyRecyclerViewAdapter.MainViewHolder> {

    private final String TAG = this.getClass().getName();
    private final Context mContext;
    private final RecyclerView mRecyclerView;
    private final OnFragmentInteractionListener mListener;
    private List<CardHolder> mCards;
    private final SQLiteDatabase mDB;
    private final String mCategory;
    private final String mPos;
    private Cursor mCursor;
    private int mOptionsLayout;

    // which card is active
    private int mCurrentActiveCard;


    class MainViewHolder extends DragSortAdapter.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        public ProgressBar progressBar;
        public ImageView activeIndic;
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
                    inflater.inflate(mOptionsLayout, popup.getMenu());
                    popup.show();
                }
            });
            container.setOnClickListener(this);
            container.setOnLongClickListener(this);
        }


        PopupMenu.OnMenuItemClickListener optionItemListener = new PopupMenu.OnMenuItemClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CardHolder cCard = mCards.get(getPosition());

                switch (item.getItemId()) {
                    case R.id.fav_removeform_fav:
                        PanelingLampContract.setFavStatus(mDB, cCard.getId(), false);
                        mCards.remove(getPosition());
                        notifyDataSetChanged();
                        break;
                    case R.id.fav_moveinform:
                        moveToForm(cCard);


                        break;
                    case R.id.fav_editform:

                        Intent editActivity = new Intent(mContext, EditFormActivity.class);
                        editActivity.putExtra(EditFormActivity.EXTRA_ID, cCard.getId());
                        editActivity.putExtra(EditFormActivity.EXTRA_NAME, cCard.getName());
                        editActivity.putExtra(EditFormActivity.EXTRA_THUMBNAIL, cCard.getThumbnail());
                        editActivity.putExtra(EditFormActivity.EXTRA_m, cCard.getMotorPos());
                        editActivity.putExtra(EditFormActivity.EXTRA_l, cCard.getLedValues());
                        editActivity.putExtra(EditFormActivity.EXTRA_IS_STANDARD, cCard.isStandard());


                        MainViewHolder holder = (MainViewHolder) mRecyclerView.findViewHolderForItemId(cCard.getId());

                        ImageView thumbnail = holder.thumbnail;

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                                (PanelingLamp)mContext,
                                Pair.create((View)thumbnail, "thumbnail_shared")
                                /*,
                                Pair.create((View)holder.name, "name_shared")*/
                        );



                        // start the new activity
                        mContext.startActivity(editActivity, options.toBundle());


                        //mContext.startActivity(editActivity, ActivityOptions.makeSceneTransitionAnimation(((PanelingLamp) mListener)).toBundle());
                        break;
                    case R.id.add_form_to_favs:
                        PanelingLampContract.setFavStatus(mDB, cCard.getId(), true);
                        mListener.updateAdatpers();
                        break;
                    case R.id.delete_form:
                        PanelingLampContract.deleteForm(mDB, cCard.getId());
                        mListener.updateAdatpers();
                        break;

                }

                return false;
            }
        };




        @Override
        public void onClick(@NonNull View v) {

            CardHolder card = mCards.get(getPosition());

            moveToForm(card);


        }

        @Override
        public boolean onLongClick(@NonNull View v) {
            startDrag();
            return true;
        }


        public void moveToForm(CardHolder card) {
            for (int i = 0; i < getItemCount(); i++) {
                CardHolder tmpCard = mCards.get(i);
                if (tmpCard.getStatus() > 0) {
                    tmpCard.setStatus(0);
                    notifyItemChanged(mRecyclerView.findViewHolderForItemId(tmpCard.getId()).getPosition());
                }
            }

            progressBar.setVisibility(View.VISIBLE);
            card.setStatus(2);
            mCurrentActiveCard = getPosition();
            mListener.sendMsg(MsgCreator.moveToForm(card.getId(), card.getMotorPos(), card.getLedValues()));

        }


    }


    public MyRecyclerViewAdapter(Context ctx, OnFragmentInteractionListener listener, RecyclerView recyclerView, SQLiteDatabase db, String column_Category, String column_Position, int optionsLayout) {
        super(recyclerView);

        mListener = listener;
        mRecyclerView = recyclerView;
        mContext = ctx;
        mDB = db;
        mCategory = column_Category;
        mPos = column_Position;
        mOptionsLayout = optionsLayout;
        AndUpdateCards();

    }

    public void AndUpdateCards() {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                PanelingLampContract.FormEntry._ID,
                PanelingLampContract.FormEntry.COLUMN_NAME_TITLE,
                mCategory,
                mPos,
                PanelingLampContract.FormEntry.COLUMN_ACTIVE,
                PanelingLampContract.FormEntry.COLUMN_IS_STANDARD,
                PanelingLampContract.FormEntry.COLUMN_PATH_THUMBNAIL,
                PanelingLampContract.FormEntry.COLUMN_POS_MOTOR_0,
                PanelingLampContract.FormEntry.COLUMN_POS_MOTOR_1,
                PanelingLampContract.FormEntry.COLUMN_POS_MOTOR_2,
                PanelingLampContract.FormEntry.COLUMN_POS_MOTOR_3,
                PanelingLampContract.FormEntry.COLUMN_POS_MOTOR_4,
                PanelingLampContract.FormEntry.COLUMN_LED_0,
                PanelingLampContract.FormEntry.COLUMN_LED_1,
                PanelingLampContract.FormEntry.COLUMN_LED_2,
                PanelingLampContract.FormEntry.COLUMN_LED_3
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = mPos + " ASC";

        // Which row to update, based on the ID
        String selection = mCategory + " LIKE ?";
        // is true
        String[] selectionArgs = {String.valueOf(1)};


        mCursor = mDB.query(
                PanelingLampContract.FormEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        mCards = CardHolder.createListfrom(mCursor, mCategory, mPos);
        notifyDataSetChanged();
    }


    public int getmCurrentActiveCard() {
        return mCurrentActiveCard;
    }

    public void setmCurrentActiveCard(int mCurrentActiveCard) {
        this.mCurrentActiveCard = mCurrentActiveCard;
    }

    public void updateStatus(long id, int status) {
        int pos = getPositionForId(id);
        mCards.get(pos).setStatus(status);
        notifyItemChanged(pos);
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

        CardHolder card = mCards.get(position);

        if (card.isStandard()) {
            holder.thumbnail.setImageDrawable(mContext.getResources().getDrawable(Integer.parseInt(mCards.get(position).getThumbnail())));
        } else {
            // TODO load from path
            Bitmap bmp = BitmapFactory.decodeFile(mCards.get(position).getThumbnail());
            holder.thumbnail.setImageBitmap(bmp);
        }



        Log.d(TAG, "onBinderView - Card Name: " + mCards.get(position).getName());
        holder.name.setText(mCards.get(position).getName());

        if (card.getStatus() == 1) {
            // active
            holder.activeIndic.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
        } else if (card.getStatus() == 0) {
            // nothing
            holder.activeIndic.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
        } else if (card.getStatus() == 2) {
            // nothing
            holder.activeIndic.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);
        }


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
        return -1;
    }

    @Override
    public void move(int fromPosition, int toPosition) {

        Log.d(TAG, "move ");
        mCards.add(toPosition, mCards.remove(fromPosition));

        //updateModel(fromPosition, toPosition);
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
