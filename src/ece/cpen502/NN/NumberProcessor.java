package ece.cpen502.NN;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class NumberProcessor {

    public static void main(String[] args) {
        String inputFile = "/Users/jiayuhuang/Desktop/CPEN502Projects/CPEN502A2/Data/NQ_m0.950000_lr0.001000_h15_10-44-54_15.365009.txt"; // Replace with your input file path
        String outputFile = "/Users/jiayuhuang/Desktop/CPEN502Projects/CPEN502A2/Data/RMS_NQ_m0.950000_lr0.001000_h15_10-44-54_15.365009.txt"; // Replace with your output file path

        try {
            List<String> lines = Files.readAllLines(Paths.get(inputFile));
            List<String> outputLines = new ArrayList<>();

            for (String line : lines) {
                try {
                    double number = Double.parseDouble(line);
                    double processedNumber = Math.sqrt(number / 960.0);
                    outputLines.add(String.valueOf(processedNumber));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }

            Files.write(Paths.get(outputFile), outputLines, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

