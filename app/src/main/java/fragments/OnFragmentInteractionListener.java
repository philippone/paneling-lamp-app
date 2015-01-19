package fragments;

import database.PanelingLampDBHelper;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 */
public interface OnFragmentInteractionListener {

    // change to the right section in navigation drawer
    public void onSectionAttached(int number);
    public boolean sendMsg(String message);
    public void resetAllMotors();
    public int getMotorCount();
    public void adjustAllMotorToZero();
    public boolean liftMotorUp(int index, float roations);
    public boolean liftMotorDown(int index, float rotations);
    public boolean moveMotorToPos(int index, float position);

    public PanelingLampDBHelper getDBHelper();
}
