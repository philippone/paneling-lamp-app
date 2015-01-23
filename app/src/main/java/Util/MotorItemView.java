package Util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.philippschardt.panelinglamp.R;

import fragments.OnFragmentInteractionListener;

/**
 * Created by philipp on 21.01.15.
 */
public class MotorItemView extends RelativeLayout {

    private int mIndex;
    private float mPos;
    private OnFragmentInteractionListener mListener;


    private TextView indexView;
    private EditText ePos;
    private ImageButton upButton;
    private ImageButton downButton;
    private ProgressBar progressBar;

    public MotorItemView(Context context) {
        super(context);

        init();
    }

    public MotorItemView(Context context, OnFragmentInteractionListener listener, int motorIndex, float position) {
        super(context);

        mListener = listener;
        mIndex = motorIndex;
        mPos = position;
        init();
    }

    private void init() {

        inflate(getContext(), R.layout.motor_item, this);

        indexView = (TextView) findViewById(R.id.motor_item_v_index);
        int i = mIndex + 1;
        indexView.setText(i + "");
        ePos = (EditText) findViewById(R.id.motor_item_ePos);
        ePos.setText(mPos + "");


        progressBar = (ProgressBar) findViewById(R.id.motor_item_progressBar);


        upButton = (ImageButton) findViewById(R.id.motor_item_button_up);
        downButton = (ImageButton) findViewById(R.id.motor_item_button_down);


        downButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mListener.sendMsg(MsgCreator.forceStop(mIndex));
                        break;
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        ePos.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        mListener.sendMsg(MsgCreator.moveDown(mIndex, 100));
                        break;
                }
                return true;
            }
        });

        upButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mListener.sendMsg(MsgCreator.forceStop(mIndex));
                        break;
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        ePos.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        mListener.sendMsg(MsgCreator.moveUp(mIndex, 100));
                        break;
                }
                return true;
            }
        });
    }


    public int getmIndex() {
        return mIndex;
    }

    public void setmIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public float getmPos() {
        String s = ePos.getText().toString();

        float f = mPos;
        try {
            f = Float.parseFloat(s);
        } catch (Exception e) {
            f = mPos;
            ePos.setText(mPos + "");
        }
        return f;

    }

    public void setmPos(float mPos) {
        this.mPos = mPos;
        ePos.setText(mPos + "");

        progressBar.setVisibility(View.GONE);
        ePos.setVisibility(View.VISIBLE);
    }

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    public void setmListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    public void activateProgress() {
        // TODO set proressbar active
        progressBar.setVisibility(View.VISIBLE);
        ePos.setVisibility(View.GONE);

    }

    public void setCurrAsZero() {

        ePos.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        ePos.setText("0.0");
        mPos = 0;
    }
}
