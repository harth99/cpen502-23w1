package ece.cpen502.Robot;

public class State {

    public enum HP {low, medium, high}

    ;

    public enum DistanceToEnemy {close, medium, far}

    ;

    public enum DistanceToWall {close, medium, far}

    ;

    public enum Action {fire, forwardLeft, forwardRight, backwardLeft, backwardRight, forward, backward, left, right}

    ;

    public enum OperationalMode {scan, performAction}

    ;

    private HP hp;
    private DistanceToEnemy distanceToEnemy;
    private DistanceToWall distanceToWall;
    private Action action;
    private OperationalMode operationalMode;

    // Constructor
    public State(HP hp, DistanceToEnemy distanceToEnemy, DistanceToWall distanceToWall, Action action, OperationalMode operationalMode) {
        this.hp = hp;
        this.distanceToEnemy = distanceToEnemy;
        this.distanceToWall = distanceToWall;
        this.action = action;
        this.operationalMode = operationalMode;
    }

    // Getters
    public HP getHp() {
        return hp;
    }

    public DistanceToEnemy getDistanceToEnemy() {
        return distanceToEnemy;
    }

    public DistanceToWall getDistanceToWall() {
        return distanceToWall;
    }

    public Action getAction() {
        return action;
    }

    public OperationalMode getOperationalMode() {
        return operationalMode;
    }

    // Setters
    public void setHp(HP hp) {
        this.hp = hp;
    }

    public void setDistanceToEnemy(DistanceToEnemy distanceToEnemy) {
        this.distanceToEnemy = distanceToEnemy;
    }

    public void setDistanceToWall(DistanceToWall distanceToWall) {
        this.distanceToWall = distanceToWall;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setOperationalMode(OperationalMode operationalMode) {
        this.operationalMode = operationalMode;
    }

}
