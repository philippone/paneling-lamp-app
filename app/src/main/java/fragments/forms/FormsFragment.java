package fragments.forms;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import net.philippschardt.panelinglamp.PanelingLamp;
import net.philippschardt.panelinglamp.R;

import fragments.OnFragmentInteractionListener;
import fragments.OnReceiverListener;
import util.MsgCreator;
import util.SlidingTabLayout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragments.OnFragmentInteractionListener} interface
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
    private FormsFragmentFavs mFavsFrag;
    private FormsStandardFragment mStandardFrag;
    private FormsStandardFragment mOwnFrag;
    private FloatingActionButton floatingButton_Add_Form;
    private ImageView alphaView;
    private FloatingActionsMenu floatingMenu;
    private FloatingActionButton floatingButton_rotate_Clockwise;
    private FloatingActionButton floatingButton_rotate_Counter_Clockwise;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNr Parameter 1.
     * @return A new instance of fragment Forms.
     */
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

        mFavsFrag = FormsFragmentFavs.newInstance("test", "test");
        mStandardFrag = FormsStandardFragment.newInstance(true);
        mOwnFrag = FormsStandardFragment.newInstance(false);


        // TODO
        mListener.sendMsg(MsgCreator.requestCurrentStatus());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sliding_tabs_forms, container, false);



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


        alphaView = (ImageView) v.findViewById(R.id.froms_apha_view);
        alphaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingMenu.toggle();
            }
        });

        floatingMenu = (FloatingActionsMenu) v.findViewById(R.id.forms_floating_menu_bttn);

        floatingMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onMenuExpanded() {


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
                alphaView.setVisibility(View.GONE);
            }
        });

        floatingButton_rotate_Clockwise = (FloatingActionButton) v.findViewById(R.id.forms_frag_floating_90_clockwise);
        floatingButton_rotate_Counter_Clockwise = (FloatingActionButton) v.findViewById(R.id.forms_frag_floating_90_counter_clockwise);


        floatingButton_rotate_Clockwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendMsg(MsgCreator.rotateClockwise());
            }
        });

        floatingButton_rotate_Counter_Clockwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendMsg(MsgCreator.rotateCounterClockwise());
            }
        });


        floatingButton_Add_Form = (FloatingActionButton) v.findViewById(R.id.forms_frag_floating_add_form);

        floatingButton_Add_Form.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                /*Intent startActivity = new Intent(getActivity(), EditFormActivity.class);
                startActivity.putExtra(EditFormActivity.EXTRA_IS_NEW_FORM, true);
                //getActivity().startActivity(startActivity);
                getActivity().startActivity(startActivity, ActivityOptions.makeSceneTransitionAnimation(((PanelingLamp) mListener)).toBundle());
                */

                // TODO change to new activity??
                PanelingLamp host = ((PanelingLamp)mListener);
                host.onNavigationDrawerItemSelected(1);


            }
        });

        return v;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter  {

        private String[] titles = new String[] {getString(R.string.favs), getString(R.id.standard) ,getString(R.string.own)};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public Fragment getItem(int num) {
            switch (num) {
                case 0:
                    return mFavsFrag;
                case 1:
                    return mStandardFrag;
                case 2:
                    return mOwnFrag;
                default:
                    return PanelingLamp.PlaceholderFragment.newInstance(num + 1);
            }
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

        //((OnReceiverListener) mFavsFrag).updateMotorPosinGUI(motorNr, motorPos);


        //((OnReceiverListener) mStandardFrag).updateMotorPosinGUI(motorNr, motorPos);
    }

    @Override
    public void updateLEDInGUI(int index, int value) {
        //nothing to do
    }


    @Override
    public void notifyAdapters() {
        mFavsFrag.notifyAdapters();
        mStandardFrag.notifyAdapters();
        mOwnFrag.notifyAdapters();
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
        if (floatingMenu.getVisibility() == View.GONE) {
            Animation slide = AnimationUtils.loadAnimation(getActivity(), R.anim.floating_action_button_show);
            floatingMenu.startAnimation(slide);
            floatingMenu.setVisibility(View.VISIBLE);
        }
    }



}
