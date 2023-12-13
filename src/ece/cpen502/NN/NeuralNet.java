package ece.cpen502.NN;

import ece.cpen502.Interface.NeuralNetInterface;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class NeuralNet implements NeuralNetInterface {
    private static final double THRESHOLD = 0.05;
    private static final double EPOCH_THRESHOLD = 250;
    private int numInputs;
    private int numHidden;
    private int numOutput;
    private double learningRate;
    private double momentum;
    private int argA;
    private int argB;
    private double[] inputNeuronsActivations;
    private double[] hiddenNeuronsActivations;
    private double outputNeuronActivation;
    private double[][] inputLayerToHiddenLayerWeights;
    private double[] hiddenLayerToOutputLayerWeights;
    private double[] hiddenNeuronsErrorSignals;
    private double outputNeuronErrorSignal;
    private double[][] inputLayerToHiddenLayerWeightsChange;
    private double[] hiddenLayerToOutputLayerWeightsChange;
    private boolean isBinary;
    private int epoch;
    private double outputDifference;
    private List<String> totalErrorList;


    public NeuralNet(int numInputs, int numHidden, int numOutput, double learningRate, double momentum, int argA, int argB) {
        // Number of neurons in each layer
        this.numInputs = numInputs;
        this.numHidden = numHidden;
        this.numOutput = numOutput;

        // Hyper-parameters
        this.learningRate = learningRate;
        this.momentum = momentum;
        this.argA = argA;
        this.argB = argB;
        this.isBinary = this.argA + this.argB != 0;

        // Neurons activations
        inputNeuronsActivations = new double[this.numInputs + 1];
        hiddenNeuronsActivations = new double[this.numHidden + 1];
        outputNeuronActivation = 0.0;

        // Weights
        inputLayerToHiddenLayerWeights = new double[this.numInputs + 1][this.numHidden];
        hiddenLayerToOutputLayerWeights = new double[this.numHidden + 1];

        // Error signals
        hiddenNeuronsErrorSignals = new double[this.numHidden];
        outputNeuronErrorSignal = 0.0;

        // Weight changes
        inputLayerToHiddenLayerWeightsChange = new double[numInputs + 1][numHidden];
        hiddenLayerToOutputLayerWeightsChange = new double[numHidden + 1];

        // Initialize weights
        initializeWeights();

        // Total error
        totalErrorList = new LinkedList<>();
    }

    public NeuralNet() {
    }


    @Override
    public double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    @Override
    public double customSigmoid(double x) {
        return (2) / (1 + Math.exp(-x)) - 1;
    }

    @Override
    public void initializeWeights() {
        for (int i = 0; i < inputLayerToHiddenLayerWeights.length; i++) {
            for (int j = 0; j < inputLayerToHiddenLayerWeights[i].length; j++) {
                inputLayerToHiddenLayerWeights[i][j] = Math.random() - 0.5;
            }
        }
        for (int i = 0; i < hiddenLayerToOutputLayerWeights.length; i++) {
            hiddenLayerToOutputLayerWeights[i] = Math.random() - 0.5;
        }
    }

    @Override
    public void zeroWeights() {
    }

    private void addBias(double[] inputArray) {
        for (int i = 0; i < inputArray.length; i++) {
            this.inputNeuronsActivations[i] = inputArray[i];
        }
        this.inputNeuronsActivations[numInputs] = bias;
        this.hiddenNeuronsActivations[numHidden] = bias;
    }

    private void feedForward(double[] inputData) {
        this.addBias(inputData);
        calculateHiddenNeuronsActivations();
        calculateOutputNeuronActivation();
    }

    private void calculateHiddenNeuronsActivations() {
        for (int j = 0; j < numHidden; j++) {
            for (int i = 0; i < numInputs + 1; i++) {
                hiddenNeuronsActivations[j] += inputLayerToHiddenLayerWeights[i][j] * inputNeuronsActivations[i];
            }
            if (isBinary) {
                hiddenNeuronsActivations[j] = this.sigmoid(hiddenNeuronsActivations[j]);
            } else {
                hiddenNeuronsActivations[j] = this.customSigmoid(hiddenNeuronsActivations[j]);
            }
        }
    }

    private void calculateOutputNeuronActivation() {
        for (int i = 0; i < numHidden + 1; i++) {
            outputNeuronActivation += hiddenLayerToOutputLayerWeights[i] * hiddenNeuronsActivations[i];
        }
        if (isBinary) {
            outputNeuronActivation = this.sigmoid(outputNeuronActivation);
        } else {
            outputNeuronActivation = this.customSigmoid(outputNeuronActivation);
        }
    }

    private void backProp() {
        calculateOutputNeuronError(isBinary);
        updateHiddenLayerToOutputLayerWeights();
        calculateHiddenNeuronsError(isBinary);
        updateInputLayerToHiddenLayerWeights();
    }

    private void calculateOutputNeuronError(boolean isBinary) {
        // error signal for output neuron
        if (isBinary) {
            outputNeuronErrorSignal = outputNeuronActivation * (1 - outputNeuronActivation) * outputDifference;
        } else {
            outputNeuronErrorSignal = 0.5 * (1 - Math.pow(outputNeuronActivation, 2)) * outputDifference;
        }
    }

    private void calculateHiddenNeuronsError(boolean isBinary) {
        // error signal for hidden neurons
        for (int i = 0; i < numHidden; i++) {
            hiddenNeuronsErrorSignals[i] = 0;
            hiddenNeuronsErrorSignals[i] = hiddenNeuronsErrorSignals[i] + hiddenLayerToOutputLayerWeights[i] * outputNeuronErrorSignal;
            if (isBinary) {
                hiddenNeuronsErrorSignals[i] = hiddenNeuronsActivations[i] * (1 - hiddenNeuronsActivations[i]) * hiddenNeuronsErrorSignals[i];
            } else {
                hiddenNeuronsErrorSignals[i] = (0.5 * (1 - Math.pow(hiddenNeuronsActivations[i], 2))) * hiddenNeuronsErrorSignals[i];
            }
        }
    }

    private void updateHiddenLayerToOutputLayerWeights() {
        // update weights with (or without) momentum for hidden layer to output layer weights
        for (int i = 0; i < numHidden + 1; i++) {
            hiddenLayerToOutputLayerWeightsChange[i] = momentum * hiddenLayerToOutputLayerWeightsChange[i] + learningRate * outputNeuronErrorSignal * hiddenNeuronsActivations[i];
            hiddenLayerToOutputLayerWeights[i] += hiddenLayerToOutputLayerWeightsChange[i];
        }
    }

    private void updateInputLayerToHiddenLayerWeights() {
        for (int i = 0; i < numHidden; i++) {
            for (int j = 0; j < numInputs + 1; j++) {
                inputLayerToHiddenLayerWeightsChange[j][i] = momentum * inputLayerToHiddenLayerWeightsChange[j][i] + learningRate * hiddenNeuronsErrorSignals[i] * inputNeuronsActivations[j];
                inputLayerToHiddenLayerWeights[j][i] += inputLayerToHiddenLayerWeightsChange[j][i];
            }
        }
    }

    public int TrainNN(double[][] trainingSamples, double[] targetLabel) {
        System.out.println("Start training...");
        this.totalErrorList.clear();
        this.epoch = 0;
        double totalError = 0.0;
        do {
            for (int i = 0; i < trainingSamples.length; i++) {
                this.feedForward(trainingSamples[i]);
                outputDifference = targetLabel[i] - outputNeuronActivation;
                totalError += Math.pow(outputDifference, 2);
                this.backProp();
            }
            totalError = totalError / 2;
            totalErrorList.add(String.valueOf(totalError));
            epoch++;
        } while (totalError > THRESHOLD);
        return epoch;
    }

    public double trainByLUT(String filePath) {
        System.out.println("Entered trainByLUT, initializing training data and targets...");
        double totalError;
//        int epochs;
        int maxNumberOfStates = 3 * 3 * 3 * 3 * 9;
        int numberOfStates = 5;

        // NN architecture, Single Q as output, where both States and Actions are inputs
        double[][] trainingDataLUT = new double[maxNumberOfStates][numberOfStates];
        double[] targetLabel = new double[maxNumberOfStates];

        // Initialize the LUT content and convert it to training data and target output
        loadLUTContent(trainingDataLUT, targetLabel, filePath);
        System.out.println("Finished loading LUT content...");

        // Normalize [-1, 1]
        normalizeLUTQValTarget(targetLabel);
        System.out.println("Finished normalization...");

//        epochs = TrainNN(trainingDataLUT, targetLabel);
        totalError = trainByLUTHelper(trainingDataLUT, targetLabel);

        String fileName = String.format("/Users/jiayuhuang/Desktop/CPEN502Projects/CPEN502A2/Data/%s_m%f_lr%f_h%d_%s_%f.txt",
                "NQ",
                this.momentum,
                this.learningRate,
                this.numHidden,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss")),
                totalError);

        System.out.println("PRINTING FILENAME: " + fileName);

        saveLUTNNResult(fileName);

        return totalError;   // stub
    }

    public double trainByLUTHelper(double[][] trainingSamples, double[] targetLabel) {
        System.out.println("Start training...");
        this.totalErrorList.clear();
        this.epoch = 0;
        double totalError = 0.0;
        do {
            if (epoch % 50 == 0) {
                System.out.println("Finished " + epoch + " epochs!!");
            }
            for (int i = 0; i < trainingSamples.length; i++) {
                this.feedForward(trainingSamples[i]);
                outputDifference = targetLabel[i] - outputNeuronActivation;
                totalError += Math.pow(outputDifference, 2);
                this.backProp();
            }
            totalError = totalError / 2;
            totalErrorList.add(String.valueOf(totalError));
            epoch++;
        } while (epoch <= EPOCH_THRESHOLD);
        return totalError;
    }


    public void normalizeLUTQValTarget(double[] targetLabel) {
        System.out.println("Entered normalization...");
        double minQ = Double.MAX_VALUE;
        double maxQ = Double.MIN_VALUE;

        for (int i = 0; i < targetLabel.length; i++) {
            if (targetLabel[i] > maxQ) {
                maxQ = targetLabel[i];
            }
            if (targetLabel[i] < minQ) {
                minQ = targetLabel[i];
            }
        }

        for (int i = 0; i < targetLabel.length; i++) {
            targetLabel[i] = (targetLabel[i] - minQ) * 2 / (maxQ - minQ) - 1;
        }
    }

    // Load LUT Q-values from A2
    public void loadLUTContent(double[][] trainingDataLUT, double[] targetLabel, String filePath) {
        // Generated by ChatGPT
        System.out.println("Loading LUT contents...");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int lineIndex = 0;

            while ((line = reader.readLine()) != null && lineIndex < trainingDataLUT.length) {
                String[] parts = line.split(",");

                for (int i = 0; i < trainingDataLUT[0].length; i++) {
                    trainingDataLUT[lineIndex][i] = Double.parseDouble(parts[i]);
                }

                // The 6th element is the target Q-value
                targetLabel[lineIndex] = Double.parseDouble(parts[5]);
                lineIndex++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void CleanFile() {
        File file = new File("./Data");
        if (file.isDirectory()) {
            String[] eachFilePath = file.list();
            if (eachFilePath != null) {
                for (String path : eachFilePath) {
                    File eachFile = new File(file.getAbsoluteFile() + "/" + path);
                    eachFile.delete();
                }
            }
        }
    }

    public void saveErrorVersusEpochs() {
        try {
            String dataType = isBinary ? "Binary" : "Bipolar";
            FileWriter fWriter = new FileWriter("./Data/ErrorVsEpochs-" + dataType + "-NumEpoch-" + epoch + ".txt");
            for (String error : totalErrorList) {
                fWriter.write(error + "\n");
            }
            fWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveErrorVersusEpochsWithMomentum() {
        try {
            String dataType = isBinary ? "Binary" : "Bipolar";
            FileWriter fWriter = new FileWriter("./Data/ErrorVsEpochsWithMomentum-" + dataType + "-NumEpoch-" + epoch + ".txt");
            for (String error : totalErrorList) {
                fWriter.write(error + "\n");
            }
            fWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveLUTNNResult(String fileName) {
        System.out.println("Saving results...");
        try {
            FileWriter fWriter = new FileWriter(fileName);
            for (String error : totalErrorList) {
                fWriter.write(error + "\n");
            }
            fWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
