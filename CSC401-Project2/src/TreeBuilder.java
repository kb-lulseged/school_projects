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


    
    
    }

}
