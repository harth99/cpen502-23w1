package ece.cpen502.Robot;

import java.awt.geom.Point2D;

//public class Enemy {
//    public String name;
//    public double bearing;
//    public double heading;
//    public double changeHeading;
//    public double x, y;
//    public double distance, speed;
//    public long ctime;
//
//    public Enemy(String name) {
//        this.name = name;
//    }
//
//    public Point2D.Double getNextPosition(long gaussTime) {
//        double diff = gaussTime - ctime;
//        double nextX = x + Math.sin(heading) * speed * diff;
//        double nextY = y + Math.cos(heading) * speed * diff;
//        return new Point2D.Double(nextX, nextY);
//    }
//}


public class Enemy
{
    String name;
    public double bearing;
    public double heading;
    public long ctime;
    public double speed;
    public double x, y;
    public double distance;
    public double changeHeading;
    public double energy;

    //Calculate the new position of the robot
    public Point2D.Double guessPosition(long when)
    {
        //ctime: when our scan data was produced.
        //when: the time that we think the bullet will reach the target.
        //diff: is the difference between the two
        double diff = when - ctime;
        double newY, newX;
        newX = x + Math.sin(heading) * speed * diff;
        newY = y + Math.cos(heading) * speed * diff;

        return new Point2D.Double(newX, newY);
    }

    public double guessX(long when)
    {
        long diff = when - ctime;
        System.out.println(diff);
        return x+Math.sin(heading)*speed*diff;
    }

    public double guessY(long when)
    {
        long diff = when - ctime;
        return y+Math.cos(heading)*speed*diff;
    }

}
