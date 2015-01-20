package database;

import android.content.Context;
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
import android.widget.TextView;

import com.makeramen.dragsortadapter.DragSortAdapter;

import net.philippschardt.panelinglamp.R;

import java.util.List;

/**
 * Created by philipp on 20.01.15.
 */
public class ExampleAdapter extends DragSortAdapter<ExampleAdapter.MainViewHolder> {


    public static final String TAG = ExampleAdapter.class.getSimpleName();

    private final List<Integer> data;
    private final Context mContext;

    public ExampleAdapter(Context ctx, RecyclerView recyclerView, List<Integer> data) {
        super(recyclerView);
        this.mContext = ctx;
        this.data = data;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.form_card, parent, false);
        MainViewHolder holder = new MainViewHolder(view);
        view.setOnClickListener(holder);
        view.setOnLongClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        int itemId = data.get(position);

        // NOTE: check for getDraggingId() match to set an "invisible space" while dragging
        holder.container.setVisibility(getDraggingId() == itemId ? View.INVISIBLE : View.VISIBLE);
        holder.container.postInvalidate();
    }

    @Override
    public long getItemId(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getPositionForId(long id) {
        return data.indexOf((int) id);
    }

    @Override
    public void move(int fromPosition, int toPosition) {
        data.add(toPosition, data.remove(fromPosition));
    }

    class MainViewHolder extends DragSortAdapter.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

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
                        notifyDataSetChanged();
                        break;
                    case R.id.fav_moveinform:
                        //ViewHolder vh = (ViewHolder) mRecyclerView.findViewHolderForItemId(getPosition());
                        //Log.d(TAG, "get " + vh.getItemId());
                        break;
                    case R.id.fav_editform:
                        break;

                }

                return false;
            }
        };


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

}
