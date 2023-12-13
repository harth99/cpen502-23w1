package ece.cpen502.LUT;

import ece.cpen502.Interface.LUTInterface;
import ece.cpen502.Robot.Action;
import ece.cpen502.Robot.State;
import robocode.RobocodeFileOutputStream;
import robocode.RobocodeFileWriter;

import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;


public class LookUpTable implements LUTInterface {

    private int myRobotTankEnergySize;
    private int enemyRobotTankEnergySize;
    private int d2ESize;
    private int d2WSize;
    private int actionSize;
    private double[][][][][] myLookUpTable;
    // visit: track for the used actions
    private int[][][][][] numberOfVisits;

    public LookUpTable(int myHP, int enemyHP, int d2E, int d2W, int action) {
        this.myRobotTankEnergySize = myHP;
        this.enemyRobotTankEnergySize = enemyHP;
        this.d2ESize = d2E;
        this.d2WSize = d2W;
        this.actionSize = action;
        myLookUpTable = new double[this.myRobotTankEnergySize][this.enemyRobotTankEnergySize][this.d2ESize][this.d2WSize][this.actionSize];
        numberOfVisits = new int[this.myRobotTankEnergySize][this.enemyRobotTankEnergySize][this.d2ESize][this.d2WSize][this.actionSize];
        initialiseLUT();
    }

    @Override
    public void initialiseLUT() {
        for (int i = 0; i < myRobotTankEnergySize; i++) {
            for (int j = 0; j < enemyRobotTankEnergySize; j++) {
                for (int k = 0; k < d2ESize; k++) {
                    for (int m = 0; m < d2WSize; m++) {
                        for (int n = 0; n < actionSize; n++) {
                            myLookUpTable[i][j][k][m][n] = Math.random();
                            numberOfVisits[i][j][k][m][n] = 0;
                        }
                    }
                }
            }
        }
    }

    public int getRandomAction() {
        Random rand = new Random();
        return rand.nextInt(actionSize);
    }

    public int getBestAction(int myEnergy, int enemyEnergy, int d2E, int d2W) {
        double maxQ = -1;
        int actionIndex = -1;

        for (int i = 0; i < actionSize; i++) {
            if (myLookUpTable[myEnergy][enemyEnergy][d2E][d2W][i] > maxQ) {
                actionIndex = i;
                maxQ = myLookUpTable[myEnergy][enemyEnergy][d2E][d2W][i];
            }
        }
        return actionIndex;
    }

    public double getQValue(int myEnergy, int enemyEnergy, int d2E, int d2W, int action) {
        return myLookUpTable[myEnergy][enemyEnergy][d2E][d2W][action];
    }

    public void setQValue(int[] x, double argValue) {
        myLookUpTable[x[0]][x[1]][x[2]][x[3]][x[4]] = argValue;
        numberOfVisits[x[0]][x[1]][x[2]][x[3]][x[4]]++;
    }

    public void save(File file) throws IOException {
        int count = 0;
        for (int i = 0; i < myRobotTankEnergySize; i++) {
            for (int j = 0; j < enemyRobotTankEnergySize; j++) {
                for (int k = 0; k < d2ESize; k++) {
                    for (int m = 0; m < d2WSize; m++) {
                        for (int n = 0; n < actionSize; n++) {
                            count++;
                            String s = String.format("%d,%d,%d,%d,%d,%3f,%d", i, j, k, m, n, myLookUpTable[i][j][k][m][n], numberOfVisits[i][j][k][m][n]);
                            RobocodeFileWriter fileWriter = new RobocodeFileWriter(file.getAbsolutePath(), true);
                            fileWriter.write(s + "\r\n");
                            if (count == myRobotTankEnergySize*enemyRobotTankEnergySize*d2ESize*d2WSize*actionSize) {
                                fileWriter.write("Suppose to end here" + "\r\n");
                            }
                            fileWriter.close();
                        }
                    }
                }
            }
        }

    }

