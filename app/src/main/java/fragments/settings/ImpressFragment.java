package fragments.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.philippschardt.panelinglamp.R;

import java.util.ArrayList;

import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;
import util.ImpressItem;
import util.MyImpressArrayAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImpressFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImpressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImpressFragment extends Fragment implements OnReceiverListener {

    private final String TAG = getClass().getName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DEV_ARG_PARAM1 = "impress_section_number";

    private int mSectionNr;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImpressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImpressFragment newInstance(int sectionNr) {
        ImpressFragment fragment = new ImpressFragment();
        Bundle args = new Bundle();
        args.putInt(DEV_ARG_PARAM1, sectionNr);
        fragment.setArguments(args);
        return fragment;
    }

    public ImpressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNr = getArguments().getInt(DEV_ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_impress, container, false);


        ListView listview = (ListView) v.findViewById(R.id.impress_listview);

        listview.setAdapter(new MyImpressArrayAdapter(getActivity(),
                R.layout.impress_item,
                new ImpressItem[] {
                      new ImpressItem(R.drawable.form, "Shape", "created by Roberto Notarangelo from the Noun Project", "http://thenounproject.com/"),
                      new ImpressItem(R.drawable.manual_control, "Control", "created by useiconic.com from the Noun Project", "http://thenounproject.com/"),
                      new ImpressItem(R.drawable.power, "Power", "created by useiconic.com from the Noun Project", "http://thenounproject.com/"),
                      new ImpressItem(R.drawable.finished, "Finished", "created by aguycalledgary from the Noun Project", "http://thenounproject.com/"),
                      new ImpressItem(R.drawable.about, "About", "created by PJ Souders from the Noun Project", "http://thenounproject.com/"),
                      new ImpressItem(R.drawable.impress, "Impress", "created by José Campos from the Noun Project", "http://thenounproject.com/"),
                      new ImpressItem(R.drawable.rotor, "Rotor", "created by Margery M Fabi from the Noun Project", "http://thenounproject.com/"),
                      new ImpressItem(R.drawable.led, "LED", "created by jon trillana from the Noun Project", "http://thenounproject.com/")
                      , new ImpressItem(R.drawable.clockwise, "Rotation", "created by Stéphanie Rusch from the Noun Project", "http://thenounproject.com/")

                }


                ));



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
        mListener.onSectionAttached(getArguments().getInt(DEV_ARG_PARAM1));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void updateMotorPosinGUI(int motorNr, float motorPos) {
        // nothing to do
    }

    @Override
    public void updateLEDInGUI(int index, int value) {
        // nothing to do
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
}
