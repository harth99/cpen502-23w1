package ece.cpen502.NN;

public class RunAssignment1 {
    public static void main(String[] args) {
        double[][] binaryTrainingSamples = new double[][]{
                {0, 0},
                {0, 1},
                {1, 0},
                {1, 1}};
        double[] binaryTargetLabel = new double[]{0, 1, 1, 0};
        double[][] bipolarTrainingSamples = new double[][]{
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1}};
        double[] bipolarTargetLabel = new double[]{-1, 1, 1, -1};

        // a) binary without momentum
        int TotalEpochs = 0;
        int numOfTraining = 200;
        NeuralNet XorBinary = new NeuralNet();
        XorBinary.CleanFile();
        for (int i = 0; i <= numOfTraining; i++) {
            XorBinary = new NeuralNet(2, 4, 1, 0.2, 0, 0, 1);
            TotalEpochs += XorBinary.TrainNN(binaryTrainingSamples, binaryTargetLabel);
            XorBinary.saveErrorVersusEpochs();
        }
        int averageEpochs = TotalEpochs / numOfTraining;
        System.out.println("Average epochs for binary take to reach a total error of less than 0.05 are: " + averageEpochs);

        // b) bipolar without momentum
        TotalEpochs = 0;
        NeuralNet XorBipolar;
        for (int i = 0; i < numOfTraining; i++) {
            XorBipolar = new NeuralNet(2, 4, 1, 0.2, 0, -1, 1);
            TotalEpochs += XorBipolar.TrainNN(bipolarTrainingSamples, bipolarTargetLabel);
            XorBipolar.saveErrorVersusEpochs();
        }
        averageEpochs = TotalEpochs / numOfTraining;
        System.out.println("Average epochs for bipolar take to reach a total error of less than 0.05 are: " + averageEpochs);


        // c)  binary momentum = 0.9
        TotalEpochs = 0;
        NeuralNet XorBinaryWithMomentum;
        for (int i = 0; i <= numOfTraining; i++) {
            XorBinaryWithMomentum = new NeuralNet(2, 4, 1, 0.2, 0.9, 0, 1);
            TotalEpochs += XorBinaryWithMomentum.TrainNN(binaryTrainingSamples, binaryTargetLabel);
            XorBinaryWithMomentum.saveErrorVersusEpochsWithMomentum();
        }
        averageEpochs = TotalEpochs / numOfTraining;
        System.out.println("With momentum = 0.9, Average epochs for binary take to reach a total error of less than 0.05 are: " + averageEpochs);


        // d) bipolar momentum = 0.9
        TotalEpochs = 0;
        NeuralNet XorBipolarWithMomentum;
        for (int i = 0; i < numOfTraining; i++) {
            XorBipolarWithMomentum = new NeuralNet(2, 4, 1, 0.2, 0.9, -1, 1);
            TotalEpochs += XorBipolarWithMomentum.TrainNN(bipolarTrainingSamples, bipolarTargetLabel);
            XorBipolarWithMomentum.saveErrorVersusEpochsWithMomentum();
        }
        averageEpochs = TotalEpochs / numOfTraining;
        System.out.println("With momentum = 0.9, Average epochs for bipolar take to reach a total error of less than 0.05 are: " + averageEpochs);
    }
}
