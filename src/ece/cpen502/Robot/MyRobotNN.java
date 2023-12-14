package ece.cpen502.Robot;

import ece.cpen502.NN.NeuralNet;
import robocode.*;

import java.io.File;

public class MyRobotNN extends AdvancedRobot {
    // Use pre-defined after-tuned hyper-parameters
    private int numberOfHiddenNeurons = 15;
    private double momentum = 0.95;
    private double learningRate = 0.001;
    private int numberOfInputNeurons = 5;   // 4 states + 1 action
    private int numberOfOutputNeurons = 1;  // 1 output neuron Q(S, A)
    private State currState;
    private State prevState;
    private Action currAction;
    private State.OperationalMode myOperationalMode = State.OperationalMode.scan;
    private double reward;
    public double myX;
    public double myY;
    public double myEnergy;
    public double enemyEnergy;
    public double d2E;


    private NeuralNet nn;

    public void run() {
        super.run();
        // Setup neural net
        nn = new NeuralNet(numberOfInputNeurons, numberOfHiddenNeurons, numberOfOutputNeurons, learningRate, momentum, -1, 1);
        currState = new State(State.HP.high, State.HP.high, State.DistanceToEnemy.close, State.DistanceToWall.far);
        prevState = new State(State.HP.high, State.HP.high, State.DistanceToEnemy.close, State.DistanceToWall.far);


        while (true) {
            switch (myOperationalMode) {
                case scan: {
                    reward = 0.0;
                    turnRadarLeft(90);
                    break;
                }
                case performAction: {
                    currState.setDistanceToWall(getDistanceFromWallLevel(myX, myY));
                    currState.setDistanceToEnemy(getDistanceToEnemy(d2E));
                    currState.setMyEnergy(getRobotTankEnergyLevel(myEnergy));
                    currState.setEnemyEnergy(getRobotTankEnergyLevel(enemyEnergy));

                    currAction = nn.getNextAction(currState);

                    currActionIndex = (Math.random() <= EPSILON)
                            ? lut.getRandomAction() // explore a random action
                            : lut.getBestAction(
                            getRobotTankEnergyLevel(myEnergy).ordinal(),
                            getRobotTankEnergyLevel(enemyEnergy).ordinal(),
                            getDistanceToEnemy(d2E).ordinal(),
                            currD2W.ordinal());
                    currAction = State.Action.values()[currActionIndex];
                    switch (currAction) {
                        case fire: {
                            turnGunRight(getHeading() - getGunHeading() + enemyBearing);
                            fire(3);
                            break;
                        }

                        case left: {
                            setTurnLeft(30);
                            execute();
                            break;
                        }

                        case right: {
                            setTurnRight(30);
                            execute();
                            break;
                        }

                        case forward: {
                            setAhead(100);
                            execute();
                            break;
                        }
                        case backward: {
                            setBack(100);
                            execute();
                            break;
                        }
                    }
                    int[] indexes = new int[]{
                            myPrevEnergy.ordinal(),
                            enemyPrevEnergy.ordinal(),
                            prevD2E.ordinal(),
                            prevD2W.ordinal(),
                            prevAction.ordinal()
                    };
                    Q_VAL = calQ(reward, ON_POLICY);
                    lut.setQValue(indexes, Q_VAL);
                    myOperationalMode = State.OperationalMode.scan;
                }
            }
        }
    }

    public State.DistanceToWall getDistanceFromWallLevel(double x1, double y1) {
        State.DistanceToWall dist2Wall = null;
        double widthBattleField = getBattleFieldWidth();
        double heightBattleField = getBattleFieldHeight();
        double distanceToTopWall = y1;
        double distanceToBottomWall = heightBattleField - y1;
        double distanceToLeftWall = x1;
        double distanceToRightWall = widthBattleField - x1;
        if(distanceToTopWall < 30 || distanceToBottomWall < 30 || distanceToLeftWall < 30 || distanceToRightWall < 30) {
            dist2Wall = State.DistanceToWall.close;
        } else if(distanceToTopWall < 80 || distanceToBottomWall < 80 || distanceToLeftWall < 80 || distanceToRightWall < 80) {
            dist2Wall = State.DistanceToWall.medium;
        } else {
            dist2Wall = State.DistanceToWall.far;
        }
        return dist2Wall;
    }

    // Move to State class
    public State.DistanceToEnemy getDistanceToEnemy(double distanceToEnemyRobot) {
        State.DistanceToEnemy level = null;
        if (distanceToEnemyRobot < 0) {
            return level;
        } else if (distanceToEnemyRobot < 300) {
            level = State.DistanceToEnemy.close;
        } else if (distanceToEnemyRobot < 600) {
            level = State.DistanceToEnemy.medium;
        } else {
            level = State.DistanceToEnemy.far;
        }
        return level;
    }

    // Move to State class
    public State.HP getRobotTankEnergyLevel(double hp) {
        State.HP level = null;
        if(hp < 0) {
            return level;
        } else if(hp <= 33) {
            level = State.HP.low;
        } else if(hp <= 67) {
            level = State.HP.medium;
        } else {
            level = State.HP.high;
        }
        return level;
    }

