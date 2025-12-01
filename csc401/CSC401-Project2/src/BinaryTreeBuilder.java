import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;

public class BinaryTreeBuilder {

    public static TreeNode buildBinaryTree(int inOrder[], int postOrder[]) {

        // empty
        if (inOrder.length == 0 || postOrder.length == 0) {
            return null;
        }

        // not the same length
        if (inOrder.length != postOrder.length) {
            return null;
        }

        // makes sure the [] are the same
        Set<Integer> inOrderSet = new HashSet<>();
        Set<Integer> postOrderSet = new HashSet<>();

        for (int i = 0; i < inOrder.length; i++) {
            inOrderSet.add(inOrder[i]);
        }

        for (int i = 0; i < postOrder.length; i++) {
            postOrderSet.add(postOrder[i]);
        }

        if (!inOrderSet.equals(postOrderSet)) {
            return null;
        }

        int postOrderRoot = postOrder[postOrder.length - 1]; // root is the last value

        int inOrderRoot = -1; // place holder

        // find root in inOrder[]
        for (int i = 0; i < inOrder.length; i++) {
            if (inOrder[i] == postOrderRoot) {
                inOrderRoot = i;
                break;
            }
        }
        if (inOrderRoot == -1) {
            return null; // never found a match so root wasnt upadted
        }

        int[] leftTreeInOrder = Arrays.copyOfRange(inOrder, 0, inOrderRoot); // takes inorder makes copy starting at index 0 to root

        int[] rightTreeInOrder = Arrays.copyOfRange(inOrder, inOrderRoot + 1, inOrder.length); // takes inorder makes copy starting one past root to end

        int[] leftTreePostOrder = Arrays.copyOfRange(postOrder, 0, inOrderRoot); // left subtree from postorder (same size as left inorder) size as left inorder)

        int[] rightTreePostOrder = Arrays.copyOfRange(postOrder, inOrderRoot, postOrder.length - 1); // right subtree from postorder (exclude root at the very end)

        TreeNode root = new TreeNode(postOrderRoot);

        root.setLeft(buildBinaryTree(leftTreeInOrder, leftTreePostOrder));
        root.setRight(buildBinaryTree(rightTreeInOrder, rightTreePostOrder));

        return root;
    }

}
