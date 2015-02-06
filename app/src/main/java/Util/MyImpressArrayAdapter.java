package util;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.philippschardt.panelinglamp.R;

/**
 * Created by philipp on 06.02.15.
 */
public class MyImpressArrayAdapter extends ArrayAdapter<ImpressItem> {


    private final Context mContext;
    private final int mLayout;
    private final ImpressItem[] mItems;

    public MyImpressArrayAdapter(Context context, int resource, ImpressItem[] objects) {
        super(context, resource, objects);

        mContext = context;
        mLayout = resource;
        mItems = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //super.getView(position, convertView, parent);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(mLayout,
                parent, false);


        ImpressItem item = mItems[position];

        LinearLayout layout = (LinearLayout) rowView.findViewById(R.id.impress_layout);
        ImageView icon = (ImageView) rowView.findViewById(R.id.impress_icon);
        TextView author = (TextView) rowView.findViewById(R.id.impress_author);
        TextView copyright = (TextView) rowView.findViewById(R.id.impress_copyright);

        icon.setImageDrawable(mContext.getResources().getDrawable(item.getIcon()));
        icon.setColorFilter(R.color.primary, PorterDuff.Mode.SRC_ATOP);

        author.setText(item.getAuthor());
        copyright.setText(item.getCopyright());

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://thenounproject.com/"));
                mContext.startActivity(browserIntent);
            }
        });


        return rowView;

    }
}
