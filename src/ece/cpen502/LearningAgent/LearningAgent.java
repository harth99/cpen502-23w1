//package ece.cpen502.LearningAgent;
//
//import ece.cpen502.LUT.LookUpTable;
//import ece.cpen502.Robot.Action;
//
//import java.util.ArrayList;
//
//public class LearningAgent {
//    public static final double LEARNING_RATE = 0.2;
//    // The higher the DISCOUNT_RATE (i.e. 1), immediate and future rewards are equally important
//    // The lower the DISCOUNT_RATE (i.e. 0) <- more focused on short term (immediate reward)
//    public static final double DISCOUNT_RATE = 0.8;
//    public static double EXPLORE_RATE = 0.6;
//    private int prevState = -1;
//    private int prevAction = -1;
//    private boolean firstRound = true;
//    private LookUpTable table;
//    public ArrayList<String> finalStates = new ArrayList<>();
//
//    public LearningAgent(LookUpTable table) {
//        this.table = table;
//    }
//
//    public void Learn(int currState, int currAction, double reward, boolean isOnPolicy, boolean isIntermidiateRewards) {
//        if(!isIntermidiateRewards) {
//            finalStates.add(currState+"-"+currAction);
//            return;
//        }
//        double newValue;
//        if(firstRound) {
//            firstRound = false;
//        } else {
//            double oldValue = table.getQValue(prevState, prevAction);
//            if(isOnPolicy) {
//                // Sarsa? (tuple)
//                newValue = oldValue + LEARNING_RATE * (reward + DISCOUNT_RATE * table.getQValue(currState, currAction)
//                        -oldValue);
//            } else {
//                // Off-policy Q learning
//                newValue = oldValue + LEARNING_RATE * (reward + DISCOUNT_RATE * table.getMaxValue(currState) - oldValue);
//            }
//            table.setQValue(prevState, prevAction, newValue);
//        }
//        prevState = currState;
//        prevAction = currAction;
//    }
//
//
//    public int getNextAction(int state) {
//        double random = Math.random();
//        if(random < EXPLORE_RATE) {
//            return (int)(Math.random() * Action.ROBOT_NUM_ACTIONS);
//        }
//        return table.getBestAction(state);
//    }
//
//    public void feedReward(double value) {
//        int n = finalStates.size();
//        double currValue, nextValue;
//        String[] strs = finalStates.get(n-1).split("-");
//        int state = Integer.valueOf(strs[0]);
//        int action = Integer.valueOf(strs[1]);
//        table.setQValue(state, action, value);
//        nextValue = value;
//        for(int i=n-2; i>=0; i--) {
//            strs = finalStates.get(i).split("-");
//            state = Integer.valueOf(strs[0]);
//            action = Integer.valueOf(strs[1]);
//            currValue = table.getQValue(state, action);
//            currValue += LEARNING_RATE * (DISCOUNT_RATE * nextValue - currValue);
//            table.setQValue(state, action, currValue);
//            nextValue = currValue;
//        }
//    }
//}


//import ece.cpen502.LUT.LookUpTable;
//import ece.cpen502.Robot.Action;
//
//public class LearningAgent {
//
//    public double learnRate=0.01;
//    public double discountRate=0.9;
//    int preState;
//    int preAction;
//    double preValue;
//    double newValue;
//
//    LookUpTable Qtable;
//    boolean first=true;
//
//    public LearningAgent(LookUpTable table)
//    {
//        this.Qtable=table;
//    }
//
//    public void Learn(int state,int action,double reward,boolean policy)
//    {
//        if(first)
//            first=false;
//        else
//        {
//            preValue=Qtable.getValue(preState,preAction);
//            //(off policy)Q learning
//            if(policy==true)
//            {
//                newValue=(1-learnRate)*preValue+learnRate*(reward+discountRate*Qtable.getMaxValue(state));
//
//            }
//            //on policy
//            else
//            {
//                newValue=(1-learnRate)*preValue+learnRate*(reward+discountRate*Qtable.getValue(state, action));
//                Qtable.visit[preState][preAction]++;
//            }
//
//            Qtable.setValue(preState,preAction,newValue);
//        }
//        preState=state;
//        preAction=action;
//
//    }
//    public int selectAction(int state,double explore)
//    {
//        int action;
//        double random=Math.random();
//        if(random<explore)
//        {
//            action=(int)(Math.random()* Action.Num);
//        }
//        else
//            action=Qtable.getBestAction(state);
//        return action;
//    }
//
//}
