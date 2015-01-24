package fragments.EditFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.philippschardt.panelinglamp.R;

import java.util.ArrayList;

import util.LedItemView;
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
    private ArrayList<LedItemView> ledItem;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment EditLEDFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditLEDFragment newInstance(int... l) {
        EditLEDFragment fragment = new EditLEDFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_LED_0, l[0]);
        args.putInt(ARG_PARAM_LED_1, l[1]);
        args.putInt(ARG_PARAM_LED_2, l[2]);
        args.putInt(ARG_PARAM_LED_3, l[3]);
        fragment.setArguments(args);
        return fragment;
    }

    public EditLEDFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ledItem = new ArrayList<LedItemView>();

        if (getArguments() != null) {
            led0 = getArguments().getInt(ARG_PARAM_LED_0);
            led1 = getArguments().getInt(ARG_PARAM_LED_1);
            led2 = getArguments().getInt(ARG_PARAM_LED_2);
            led3 = getArguments().getInt(ARG_PARAM_LED_3);


            ledItem.add(new LedItemView(getActivity(), mListener, 0, "1", led0));
            ledItem.add(new LedItemView(getActivity(), mListener, 1, "2", led1));
            ledItem.add(new LedItemView(getActivity(), mListener, 2, "3", led2));
            ledItem.add(new LedItemView(getActivity(), mListener, 3, "4", led3));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_led, container, false);
        LinearLayout cont = (LinearLayout) v.findViewById(R.id.frag_edit_led_container);

        for(LedItemView lv : ledItem) {
            cont.addView(lv);
        }




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
        // has no motors
    }

    @Override
    public void notifyAdapters() {
        // do nothing
        // has no adapters
    }

    @Override
    public void onScrollUp() {
        // nothing to do
    }

    @Override
    public void onScrollDown() {
        // nothing to do
    }


    public ArrayList<LedItemView> getLedItem() {
        return ledItem;
    }

    public void setLedItem(ArrayList<LedItemView> ledItem) {
        this.ledItem = ledItem;
    }
}
