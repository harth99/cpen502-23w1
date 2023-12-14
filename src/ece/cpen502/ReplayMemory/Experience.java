package ece.cpen502.ReplayMemory;

import ece.cpen502.Robot.Action;
import ece.cpen502.Robot.State;

public class Experience {
    private double reward;
    private State currState;
    private State prevState;
    private Action prevAction;

    public Experience(double reward, State currState, State prevState, Action prevAction) {
        this.reward = reward;
        this.currState = currState;
        this.prevState = prevState;
        this.prevAction = prevAction;
    }

    // Getter for reward
    public double getReward() {
        return reward;
    }

    // Setter for reward
    public void setReward(double reward) {
        this.reward = reward;
    }

    // Getter for currState
    public State getCurrState() {
        return currState;
    }

    // Setter for currState
    public void setCurrState(State currState) {
        this.currState = currState;
    }

    // Getter for prevState
    public State getPrevState() {
        return prevState;
    }

    // Setter for prevState
    public void setPrevState(State prevState) {
        this.prevState = prevState;
    }

    // Getter for prevAction
    public Action getPrevAction() {
        return prevAction;
    }

    // Setter for prevAction
    public void setPrevAction(Action prevAction) {
        this.prevAction = prevAction;
    }
}
