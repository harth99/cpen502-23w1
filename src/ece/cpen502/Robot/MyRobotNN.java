package ece.cpen502.Robot;

import ece.cpen502.NN.NeuralNet;
import ece.cpen502.ReplayMemory.Experience;
import ece.cpen502.ReplayMemory.ReplayMemory;
import robocode.*;

import java.io.File;
import java.util.Random;

public class MyRobotNN extends AdvancedRobot {
    // Use pre-defined after-tuned hyper-parameters
    private static final int MEMORY_CAPACITY = 10;
    private static final boolean INTERMEDIATE_REWARD = true;
    private int numberOfHiddenNeurons = 15;
    private double momentum = 0.95;
    private double learningRate = 0.001;
    private int numberOfInputNeurons = 5;   // 4 states + 1 action
    private int numberOfOutputNeurons = 1;  // 1 output neuron Q(S, A)
    private State currState;
    private State prevState;
    private Action currAction;
    private Action prevAction;
    private final double immediateBonus = 0.5;
    private final double terminalBonus = 1.0;
    private final double immediatePenaltyLow = -0.1;
    private final double immediatePenaltyMedium = -0.2;
    private final double terminalPenalty = -0.5;
    private State.OperationalMode myOperationalMode = State.OperationalMode.scan;
    static ReplayMemory<Experience> replayMemory = new ReplayMemory<>(MEMORY_CAPACITY);
    private double reward;
    public double myX;
    public double myY;
    public double myEnergy;
    public double enemyEnergy;
    public double d2E;
    public static int totalRound = 0;
    public static int winRound = 0;
    public static final double EPSILON = 0.1;
    public static double enemyBearing;


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

                    if (Math.random() <= EPSILON) {
                        currAction = Action.values()[new Random().nextInt(Action.values().length)];
                    } else {
                        currAction = getBestActionUsingNN(currState);
                    }

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
                    updatePrevQ();
                    myOperationalMode = State.OperationalMode.scan;
                }
            }
        }
    }

    public Action getBestActionUsingNN(State state) {
        int actionWithMaxQEnum = 0;
        double maxQ = 0.0;
        for (int i = 0; i < Action.values().length; i++) {
            double resQ = nn.getThisActionOutputNeuronActivation(state, Action.values()[i]);
            if (resQ > maxQ) {
                maxQ = resQ;
                actionWithMaxQEnum = i;
            }
        }
        return Action.values()[actionWithMaxQEnum];
    }

    public State.DistanceToWall getDistanceFromWallLevel(double x1, double y1) {
        State.DistanceToWall dist2Wall = null;
        double widthBattleField = getBattleFieldWidth();
        double heightBattleField = getBattleFieldHeight();
        double distanceToTopWall = y1;
        double distanceToBottomWall = heightBattleField - y1;
        double distanceToLeftWall = x1;
        double distanceToRightWall = widthBattleField - x1;
        if (distanceToTopWall < 30 || distanceToBottomWall < 30 || distanceToLeftWall < 30 || distanceToRightWall < 30) {
            dist2Wall = State.DistanceToWall.close;
        } else if (distanceToTopWall < 80 || distanceToBottomWall < 80 || distanceToLeftWall < 80 || distanceToRightWall < 80) {
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
        if (hp < 0) {
            return level;
        } else if (hp <= 33) {
            level = State.HP.low;
        } else if (hp <= 67) {
            level = State.HP.medium;
        } else {
            level = State.HP.high;
        }
        return level;
    }

    public void updatePrevQ() {
        double[] x = new double[]{
                prevState.getMyEnergy().ordinal(),
                prevState.getEnemyEnergy().ordinal(),
                prevState.getDistanceToWall().ordinal(),
                prevState.getDistanceToEnemy().ordinal(),
        };
        replayMemoryTrain(x);
    }

    public void replayMemoryTrain(double[] x) {
        Experience experience = new Experience(reward, currState, prevState, currAction);
        replayMemory.add(experience);
        if (replayMemory.sizeOf() >= MEMORY_CAPACITY) {
            for (Object object : replayMemory.randomSample(MEMORY_CAPACITY)) {
                Experience exp = (Experience) object;
                nn.qTrain(exp.getReward(),
                        exp.getCurrState(),
                        exp.getPrevState(),
                        exp.getPrevAction());
            }
        }
    }

    // Compute the Q

    public void onScannedRobot(ScannedRobotEvent e) {
        super.onScannedRobot(e);
        enemyBearing = e.getBearing();

        myX = getX();
        myY = getY();
        myEnergy = getEnergy();
        enemyEnergy = e.getEnergy();
        d2E = e.getDistance();

        prevAction = currAction;

        myOperationalMode = State.OperationalMode.performAction;
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        if (INTERMEDIATE_REWARD) {
            reward += immediatePenaltyMedium;
        }
    }

    @Override
    public void onBulletHit(BulletHitEvent e) {
        if (INTERMEDIATE_REWARD) {
            reward += immediateBonus;
        }
    }

    @Override
    public void onBulletMissed(BulletMissedEvent e) {
        if (INTERMEDIATE_REWARD) {
            reward += immediatePenaltyLow;
        }
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        if (INTERMEDIATE_REWARD) {
            reward += immediatePenaltyLow;
        }
        moveAwayWhenHit();
    }

    public void moveAwayWhenHit() {
        setBack(200);
        setTurnRight(60);
        execute();
    }

    @Override
    public void onHitRobot(HitRobotEvent e) {
        if (INTERMEDIATE_REWARD) {
            reward += immediatePenaltyLow;
        }
        moveAwayWhenHit();
    }

    @Override
    public void onWin(WinEvent e) {
        reward = terminalBonus;
        totalRound++;
        winRound++;
    }

    @Override
    public void onDeath(DeathEvent e) {
        reward = terminalPenalty;
        totalRound++;
    }
}
