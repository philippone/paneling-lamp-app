package fragments.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.philippschardt.panelinglamp.R;

import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;


public class SettingsFragment extends Fragment implements OnReceiverListener {

    private static final String SETTINGS_ARG_PARAM1 = "settings_section_number";
    private OnFragmentInteractionListener mListener;
    private int mSectionNr;


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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_settings, container, false);

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
    public void updateActiveStatus(long formID) {

    }
}
