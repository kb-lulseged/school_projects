import java.io.*;
import java.util.*;
import java.util.stream.*;

public class RegularOperations {
    public static void main(String[] args) throws IOException {
        File dir = new File(".");

        List<String> files = Arrays.stream(dir.list((d, n) -> n.endsWith(".txt"))).sorted().collect(Collectors.toList());

        Scanner scanner = new Scanner(System.in);
        int k = 0;
        try {
            System.out.print("Enter k value for A^k operation: ");
            System.out.flush();
            if (scanner.hasNextInt()) {
                k = scanner.nextInt();
            } else {
                System.out.println("Invalid input. Using default k=3");
                k = 3;
            }
        } catch (Exception e) {
            System.out.println("Error reading input. Using default k=3");
            k = 3;
        }

        for (String inputFile: files) {
            System.out.println("=== " + inputFile + " ===");
            List<String> languageA = new ArrayList<>();
            List<String> languageB = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line1 = reader.readLine();
            String line2 = reader.readLine();
            reader.close();

            // Parsing language A 
            if (line1 != null) {
                String[] dataA = line1.replace("{", "").replace("}", "").trim().split(",");
                for (String str : dataA) {
                    if (!str.trim().isEmpty()) {
                        languageA.add(str.trim());
                    }
                }
            }

            // Parsing language B 
            if (line2 != null) {
                String[] dataB = line2.replace("{", "").replace("}", "").trim().split(",");
                for (String str : dataB) {
                    if (!str.trim().isEmpty()) {
                        languageB.add(str.trim());
                    }
                }
            }


            // Operations
            List<String> union = performUnion(languageA, languageB);
            List<String> concatenation = performConcatenation(languageA, languageB);
            List<String> exponentiation = performExponentiation(languageA, k);


            System.out.println("AUB = " + formatLanguage(union));
            System.out.println("A◦B = " + formatLanguage(concatenation));
            System.out.println("A^" + k + " = " + formatLanguage(exponentiation));
            System.out.println();
        }

        scanner.close();
    }

    public static List<String> performUnion(List<String> A, List<String> B) {
        List<String> result = new ArrayList<>();

        for (String element: A) {
            if (!result.contains(element)) {
                result.add(element);
            }
        }

        for (String element : B) {
            if (!result.contains(element)) {
                result.add(element);
            }
        }

        Collections.sort(result);
        return result;
    }   
    
    public static List<String> performConcatenation(List<String> A, List<String> B) {
        List<String> result = new ArrayList<>();

        for (String x : A) {
            for (String y : B) {
                result.add(x + y);
            }
        }
        
        Collections.sort(result);
        return result;
    }

    public static List<String> performExponentiation(List<String> A, int k) {
        List<String> result = new ArrayList<>();

        if (k == 0) {
            result.add("");
            return result;
        } 

        if (k == 1) {
            result.addAll(A);
            Collections.sort(result);
            return result;
        }

        List<String> previousK = performExponentiation(A, k - 1);
        
        for (String prev : previousK) {
            for (String current : A) {
                result.add(prev + current);
            }
        }

        Collections.sort(result);
        return result;
    
    }


    public static String formatLanguage(List<String> language) {
        if (language.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        for (int i = 0; i < language.size(); i++) {
            if (language.get(i).isEmpty()) {
                sb.append("ε");
            } else {
                sb.append(language.get(i));
            }

            if (i < language.size() - 1) {
                sb.append(",");
            }
        }


        sb.append("}");
        return sb.toString();
    }
    
    

}
