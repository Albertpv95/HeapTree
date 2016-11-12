import java.lang.reflect.Array;

/**
 *
 * MIT License
 * Copyright (c) 2016 Albert Pernía Vázquez
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 * Created by Albertpv on 21/7/16.
 */
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
    private static final double RESIZE_FACTOR    = 0.90;

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
    @SuppressWarnings("unchecked")
    public HeapTree() {

        nodes = (Node[]) Array.newInstance(Node.class, SIZE);

        maxHeapMode = true;
        elements = 0;
        current = 0;
    }

    /**
     * This method gets the element on the root and extracts it from the tree.
     *
     * @return the element on the root or null if the tree is empty.
     */
    public T extract() {

        T element = top();
        nodes[FIRST_ELEMENT] = null;

        // there was an element in the tree, now it is time to shift the array of nodes
        if (element != null && elements > 0) {

            elements--;

            // for the next insertions. If we delete this son, we need to be situated on the parent
            // putting the future next element where we are removing the current one
            current = getParentPos(elements);

            nodes[FIRST_ELEMENT] = nodes[elements];
            nodes[elements] = null;

            resort();
        }

        return element;
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
            nodes[current] = new Node(element);
            elements++;
        }
        else {

            int pos = getLeftSonPos(current);
            if (nodes[pos] == null) {           // still no leftSon,

                nodes[pos] = new Node(element);
                nodes[current].leftSon = nodes[pos];
                elements++;
            }
            else {

                pos = getRightSonPos(current);
                if (nodes[pos] == null) {           // this condition must be always true, but just in case
                    nodes[pos] = new Node(element);
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

        @SuppressWarnings("unchecked")
        Node[] copy = (Node[]) Array.newInstance(Node.class, SIZE);

        System.arraycopy(nodes, 0, copy, 0, elements);

        nodes = copy;
    }

    /**
     * This method is called when an element is extracted from the tree.
     *
     * When it happens, the last element inserted is moved to the root of the tree
     * having to resort the structure as a consequence.
     */
    private void resort() {
        boolean ordered = false;
        int current = FIRST_ELEMENT;

        while (!ordered) {

            int left = getLeftSonPos(current);
            int right = getRightSonPos(current);

            // there are not more elements in this branch of the tree, it is ordered then
            if (left >= elements) ordered = true;
            else {

                if (maxHeapMode) {

                    // if there is no element on the right or the element on the left is bigger
                    if (nodes[right] == null || nodes[left].element.compareTo(nodes[right].element) > 0) {

                        // the element on the left is bigger than the current one, we need to swap them
                        if (nodes[current].element.compareTo(nodes[left].element) < 0) {

                            T aux = nodes[current].element;
                            nodes[current].element = nodes[left].element;
                            nodes[left].element = aux;
                            current = left;
                        }
                        else ordered = true;
                    }
                    else if (nodes[left].element.compareTo(nodes[right].element) < 0) {

                        // the element on the right is bigger than the current one, we need to swap them
                        if (nodes[current].element.compareTo(nodes[right].element) < 0) {
                            T aux = nodes[current].element;
                            nodes[current].element = nodes[right].element;
                            nodes[right].element = aux;
                            current = right;
                        }
                        else ordered = true;
                    }
                }
                else {  // min heap case

                    // if there is no element on the right or the element on the left is smaller
                    if (nodes[right] == null || nodes[left].element.compareTo(nodes[right].element) < 0) {

                        // the element on the left is smaller than the current one, we need to swap them
                        if (nodes[current].element.compareTo(nodes[left].element) > 0) {

                            T aux = nodes[current].element;
                            nodes[current].element = nodes[left].element;
                            nodes[left].element = aux;
                            current = left;
                        }
                        else ordered = true;
                    }
                    else if (nodes[left].element.compareTo(nodes[right].element) > 0) {

                        // the element on the right is smaller than the current one, we need to swap them
                        if (nodes[current].element.compareTo(nodes[right].element) > 0) {

                            T aux = nodes[current].element;
                            nodes[current].element = nodes[right].element;
                            nodes[right].element = aux;
                            current = right;
                        }
                        else ordered = true;
                    }
                }
            }
        }
    }


    // improvement for the future: copy all into a new tree and set maxheapmode
    public void setMaxHeapMode(boolean maxHeapMode) throws IllegalAccessException {

        if (elements > 0) throw new IllegalAccessException("This method can only be called if the heap is empty.");

        if (maxHeapMode) this.maxHeapMode = true;
        else this.maxHeapMode = false;

    }

    // improvement for the future: copy all into a new tree and set minheapmode
    public void setMinHeapMode(boolean minHeapMode) throws IllegalAccessException {

        if (elements > 0) throw new IllegalAccessException("This method can only be called if the heap is empty.");

        if (minHeapMode) maxHeapMode = false;
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
    private void sort(int addedPos) {
        int parentPos = getParentPos(addedPos);
        boolean ordered = false;

        while (parentPos >= 0 && !ordered) {

            // the new element is smaller and we are in minHeapMode
            if (nodes[addedPos].element.compareTo(nodes[parentPos].element) < 0 && !maxHeapMode) {

                T aux = nodes[addedPos].element;
                nodes[addedPos].element = nodes[parentPos].element;
                nodes[parentPos].element = aux;
                addedPos = parentPos;
            }
            // the new element is bigger and we are in maxHeapMode
            else if (nodes[addedPos].element.compareTo(nodes[parentPos].element) > 0 && maxHeapMode) {

                T aux = nodes[addedPos].element;
                nodes[addedPos].element = nodes[parentPos].element;
                nodes[parentPos].element = aux;
                addedPos = parentPos;
            }
            else ordered = true;

            parentPos = getParentPos(addedPos);
        }
    }

    /**
     * @return the element in the root if it exists, null if not.
     */
    public T top() {

        if (nodes == null) return null;

        return nodes[FIRST_ELEMENT].element;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < elements; i++) {
            stringBuilder.append(nodes[i].element.toString());
            stringBuilder.append("\n");
        }


        return stringBuilder.toString();
    }

    private class Node {

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
            heapTree.setMinHeapMode(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        heapTree.put(4);
        heapTree.put(12);
        heapTree.put(14);
        heapTree.put(5);
        heapTree.put(8);
        heapTree.put(15);
        heapTree.put(30);
        heapTree.put(1);

        System.out.println("Size: " + heapTree.size());
        System.out.println(heapTree.toString());

        int extract = heapTree.extract();
        System.out.println("Extracted: " + extract);
        System.out.println(heapTree.toString());

        extract = heapTree.extract();
        System.out.println("Extracted: " + extract);
        System.out.println(heapTree.toString());

        extract = heapTree.extract();
        System.out.println("Extracted: " + extract);
        System.out.println(heapTree.toString());


        heapTree.put(6);
        heapTree.put(13);

        System.out.println(heapTree.toString());

        /*
        HeapTree<String> stringHeapTree = new HeapTree<>();

        stringHeapTree.put("Albert");
        stringHeapTree.put("Jorge");
        stringHeapTree.put("Carla");
        stringHeapTree.put("Samu");
        stringHeapTree.put("Marti");

        System.out.println(stringHeapTree.toString());

        String extracted = stringHeapTree.extract();
        System.out.println("Extracted: " + extracted);

        System.out.println(stringHeapTree.toString());

        */
    }

}
