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

import util.MotorItemView;
import util.MsgCreator;
import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link EditMotorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditMotorsFragment extends Fragment implements OnReceiverListener{


    private static final String ARG_PARAM_M_0 = "EditMotorsFragment_m0";
    private static final String ARG_PARAM_M_1 = "EditMotorsFragment_m1";
    private static final String ARG_PARAM_M_2 = "EditMotorsFragment_m2";
    private static final String ARG_PARAM_M_3 = "EditMotorsFragment_m3";
    private static final String ARG_PARAM_M_4 = "EditMotorsFragment_m4";

    private float m0 = 0;
    private float m1 = 0;
    private float m2 = 0;
    private float m3 = 0;
    private float m4 = 0;

    private OnFragmentInteractionListener mListener;



    private ArrayList<MotorItemView> motorItem;
    private boolean attached = false;
    private boolean requestCurrentStatus = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment EditMotorsFragment.
     */
    public static EditMotorsFragment newInstance(float... m) {
        EditMotorsFragment fragment = new EditMotorsFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_PARAM_M_0, m[0]);
        args.putFloat(ARG_PARAM_M_1, m[1]);
        args.putFloat(ARG_PARAM_M_2, m[2]);
        args.putFloat(ARG_PARAM_M_3, m[3]);
        args.putFloat(ARG_PARAM_M_4, m[4]);

        fragment.setArguments(args);
        return fragment;
    }

    public static EditMotorsFragment newInstance() {
        EditMotorsFragment fragment = new EditMotorsFragment();
        return fragment;
    }

    public EditMotorsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        motorItem = new ArrayList<MotorItemView>();

        if (getArguments() != null) {
            m0 = getArguments().getFloat(ARG_PARAM_M_0);
            m1 = getArguments().getFloat(ARG_PARAM_M_1);
            m2 = getArguments().getFloat(ARG_PARAM_M_2);
            m3 = getArguments().getFloat(ARG_PARAM_M_3);
            m4 = getArguments().getFloat(ARG_PARAM_M_4);
        } else {
                // request current status
                mListener.sendMsg(MsgCreator.requestCurrentStatus());
        }

        motorItem.add(new MotorItemView(getActivity(), mListener, 0, m0));
        motorItem.add(new MotorItemView(getActivity(), mListener, 1, m1));
        motorItem.add(new MotorItemView(getActivity(), mListener, 2, m2));
        motorItem.add(new MotorItemView(getActivity(), mListener, 3, m3));
        motorItem.add(new MotorItemView(getActivity(), mListener, 4, m4));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_edit_motors, container, false);

        LinearLayout cont = (LinearLayout) v.findViewById(R.id.frag_edit_motor_container);

        // add motor items
        for (MotorItemView mv : motorItem) {
            cont.addView(mv);
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
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void updateMotorPosinGUI(int motorNr, float motorPos) {
        if (motorItem != null)
            motorItem.get(motorNr).setmPos(motorPos);
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

    public ArrayList<MotorItemView> getMotorItem() {
        return motorItem;
    }

    public void setMotorItem(ArrayList<MotorItemView> motorItem) {
        this.motorItem = motorItem;
    }

    public void activateProgress(int... index) {
        for (int i : index) {
            if (i < motorItem.size())
                motorItem.get(i).activateProgress();
        }
    }
}
