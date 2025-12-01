import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * This program automatically processes all `.txt` files in the current project
 * directory.
 * 
 * Purpose: By modifying and using this class, users do not need to manually
 * enter the paths or names of the files one by one. The program will
 * automatically detect all `.txt` files in the current project folder, read
 * their contents, and process them sequentially. This is especially useful for
 * batch-processing or testing multiple input files.
 */

public class Main {
	public static void main(String[] args) throws IOException {
		// Create a File object pointing to the current project directory
		File dir = new File(".");

		// List all .txt files in the current project directory, sort them
		// alphabetically, and collect into a List
		List<String> files = Arrays.stream(dir.list((d, n) -> n.endsWith(".txt"))).sorted()
				.collect(Collectors.toList());

		// Loop through each .txt file
		for (String inputFile : files) {
			System.out.println("=== " + inputFile + " ===");

			// Open the file for reading
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			String tempString = null;

			// Read the file line by line
			while ((tempString = reader.readLine()) != null) {

				/////////////////////////////////////////////////////////////////////
				// Create additional Java classes and/or functions for this project if necessary
				// and then invoke them here to handle the files one by one.
				// Feel free to modify this program to suit your specific needs. For example,
				// you may not require the following for loop that prints the contents of each
				// String array, this part can be customized or removed based on your goals.
				/////////////////////////////////////////////////////////////////////

				// Remove curly braces, trim spaces, and split the line by commas
				String[] data = tempString.replace("{", "").replace("}", "").trim().split(",");

				// Print each data element in the array separated by space
				for (int i = 0; i < data.length; i++)
					System.out.print(data[i] + " ");
				System.out.println(); // Print a newline after each line's data
			}

			// Close the file reader
			reader.close();
		}
	}
}
