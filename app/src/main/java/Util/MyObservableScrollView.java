package util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import fragments.OnFragmentInteractionListener;

/**
 * Created by philipp on 31.01.15.
 */
public class MyObservableScrollView extends ScrollView {



    private OnFragmentInteractionListener mListener = null;

    public MyObservableScrollView(Context context) {
        super(context);
    }

    public MyObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFragInteractionListener(OnFragmentInteractionListener listener) {
        mListener = listener;

    }



    @Override
    protected void onScrollChanged(int l, int t, int dx, int dy) {
        super.onScrollChanged(l, t, dx, dy);
        if (mListener != null) {




            if (t > dy) {
                // scrolling up
                mListener.onScrollUp(l, t, dx, dy, getScrollX(), getScrollY());
            } else
                mListener.onScrollDown(l,t,dx,dy, getScrollX(), getScrollY());
        }
    }
}
