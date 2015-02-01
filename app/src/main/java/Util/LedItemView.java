package util;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.philippschardt.panelinglamp.R;

import fragments.OnFragmentInteractionListener;

/**
 * Created by philipp on 21.01.15.
 */
public class LedItemView extends RelativeLayout{


    protected OnFragmentInteractionListener mListener;
    protected int index;
    protected String indicator;
    protected int value;

    private TextView mIndicatorView;
    protected SeekBar mSeekBar;



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

        mSeekBar.setOnSeekBarChangeListener(seekBarListener);
    }

    protected SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            value = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mListener.sendMsg(MsgCreator.setLED(index, value));
        }
    };

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        mSeekBar.setProgress(value);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }
}