    public void load(String argFileName) throws IOException {
        try {
            BufferedReader in = new BufferedReader(new FileReader(argFileName));
            for (int i = 0; i < myRobotTankEnergySize; i++) {
                for (int j = 0; j < enemyRobotTankEnergySize; j++) {
                    for (int k = 0; k < d2ESize; k++) {
                        for (int m = 0; m < d2WSize; m++) {
                            for (int n = 0; n < actionSize; n++) {
                                String line = in.readLine();
                                String[] args = line.split(",");
                                System.out.println(line);
                                double q = Double.parseDouble(args[5]);
                                myLookUpTable[i][j][k][m][n] = q;
                            }
                        }
                    }
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int visit(double[] x) throws ArrayIndexOutOfBoundsException {
        if (x.length != 5) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            int a = (int) x[0];
            int b = (int) x[1];
            int c = (int) x[2];
            int d = (int) x[3];
            int e = (int) x[4];
            return numberOfVisits[a][b][c][d][e];
        }
    }

    public double outputFor(double[] X) {
        return 0;
    }

    public double train(double[] X, double argValue) {
        return 0;
    }
}


//public class LookUpTable {
//    private double[][] table;
//
//    public int [][]visit;
//    public LookUpTable()
//    {
//
//        table=new double[State.Num][Action.Num];
//        visit=new int[State.Num][Action.Num];
//        initialize();
//    }
//    public void initialize()
//    {
//        for (int i=0;i<State.Num;i++)
//            for(int j=0;j<Action.Num; j++)
//            {
//                table[i][j]=0.0d;
//                visit[i][j]=0;
//
//            }
//    }
//
//    public double getValue(int state,int action)
//    {
//        return table[state][action];
//
//    }
//    public void setValue(int state,int action,double value)
//    {
//        table[state][action]=value;
//    }
//    public int getVisitTimes(int state,int action)
//    {
//        return visit[state][action];
//    }
//    public double getMaxValue(int state)
//    {
//        double maxvalue=-100000000;
//        for(int i=0;i<table[state].length;i++)
//        //	for(int i=0;i<RobotAction.Num;i++)
//        {
//            if (table[state][i]>maxvalue)
//            {
//                maxvalue=table[state][i];
//            }
//        }
//        return maxvalue;
//    }
//    public int getBestAction(int state)
//    {
//        int action=0;
//        double maxvalue=-1000000000;
//        for(int i=0;i<table[state].length;i++)
//        //for(int i=0;i<NumActions;i++)
//        {
//            if(table[state][i]>maxvalue)
//            {
//                maxvalue=table[state][i];
//                action=i;
//            }
//        }
//        return action;
//    }
//
//    public void loadData(File file)
//    {
//        BufferedReader r = null;
//        try
//        {
//            r = new BufferedReader(new FileReader(file));
//            for (int i = 0; i < State.Num; i++)
//                for (int j = 0; j < Action.Num; j++)
//                    table[i][j] = Double.parseDouble(r.readLine());
//        }
//        catch (IOException e)
//        {
//            System.out.println("IOException trying to open reader: " + e);
//            initialize();
//        }
//        catch (NumberFormatException e)
//        {
//            initialize();
//        }
//        catch(NullPointerException e){}
//        finally
//        {
//            try
//            {
//                if (r != null)
//                    r.close();
//            }
//            catch (IOException e)
//            {
//                System.out.println("IOException trying to close reader: " + e);
//            }
//        }
//    }
//
//    public void saveData(File file)
//    {
//        PrintStream w = null;
//        try
//        {
//            w = new PrintStream(new RobocodeFileOutputStream(file));
//            for (int i = 0; i < State.Num; i++)
//                for (int j = 0; j < Action.Num; j++)
//                    w.println(new Double(table[i][j]));
//
//            if (w.checkError())
//                System.out.println("Could not save the data!");
//            w.close();
//        }
//        catch (IOException e)
//        {
//            System.out.println("IOException trying to write: " + e);
//        }
//        finally
//        {
//            try
//            {
//                if (w != null)
//                    w.close();
//            }
//            catch (Exception e)
//            {
//                System.out.println("Exception trying to close witer: " + e);
//            }
//        }
//    }
//
//
//}
