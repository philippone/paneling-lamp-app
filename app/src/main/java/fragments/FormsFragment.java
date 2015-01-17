package fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.philippschardt.panelinglamp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FormsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormsFragment extends Fragment implements OnReceiverListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FORMS_ARG_PARAM1 = "forms_section_number";

    // TODO: Rename and change types of parameters
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
    public static FormsFragment newInstance(int sectionNr) {
        FormsFragment fragment = new FormsFragment();
        Bundle args = new Bundle();
        args.putInt(FORMS_ARG_PARAM1, sectionNr);
        fragment.setArguments(args);
        return fragment;
    }

    public FormsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNr = getArguments().getInt(FORMS_ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forms, container, false);

        Button test = (Button) v.findViewById(R.id.button_test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "klappt");

            }
        });

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

        // set section title
        mListener.onSectionAttached(getArguments().getInt(FORMS_ARG_PARAM1));

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    @Override
    public void updateMotorPosinGUI(int motorNr, float motorPos) {
        // called if pos update is received
    }
}
