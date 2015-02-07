package fragments.EditFragments;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import net.philippschardt.panelinglamp.PanelingLamp;
import net.philippschardt.panelinglamp.R;

import java.util.ArrayList;

import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;
import util.MotorItemView;
import util.MsgCreator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragments.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManualControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManualControlFragment extends Fragment implements OnReceiverListener {

    private final String TAG = getClass().getName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DEV_ARG_PARAM1 = "dev_section_number";

    private int mSectionNr;

    private OnFragmentInteractionListener mListener;
    private ViewPager mViewPager;
    private ViewPagerAdapter pagerAdapter;

    private EditLEDFragment mEditLEDFragment;
    private EditMotorsFragment mEditMotorsFragment;
    private ImageView alphaView;
    private FloatingActionsMenu floatingMenu;
    private FloatingActionButton floatingButton_resetMotors;
    private FloatingActionButton floatingButton_set_zero;
    private FloatingActionButton floatingButton_moveMotors;
    private FloatingActionButton floatingButton_saveForm;
    private boolean ppValue;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNr Parameter 1.
     * @return A new instance of fragment Forms.
     */
    // TODO: Rename and change types and number of parameters
    public static ManualControlFragment newInstance(int sectionNr) {
        ManualControlFragment fragment = new ManualControlFragment();
        Bundle args = new Bundle();
        args.putInt(DEV_ARG_PARAM1, sectionNr);
        fragment.setArguments(args);
        return fragment;
    }

    public ManualControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNr = getArguments().getInt(DEV_ARG_PARAM1);
        }

        mEditMotorsFragment = EditMotorsFragment.newInstance();
        mEditLEDFragment = EditLEDFragment.newInstance(new int[]{0, 0, 0, 0,0,0,0});

        mListener.sendMsg(MsgCreator.requestCurrentStatus());


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sliding_tabs_manual_control, container, false);

        mViewPager = (ViewPager) v.findViewById(R.id.frag_forms_viewPager);
        pagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);

        ViewPager.SimpleOnPageChangeListener pageListener =
         new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                showFloatingMenu();

            }
        };

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) v.findViewById(R.id.frag_forms_tabs);
        tabs.setViewPager(mViewPager);
        tabs.setOnPageChangeListener(pageListener);


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


        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        ppValue = sharedPref.getBoolean(getString(R.string.preference_settings_password_protection), false);




        floatingButton_saveForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingMenu.toggle();
                float[] m = new float[PanelingLamp.MOTOR_NUMBER];
                int[] l = new int[PanelingLamp.LED_NUMBER];

                for (int i = 0; i < m.length; i++) {
                    m[i] = mEditMotorsFragment.getMotorItem().get(i).getmPos();
                }
                for (int f = 0; f < l.length; f++) {
                    l[f] = mEditLEDFragment.getLedItem().get(f).getValue();
                }
                mListener.showAddNewFormDialog(m, l);

            }
        });


        floatingButton_moveMotors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ArrayList<MotorItemView> motorItems = mEditMotorsFragment.getMotorItem();
                for (MotorItemView mv : motorItems) {
                    try {
                       Float.parseFloat(mv.getePos().getText().toString());
                    } catch (NumberFormatException e) {
                        floatingMenu.toggle();
                        showToast();
                        return;
                    }
                }
                mEditMotorsFragment.activateProgress(0, 1, 2, 3, 4);
                mListener.sendMsg(MsgCreator.moveToForm(-1, motorItems, mEditLEDFragment.getLedItem()));
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

                // if password protection is active, deactivete set current as zero button
                if (!ppValue) {


                for(MotorItemView mV : mEditMotorsFragment.getMotorItem()) {

                    mV.setCurrAsZero();

                    mListener.sendMsg(MsgCreator.overridePos(mV.getmIndex(), mV.getmPos()));
                    floatingMenu.toggle();

                }
                } else {
                    Toast t = Toast.makeText(getActivity(), R.string.wrong_permission, Toast.LENGTH_SHORT);
                    t.show();
                }

            }
        });

        return v;
    }


    private void showToast() {
        Toast toast =  Toast.makeText(getActivity(), R.string.enter_valid_number,Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void updateMotorPosinGUI(int motorNr, float motorPos) {

        mEditMotorsFragment.updateMotorPosinGUI(motorNr, motorPos);
    }

    @Override
    public void updateLEDInGUI(int index, int value) {
        mEditLEDFragment.updateLEDInGUI(index, value);
    }

    @Override
    public void notifyAdapters() {
        // do nothing
        // has no adapters
    }

    @Override
    public void onScrollUp() {
        hideFloatingMenu();
    }



    @Override
    public void onScrollDown() {
        showFloatingMenu();
    }



    private void hideFloatingMenu() {
        if (floatingMenu.getVisibility() == View.VISIBLE) {
            Animation slide = AnimationUtils.loadAnimation(getActivity(), R.anim.floating_action_button_hide);
            floatingMenu.startAnimation(slide);
            floatingMenu.setVisibility(View.GONE);
        }
    }

    private void showFloatingMenu() {
        Log.d(TAG, "showFloatingMenu");
        if (floatingMenu.getVisibility() == View.GONE) {
            Animation slide = AnimationUtils.loadAnimation(getActivity(), R.anim.floating_action_button_show);
            floatingMenu.startAnimation(slide);
            floatingMenu.setVisibility(View.VISIBLE);
        }
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private String[] titles = new String[]{getString(R.string.motorTab), getString(R.string.ledTab)};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }




        public Fragment getItem(int num) {
            switch (num) {
                case 0:
                    return mEditMotorsFragment;
                case 1:
                    return mEditLEDFragment;

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