    // Compute the Q
    public double calQ(double reward, boolean onPolicy) {
        double previousQ = lut.getQValue(
                myPrevEnergy.ordinal(),
                enemyPrevEnergy.ordinal(),
                prevD2E.ordinal(),
                prevD2W.ordinal(),
                prevAction.ordinal()
        );

        double currentQ = lut.getQValue(
                myCurrEnergy.ordinal(),
                enemyCurrEnergy.ordinal(),
                currD2E.ordinal(),
                currD2W.ordinal(),
                currAction.ordinal()
        );

        int bestActionIndex = lut.getBestAction(
                myCurrEnergy.ordinal(),
                enemyCurrEnergy.ordinal(),
                currD2E.ordinal(),
                currD2W.ordinal()
        );

        // Get the maximum Q ( Off-policy )
        double maxQ = lut.getQValue(
                myCurrEnergy.ordinal(),
                enemyCurrEnergy.ordinal(),
                currD2E.ordinal(),
                currD2W.ordinal(),
                bestActionIndex
        );

        // onPolicy : Sarsa
        // offPolicy : Q-Learning
        double res = onPolicy ?
                previousQ + LEARNING_RATE * (reward + DISCOUNT_FACTOR * currentQ - previousQ) :
                previousQ + LEARNING_RATE * (reward + DISCOUNT_FACTOR * maxQ - previousQ);

        return res;
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        super.onScannedRobot(e);
        enemyBearing = e.getBearing();

        myX = getX();
        myY = getY();
        myEnergy = getEnergy();
        enemyEnergy = e.getEnergy();
        d2E = e.getDistance();

        myPrevEnergy = myCurrEnergy;
        enemyPrevEnergy = enemyCurrEnergy;
        prevD2E = currD2E;
        prevD2W = currD2W;
        prevAction = currAction;

        myCurrEnergy = getRobotTankEnergyLevel(myEnergy);
        enemyCurrEnergy = getRobotTankEnergyLevel(enemyEnergy);
        currD2E = getDistanceToEnemy(d2E);
        currD2W = getDistanceFromWallLevel(myX, myY);
        myOperationalMode = State.OperationalMode.performAction;
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e){
        if(INTERMEDIATE_REWARD) {
            reward += immediatePenaltyMedium;
        }
    }

    @Override
    public void onBulletHit(BulletHitEvent e){
        if(INTERMEDIATE_REWARD) {
            reward += immediateBonus;
        }
    }

    @Override
    public void onBulletMissed(BulletMissedEvent e){
        if(INTERMEDIATE_REWARD) {
            reward += immediatePenaltyLow;
        }
    }

    @Override
    public void onHitWall(HitWallEvent e){
        if(INTERMEDIATE_REWARD) {
            reward += immediatePenaltyLow;
        }
        avidObstacle();
    }
    public void avidObstacle() {
        setBack(200);
        setTurnRight(60);
        execute();
    }
    @Override
    public void onHitRobot(HitRobotEvent e) {
        if(INTERMEDIATE_REWARD) {
            reward += immediatePenaltyLow;
        }
        avidObstacle();
    }
    @Override
    public void onWin(WinEvent e){

        reward = terminalBonus;
        int[] indexes = new int []{
                myPrevEnergy.ordinal(),
                enemyPrevEnergy.ordinal(),
                prevD2E.ordinal(),
                prevD2W.ordinal(),
                prevAction.ordinal()};
        Q_VAL = calQ(reward, ON_POLICY);
        lut.setQValue(indexes, Q_VAL);
        winRound++;
        totalRound++;
        if((totalRound % 100 == 0) && (totalRound != 0)){
            winPercentage = (double) winRound / 100;
            System.out.println(String.format("%d, %.3f",++round, winPercentage));
            File folderDst1 = getDataFile(fileToSaveName);
            log.writeToFile(folderDst1, winPercentage, round);
            winRound = 0;
//            saveTable();
        }
        if (totalRound >= 6500) {
            saveTable();
        }
    }

    @Override
    public void onDeath(DeathEvent e){

        reward = terminalPenalty;
        // why int instead of double?
        int[] indexes = new int []{
                myPrevEnergy.ordinal(),
                enemyPrevEnergy.ordinal(),
                prevD2E.ordinal(),
                prevD2W.ordinal(),
                prevAction.ordinal()};
        Q_VAL = calQ(reward, ON_POLICY);
        lut.setQValue(indexes, Q_VAL);
        /*saveTable();*/
        totalRound++;
        if((totalRound % 100 == 0) && (totalRound != 0)){
            winPercentage = (double) winRound / 100;
            System.out.println(String.format("%d, %.3f",++round, winPercentage));
            File folderDst1 = getDataFile(fileToSaveName);
            log.writeToFile(folderDst1, winPercentage, round);
            winRound = 0;
//            saveTable();
        }
        if (totalRound >= 6500) {
            saveTable();
        }

    }
    public void saveTable() {
        try {
            String file = fileToSaveLUT + "-" + round + ".log";
            lut.save(getDataFile(file));
        } catch (Exception e) {
            System.out.println("Save Error!" + e);
        }
    }

}
