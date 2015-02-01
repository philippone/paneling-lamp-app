package util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.widget.SeekBar;

import net.philippschardt.panelinglamp.R;

import java.util.ArrayList;

import fragments.OnFragmentInteractionListener;

/**
 * Created by philipp on 01.02.15.
 */
public class LedAllItemView extends LedItemView{


    private CardView mCardView;
    private final Context mContext;
    private  ArrayList<LedItemView> ledListener;



    private void notifyListeners(int progress) {
        for (LedItemView v : ledListener) {
            v.setValue(progress);
        }
    }

    public LedAllItemView(Context context, OnFragmentInteractionListener l, int index, String name, int value, ArrayList<LedItemView> listener) {
        super(context, l, index, name, value);
        ledListener = listener;

        mContext = context;


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LedAllItemView.this.value = progress;
                notifyListeners(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mListener.sendMsg(MsgCreator.setLED(-1, LedAllItemView.this.value));

            }
        });

        

        mCardView = (CardView) findViewById(R.id.card_form_card_view);
        mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.floatingButtonNormal) );
    }





    public void update(int value) {
        if (value > this.value) {
            this.value = value;
            mSeekBar.setProgress(value);
        }
    }
}
