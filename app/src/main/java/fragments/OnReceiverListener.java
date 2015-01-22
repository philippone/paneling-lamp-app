package fragments;

public interface OnReceiverListener {
    public void updateMotorPosinGUI(int motorNr, float motorPos);
    //public void updateActiveStatus(long formID);

    public void notifyAdapters();
}
