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

            // Transition table: state -> targets
            Map<Integer, List<Integer>> transitions = new HashMap<>();

			// Read the file line by line
			while ((tempString = reader.readLine()) != null) {
                String line = tempString.trim();
                if (line.isEmpty()) continue;

                int comma = line.indexOf(',');
                if (comma < 0) {
                    System.out.println("[WARNING] malformed line (no comma): " + line);
                    continue;
                }

                String left = line.substring(0, comma).trim();
                String right = line.substring(comma + 1).trim();

                int state;
                try {
                    state = Integer.parseInt(left);
                } catch (NumberFormatException nfe) {
                    System.out.println("[WARNING] bad state id: " + left);
                    continue;
                }

                // Parse the right side 
                List<Integer> targets = new ArrayList<>();
                if (!right.equalsIgnoreCase("empty")) {
                    if (!right.startsWith("{") || !right.endsWith("}")) {
                        System.out.println("[WARN] expected {..} or 'empty': " + right);
                    } else {
                        String inside = right.substring(1, right.length() - 1).trim();
                        if (!inside.isEmpty()) {
                            String[] parts = inside.split(",");
                            for (String p: parts) {
                                String token = p.trim();
                                if (token.isEmpty()) continue;
                                try {
                                    int t = Integer.parseInt(token);
                                    if (!targets.contains(t)) targets.add(t);
                                } catch (NumberFormatException e) {
                                    System.out.println("[WARNING] bad target id: " + token);
                                }
                            }
                        }
                    }
                }

                // Merge targets
                List<Integer> current = transitions.get(state);
                if (current == null) {
                    transitions.put(state, targets);
                } else {
                    for (int t : targets) if (!current.contains(t)) current.add(t);
                }
            }


            // Compute E for each state 
            
            List<Integer> allStates = collectAllStates(transitions);
            Collections.sort(allStates);

            for (int q : allStates) {
                List<Integer> visited = new ArrayList<>();
                List<Integer> closure = eClosure(q, transitions, visited);
                Collections.sort(closure);
                System.out.println(formatClosure(q, closure));
            }
            
			// Close the file reader
			reader.close();
		}
	}

    // Compute E() using a List to keep track of 'visited' 
    private static List<Integer> eClosure(int state, Map<Integer, List<Integer>> transitions, List<Integer> visited) {
        if (!visited.contains(state)) {
            visited.add(state);
        } else {
            return visited;
        }

        List<Integer> outs = transitions.get(state);

        if (outs != null) {
            for (int next: outs) {
                if (!visited.contains(next)) {
                    eClosure(next, transitions, visited);
                }
            }
        }
        return visited;
    }


    private static List<Integer> collectAllStates(Map<Integer, List<Integer>> transitions) {
        List<Integer> all = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> e: transitions.entrySet()) {
            int src = e.getKey();
            if (!all.contains(src)) all.add(src);
            List<Integer> outs = e.getValue();

            if (outs != null) {
                for (int t : outs) {
                    if (!all.contains(t)) all.add(t);
                    if (!transitions.containsKey(t)) {
                        transitions.put(t, new ArrayList<Integer>());
                    }
                }
            }
        }

        return all;
    }

    private static String formatClosure(int state, List<Integer> closure) {
        StringBuilder sb = new StringBuilder();

        sb.append("E(").append(state).append(") = {");
        for (int i = 0; i < closure.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(closure.get(i));
        }
        sb.append("}");

        return sb.toString();
    }
}
