package fragments.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import net.philippschardt.panelinglamp.R;

import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;
import util.MsgCreator;


public class SettingsFragment extends Fragment implements OnReceiverListener {

    private static final String SETTINGS_ARG_PARAM1 = "settings_section_number";
    private OnFragmentInteractionListener mListener;
    private int mSectionNr;
    private LinearLayout upperBoundLayout;
    private LinearLayout lowerBoundLayout;
    private CheckBox upperBoundCheck;
    private CheckBox lowerBoundCheck;
    private EditText upperBoundValue;
    private EditText lowerBoundValue;
    private LinearLayout resetDatabaseLayout;


    public static SettingsFragment newInstance(int sectionNr) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(SETTINGS_ARG_PARAM1, sectionNr);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mSectionNr = getArguments().getInt(SETTINGS_ARG_PARAM1);
        }
        super.onCreate(savedInstanceState);

        mListener.sendMsg(MsgCreator.requestCurrentBounds());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_settings, container, false);


        upperBoundLayout = (LinearLayout) v.findViewById(R.id.settings_upper_bound_layout);
        lowerBoundLayout = (LinearLayout) v.findViewById(R.id.settings_lower_bound_layout);

        upperBoundCheck = (CheckBox) v.findViewById(R.id.settings_checkBox_upper_bound);
        lowerBoundCheck = (CheckBox) v.findViewById(R.id.settings_checkBox_lower_bound);

        upperBoundValue = (EditText) v.findViewById(R.id.settings_editText_upper_bound);
        lowerBoundValue = (EditText)  v.findViewById(R.id.settings_editText_lower_bound);


        resetDatabaseLayout = (LinearLayout) v.findViewById(R.id.settings_database_reset_layout);



        upperBoundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                upperBoundCheck.setChecked(!upperBoundCheck.isChecked());

                float value = Float.parseFloat(upperBoundValue.getText().toString());
                sendBound(true, upperBoundCheck.isChecked(),  value);
            }
        });


        lowerBoundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lowerBoundCheck.setChecked(!lowerBoundCheck.isChecked());

                float value = Float.parseFloat(lowerBoundValue.getText().toString());
                sendBound(false, lowerBoundCheck.isChecked(),  value);
            }
        });


        upperBoundCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float value = Float.parseFloat(upperBoundValue.getText().toString());
                sendBound(true, isChecked,  value);
            }
        });

        lowerBoundCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float value = Float.parseFloat(lowerBoundValue.getText().toString());
                sendBound(false, isChecked,  value);
            }
        });

        return v;
    }


    private void sendBound(boolean isUpperBound, boolean boundValue, float value) {
        mListener.sendMsg(MsgCreator.setBound(isUpperBound, boundValue, value));
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
        mListener.onSectionAttached(getArguments().getInt(SETTINGS_ARG_PARAM1));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void updateMotorPosinGUI(int motorNr, float motorPos) {

    }

    @Override
    public void notifyAdapters() {
        // nothing to do
    }

    @Override
    public void onScrollUp() {
        // nothing to do
    }

    @Override
    public void onScrollDown() {
        // nothing to do
    }


    public void updateBoundInGui(boolean upperBActive, float upperBValue, boolean lowerBActive, float lowerBValue) {
        upperBoundValue.setText(upperBValue/1600 + "");
        lowerBoundValue.setText(lowerBValue/1600 + "");

        upperBoundCheck.setChecked(upperBActive);
        lowerBoundCheck.setChecked(lowerBActive);
    }
}
