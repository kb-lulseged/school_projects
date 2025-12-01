
public class HuffmanNode implements Comparable<HuffmanNode> {
    char character;
    int frequency;
    HuffmanNode left;
    HuffmanNode right;

    // Consturctor for leaf node
    public HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    // Consturctor for internal node
    public HuffmanNode(int frequency) {
        this.character = '\0';
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    public boolean isLeaf() { return left == null && right == null; }

    @Override
    public int compareTo(HuffmanNode other) { return this.frequency - other.frequency; }

    @Override
    public String toString() {
        if (isLeaf()) {
            return "Leaf('" + character + "', freq=" + frequency + ")";
        }

        return "Internal (freq=" + frequency + ")";
    }
}

