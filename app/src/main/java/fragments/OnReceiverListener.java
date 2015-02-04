package fragments;

public interface OnReceiverListener {
    public void updateMotorPosinGUI(int motorNr, float motorPos);
    public void updateLEDInGUI(int index, int value);
    //public void updateActiveStatus(long formID);

    public void notifyAdapters();


    public void onScrollUp();
    public void onScrollDown();
}
