

public class HeapTree <T extends Comparable<T>> {

    /**
     * An array of Nodes of the tree to perform a faster access during the insertions.
     */
    private Node[] nodes;

    /**
     * Current number of elements in the tree, not the number of nodes allocated.
     */
    private int elements;

    /**
     * Stores the current position where a new element must be inserted.
     */
    private int current;

    /**
     * A flag to determinate whether the heapTree is sorted
     * by max values or min values.
     */
    private boolean maxHeapMode;

    /**
     * Just a useless flag to understand the code better.
     */
    private static final int FIRST_ELEMENT  = 0;

    /**
     * A constant value to determinate when a HeapTree instance
     * must be resized to allocate a new maximum of elements.
     */
    private static final double RESIZE_FACTOR    = 0.80;

    /**
     * Initially, the heap tree can store 100 elements.
     * When the tree is 80% full, the heap tree is resized
     * for 100 more elements.
     */
    private static int SIZE     =   100;

    /**
     * When a resize is done, this is the number of new empty
     * nodes allocated in the tree plus the current size.
     */
    private static final int RESIZE_INCREMENT   = 100;


    /**
     * Creates a new HeapTree with 100 nodes of space allocated
     * and max mode enabled by default.
     */
    public HeapTree() {

        nodes = new Node[SIZE];
        for (int i = 0; i < SIZE; i++) nodes[i] = null;

        maxHeapMode = true;
        elements = 0;
        current = 0;
    }

    private int getLeftSonPos(int parentPos) {

        return (2*parentPos) + 1;
    }

    private int getRightSonPos(int parentPos) {

        return (2*parentPos) + 2;
    }

    private int getParentPos(int sonPos) {
        int parentPos;

        if (sonPos % 2 == 0) parentPos = (sonPos/2) - 1;
        else parentPos = sonPos/2;

        return parentPos;
    }

    /**
     * Adds a new element into the HeapTree in order.
     *
     * Also, if the % of elements is >= RESIZE_FACTOR the heapTree
     * is resized to allocate a bigger quantity of elements.
     *
     * @param element   The element to insert.
     */
    public void put(T element) {

        // first element case
        if (current == FIRST_ELEMENT && nodes[current] == null) {
            System.out.println("Adding root: " + element.toString());
            nodes[current] = new Node<>(element);
            elements++;
        }
        else {

            int pos = getLeftSonPos(current);
            if (nodes[pos] == null) {           // still no leftSon,

                System.out.println("Adding left: " + element.toString() + " in pos: " + pos);

                nodes[pos] = new Node<>(element);
                nodes[current].leftSon = nodes[pos];
                elements++;
            }
            else {

                pos = getRightSonPos(current);
                if (nodes[pos] == null) {           // this condition must be always true, but just in case
                    System.out.println("Adding right: " + element.toString() + " in pos: " + pos);
                    nodes[pos] = new Node<>(element);
                    nodes[current].rightSon = nodes[pos];
                    elements++;
                    current++;
                }
            }

            sort(pos);  // now we have to check if the element is in the right position (max's o min's)
        }

        // if 80% full
        if (resizeIsNeeded()) resizeTree();
    }

    /**
     * Checks if the tree is (RESIZE_FACTOR)% full.
     *
     * @return true if 80% full, false if not.
     *
     */
    private boolean resizeIsNeeded() {

        return ((double)(elements/SIZE)) >= RESIZE_FACTOR;
    }

    /**
     * Creates a new array of nodes copying all the elements of the current array of nodes
     * into the new one. Finally, the copy is assigned to the existing nodes reference.
     */
    private void resizeTree() {

        SIZE += RESIZE_INCREMENT;
        Node[] copy = new Node[SIZE];
        for (int i = 0; i < elements; i++) copy[i] = nodes[i];

        nodes = copy;
    }



    // improvement for the future: copy all into a new tree and set maxheapmode
    public void setMaxHeapMode(boolean maxHeapMode) throws IllegalAccessException {

        if (elements > 0) throw new IllegalAccessException("This method can only be called if the heap is empty.");

        if (maxHeapMode == true) this.maxHeapMode = true;
        else this.maxHeapMode = false;

    }

    // improvement for the future: copy all into a new tree and set minheapmode
    public void setMinHeapMode(boolean minHeapMode) throws IllegalAccessException {

        if (elements > 0) throw new IllegalAccessException("This method can only be called if the heap is empty.");

        if (minHeapMode == true) maxHeapMode = false;
        else maxHeapMode = true;
    }

    /**
     * @return The number of elements inside the tree.
     */
    public int size() {

        return elements;
    }

    /**
     * Puts the new element inserted in the correct position of the tree.
     *
     * @param addedPos the position of the new element inserted
     *
     * SuppressWarnings: the default warnings about casts that aren't checked can be ignored...
     */
    @SuppressWarnings("unchecked")
    private void sort(int addedPos) {
        int parentPos = getParentPos(addedPos);
        boolean ordered = false;

        while (parentPos >= 0 && !ordered) {

            // the new element is smaller and we are in minHeapMode
            if (nodes[addedPos].element.compareTo(nodes[parentPos].element) < 0 && !maxHeapMode) {

                T aux = (T) nodes[addedPos].element;
                nodes[addedPos].element = nodes[parentPos].element;
                nodes[parentPos].element = (T) aux;
                addedPos = parentPos;
            }
            // the new element is bigger and we are in maxHeapMode
            else if (nodes[addedPos].element.compareTo(nodes[parentPos].element) > 0 && maxHeapMode) {

                T aux = (T) nodes[addedPos].element;
                nodes[addedPos].element = nodes[parentPos].element;
                nodes[parentPos].element = (T) aux;
                addedPos = parentPos;
            }
            else ordered = true;

            parentPos = getParentPos(addedPos);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < elements; i++)
            stringBuilder.append(nodes[i].element.toString() + '\n');

        return stringBuilder.toString();
    }

    private class Node<T extends Comparable<T>> {

        T element;
        Node leftSon;
        Node rightSon;

        private Node(T element) {

            this.element = element;
            this.leftSon = this.rightSon = null;
        }

        private Node(T element, Node leftSon, Node rightSon) {

            this.element = element;
            this.leftSon = leftSon;
            this.rightSon = rightSon;
        }

    }

    public static void main(String[] args) {

        HeapTree<Integer> heapTree = new HeapTree<>();

        try {
            heapTree.setMinHeapMode(true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        heapTree.put(4);
        heapTree.put(12);
        System.out.println(heapTree.toString());
        heapTree.put(14);
        System.out.println(heapTree.toString());
        heapTree.put(5);
        System.out.println(heapTree.toString());
        heapTree.put(8);
        System.out.println(heapTree.toString());
        heapTree.put(15);
        System.out.println(heapTree.toString());
        heapTree.put(30);
        heapTree.put(1);

        System.out.println("Size: " + heapTree.size());
        System.out.println(heapTree.toString());


        HeapTree<String> stringHeapTree = new HeapTree<>();

        stringHeapTree.put("Albert");
        stringHeapTree.put("Jorge");
        stringHeapTree.put("Carla");
        stringHeapTree.put("Samu");
        stringHeapTree.put("Marti");

        System.out.println(stringHeapTree.toString());
    }

}
