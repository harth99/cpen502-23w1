package ece.cpen502.ReplayMemory;

import ece.cpen502.Robot.State;

public class Experience {
    private double reward;
    private State[] curState;
    private State[] prevState;
    private State.Action prevAction;
}
