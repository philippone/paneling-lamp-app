package Util;

import android.content.Context;
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

    public MotorItemView(Context context, OnFragmentInteractionListener listener, int motorIndex, float position ) {
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
        indexView.setText(i +"");
        ePos = (EditText) findViewById(R.id.motor_item_ePos);
        ePos.setText(mPos + "");
    }


    public int getmIndex() {
        return mIndex;
    }

    public void setmIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public float getmPos() {
        return mPos;
    }

    public void setmPos(float mPos) {
        this.mPos = mPos;
        ePos.setText(mPos + "");
    }

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    public void setmListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }
}
