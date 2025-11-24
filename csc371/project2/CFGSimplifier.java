import java.io.*;
import java.util.*;
import java.util.stream.*;

public class CFGSimplifier {

    private Map<String, List<String>> grammar;
    private List<String> variableOrder;
    private String startSymbol;

    public CFGSimplifier() {
        grammar = new LinkedHashMap<>();
        variableOrder = new ArrayList<>();
        startSymbol = null;
    }

    // Parse from input lines
    public void parse(List<String> lines) {
        grammar.clear();
        variableOrder.clear();
        startSymbol = null;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("-", 2);
            if (parts.length != 2) continue;

            String variable = parts[0].trim();
            String[] productions = parts[1].split("\\|");

            if (startSymbol == null) {
                startSymbol = variable;
            }

            if (!grammar.containsKey(variable)) {
                variableOrder.add(variable);
                grammar.put(variable, new ArrayList<>());
            }

            for (String prod: productions) {
                prod = prod.trim();
                grammar.get(variable).add(prod);
            }
        }
    }


    // Finding all nullables 
    private Set<String> findNullableVariables() {
        Set<String> nullable = new HashSet<>();

        // First pass
        for (String var : grammar.keySet()) {
            for (String prod : grammar.get(var)) {
                if (prod.equals("0")) {
                    nullable.add(var);
                    break;
                }
            }
        }

        // Fixed point iteration
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String var : grammar.keySet()) {
                if (nullable.contains(var)) continue;

                for (String prod : grammar.get(var)) {
                    if (isAllNullable(prod, nullable)) {
                        nullable.add(var);
                        changed = true;
                        break;
                    }
                }
            }
        }

        return nullable;
    }


    // Check if all nullable symbols 
    private boolean isAllNullable(String prod, Set<String> nullable) {
        if (prod.equals("0")) return true;

        List<String> symbols = parseProduction(prod);
        for (String sym : symbols) {
            if (!nullable.contains(sym)) {
                return false;
            }
        }
        return true;
    }


    // Parsing production into List
    private List<String> parseProduction(String prod) {
        List<String> symbols = new ArrayList<>();
        for (char c : prod.toCharArray()) {
            symbols.add(String.valueOf(c));
        }
        return symbols;
    }


    // Check if uppercase -> variable
    private boolean isVariable(String s) {
        return s.length() == 1 && Character.isUpperCase(s.charAt(0));
    }


    // Remove e-rules
    public void removeERules() {
        Set<String> nullable = findNullableVariables();
        Map<String, List<String>> newGrammar = new LinkedHashMap<>();

        for (String var : variableOrder) {
            Set<String> newProductions = new LinkedHashSet<>();

            for (String prod : grammar.get(var)) {
                if (prod.equals("0")) {
                    continue;
                }

                List<String> combinations = generateCombinations(prod, nullable);
                for (String comb : combinations) {
                    if (!comb.isEmpty() && !comb.equals(var)) {
                        newProductions.add(comb);
                    }
                }
            }

            newGrammar.put(var, new ArrayList<>(newProductions));
        }

        grammar = newGrammar;
    }

    // Generate combinations removing nullables
    private List<String> generateCombinations(String prod, Set<String> nullable) {
        List<String> symbols = parseProduction(prod);
        List<Integer> nullablePositions = new ArrayList<>();

        // Find positions of nullable 
        for (int i = 0; i < symbols.size(); i++) {
            if (nullable.contains(symbols.get(i))) {
                nullablePositions.add(i);
                }
        }

        List<String> result = new ArrayList<>();
        int numNullable = nullablePositions.size();

        // Generate 2^n combinations
        for (int mask = 0; mask < (1 << numNullable); mask++) {
            StringBuilder sb = new StringBuilder();
            Set<Integer> excludePositions = new HashSet<>();

            // Reverse so incrementing mask removes from right first 
            for (int i = 0; i < numNullable; i++) {
                if ((mask & (1 << i)) != 0) {
                    excludePositions.add(nullablePositions.get(numNullable - 1 - i));
                }
            }

            for (int i = 0; i < symbols.size(); i++) {
                if (!excludePositions.contains(i)) {
                    sb.append(symbols.get(i));
                }
            }

            String combinations = sb.toString();
            if (!combinations.isEmpty() && !result.contains(combinations)) {
                result.add(combinations);
            }
        }

       return result;
    }



    private Set<String> findGeneratingVariables() {
        Set<String> generating = new HashSet<>();

        boolean changed = true;
        while (changed) {
            changed = false;
            for (String var : grammar.keySet()) {
                if (generating.contains(var)) continue;

                for (String prod : grammar.get(var)) {
                    if (isGenerating(prod, generating)) {
                        generating.add(var);
                        changed = true;
                        break;
                    }
                }
            }
        }
        
        return generating;
    }

    private boolean isGenerating(String prod, Set<String> generating) {
        List<String> symbols = parseProduction(prod);
        for (String sym : symbols) {
            if (isVariable(sym) && !generating.contains(sym)) {
                return false;
            }
        }

        return true;
    }

    // Find reachable vars from start symbol 
    private Set<String> findReachableVariables() {
        Set<String> reachable = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        if (startSymbol != null) {
            reachable.add(startSymbol);
            queue.add(startSymbol);
        }

        while (!queue.isEmpty()) {
            String var = queue.poll();
            if (!grammar.containsKey(var)) continue;

            for (String prod : grammar.get(var)) {
                List<String> symbols = parseProduction(prod);
                for (String sym : symbols) {
                    if (isVariable(sym) && !reachable.contains(sym)) {
                        reachable.add(sym);
                        queue.add(sym);
                    }
                }
            }
        }

        return reachable;
    }


    // Remove useless rules 
    public void removeUselessRules() {
        
        // remove non-generating symbols
        Set<String> generating = findGeneratingVariables();

        Map<String, List<String>> tempGrammar = new LinkedHashMap<>();
        List<String> tempOrder = new ArrayList<>();

        for (String var : variableOrder) {
            if (!generating.contains(var)) continue;

            List<String> newProductions = new ArrayList<>();
            for (String prod : grammar.get(var)) {
                if (isGenerating(prod, generating)) {
                    newProductions.add(prod);
                }
            }

            if (!newProductions.isEmpty()) {
                tempGrammar.put(var, newProductions);
                tempOrder.add(var);
            }
        }

        grammar = tempGrammar;
        variableOrder = tempOrder;

        
        // remove non-reachable
        Set<String> reachable = findReachableVariables();

        Map<String, List<String>> finalGrammar = new LinkedHashMap<>();
        List<String> finalOrder = new ArrayList<>();

        for (String var : variableOrder) {
            if (!reachable.contains(var)) continue;

            List<String> newProductions = new ArrayList<>();
            for (String prod: grammar.get(var)) {
                boolean allReachable = true;
                List<String> symbols = parseProduction(prod);
                for (String sym : symbols) {
                    if (isVariable(sym) && !reachable.contains(sym)) {
                        allReachable = false;
                        break;
                    }
                }
                if (allReachable) {
                    newProductions.add(prod);
                }
            }

            if (!newProductions.isEmpty()) {
                finalGrammar.put(var, newProductions);
                finalOrder.add(var);
            }
        }

        grammar = finalGrammar;
        variableOrder = finalOrder;
    }



    public void simplify() {
        removeERules();
        removeUselessRules();
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String var : variableOrder) {
            if (!grammar.containsKey(var) || grammar.get(var).isEmpty()) continue;

            sb.append(var).append("-");
            sb.append(String.join("|", grammar.get(var)));
            sb.append("\n");
        }
        return sb.toString().trim();
    }



    public static void main(String[] args) throws IOException {
        // Create a File object pointing to the current project directory
        File dir = new File(".");

        // List all .txt files in the current directory, sort them
        // alphabetically, and collect into a List 
        List<String> files = Arrays.stream(dir.list((d, n) -> n.endsWith(".txt")))
            .sorted()
            .collect(Collectors.toList());

        // Loop through each .txt file 
        for (String inputFile : files) {
            System.out.println("====== " + inputFile + " ======");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();


            // Parse and simplify 
            CFGSimplifier simplifier = new CFGSimplifier();
            simplifier.parse(lines);
            simplifier.simplify();

            // Print 
            System.out.println(simplifier.toString());
            System.out.println();
        }
    }
}






    


    



