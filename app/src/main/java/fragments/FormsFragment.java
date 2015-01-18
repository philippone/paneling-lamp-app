package fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.philippschardt.panelinglamp.PanelingLamp;
import net.philippschardt.panelinglamp.R;

import Util.SlidingTabLayout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FormsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormsFragment extends Fragment implements OnReceiverListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FORMS_ARG_PARAM1 = "forms_section_number";

    // TODO: Rename and change types of parameters
    private int mSectionNr;

    private OnFragmentInteractionListener mListener;

    private ViewPagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

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


        // init view pager
        pagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        //pager.setAdapter(pagerAdapter);


        mViewPager = (ViewPager) v.findViewById(R.id.frag_forms_viewPager);
        mViewPager.setAdapter(pagerAdapter);
        mSlidingTabLayout = (SlidingTabLayout) v.findViewById(R.id.frag_forms_sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        return v;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private int pages = 2;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public Fragment getItem(int num) {
            switch (num) {
                case 0:
                    break;
            }
            return PanelingLamp.PlaceholderFragment.newInstance(num + 1);
        }

        @Override
        public int getCount() {
            return pages;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Sezione " + position;
        }

    }





    public class MyFormsPagerAdapter extends FragmentPagerAdapter {

        private int pages = 2;

        public MyFormsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PanelingLamp.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return pages;
        }
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
