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

    private HP myEnergy;
    private HP enemyEnergy;
    private DistanceToEnemy distanceToEnemy;
    private DistanceToWall distanceToWall;
    private Action action;
    private OperationalMode operationalMode;

    // Constructor
    public State(HP myEnergy, HP enemyEnergy, DistanceToEnemy distanceToEnemy, DistanceToWall distanceToWall, Action action) {
        this.myEnergy = myEnergy;
        this.enemyEnergy = enemyEnergy;
        this.distanceToEnemy = distanceToEnemy;
        this.distanceToWall = distanceToWall;
        this.action = action;
    }

    public State(HP myEnergy, HP enemyEnergy, DistanceToEnemy distanceToEnemy, DistanceToWall distanceToWall) {
        this.myEnergy = myEnergy;
        this.enemyEnergy = enemyEnergy;
        this.distanceToEnemy = distanceToEnemy;
        this.distanceToWall = distanceToWall;
    }

    // Getters
    public HP getMyEnergy() {
        return myEnergy;
    }

    public HP getEnemyEnergy() {
        return enemyEnergy;
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

    // Setters
    public void setMyEnergy(HP myEnergy) {
        this.myEnergy = myEnergy;
    }

    public void setEnemyEnergy(HP enemyEnergy) {
        this.enemyEnergy = enemyEnergy;
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

}
