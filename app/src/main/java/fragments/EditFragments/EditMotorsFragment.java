package fragments.EditFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.philippschardt.panelinglamp.R;

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

    // TODO: Rename and change types of parameters
    private float m0;
    private float m1;
    private float m2;
    private float m3;
    private float m4;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment EditMotorsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditMotorsFragment newInstance(float m0, float m1, float m2, float m3, float m4) {
        EditMotorsFragment fragment = new EditMotorsFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_PARAM_M_0, m0);
        args.putFloat(ARG_PARAM_M_1, m1);
        args.putFloat(ARG_PARAM_M_2, m2);
        args.putFloat(ARG_PARAM_M_3, m3);
        args.putFloat(ARG_PARAM_M_4, m4);

        fragment.setArguments(args);
        return fragment;
    }

    public EditMotorsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            m0 = getArguments().getFloat(ARG_PARAM_M_0);
            m1 = getArguments().getFloat(ARG_PARAM_M_1);
            m2 = getArguments().getFloat(ARG_PARAM_M_2);
            m3 = getArguments().getFloat(ARG_PARAM_M_3);
            m4 = getArguments().getFloat(ARG_PARAM_M_4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_edit_motors, container, false);

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
        // TODO update motors
    }

    @Override
    public void updateActiveStatus(long formID) {
        // do nothing
    }
}
