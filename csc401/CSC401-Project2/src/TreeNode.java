public class TreeNode {
    private int value;
    private TreeNode left; 
    private TreeNode right;

    public TreeNode(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }

    public int getValue() { return this.value; }
    public TreeNode getLeft() { return this.left; }
    public TreeNode getRight() { return this.right; }
    public void setValue(int value) { this.value = value; }
    public void setLeft(TreeNode left) { this.left = left; }
    public void setRight(TreeNode right) { this.right = right; }

    @Override
    public String toString() {
        return "Node(" + value + ")"; 
    }
}

