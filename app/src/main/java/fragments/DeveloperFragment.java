package fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.philippschardt.panelinglamp.R;

import java.util.ArrayList;
import java.util.Arrays;

import Util.Motor;
import Util.MsgCreator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeveloperFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeveloperFragment extends Fragment implements OnReceiverListener{

    private final String TAG = getClass().getName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DEV_ARG_PARAM1 = "dev_section_number";

    private int mSectionNr;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNr Parameter 1.
     * @return A new instance of fragment Forms.
     */
    // TODO: Rename and change types and number of parameters
    public static DeveloperFragment newInstance(int sectionNr) {
        DeveloperFragment fragment = new DeveloperFragment();
        Bundle args = new Bundle();
        args.putInt(DEV_ARG_PARAM1, sectionNr);
        fragment.setArguments(args);
        return fragment;
    }

    public DeveloperFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNr = getArguments().getInt(DEV_ARG_PARAM1);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_developer, container, false);

        // init GUI
        initGUI(v);
        
        return v;
    }

   

    @Override
    public void onResume() {
        super.onResume();

       
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        // set section title
        mListener.onSectionAttached(getArguments().getInt(DEV_ARG_PARAM1));

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    /**
     *
     *
     * * GUI methods
     *
     *
     * */

    // View stuff
    private EditText rotationEdit;
    private EditText absPosEdit;


    // motor stuff
    private Motor[] motor;
    private ArrayList<TextView> motorPosView;
    private ArrayList<ProgressBar> motorProgView;
    short currentMotor = 0;
    float currRotation = 1;
    private int dimmerProgress = 0;

    private void initGUI(View v) {

        motorPosView = new ArrayList<TextView>(Arrays.asList(
                (TextView) v.findViewById(R.id.textView_motor1_position),
                (TextView) v.findViewById(R.id.textView_motor2_position),
                (TextView) v.findViewById(R.id.textView_motor3_position),
                (TextView) v.findViewById(R.id.textView_motor4_position),
                (TextView) v.findViewById(R.id.textView_motor5_position)
        ));

        motorProgView = new ArrayList<ProgressBar>(Arrays.asList(
                (ProgressBar) v.findViewById(R.id.progressBar_motor1),
                (ProgressBar) v.findViewById(R.id.progressBar_motor2),
                (ProgressBar) v.findViewById(R.id.progressBar_motor3),
                (ProgressBar) v.findViewById(R.id.progressBar_motor4),
                (ProgressBar) v.findViewById(R.id.progressBar_motor5)
        ));


        // VIEWs
        // roation/position view
        rotationEdit = (EditText) v.findViewById(R.id.editText_rel_rot);
        absPosEdit = (EditText) v.findViewById(R.id.editText_rot_abs);

        // dimmer
        SeekBar dimmer = (SeekBar) v.findViewById(R.id.dimmer);
        dimmer.setOnSeekBarChangeListener(seekbarDimmerListener);

        // radio motor button
        RadioButton rm1 = (RadioButton) v.findViewById(R.id.radio_motor1);
        rm1.setOnClickListener(chooseMotorListener);
        RadioButton rm2 = (RadioButton) v.findViewById(R.id.radio_motor2);
        rm2.setOnClickListener(chooseMotorListener);
        RadioButton rm3 = (RadioButton) v.findViewById(R.id.radio_motor3);
        rm3.setOnClickListener(chooseMotorListener);
        RadioButton rm4 = (RadioButton) v.findViewById(R.id.radio_motor4);
        rm4.setOnClickListener(chooseMotorListener);
        RadioButton rm5 = (RadioButton) v.findViewById(R.id.radio_motor5);
        rm5.setOnClickListener(chooseMotorListener);

        // power button
        ToggleButton powerButton = (ToggleButton) v.findViewById(R.id.powerButton);
        powerButton.setOnClickListener(togglePowerListener);

        // reset button
        Button resetButton = (Button) v.findViewById(R.id.button_reset);
        resetButton.setOnClickListener(resetListener);

        // set zero button
        Button setZeroButton = (Button) v.findViewById(R.id.button_set_zero);
        setZeroButton.setOnClickListener(setZeroListener);

        // up/down button
        Button upButton = (Button) v.findViewById(R.id.button_up);
        Button downButton = (Button) v.findViewById(R.id.button_down);

        upButton.setOnClickListener(upListener);
        downButton.setOnClickListener(downListener);

        // set button
        Button setButton = (Button) v.findViewById(R.id.button_dev_set_pos);
        setButton.setOnClickListener(setPosListener);


    }

    View.OnClickListener setZeroListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.adjustAllMotorToZero();
            // TODO dont know if lamp send 0  position as reply???
        }
    };


    View.OnClickListener upListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String value = rotationEdit.getText().toString();
            if (value != "") {
                currRotation = Float.parseFloat(value);
            } else {
                // do nothing
                return;
            }

            boolean b = mListener.liftMotorUp(currentMotor, currRotation);
            if (b) {
                motorPosView.get(currentMotor).setVisibility(View.GONE);
                motorProgView.get(currentMotor).setVisibility(View.VISIBLE);
            }

        }
    };

    View.OnClickListener downListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String value = rotationEdit.getText().toString();
            if (value != "") {
                currRotation = Float.parseFloat(value);
            } else {
                // do nothing
                return;
            }

            boolean b = mListener.liftMotorDown(currentMotor, currRotation);
            if (b) {
                motorPosView.get(currentMotor).setVisibility(View.GONE);
                motorProgView.get(currentMotor).setVisibility(View.VISIBLE);
            }
        }
    };

    View.OnClickListener setPosListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            float position;
            String value = absPosEdit.getText().toString();
            if (value != "") {
                position = Float.parseFloat(value);
            } else {
                // do nothing
                return;
            }
            mListener.moveMotorToPos(currentMotor, position);

            // GUI action
            motorPosView.get(currentMotor).setVisibility(View.GONE);
            motorProgView.get(currentMotor).setVisibility(View.VISIBLE);

        }
    };


    View.OnClickListener resetListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mListener.resetAllMotors();
            for(int i = 0; i < mListener.getMotorCount(); i++) {
                motorPosView.get(i).setVisibility(View.GONE);
                motorProgView.get(i).setVisibility(View.VISIBLE);
            }
        }
    };

    View.OnClickListener togglePowerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Is the toggle on?
            boolean on = ((ToggleButton) view).isChecked();

            if (on) {
                // lamp is on
                // TODO send command to arduino
            } else {
                // lamp is off
                // TODO send command to arduino
            }
        }
    };

    SeekBar.OnSeekBarChangeListener seekbarDimmerListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // set new dimmer value
            dimmerProgress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { /* nothing*/}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // if more than 1 LED dimmer use seekBar.getID() to get LED ID;

            // send dimmer value to lamp
            mListener.sendMsg(MsgCreator.setLED(0, dimmerProgress));
        }
    };

    View.OnClickListener chooseMotorListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean checked = ((RadioButton) view).isChecked();

            // Check which radio button was clicked
            switch(view.getId()) {
                case R.id.radio_motor1:
                    if (checked)
                        currentMotor = 0;
                    break;
                case R.id.radio_motor2:
                    if (checked)
                        currentMotor = 1;
                    break;
                case R.id.radio_motor3:
                    if (checked)
                        currentMotor = 2;
                    break;
                case R.id.radio_motor4:
                    if (checked)
                        currentMotor = 3;
                    break;
                case R.id.radio_motor5:
                    if (checked)
                        currentMotor = 4;
                    break;
            }
        }
    };



    public void togglePower (View view) {

    }

    public void controlUPDOWN(View view) {

        String value = rotationEdit.getText().toString();
        if (value != "") {
            currRotation = Float.parseFloat(value);
        } else {
            // do nothing
            return;
        }

        switch(view.getId()) {
            case R.id.button_down:
                //send position to arduino
                boolean b = mListener.sendMsg(MsgCreator.move(currentMotor, -currRotation));
                if (b) {
                    motorPosView.get(currentMotor).setVisibility(View.GONE);
                    motorProgView.get(currentMotor).setVisibility(View.VISIBLE);
                }
                break;
            case R.id.button_up:
                //send position to arduino
                boolean b1 = mListener.sendMsg(MsgCreator.move(currentMotor, currRotation));
                if (b1) {
                    motorPosView.get(currentMotor).setVisibility(View.GONE);
                    motorProgView.get(currentMotor).setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void resetMotors(View v) {
        // TODO test if motors are running
        for(int i = 0; i < motor.length; i++) {
            mListener.sendMsg(MsgCreator.forceReset(i));
            motorPosView.get(i).setVisibility(View.GONE);
            motorProgView.get(i).setVisibility(View.VISIBLE);
        }
    }

    public void setZero(View v) {
        for(int i = 0; i < motor.length; i++) {
            mListener.sendMsg(MsgCreator.overridePos(i, 0));
        }
    }

    /**
     * set Button:
     * sets the given position to selected motor
     * */
    public void setPosition(View v) {
        Log.d(TAG, "set position ");
        float position = 0;
        String value = absPosEdit.getText().toString();
        if (value != "") {
            position = Float.parseFloat(value);
        } else {
            // do nothing
            return;
        }
        mListener.sendMsg(MsgCreator.moveTo(currentMotor, position));
        motorPosView.get(currentMotor).setVisibility(View.GONE);
        motorProgView.get(currentMotor).setVisibility(View.VISIBLE);

    }


    /*
    * update the gui
    *
    * set the received information in the related views
    * */
    public void updateMotorPosinGUI(int motorIndex, float rotations) {
        motorPosView.get(motorIndex).setText(rotations + " rotations");

        motorPosView.get(motorIndex).setVisibility(View.VISIBLE);
        motorProgView.get(motorIndex).setVisibility(View.INVISIBLE);
    }


}

