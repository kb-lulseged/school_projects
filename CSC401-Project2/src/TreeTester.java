import java.util.*;

public class TreeTester {
    
    // Genereate inorder traversal 
    public static List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorderHelper(root, result);
        return result;
    }

    private static void inorderHelper(TreeNode node, List<Integer> result) {
        if (node == null) return;
        inorderHelper(node.getLeft(), result);
        result.add(node.getValue());
        inorderHelper(node.getRight(), result);
    }

    // Generate postorder traversal 
    public static List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postorderHelper(root, result);
        return result;
    }

    private static void postorderHelper(TreeNode node, List<Integer> result) {
        if (node == null) return;
        postorderHelper(node.getLeft(), result);
        postorderHelper(node.getRight(), result);
        result.add(node.getValue());
    }


    // Print Tree 
    public static void printTree(TreeNode root) {
        printTreeHelper(root, "", true);
    }   

    private static void printTreeHelper(TreeNode node, String prefix, boolean isTail) {
        if (node == null) {
            System.out.println(prefix + (isTail ? "└── ": "├── ") + "null");
            return;
        }   

        System.out.println(prefix + (isTail ? "└── ": "├── ") + node.getValue());

        if (node.getLeft() != null || node.getRight() != null) {
            if (node.getLeft() != null) {
                printTreeHelper(node.getLeft(), prefix + (isTail ? "    " : "|   "), false);
            } else {
                System.out.println(prefix + (isTail ? "    " : "|   ") + "├── null");
            }

            if (node.getRight() != null) {
                printTreeHelper(node.getRight(), prefix + (isTail ? "    " : "|   "), true);
            } else {
                System.out.println(prefix + (isTail ? "    " : "|   ") + "└── null");
            }
        }
    }

    // Generate valid tree test case
    public static int[][] generateValidTree(int n) {
        if (n == 0) return new int[][] {{}, {}};

        List<Integer> lables = new ArrayList<>();
        for (int i = 0; i < n; i++) lables.add(i);

        TreeNode root = buildRandomTree(lables);
        List<Integer> inorder = inorderTraversal(root);
        List<Integer> postorder = postorderTraversal(root);

        return new int[][] {
            inorder.stream().mapToInt(Integer::intValue).toArray(),
            postorder.stream().mapToInt(Integer::intValue).toArray(),
        };

    }

    private static TreeNode buildRandomTree(List<Integer> labels) {
        if (labels.isEmpty()) return null;
        if (labels.size() == 1) return new TreeNode(labels.get(0));

        Random rand = new Random();
        int rootIdx = rand.nextInt(labels.size());
        TreeNode root = new TreeNode(labels.get(rootIdx));

        List<Integer> leftLabels = new ArrayList<>(labels.subList(0, rootIdx));
        List<Integer> rightLabels = new ArrayList<>(labels.subList(rootIdx + 1, labels.size()));

        root.setLeft(buildRandomTree(leftLabels));
        root.setRight(buildRandomTree(rightLabels));

        return root;
    }

    // Run a single test
    public static void runTest(String testName, int[] inorder, int[] postorder) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("Test: " + testName);
        System.out.println("=".repeat(70));
        System.out.println("Inorder:   " + Arrays.toString(inorder));
        System.out.println("Postorder: " + Arrays.toString(postorder));

        long startTime = System.nanoTime();
        TreeNode result = TreeBuilder.buildTree(inorder, postorder);
        long endTime = System.nanoTime();

        double elapsedTime = (endTime - startTime) / 1_000_000.0;

        if (result == null) {
            System.out.println("Result: INVALID - No solution exists:");
            System.out.printf("Time: %.6f ms\n", elapsedTime);
            return;
        }
        
        System.out.println("Result: Valid tree constructed");

        if (inorder.length <= 10) {
            System.out.println("\nTree structure:");
            printTree(result);

            // Correctness
            List<Integer> verifyIn = inorderTraversal(result);
            List<Integer> verifyPost = postorderTraversal(result);

            int[] verifyInArray = verifyIn.stream().mapToInt(Integer::intValue).toArray();
            int[] verifyPostArray = verifyPost.stream().mapToInt(Integer::intValue).toArray();

            System.out.println("\nVerification:");
            System.out.println("  Inorder matches:   " + Arrays.equals(inorder, verifyPostArray));
            System.out.println("  Postorder matches: " + Arrays.equals(postorder, verifyPostArray));
        }

        System.out.printf("Time: %.6f ms\n", elapsedTime);
    }

    public static void main(String[] args) {
        System.out.println("BINARY TREE CONSTRUCTION FROM TRAVERSALS");

        // Empty Tree
        runTest("Empty Tree (n=0)", new int[]{}, new int[]{});

        runTest("No Solution - different elements", new int[]{0, 1, 2}, new int[]{0, 1, 3});

        runTest("No Solution - Different Lengths", new int[]{0,1,2}, new int[]{0,1});

        runTest("No Solution - Invalid Ordering", new int[]{0, 1, 2}, new int[]{2, 0, 1});

        runTest("Simple Valid tree (n=7)", new int[]{3, 1, 4, 0, 5, 2, 6}, new int[]{3, 4, 1, 5, 6, 2, 0});

        int[][] test20 = generateValidTree(20);
        runTest("Random Valid Tree (n=20)", test20[0], test20[1]);

        int[][] test50 = generateValidTree(50);
        runTest("Random Valid Tree (n=50)", test50[0], test50[1]);
        
        int[][] test100 = generateValidTree(100);
        runTest("Random Valid Tree (n=100)", test100[0], test100[1]);

        int[][] test200 = generateValidTree(200);
        runTest("Random Valid Tree (n=200)", test200[0], test200[1]);

        System.out.println("\n" + "=".repeat(70));
        System.out.println("ALL TESTS COMPLETED");
        System.out.println("=".repeat(70));

    }
    
    
}
