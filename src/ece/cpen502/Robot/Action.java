package ece.cpen502.Robot;

public enum Action {
    fire,
    forwardLeft,
    forwardRight,
    backwardLeft,
    backwardRight,
    forward,
    backward,
    left,
    right;

    public static final int NUM_ACTIONS = Action.values().length;

    public static int getActionNum(Action action) {
        for (int i = 0; i < NUM_ACTIONS; i++) {
            if (getAction(i).equals(action)) {
                return i;
            }
        }
        return 0;
    }

    public static Action getAction(int i) {
        return Action.values()[i];
    }
}




