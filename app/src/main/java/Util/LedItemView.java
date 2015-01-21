package Util;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.philippschardt.panelinglamp.R;

import fragments.OnFragmentInteractionListener;

/**
 * Created by philipp on 21.01.15.
 */
public class LedItemView extends RelativeLayout{


    private OnFragmentInteractionListener mListener;
    private int index;
    private String indicator;
    private  int value;

    private TextView mIndicatorView;
    private SeekBar mSeekBar;

    public LedItemView(Context context) {
        super(context);

        init();
    }

    public LedItemView(Context context, OnFragmentInteractionListener l, int index, String name, int value) {
        super(context);


        this.mListener = l;
        this.index = index;
        this.indicator = name;
        this.value = value;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.led_item, this);


        mSeekBar = (SeekBar) findViewById(R.id.led_item_seekBalr);
        mSeekBar.setProgress(value);
        mIndicatorView = (TextView) findViewById(R.id.led_item_v_index);
        mIndicatorView.setText(indicator);
    }
}
