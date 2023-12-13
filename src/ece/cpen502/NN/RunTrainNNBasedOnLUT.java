package ece.cpen502.NN;

import java.util.Scanner;

public class RunTrainNNBasedOnLUT {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        double totalError;

        while (true) {
            int numInputNeuron = 5;
            int numOutputNeuron = 1;
            String filePath = "/Users/jiayuhuang/Desktop/CPEN502Projects/CPEN502A2/src/ece/cpen502/NN/lutcontent.txt";
            NeuralNet lutNN;

            System.out.println("Please enter the momentum: ");
            double momentum = Double.parseDouble(s.nextLine());
            System.out.println("You entered: " + momentum);

            System.out.println("Please enter the number of hidden neurons: ");
            int numHiddenNeuron = Integer.parseInt(s.nextLine());
            System.out.println("You entered: " + numHiddenNeuron);

            System.out.println("Please enter the learning rate: ");
            double learningRate = Double.parseDouble(s.nextLine());
            System.out.println("You entered: " + learningRate);

            lutNN = new NeuralNet(numInputNeuron, numHiddenNeuron, numOutputNeuron, learningRate, momentum, -1, 1);
            System.out.println("Running offline training NN using LUT content...");
            totalError = lutNN.trainByLUT(filePath);

            System.out.println("PRINTING FINAL NUMBER OF EPOCHS REACHING 5000... TOTAL ERROR: " + totalError);
        }
    }
}
