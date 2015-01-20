package fragments.forms;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.philippschardt.panelinglamp.R;

import java.util.ArrayList;
import java.util.List;

import database.MyRecyclerViewAdapter;
import database.PanelingLampContract;
import fragments.OnFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FormsStandardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormsStandardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private MyRecyclerViewAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FormsPreInstalled.
     */
    // TODO: Rename and change types and number of parameters
    public static FormsStandardFragment newInstance(String param1, String param2) {
        FormsStandardFragment fragment = new FormsStandardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FormsStandardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forms_standard, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.frag_standard_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)

        // create cursoradapter
        SQLiteDatabase sqLiteDatabase = mListener.getDBHelper().getWritableDatabase();


        mAdapter = new MyRecyclerViewAdapter(getActivity(),mRecyclerView, sqLiteDatabase, PanelingLampContract.FormEntry.COLUMN_FAV,  PanelingLampContract.FormEntry.COLUMN_FAV_POS);

        int dataSize = 100;
        List<Integer> data = new ArrayList<>(dataSize);
        for (int i = 1; i < dataSize + 1; i++) {
            data.add(i);
        }

        //ExampleAdapter example = new ExampleAdapter(getActivity(), mRecyclerView, data);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
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



}
