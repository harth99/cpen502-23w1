package ece.cpen502.Robot;

import ece.cpen502.LUT.LookUpTable;
import robocode.*;

import java.awt.*;
import java.io.File;


public class BumbleBee extends AdvancedRobot {
    private State currState;
    private State prevState;
    private State.HP myCurrEnergy = State.HP.high;
    private State.HP enemyCurrEnergy = State.HP.high;
    private State.DistanceToEnemy currD2E = State.DistanceToEnemy.close;
    private State.DistanceToWall currD2W = State.DistanceToWall.far;
    private State.Action currAction = State.Action.forward;
    private State.HP myPrevEnergy = State.HP.high;
    private State.HP enemyPrevEnergy = State.HP.high;
    private State.DistanceToEnemy prevD2E = State.DistanceToEnemy.close;
    private State.DistanceToWall prevD2W = State.DistanceToWall.far;
    private State.Action prevAction = State.Action.forward;
    private State.OperationalMode myOperationalMode = State.OperationalMode.scan;

    public double myX = 0.0;
    public double myY = 0.0;
    public double myEnergy = 100;
    public double enemyEnergy = 100;
    public double d2E = 0.0;
    public static boolean INTERMEDIATE_REWARD = true;
    public static boolean ON_POLICY = true;
    private final double DISCOUNT_FACTOR = 0.2;
    private final double LEARNING_RATE = 0.01;
    private final double EPSILON = 0.1;
    private double Q_VAL = 0.0;
    private double reward = 0.0;
    private final double immediateBonus = 0.5;
    private final double terminalBonus = 1.0;
    private final double immediatePenaltyLow = -0.1;
    private final double immediatePenaltyMedium = -0.2;
    private final double terminalPenalty = -0.5;

    // Whether take greedy method
    public static int currActionIndex;
    public static double enemyBearing;

    // static numbers for winning rounds
    public static int totalRound = 0;
    public static int round = 0;
    public static int winRound = 0;
    public static double winRate = 0.0;
    public static String fileToSaveName = BumbleBee.class.getSimpleName() + "-"  + "winningRate"+ ".log";
    static LogFile log = new LogFile();

    public static LookUpTable lut = new LookUpTable(State.HP.values().length,
            State.HP.values().length,
            State.DistanceToEnemy.values().length,
            State.DistanceToWall.values().length,
            State.Action.values().length);


    @Override
    public void run() {
        super.run();
        setBulletColor(Color.red);
        setGunColor(Color.darkGray);
        setBodyColor(Color.blue);
        setRadarColor(Color.white);

        currState = new State(myCurrEnergy, enemyCurrEnergy, currD2E, currD2W, currAction);
        prevState = new State(myPrevEnergy, enemyPrevEnergy, prevD2E, prevD2W, prevAction);

        while (true) {
            switch (myOperationalMode) {
                case scan: {
                    reward = 0.0;
                    turnRadarLeft(90);
                    break;
                }
                case performAction: {
                    currD2W = getDistanceFromWallLevel(myX, myY);

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

    // Move to State class
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

    @Override
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
        moveAwayWhenHit();
    }
    @Override
    public void onWin(WinEvent e){

        reward = terminalBonus;
        int[] indexArr = new int []{
                myPrevEnergy.ordinal(),
                enemyPrevEnergy.ordinal(),
                prevD2E.ordinal(),
                prevD2W.ordinal(),
                prevAction.ordinal()};
        Q_VAL = calQ(reward, ON_POLICY);
        lut.setQValue(indexArr, Q_VAL);

        totalRound++;
        winRound++;

        if((totalRound != 0) && (totalRound % 100 == 0)){
            winRate = (double) winRound / 100;
            System.out.println(String.format("%d, %.3f",++round, winRate));
            File folderDst1 = getDataFile(fileToSaveName);
            log.writeToFile(folderDst1, winRate, round);
            winRound = 0;
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
        totalRound++;
        if((totalRound % 100 == 0) && (totalRound != 0)){
            winRate = (double) winRound / 100;
            System.out.println(String.format("%d, %.3f",++round, winRate));
            File folderDst1 = getDataFile(fileToSaveName);
            log.writeToFile(folderDst1, winRate, round);
            winRound = 0;
        }

    }

    public void moveAwayWhenHit() {
        setBack(200);
        setTurnRight(60);
        execute();
    }
    @Override
    public void onHitRobot(HitRobotEvent e) {
        if(INTERMEDIATE_REWARD) {
            reward += immediatePenaltyLow;
        }
        moveAwayWhenHit();
    }

//    public void saveTable() {
//        try {
//            String file = fileToSaveLUT + "-" + round + ".log";
//            lut.save(getDataFile(file));
//        } catch (Exception e) {
//            System.out.println("Save Error!" + e);
//        }
//    }

//    public void loadTable() {
//        try {
//            lut.load(fileToSaveLUT);
//        } catch (Exception e) {
//            System.out.println("Save Error!" + e);
//        }
//    }

//    public void saveLUTRes(String res) {
//        try {
//            String lutFileName = "./DataLUT/winningPercentage.txt";
//            FileWriter fWriter = new FileWriter(lutFileName);
//            fWriter.write(res + "\n");
//            fWriter.close();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
}
