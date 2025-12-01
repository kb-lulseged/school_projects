import java.util.*;

public class TreeBuilder {
    public static TreeNode buildTree(int [] inorder, int [] postorder) {
        if (inorder.length == 0 & postorder.length == 0) // Empty tree 
            return null;

        if (inorder.length != postorder.length) // length mistatch
            return null;

        if (!sameElements(inorder, postorder))
            return null;

        int rootValue = postorder[postorder.length - 1];

        int rootIndex = -1;
        for (int i = 0; i < inorder.length; i++) {
            if (inorder[i] == rootValue) {
                rootIndex = i;
                break;
            }
        }
        if (rootIndex == -1) {
            return null;
        }

        // Split Arrays and build tree
        int leftSize = rootIndex;
        int[] leftInorder = Arrays.copyOfRange(inorder, 0, rootIndex);
        int[] rightInorder = Arrays.copyOfRange(inorder, rootIndex + 1, inorder.length);
        int[] leftPostorder = Arrays.copyOfRange(postorder, 0, leftSize);
        int[] rightPostorder = Arrays.copyOfRange(postorder, leftSize, postorder.length - 1);

        TreeNode root = new TreeNode(rootValue);
        root.setLeft(buildTree(leftInorder, leftPostorder));
        root.setRight(buildTree(rightInorder, rightPostorder));

        return root;
    
    }

    // Helper
    private static boolean sameElements(int[] arr1, int[] arr2) {
        Set<Integer> set1 = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();

        for (int num : arr1) set1.add(num);
        for (int num : arr2) set2.add(num);

        return set1.equals(set2);
    }

}
