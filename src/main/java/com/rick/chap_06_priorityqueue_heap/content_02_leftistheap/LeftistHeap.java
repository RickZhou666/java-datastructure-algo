package com.rick.chap_06_priorityqueue_heap.content_02_leftistheap;

import com.rick.error.UnderFlowException;

/**
 * @Author: Rick
 * @Date: 2022/10/29 22:46
 */
public class LeftistHeap<AnyType extends Comparable<? super AnyType>> {

    private Node<AnyType> root; // root


    public LeftistHeap() {
        root = null;
    }

    /**
     * Merge rhs into the priority queue.
     * rhs becomes empty. rhs must be different from this.
     *
     * @param rhs the other leftist heap
     */
    public void merge(LeftistHeap<AnyType> rhs) {
        if (this == rhs) // Avoid aliasing problems
            return;

        root = merge(root, rhs.root);
        rhs.root = null;
    }

    /**
     * INsert into priority queue, maintaining heap order
     *
     * @param x the item to insert
     */
    public void insert(AnyType x) {
        root = merge(new Node<>(x), root);
    }

    public AnyType findMin() {
        if (isEmpty())
            throw new UnderFlowException();
        return root.element;
    }


    /**
     * Remove the smallest item from the priority queue.
     *
     * @return the smallest item, or throw UnderflowException if empty.
     */
    public AnyType deleteMin() {
        if (isEmpty())
            throw new UnderFlowException();

        AnyType minItem = root.element;
        root = merge(root.left, root.right);
        return minItem;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void makeEmpty() {
        root = null;
    }

    private static class Node<AnyType> {
        AnyType element;        // The data in the node
        Node<AnyType> left;     // Left node
        Node<AnyType> right;    // Right node
        int npl;                // null path length

        // Constructors
        public Node(AnyType theElement) {
            this(theElement, null, null);
        }

        Node(AnyType theElement, Node<AnyType> lt, Node<AnyType> rt) {
            element = theElement;
            left = lt;
            right = rt;
        }
    }

    /**
     * Internal method to merge two roots.
     * Deals with deviant cases and calls recursive merge1
     *
     * @param h1
     * @param h2
     * @return
     */
    private Node<AnyType> merge(Node<AnyType> h1, Node<AnyType> h2) {
        if (h1 == null)
            return h2;
        if (h2 == null)
            return h1;
        if (h1.element.compareTo(h2.element) < 0)
            return merge1(h1, h2);
        else
            return merge1(h2, h1);
    }

    /**
     * Internal method to merge two roots.
     * Assumes trees are not empty, and h1's root contains smallest item.
     *
     * @param h1
     * @param h2
     * @return
     */
    private Node<AnyType> merge1(Node<AnyType> h1, Node<AnyType> h2) {
        if (h1.left == null)    // Single node
            h1.left = h2;       // Other fields in h1 already accurate
        else {
            h1.right = merge(h1.right, h2);
            if (h1.left.npl < h1.right.npl)
                swapChildren(h1);
            h1.npl = h1.right.npl + 1;
        }
        return h1;
    }

    private void swapChildren(Node<AnyType> t) {
        Node<AnyType> tmp = t.left;
        t.left = t.right;
        t.right = tmp;
    }

    public static void main(String[] args) {
        int numItems = 100;
        LeftistHeap<Integer> h = new LeftistHeap<>();
        LeftistHeap<Integer> h1 = new LeftistHeap<>();
        int i = 37;

        for (i = 37; i != 0; i = (i + 37) % numItems) {
            if (i % 2 == 0)
                h1.insert(i);
            else
                h.insert(i);
        }
        h.merge(h1);
        for (i = 1; i < numItems; i++)
            if (h.deleteMin() != i)
                System.out.println("Oops! " + i);

    }
}
