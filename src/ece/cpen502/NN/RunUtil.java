package ece.cpen502.NN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RunUtil {
    public static void main(String[] args) {
        String inputFile = "/Users/jiayuhuang/Desktop/CPEN502Projects/CPEN502A2/src/ece/cpen502/NN/lutcontent.txt";  // replace with your input file path
        String outputFile = "/Users/jiayuhuang/Desktop/CPEN502Projects/CPEN502A2/src/ece/cpen502/NN/output.txt";  // replace with your desired output file path

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 3) {
                    String formattedLine = formatLine(parts[0], parts[1], parts[2]);
                    writer.write(formattedLine);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatLine(String part1, String part2, String part3) {
        StringBuilder sb = new StringBuilder();
        for (char ch : part1.toCharArray()) {
            sb.append(ch).append(',');
        }
        sb.deleteCharAt(sb.length() - 1);  // Remove the last comma
        sb.append(',').append(part2).append(',').append(part3);
        return sb.toString();
    }
}
