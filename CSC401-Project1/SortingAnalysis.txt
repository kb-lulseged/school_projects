import java.util.*;
import java.io.*;

public class SortingAnalysis {
    private static long comparisonCount = 0;
    private static long startTime, endTime;

    public static void sortAnalysis(int[] arr) {
        int n = arr.length;
        comparisonCount = 0;

        for (int i = 1; i < n; i++) {
            int v = arr[i];
            int j = i - 1;

            while (j >= 0) {
                comparisonCount++;
                if (arr[j] > v) {
                    arr[j + 1] = arr[j];
                    j--;
                } else {
                    break;
                }
           }
           arr[j+1] = v;
        }
    }      


    public static int[] generateRandomArray(int size) {
        Random rand = new Random();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(10000);
        }
        return arr;
    }


    public static void measurePerformace(int size, PrintWriter writer) {
        int[] arr = generateRandomArray(size);
        int[] arrCopy = arr.clone();

        startTime = System.nanoTime();
        sortAnalysis(arr);
        endTime = System.nanoTime();

        long time = (endTime - startTime) / 1000000;

        writer.println(size + "," + comparisonCount + "," + time);
        System.out.println("Size: " + size + ", Comparisons: " + comparisonCount + ", Time: " + time + "ms");

    }

    public static void main(String[] args) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("sorting_results.csv"));
            writer.println("ArraySize,Comparisons,TimeMillis");

            for (int size = 1000; size <= 9500; size += 500) {
                measurePerformace(size, writer);
            }

            writer.close();
            System.out.println("Results written to sorting_results.csv");

            System.out.println("\n--- Estimation for Array Size 10,000 ---");
            measurePerformace(10000, new PrintWriter(System.out));

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}



        




