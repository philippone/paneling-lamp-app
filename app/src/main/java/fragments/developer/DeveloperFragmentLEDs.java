package fragments.developer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import net.philippschardt.panelinglamp.R;

import util.MsgCreator;
import fragments.OnFragmentInteractionListener;


public class DeveloperFragmentLEDs extends Fragment {

    private OnFragmentInteractionListener mListener;
    private int dimmerProgress;

    private static final String ARG_PARAM_DIMMER_PROG = "DeveloperFragmentLEDs_dimmerProgress";
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment DeveloperFragmentLEDs.
     */
    public static DeveloperFragmentLEDs newInstance(int dimmerProgress) {
        DeveloperFragmentLEDs fragment = new DeveloperFragmentLEDs();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_DIMMER_PROG, dimmerProgress);
        fragment.setArguments(args);
        return fragment;
    }

    public DeveloperFragmentLEDs() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dimmerProgress = getArguments().getInt(ARG_PARAM_DIMMER_PROG);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_developer_fragment_leds, container, false);

        initGUI(v);


        return v;
    }

    private void initGUI(View v) {
        // dimmer
        SeekBar dimmer = (SeekBar) v.findViewById(R.id.dimmer);
        dimmer.setOnSeekBarChangeListener(seekbarDimmerListener);
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
}
