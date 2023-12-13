package ece.cpen502.Robot;

public class State {

    public enum HP {low, medium, high};
    public enum DistanceToEnemy {close, medium, far};
    public enum DistanceToWall {close, medium, far};
    public enum Action {fire, forwardLeft, forwardRight, backwardLeft, backwardRight, forward, backward, left, right};
    public enum OperationalMode {scan, performAction};

}
