import java.util.*;
import java.io.*;

public class HuffmanCoding {
    private HuffmanNode root;
    private Map<Character, String> codes;
    private Map<String, Character> reverseCodes;

    public HuffmanCoding() {
        this.root = null;
        this.codes = new HashMap<>();
        this.reverseCodes = new HashMap<>();
    }

    public Map<Character, Integer> calculateFrequencies(String text) {
        Map<Character, Integer> frequencies = new HashMap<>();

        for (char c : text.toCharArray()) {
            frequencies.put(c, frequencies.getOrDefault(c, 0) + 1);
        }

        return frequencies;
    }

    
    public HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencies) {
        if (frequencies.isEmpty()) { return null; }
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

        // Creating leaf nodes 
        for (Map.Entry<Character, Integer> entry: frequencies:entrySet()) {
            HuffmanNode node = new HuffmanNode(entry.getKey(), entry.getValue());
            pq.offer(node);
        }

        if (pq.size() == 1) {
            HuffmanNode single = pq.poll();
            HuffmanNode newRoot = new HuffmanNode(single.frequency);
            newRoot.left = single;
            return newRoot;
        }

        // Build tree - Greedy style
        while (pq.size() > 1)) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();

            HuffmanNode merged = new HuffmanNode(left.frequency + right.frequency);
            merged.left = left;
            merged.right = right;

            pq.offer(merged);
        }

        return pq.poll();
    }


    public void generateCodes(HuffmanNode node, String code) {
        if (node == null) { return; }

        if (node.isLeaf()) {
            String finalCode = code.isEmpty() ? "0" : code;
            codes.put(node.character, finalCode);
            reverseCodes.put(finalCode, node.character);
            return;
        }

        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    
    }


    public String encode(String text) {
        if (text == null || text.isEmpty()) { return; }

        Map<Character, Integer> frequencies = calculateFrequencies(text);
        root = buildHuffmanTree(frequencies);

        codes.clear();
        reverseCodes.clear();
        generateCodes(root, "");

        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) {
            encoded.append(codes.get(c));
        }

        return encoded.toString();
    }


    public String decode(String encodedText) {
        if (encodedText == null || encodedText.isEmpty() || root == null) {
            return "";
        }

        StringBuilder decoded = new StringBuilder();
        HuffmanNode current = node;

        for (char bit : encodedText.toCharArray()) {
            if (bit == '0') 
                current = current.left;
            else 
                current = current.right;

            if (current.isLeaf()) {
                decoded.append(current.character);
                current = root;
            }
        }

        return decoded.toString();
    }

    
    public String decode(String encodedText, Map<Character, Integer> frequencies) {
        root = buildHuffmanTree(frequencies);
        codes.clear();
        reverseCodes.clear();
        generateCodes(root, "");

        return decode(encodedText);
    }


    public CompressionStats getCompressionStats(String originalText, String encodedText) {
        int originalBits = originalText.length() * 8;
        int encodedBits = encodedText.length();
        double compressionRatio = originalBits > 0 ? (1.0 - (double)encodedBits / originalBits) * 100 : 0.0;

        return new CompressionStats(
                originalText.length(),
                originalBits,
                encodedBits,
                compressionRatio,
                originalBits - encodedBits
                );
    }

    public void printCodes() {
        System.out.println("\nHuffman Codes: ");
        System.out.println("-".repeat(40));

        List<Map.Entry<Character, String>> sortedCodes = new ArrayList<>(codes.entrySet());
        sortedCodes.sort(Comparator.comparingInt(e -> e.getValue().length()));

        for (Map.Entry<Character, String> entry : sortedCodes) {
            char c = entry.getKey();
            String code = entry.getValue();
            String display;

            if (c == '\n') display = "\\n";
            else if (c == ' ') display = "SPACE";
            else if (c == '\t') display = "\\t";
            else display = String.valueOf(c);

            System.out.printf("'%s': %s (length: %d)%n", display, code, code.length());
        }
    }

    public Map<Character, String> getCodes() {
        return new HashMap<>(codes);
    }

    public HuffmanNode getRoot() {
        return root;
    }

    public void saveEncodedData(String filename, String encodedText, Map<Character, Integer> frequencies) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(encodedText);
            oos.writeObject(frequencies);
        }
    }

    @SuppressWarnings("unchecked")
    public Object[] loadEncodedData(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            String encodedText = (String) ois.readObject();
            Map<Character, Integer> frequencies = (Map<Character, Integer>) ois.readObject();

            return new Object[]{encodedText, frequencies};
        }
    }



    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("HUFFMAN CODING DEMONSTRATION");
        System.out.println("=".repeat(60));

        String sampleText = "this is an example for huffman encoding";

        HuffmanCoding huffman = new HuffmanCoding();
        String encoded = huffman.encode(sampleText);

        System.out.println("\nOriginal text: " + sampleText);
        System.out.println("Original length: " + sampleText.length() + " characters");

        huffman.printCodes();

        System.out.println("\nEncoded (first 100 bits): " + encoded.substring(0, Math.min(100, encoded.length())) + "...");
        System.out.println("Total encoded length: " + encoded.length() + " bits");

        CompressionStats stats = huffman.getCompressionStats(sampleText, encoded);
        System.out.println("\n" + "=".repeat(60));
        System.out.println("COMPRESSION STATISTICS");
        System.out.println("=".repeat(60));
        System.out.println(stats);


        // Test Decoding
        String decoded = huffman.decoded(encoded);
        System.out.println("\n" + "=".repeat(60));
        System.out.println("DECODING VERIFICATION");
        System.out.println("=".repeat(60));
        System.out.println("Decoded text: " + decoded);
        System.out.println("Decoding successful: " + decoded.equals(sampleText));

    }
}


