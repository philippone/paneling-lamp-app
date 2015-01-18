package fragments.developer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import net.philippschardt.panelinglamp.R;

import java.util.ArrayList;
import java.util.Arrays;

import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;



public class DeveloperFragmentMotors extends Fragment implements OnReceiverListener {
    private final String TAG = getClass().getName();

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeveloperFragmentMotors.
     */
    public static DeveloperFragmentMotors newInstance() {
        DeveloperFragmentMotors fragment = new DeveloperFragmentMotors();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    public DeveloperFragmentMotors() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
          //  mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_developer_fragment_motors, container, false);

        // init GUI
        initGUI(v);

        return v;

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
    private ArrayList<TextView> motorPosView;
    private ArrayList<ProgressBar> motorProgView;
    short currentMotor = 0;
    float currRotation = 1;

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
