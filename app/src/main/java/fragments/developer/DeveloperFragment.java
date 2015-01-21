package fragments.developer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import net.philippschardt.panelinglamp.PanelingLamp;
import net.philippschardt.panelinglamp.R;

import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragments.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeveloperFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeveloperFragment extends Fragment implements OnReceiverListener {

    private final String TAG = getClass().getName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DEV_ARG_PARAM1 = "dev_section_number";

    private int mSectionNr;

    private OnFragmentInteractionListener mListener;
    private ViewPager mViewPager;
    private ViewPagerAdapter pagerAdapter;
    private DeveloperFragmentMotors motorFragment;
    private DeveloperFragmentLEDs ledFragment;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNr Parameter 1.
     * @return A new instance of fragment Forms.
     */
    // TODO: Rename and change types and number of parameters
    public static DeveloperFragment newInstance(int sectionNr) {
        DeveloperFragment fragment = new DeveloperFragment();
        Bundle args = new Bundle();
        args.putInt(DEV_ARG_PARAM1, sectionNr);
        fragment.setArguments(args);
        return fragment;
    }

    public DeveloperFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNr = getArguments().getInt(DEV_ARG_PARAM1);
        }

        
        motorFragment = DeveloperFragmentMotors.newInstance();
        // todo set last value/ currentvalue
        // TODO besser wert an lampe abfragen
        ledFragment = DeveloperFragmentLEDs.newInstance(0);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sliding_tabs, container, false);
        
        mViewPager = (ViewPager) v.findViewById(R.id.frag_forms_viewPager);
        pagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) v.findViewById(R.id.frag_forms_tabs);
        tabs.setViewPager(mViewPager);
        
        
        return v;
    }

    @Override
    public void updateMotorPosinGUI(int motorNr, float motorPos) {
        motorFragment.updateMotorPosinGUI(motorNr, motorPos);
    }

    @Override
    public void updateActiveStatus(long formID) {
        // do nothing
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private String[] titles = new String[] {getString(R.string.motorTab), getString(R.string.ledTab)};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public Fragment getItem(int num) {
            switch (num) {
                case 0:
                    return motorFragment;
                case 1:
                    return ledFragment;
            }
            return PanelingLamp.PlaceholderFragment.newInstance(num + 1);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

   

    @Override
    public void onResume() {
        super.onResume();

       
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







}

