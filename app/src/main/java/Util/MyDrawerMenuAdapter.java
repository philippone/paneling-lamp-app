package util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.philippschardt.panelinglamp.R;

/**
 * Created by philipp on 31.01.15.
 */
public class MyDrawerMenuAdapter extends ArrayAdapter<DrawerItem> {


    private Context mContext;
    private int mLayout = R.layout.drawer_item_layout;
    private DrawerItem[] mItems;

    private int selectedItem;

    public MyDrawerMenuAdapter(Context context, int resource, DrawerItem[] objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mLayout = resource;
        this.mItems = objects;
        selectedItem = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(mLayout,
                parent, false);


        ImageView icon = (ImageView) rowView.findViewById(R.id.drawer_item_icon);
        TextView title = (TextView) rowView.findViewById(R.id.drawer_item_title);


        icon.setImageResource(mItems[position].getIcon());
        title.setText(mItems[position].getTitle());

        if (position == selectedItem) {
            //LinearLayout background = (LinearLayout) rowView.findViewById(R.id.drawer_item_background);
            //background.setBackgroundColor(mContext.getResources().getColor(R.color.drawer_selected_background));
            title.setTextColor(mContext.getResources().getColor(R.color.primary));
        }

        return rowView;
    }


    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }
}
