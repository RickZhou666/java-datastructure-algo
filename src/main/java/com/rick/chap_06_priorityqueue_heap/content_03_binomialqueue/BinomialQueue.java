package com.rick.chap_06_priorityqueue_heap.content_03_binomialqueue;

import com.rick.error.UnderFlowException;

/**
 * @Author: Rick
 * @Date: 2022/10/29 23:26
 */
public class BinomialQueue<AnyType extends Comparable<? super AnyType>> {
    private static final int DEFAULT_TREES = 1;

    private int currentSize;
    private Node<AnyType>[] theTrees;

    private static class Node<AnyType> {
        AnyType element;            // the data in the node
        Node<AnyType> leftChild;    // Left child
        Node<AnyType> nextSibling;   // Right child

        // Constructors
        public Node(AnyType element) {
            this(element, null, null);
        }

        public Node(AnyType element, Node<AnyType> lt, Node<AnyType> rt) {
            this.element = element;
            this.leftChild = lt;
            this.nextSibling = rt;
        }
    }

    public BinomialQueue() {
        theTrees = new Node[DEFAULT_TREES];
        makeEmpty();
    }

    public BinomialQueue(AnyType item) {
        currentSize = 1;
        theTrees = new Node[1];
        theTrees[0] = new Node<>(item, null, null);
    }

    /**
     * Merge rhs into the priorty queue.
     * rhs becomes empty, rhs must be different from this.
     *
     * @param rhs the other binomial queue.
     */
    public void merge(BinomialQueue<AnyType> rhs) {
        if (this == rhs) // avoid aliasing problems
            return;

        currentSize += rhs.currentSize;
        if (currentSize > capacity()) {
            int maxLength = Math.max(theTrees.length, rhs.theTrees.length);
            expandTheTrees(maxLength + 1);
        }

        Node<AnyType> carry = null;
        for (int i = 0, j = 1; j <= currentSize; i++, j *= 2) {
            Node<AnyType> t1 = theTrees[i];
            Node<AnyType> t2 = i < rhs.theTrees.length ? rhs.theTrees[i] : null;

            int whichCase = t1 == null ? 0 : 1;
            whichCase += t2 == null ? 0 : 2;
            whichCase += carry == null ? 0 : 4;

            switch (whichCase) {
                case 0: /* No trees */
                case 1: /* Only this */
                    break;
                case 2: /* No rhs */
                    theTrees[i] = t2;
                    rhs.theTrees[i] = null;
                    break;
                case 4: /* Only carry */
                    theTrees[i] = carry;
                    carry = null;
                    break;
                case 3: /* this and rhs */
                    carry = combineTrees(t1, t2);
                    theTrees[i] = rhs.theTrees[i] = null;
                    break;
                case 5: /* this and carry */
                    carry = combineTrees(t1, carry);
                    theTrees[i] = null;
                    break;
                case 6: /* rhs and carry */
                    carry = combineTrees(t2, carry);
                    rhs.theTrees[i] = null;
                    break;
                case 7: /* All three */
                    theTrees[i] = carry;
                    carry = combineTrees(t1, t2);
                    rhs.theTrees[i] = null;
                    break;
            }
        }

        for (int k = 0; k < rhs.theTrees.length; k++) {
            rhs.theTrees[k] = null;
        }
        rhs.currentSize = 0;
    }


    /**
     * Insert into the priority queue, maintaining heap order.
     * This implementation is not optimized for O(1) performance.
     *
     * @param x the item to insert
     */
    public void insert(AnyType x) {
        merge(new BinomialQueue<>(x));
    }

    public AnyType findMin() {
        if (isEmpty())
            throw new UnderFlowException();

        return theTrees[findMinIndex()].element;
    }

    /**
     * Remove the smallest item from the priority queue.
     *
     * @return the smallest item, or throw UnderflowException if empty.
     */
    public AnyType deleteMin() {
        if (isEmpty())
            throw new UnderFlowException();

        int minIndex = findMinIndex();
        AnyType minItem = theTrees[minIndex].element;

        Node<AnyType> deletedTree = theTrees[minIndex].leftChild;

        // Construct H''
        BinomialQueue<AnyType> deletedQueue = new BinomialQueue<>();
        deletedQueue.expandTheTrees(minIndex + 1);

        deletedQueue.currentSize = (1 << minIndex) - 1;
        for (int j = minIndex - 1; j >= 0; j--) {
            deletedQueue.theTrees[j] = deletedTree;
            deletedTree = deletedTree.nextSibling;
            deletedQueue.theTrees[j].nextSibling = null;
        }

        // Construct H'
        theTrees[minIndex] = null;
        currentSize -= deletedQueue.currentSize + 1;

        merge(deletedQueue);

        return minItem;
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    public void makeEmpty() {
    }

    private void expandTheTrees(int newNumTrees) {
        Node<AnyType>[] old = theTrees;
        int oldNumTrees = theTrees.length;

        theTrees = new Node[newNumTrees];
        for (int i = 0; i < Math.min(oldNumTrees, newNumTrees); i++) {
            theTrees[i] = old[i];
        }
        for (int i = oldNumTrees; i < newNumTrees; i++) {
            theTrees[i] = null;

        }
    }

    /**
     * Return the result of merging equal-sized t1 and t2
     *
     * @param t1
     * @param t2
     * @return
     */
    private Node<AnyType> combineTrees(Node<AnyType> t1, Node<AnyType> t2) {
        if (t1.element.compareTo(t2.element) > 0)
            return combineTrees(t2, t1);
        t2.nextSibling = t1.leftChild;
        t1.leftChild = t2;
        return t1;
    }

    private int capacity() {
        return (1 << theTrees.length) - 1;
    }

    private int findMinIndex() {
        int i;
        int minIndex;

        for (i = 0; theTrees[i] == null; i++) {

        }

        for (minIndex = i; i < theTrees.length; i++) {
            if (theTrees[i] != null
                    && theTrees[i].element.compareTo(theTrees[minIndex].element) < 0)
                minIndex = i;

        }

        return minIndex;
    }

    public static void main(String[] args) {
        int numItems = 10_000;
        BinomialQueue<Integer> h = new BinomialQueue<>();
        BinomialQueue<Integer> h1 = new BinomialQueue<>();
        int i = 37;

        System.out.println("Starting check.");

        for (i = 37; i != 0; i = (i + 37) % numItems) {
            if (i % 2 == 0)
                h1.insert(i);
            else
                h.insert(i);
        }
        h.merge(h1);

        for (i = 1; i < numItems; i++) {
            if (h.deleteMin() != i)
                System.out.println("Oops! " + i);
        }

        System.out.println("Check done.");
    }
}

