package ece.cpen502.Interface;

public interface LUTInterface {

    /**
     * Constructor. You will need to define one in your implementation
     * @param argNumInputs The number of inputs in your input vector
     * @param argVariableFloor An array specifying the lowest value of each variable in the input vector
     * @param argVariableCeiling An array specifying the highest value of each of the variables in the input vector.
     * The ordr must match the order as reffered to in argVariableFloor.
     */
/*    public LookUpTable (
            int argNumInputs,
            int [] argVariableFloor,
            int [] argVariableCeiling
    );*/

    /**
     * Initialise the look up table to all zeros.
     */
    public void initialiseLUT();



}
