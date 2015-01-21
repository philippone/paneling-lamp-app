package fragments.EditFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.philippschardt.panelinglamp.R;

import Util.LedItemView;
import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;


public class EditLEDFragment extends Fragment implements OnReceiverListener{

    private static final String ARG_PARAM_LED_0 = "EditLEDFragment_led_0";
    private static final String ARG_PARAM_LED_1 = "EditLEDFragment_led_1";
    private static final String ARG_PARAM_LED_2 = "EditLEDFragment_led_2";
    private static final String ARG_PARAM_LED_3 = "EditLEDFragment_led_3";


    private int led0;
    private int led1;
    private int led2;
    private int led3;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment EditLEDFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditLEDFragment newInstance(int l0, int l1, int l2, int l3) {
        EditLEDFragment fragment = new EditLEDFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_LED_0, l0);
        args.putInt(ARG_PARAM_LED_1, l1);
        args.putInt(ARG_PARAM_LED_2, l2);
        args.putInt(ARG_PARAM_LED_3, l3);
        fragment.setArguments(args);
        return fragment;
    }

    public EditLEDFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            led0 = getArguments().getInt(ARG_PARAM_LED_0);
            led1 = getArguments().getInt(ARG_PARAM_LED_1);
            led2 = getArguments().getInt(ARG_PARAM_LED_2);
            led3 = getArguments().getInt(ARG_PARAM_LED_3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_led, container, false);

        // todo set led values to GUI

        LinearLayout cont = (LinearLayout) v.findViewById(R.id.frag_edit_led_container);

        cont.addView(new LedItemView(getActivity(), mListener, 0, "All", 0));
        cont.addView(new LedItemView(getActivity(), mListener, 0, "1", 0));
        cont.addView(new LedItemView(getActivity(), mListener, 1, "2", 0));
        cont.addView(new LedItemView(getActivity(), mListener, 2, "3", 0));
        cont.addView(new LedItemView(getActivity(), mListener, 3, "4", 0));

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


    @Override
    public void updateMotorPosinGUI(int motorNr, float motorPos) {
        // nothing to do in this fragment
    }

    @Override
    public void updateActiveStatus(long formID) {
        // nothing to do in this fragment
    }
}
