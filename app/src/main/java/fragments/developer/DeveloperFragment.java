package fragments.developer;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import net.philippschardt.panelinglamp.PanelingLamp;
import net.philippschardt.panelinglamp.R;

import Util.MotorItemView;
import Util.MsgCreator;
import fragments.EditFragments.EditLEDFragment;
import fragments.EditFragments.EditMotorsFragment;
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

    private EditLEDFragment mEditLEDFragment;
    private EditMotorsFragment mEditMotorsFragment;
    private ImageView alphaView;
    private FloatingActionsMenu floatingMenu;
    private FloatingActionButton floatingButton_resetMotors;
    private FloatingActionButton floatingButton_set_zero;
    private FloatingActionButton floatingButton_moveMotors;
    private FloatingActionButton floatingButton_saveForm;

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
        mEditMotorsFragment = EditMotorsFragment.newInstance();
        mEditLEDFragment = EditLEDFragment.newInstance(new int[]{0, 0, 0, 0});


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sliding_tabs_manual_control, container, false);

        mViewPager = (ViewPager) v.findViewById(R.id.frag_forms_viewPager);
        pagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) v.findViewById(R.id.frag_forms_tabs);
        tabs.setViewPager(mViewPager);


        alphaView = (ImageView) v.findViewById(R.id.manual_control_apha_view);
        floatingMenu = (FloatingActionsMenu) v.findViewById(R.id.manual_control_floating_menu_bttn);

        floatingMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onMenuExpanded() {
                Log.d(TAG, "expand menu");

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    // get the center for the clipping circle
                    int cx = alphaView.getRight() - 200;
                    int cy = alphaView.getBottom() + 200;

                    // get the final radius for the clipping circle
                    int finalRadius = Math.max(alphaView.getWidth(), alphaView.getHeight());

                    // create the animator for this view (the start radius is zero)
                    Animator anim =
                            ViewAnimationUtils.createCircularReveal(alphaView, cx, cy, 0, finalRadius);

                    // make the view visible and start the animation
                    alphaView.setVisibility(View.VISIBLE);
                    anim.start();
                } else {

                    alphaView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onMenuCollapsed() {
                Log.d(TAG, "collapse menu");
                alphaView.setVisibility(View.GONE);
            }
        });

        alphaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingMenu.toggle();
            }
        });


        floatingButton_resetMotors = (FloatingActionButton) v.findViewById(R.id.manual_control_floating_bttn_resetMotors);
        floatingButton_set_zero = (FloatingActionButton) v.findViewById(R.id.manual_control_floating_bttn_set_as_zero);
        floatingButton_moveMotors = (FloatingActionButton) v.findViewById(R.id.manual_control_floating_bttn_move_motors);
        floatingButton_saveForm = (FloatingActionButton) v.findViewById(R.id.manual_control_floating_bttn_save_as_form);

        floatingButton_moveMotors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditMotorsFragment.activateProgress(0, 1, 2, 3, 4);
                mListener.sendMsg(MsgCreator.moveToForm(-1,mEditMotorsFragment.getMotorItem(), mEditLEDFragment.getLedItem()));
                floatingMenu.toggle();
            }
        });


        floatingButton_resetMotors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEditMotorsFragment.activateProgress(0, 1, 2, 3, 4);

                mListener.sendMsg(MsgCreator.forceReset(0));
                mListener.sendMsg(MsgCreator.forceReset(1));
                mListener.sendMsg(MsgCreator.forceReset(2));
                mListener.sendMsg(MsgCreator.forceReset(3));
                mListener.sendMsg(MsgCreator.forceReset(4));

                floatingMenu.toggle();
            }
        });


        floatingButton_set_zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(MotorItemView mV : mEditMotorsFragment.getMotorItem()) {

                    mV.setCurrAsZero();

                    mListener.sendMsg(MsgCreator.overridePos(mV.getmIndex(), mV.getmPos()));
                    floatingMenu.toggle();

                }

            }
        });

        return v;
    }

    @Override
    public void updateMotorPosinGUI(int motorNr, float motorPos) {
        motorFragment.updateMotorPosinGUI(motorNr, motorPos);
        mEditMotorsFragment.updateMotorPosinGUI(motorNr, motorPos);
    }

    @Override
    public void notifyAdapters() {
        // do nothing
        // has no adapters
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private String[] titles = new String[]{getString(R.string.motorTab), getString(R.string.ledTab), "tmpMotors"};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public Fragment getItem(int num) {
            switch (num) {
                case 0:
                    return mEditMotorsFragment;
                case 1:
                    return mEditLEDFragment;
                case 2:
                    return motorFragment;
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

        mListener.sendMsg(MsgCreator.requestCurrentStatus());


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

