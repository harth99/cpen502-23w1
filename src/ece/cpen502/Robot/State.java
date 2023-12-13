package ece.cpen502.Robot;

//public class State {
//    public static final double TOTAL_ANGLE = 360.0;
//    public static final double CIRCLE = Math.PI * 2;
//    public static int NumStates;
//    public static final int NUM_DISTANCE = 10;
//    public static final int NUM_BEARING = 4;
//    public static final int NUM_HEADING = 4;
//    public static final int NUM_HIT_BY_BULLETS = 2;
//    public static final int NUM_HIT_WALL = 2;
//    public static final int NUM_ENERGY = 5;
//    public static int states[][][][][][];
//
//    // Quantization ...
//    static {
//        states = new int[NUM_DISTANCE][NUM_BEARING][NUM_HEADING][NUM_HIT_BY_BULLETS][NUM_HIT_WALL][NUM_ENERGY];
//        int cnt = 0;
//        for(int a=0; a<NUM_DISTANCE; a++) {
//            for(int b=0; b<NUM_BEARING; b++) {
//                for(int c=0; c<NUM_HEADING; c++) {
//                    for(int d=0; d<NUM_HIT_BY_BULLETS; d++) {
//                        for(int e=0; e<NUM_HIT_BY_BULLETS; e++) {
//                            for(int f=0; f<NUM_ENERGY; f++) {
//                                states[a][b][c][d][e][f] = cnt++;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        NumStates = cnt;
//    }
//
//    public static int getDistance(double distance) {
//        int res = (int)(distance / 100.0);
//        return Math.min(NUM_DISTANCE-1, res);
//    }
//
//    public static int getHeading(double heading) {
//        double angle = TOTAL_ANGLE / NUM_HEADING;
//        double newHeading = heading+angle/2;
//        while (newHeading > TOTAL_ANGLE) {
//            newHeading-=TOTAL_ANGLE;
//        }
//        return (int)(newHeading/angle);
//    }
//
//    public static int getBearing(double bearing) {
//        double angle=CIRCLE / NUM_BEARING;
//        double newBearing = bearing;
//        if(bearing < 0) {
//            newBearing += CIRCLE;
//        }
//        newBearing += angle / 2;
//        if(newBearing > CIRCLE) {
//            newBearing = newBearing - CIRCLE;
//        }
//        return (int) (newBearing / angle);
//    }
//
//    public static int getEnergyLevel(double energy) {
//        double levels = 100 / NUM_ENERGY;
//        return Math.min((int)(energy/levels), NUM_ENERGY-1);
//    }
//}


//public class State {
//    //Enum type
//    public enum xPos {left, middle, right};
//    public enum yPos {top, center, bottom};
//    public enum energy {low, medium, high};
//    public enum distance {close, medium, far};
//    public enum Action {up, down, left, right, fire};
//
//    public static final int XPOS_NUM = xPos.values().length;
//    public static final int YPOS_NUM = yPos.values().length;
//    public static final int ENERGY_NUM = energy.values().length;
//    public static final int DISTANCE_NUM = distance.values().length;
//    public static final int ACTION_NUM = Action.values().length;
//
//    public xPos myX;
//    public yPos myY;
//    public energy myEnergy;
//    public distance distanceToEnemy;
//    public energy enemyEnergy;
//    public Action action;
//
//    public State(){
//        myX = xPos.left;
//        myY = yPos.top;
//        myEnergy = energy.high;
//        distanceToEnemy = distance.medium;
//        enemyEnergy = energy.high;
//        action = Action.down;
//    }
//
//    public State(xPos x, yPos y, energy e1, distance d, energy e2, Action a){
//        myX = x;
//        myY = y;
//        myEnergy = e1;
//        distanceToEnemy = d;
//        enemyEnergy = e2;
//        action = a;
//    }
//
//    //Define xPos
//    public static xPos getXPosLevel(double x){
//        if(x <= 300){
//            return xPos.left;
//        }
//        else if(x <= 500){
//            return xPos.middle;
//        }
//        else{
//            return xPos.right;
//        }
//    }
//
//    //Define yPos
//    public static yPos getYPosLevel(double y){
//        if(y <= 200){
//            return yPos.bottom;
//        }
//        else if(y <= 500){
//            return yPos.center;
//        }
//        else{
//            return yPos.top;
//        }
//    }
//
//    //Define mEnergy
//    public static energy getEnergyLevel(double e){
//        if(e <= 33){
//            return energy.low;
//        }
//        else if(e <= 67){
//            return energy.medium;
//        }
//        else{
//            return energy.high;
//        }
//    }
//
//    //Define distance to enemy
//    public static distance getDistanceLevel(double d){
//        if(d <= 350){
//            return distance.close;
//        }
//        else if(d <= 650){
//            return distance.medium;
//        }
//        else{
//            return distance.far;
//        }
//    }
//}



public class State {

    public enum HP {low, medium, high};
    public enum DistanceToEnemy {close, medium, far};
    public enum DistanceToWall {close, medium, far};
    public enum Action {fire, forwardLeft, forwardRight, backwardLeft, backwardRight, forward, backward, left, right};
    public enum OperationalMode {scan, performAction};

}



//public class State {
//
//    public static int Num;
//    public static int NumDistance=10;
//    public static int NumBearing=4;
//    public static int NumHeading=4;
//    public static int NumHitWall=2;//hit or not
//    public static int NumHitByBullet=2;//hit or not
//
//    public static int MapState[][][][][];
//
//    static
//    {
//        MapState=new int[NumDistance][NumBearing][NumHeading][NumHitWall][NumHitByBullet];
//        int count=0;
//        for(int i=0;i<NumDistance;i++)
//            for(int j=0;j<NumBearing;j++)
//                for (int m=0;m<NumHeading;m++)
//                    for(int p=0;p<NumHitWall;p++)
//                        for(int q=0;q<NumHitByBullet;q++)
//
//                        {
//                            MapState[i][j][m][p][q]=count++;
//
//                        }
//        Num=count;
//    }
//
//    public static int getEnermyDistance(double value)
//    {
//        int distance=(int)(value/100);
//        if(distance>NumDistance-1)
//            distance=NumDistance-1;
//        return distance;
//
//    }
//    public static int getEnermyBearing(double bearing)
//    {
//        double  totalAngle=Math.PI*2;
//        if(bearing<0)
//            bearing=totalAngle+bearing;
//
//        double angle=totalAngle/NumBearing;
//        double newBearing=bearing+angle/2;
//        if(newBearing>totalAngle)
//            newBearing=newBearing-totalAngle;
//        return (int) (newBearing/angle);
//    }
//    public static int getHeading(double heading)
//    {
//        double totalAngle=360.0d;
//        double angle=totalAngle/NumHeading;
//        double newHeading=heading+angle/2;
//        if(newHeading>totalAngle)
//            newHeading-=totalAngle;
//        return(int)(newHeading/angle);
//    }
//}
