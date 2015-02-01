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

import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;
import util.LedAllItemView;
import util.LedItemView;
import util.MyObservableScrollView;


public class EditLEDFragment extends Fragment implements OnReceiverListener{

    private static final String ARG_PARAM_LED = "EditLEDFragment_led";
    private static final String ARG_PARAM_LED_0 = "EditLEDFragment_led_0";
    private static final String ARG_PARAM_LED_1 = "EditLEDFragment_led_1";
    private static final String ARG_PARAM_LED_2 = "EditLEDFragment_led_2";
    private static final String ARG_PARAM_LED_3 = "EditLEDFragment_led_3";


    private int[] led;


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
        args.putIntArray(ARG_PARAM_LED, l);
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
            led = getArguments().getIntArray(ARG_PARAM_LED);


            for (int i = 0; i < led.length; i++) {
                ledItem.add(new LedItemView(getActivity(), mListener, i, i+1 +"", led[i]));
            }

        }
    }

    private int getMax(int[] led) {
        int max = 0;
        for (int i = 0; i < led.length; i++) {
            if (led[i] > max)
                max = led[i];
        }
        return max;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_led, container, false);
        LinearLayout cont = (LinearLayout) v.findViewById(R.id.frag_edit_led_container);


        cont.addView(new LedAllItemView(getActivity(), mListener, -1, "All", getMax(led), ledItem));
        for(LedItemView lv : ledItem) {
            cont.addView(lv);
        }


        MyObservableScrollView mScrollView = (MyObservableScrollView) v.findViewById(R.id.frag_edit_led_scrollView);
        mScrollView.setFragInteractionListener(mListener);

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

        //mListener.onScrollUp(0, 0, 0, 0);
    }

    @Override
    public void onScrollDown() {
        //mListener.onScrollDown(0, 0, 0, 0);
    }


    public ArrayList<LedItemView> getLedItem() {
        return ledItem;
    }

    public void setLedItem(ArrayList<LedItemView> ledItem) {
        this.ledItem = ledItem;
    }
}
