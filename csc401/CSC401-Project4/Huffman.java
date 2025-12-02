import java.util.*;

public class Huffman {

    static class Node implements Comparable<Node> {
        char character;
        int frequency;
        Node left, right;


        Node(char c, int f) {
            character = c;
            frequency = f;
        }

        Node(int f) {
            character = '\0';
            frequency = f;
        }

        boolean isLeaf() { return left == null && right == null; }

        @Override
        public int compareTo(Node other) {
            return this.frequency - other.frequency;
        }
    }


    private Node root;
    private Map<Character, String> codes;

    public Huffman() { codes = new HashMap<>(); }

    public Map<Character, Integer> calculateFrequencies(String text) {
        Map<Character, Integer> frequencies = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencies.put(c, frequencies.getOrDefault(c, 0) + 1);
        }
        return frequencies;
    }

    public Node buildTree(Map<Character, Integer> frequencies) {
        PriorityQueue<Node> pq = new PriorityQueue<>();

        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            pq.offer(new Node(entry.getKey(), entry.getValue()));
        }

        if (pq.size() == 1) {
            Node single = pq.poll();
            Node newRoot = new Node(single.frequency);
            newRoot.left = single;
            return newRoot;
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node(left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            pq.offer(parent);
        }

        return pq.poll();
    }

    private void generateCodes(Node node, String code) {
        if (node == null) return;
        if (node.isLeaf()) {
            codes.put(node.character, code.isEmpty() ? "0" : code);
            return;
        }

        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    }


    private String encode(String text) {
        Map<Character, Integer> frequencies = calculateFrequencies(text);
        root = buildTree(frequencies);
        codes.clear();
        generateCodes(root, "");

        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) {
            encoded.append(codes.get(c));
        }
        return encoded.toString();
    }

    public String decode(String encoded) {
        StringBuilder decoded = new StringBuilder();
        Node current = root;

        for (char bit : encoded.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;

            if (current.isLeaf()) {
                decoded.append(current.character);
                current = root;
            }
        }
        return decoded.toString();
    }

    public double getCompressionRatio(String original, String encoded) {
        int originalBits = original.length() * 8;
        int encodedBits = encoded.length();
        return (1.0 - (double)encodedBits / originalBits) * 100;
    }

     public static void test1000WordText() {
        String text = "The history of computer science began long before the modern discipline " +
                     "of computer science, usually appearing in forms like mathematics or physics. " +
                     "Developments in previous centuries alluded to the discipline that we now know " +
                     "as computer science. This progression, from mechanical inventions and mathematical " +
                     "theories towards modern computer concepts and machines, led to the development of " +
                     "a major academic field and the basis of a massive worldwide industry. " +
                     "The earliest known tool for use in computation was the abacus, developed in the " +
                     "period between 2700 and 2300 BCE in Sumer. The Sumerians abacus consisted of a " +
                     "table of successive columns which delimited the successive orders of magnitude of " +
                     "their sexagesimal number system. Its original style of usage was by lines drawn in " +
                     "sand with pebbles. Abaci of a more modern design are still used as calculation tools today. " +
                     "In 1837, Charles Babbage first described his Analytical Engine which is accepted as " +
                     "the first design for a modern computer. The analytical engine had expandable memory, " +
                     "an arithmetic unit, and logic processing capabilities able to interpret a programming " +
                     "language with loops and conditional branching. Although never built, the design has " +
                     "been studied extensively and is understood to be Turing complete. The analytical engine " +
                     "would have been a digital computer. Alan Turing is widely considered to be the father " +
                     "of theoretical computer science and artificial intelligence. During World War II, Turing " +
                     "worked for the Government Code and Cypher School at Bletchley Park, Britain's codebreaking " +
                     "centre that produced Ultra intelligence. For a time he led Hut 8, the section that was " +
                     "responsible for German naval cryptanalysis. Here, he devised a number of techniques for " +
                     "speeding the breaking of German ciphers, including improvements to the pre-war Polish bombe " +
                     "method and an electromechanical machine that could find settings for the Enigma machine. " +
                     "The first electronic digital computer was developed in the period from 1937 to 1942 at Iowa " +
                     "State College by John Atanasoff. The Atanasoff Berry Computer used vacuum tubes and binary " +
                     "arithmetic. In 1945, John von Neumann wrote First Draft of a Report on the EDVAC which " +
                     "described a stored-program computer design where data and programs were stored together. " +
                     "This design became known as the von Neumann architecture and formed the basis for most " +
                     "modern computers. Programming languages have evolved dramatically over the decades. The " +
                     "first high-level programming language was Plankalkuel, designed by Konrad Zuse in the 1940s " +
                     "but not published until 1972. The first widely used high-level language was Fortran, " +
                     "developed by a team at IBM led by John Backus, and released in 1957. Since then, thousands " +
                     "of programming languages have been created, including C, Python, Java, and JavaScript, each " +
                     "designed to solve different types of problems or to make programming more accessible to " +
                     "different groups of people.";
        
        Huffman huffman = new Huffman();
        String encoded = huffman.encode(text);
        String decoded = huffman.decode(encoded);
        
        System.out.println("\n=== TEST 2: 1000-WORD TEXT  ===");
        System.out.println("Word count: " + text.split("\\s+").length);
        System.out.println("Original size: " + (text.length() * 8) + " bits");
        System.out.println("Compressed size: " + encoded.length() + " bits");
        System.out.printf("Compression ratio: %.2f%%\n", huffman.getCompressionRatio(text, encoded));
        System.out.println("Decoding successful: " + text.equals(decoded));
    }

    public static void testFrequencySensitivity() {
        String text = "the quick brown fox jumps over the lazy dog ".repeat(20);

        // Actual frequencies 
        Huffman huffmanActual = new Huffman();
        String encodedActual = huffmanActual.encode(text);
        double ratioActual = huffmanActual.getCompressionRatio(text, encodedActual);

        // Standard approximations
        Map<Character, Integer> standardFreq = new HashMap<>();
        standardFreq.put('e', 127); standardFreq.put('t', 91); standardFreq.put('a', 82);
        standardFreq.put('o', 75); standardFreq.put('i', 70); standardFreq.put('n', 67);
        standardFreq.put('s', 63); standardFreq.put('h', 61); standardFreq.put('r', 60);
        standardFreq.put(' ', 183);
        for (char c : text.toCharArray()) {
            if (!standardFreq.containsKey(c)) {
                standardFreq.put(c, 1);
            }
        }

        Huffman huffmanStandard = new Huffman();
        huffmanStandard.root = huffmanStandard.buildTree(standardFreq);
        huffmanStandard.codes.clear();
        huffmanStandard.generateCodes(huffmanStandard.root, "");

        StringBuilder encodedStandard = new StringBuilder();
        for (char c: text.toCharArray()) {
            String code = huffmanStandard.codes.get(c);
            encodedStandard.append(code != null ? code : "00000000");
        }
        double ratioStandard = huffmanStandard.getCompressionRatio(text, encodedStandard.toString());

        System.out.println("\n=== TEST 3: ACTUAL VS STANDARD FREQUENCIES ===");
        System.out.printf("Compression with actual frequencies: %.2f%%\n", ratioActual);
        System.out.printf("Compression with standard frequencies: %.2f%%\n", ratioStandard);
        System.out.printf("Difference: %.2f%%\n", Math.abs(ratioActual - ratioStandard));
    }

    public static void testCompressionRange() {
        String[] texts = {
            ("Computer science spans theoretical disciplines to applied disciplines. " +
             "Algorithms and data structures are central to computer science. ").repeat(40),
            ("the the the and and and for for for with with with ").repeat(50),
            ("Pack my box with five dozen liquor jugs. Quick brown fox jumps. ").repeat(30)
        };
        
        System.out.println("\n=== TEST 4: COMPRESSION RANGE ===");
        double min = 100, max = 0;
        
        for (int i = 0; i < texts.length; i++) {
            Huffman huffman = new Huffman();
            String encoded = huffman.encode(texts[i]);
            double ratio = huffman.getCompressionRatio(texts[i], encoded);
            
            System.out.printf("Text %d compression: %.2f%%\n", i + 1, ratio);
            min = Math.min(min, ratio);
            max = Math.max(max, ratio);
        }
        
        System.out.printf("Compression range: %.2f%% - %.2f%%\n", min, max);
    }
    


    public static void testBasicEncodeDecode() {
        String text = "hello world";
        
        Huffman huffman = new Huffman();
        String encoded = huffman.encode(text);
        String decoded = huffman.decode(encoded);
        
        System.out.println("\n=== TEST 1: BASIC ENCODE/DECODE ===");
        System.out.println("Original: " + text);
        System.out.println("Encoded: " + encoded);
        System.out.println("Decoded: " + decoded);
        System.out.println("Success: " + text.equals(decoded));
        System.out.printf("Compression ratio: %.2f%%\n", huffman.getCompressionRatio(text, encoded));
    }


    public static void main(String[] args) {
        System.out.println("CSC401 PROJECT 4");
        System.out.println("=".repeat(30));

        testBasicEncodeDecode();
        test1000WordText();
        testFrequencySensitivity();
        testCompressionRange();

        System.out.println();
        System.out.println("=".repeat(30));
        System.out.println("ALL REQUIREMENTS TESTED");
    }
}
