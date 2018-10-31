package com.csguys.multiwordsuggestion.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  Radix search tree
 */
public class Trie {
    public Node root = new Node(false);
    private final char CASE;    // 'a' for lower, 'A' for upper
 
    public Trie() {
        CASE = 'a';     // default case
    }
 
    private Trie(char CASE) {
        this.CASE = CASE;   // constructor accepting the starting symbol
    }

    /**
     * method to insert a word in search tree , this method also required to pass index of search
     * key from list of all search keywords
     * @param word search key
     * @param valIndex index of search key
     */
    public void insert(String word, int valIndex) {
        Node trav = root;
        int i = 0;
 
        while (i < word.length() && trav.edgeLabel[word.charAt(i) - CASE] != null) {
            int index = word.charAt(i) - CASE, j = 0;
            StringBuilder label = trav.edgeLabel[index];
 
            while (j < label.length() && i < word.length() && label.charAt(j) == word.charAt(i)) {
                ++i;
                ++j;
            }
 
            if (j == label.length()) {
                trav = trav.children[index];
            } else {
                if (i == word.length()) {   // inserting a prefix of exisiting word
                    Node existingChild = trav.children[index];
                    Node newChild = new Node(true);
//                    newChild.addIndex(valIndex);
                    addIndexToNode(valIndex, newChild);
                    StringBuilder remainingLabel = strCopy(label, j);
                     
                    label.setLength(j);     // making "faceboook" as "face"
                    trav.children[index] = newChild;    // new node for "face"
                    newChild.children[remainingLabel.charAt(0) - CASE] = existingChild;
                    newChild.edgeLabel[remainingLabel.charAt(0) - CASE] = remainingLabel;
                } else {     // inserting word which has a partial match with existing word
                    StringBuilder remainingLabel = strCopy(label, j);
                    Node newChild = new Node(false);
                    StringBuilder remainingWord = strCopy(word, i);
                    Node temp = trav.children[index];
                     
                    label.setLength(j);
                    trav.children[index] = newChild;
                    newChild.edgeLabel[remainingLabel.charAt(0) - CASE] = remainingLabel;
                    newChild.children[remainingLabel.charAt(0) - CASE] = temp;
                    newChild.edgeLabel[remainingWord.charAt(0) - CASE] = remainingWord;
                    Node nodeTemp = new Node(true);
//                    nodeTemp.addIndex(valIndex);
                    addIndexToNode(valIndex, nodeTemp);
                    newChild.children[remainingWord.charAt(0) - CASE] = nodeTemp;
                }
 
                return;
            }
        }
 
        if (i < word.length()) {    // inserting new node for new word
            trav.edgeLabel[word.charAt(i) - CASE] = strCopy(word, i);
            Node nodeNew = new Node(true);
//            nodeNew.addIndex(valIndex);
            addIndexToNode(valIndex, nodeNew);
            trav.children[word.charAt(i) - CASE] = nodeNew;
        } else {    // inserting "there" when "therein" and "thereafter" are existing
            trav.isEnd = true;
//            trav.addIndex(valIndex);
            addIndexToNode(valIndex, trav);
        }
    }

    /**
     * method to add new index to node if it does not exist
     * @param index index
     * @param node node in which index need to be inserted
     */
    private void addIndexToNode(final int index, final Node node) {
        if (!node.isIndexExist(index)) {
            node.addIndex(index);
        }
    }

    /**
     * reset Radix tree
     */
    public void resetTree() {
        root = new Node(false);
    }
     
    // Creates a new String from an existing
    // string starting from the given index
    private StringBuilder strCopy(CharSequence str, int index) {
        StringBuilder result = new StringBuilder(100);
         
        while (index != str.length()) {
            result.append(str.charAt(index++));
        }
         
        return result;
    }

    /**
     * print all labels
     */
    public void print() {
        printUtil(root, new StringBuilder());
    }
 
    private void printUtil(Node node, StringBuilder str) {
        if (node.isEnd) {
            System.out.println(str);
        }
 
        for (int i = 0; i < node.edgeLabel.length; ++i) {
            if (node.edgeLabel[i] != null) {
                int length = str.length();
 
                str = str.append(node.edgeLabel[i]);
                printUtil(node.children[i], str);
                str = str.delete(length, str.length());
            }
        }
    }

    public boolean search(final String word) {
        int i = 0;
        Node trav = root;
 
        while (i < word.length() && trav.edgeLabel[word.charAt(i) - CASE] != null) {
            int index = word.charAt(i) - CASE;
            StringBuilder label = trav.edgeLabel[index];
            int j = 0;
 
            while (i < word.length() && j < label.length()) {
                if (word.charAt(i) != label.charAt(j)) {
                    return false;   // character mismatch
                }

                ++i;
                ++j;
            }

            if (j == label.length() && i <= word.length()) {
                trav = trav.children[index];    // traverse further
            } else {
                // edge label is larger than target word
                // searching for "face" when tree has "facebook"
                return false;
            }
        }
         
        // target word fully traversed and current node is a word ending
        return i == word.length() && trav.isEnd; 
    }

    /**
     * method which search key in radix tree and return list of matching result
     * @param word key to search
     * @return list of matching suggestions
     */
    public List<Integer> subStringMatcher(final String word) {
        int i = 0;
        boolean found = false;
        Node trav = root;
        List<Integer> list = new ArrayList<>();
        while (i < word.length() && trav.edgeLabel[word.charAt(i) - CASE] != null) {
            found = true;
            int index = word.charAt(i) - CASE;
            StringBuilder label = trav.edgeLabel[index];
            int j = 0;
            while (i < word.length() && j < label.length() && label.charAt(j) == word.charAt(i)) {
                ++i;
                ++j;
            }
            if (j <= label.length()) {
                trav = trav.children[index];    // traverse further
            }
        }
        if (!found || i < word.length()) {
            return list;
        }
        if (trav.isEnd) {
            list.addAll(trav.list);
        }
        for (final String key : trav.getAllKeys()) {
            childKeyTraversal(trav.children[key.charAt(0) - CASE], list);
        }
        return list;
    }


    private void childKeyTraversal(final Node node, final List<Integer> list) {
        if (node.isEnd) {
            list.addAll(node.list);
        }
        if (node.getAllKeys().size() == 0) {
            return;
        }
        for (final String key : node.getAllKeys()) {
            childKeyTraversal(node.children[key.charAt(0) - CASE], list);
        }
    }

    /**
     *Single node data structure for Radix tree
     */
    public class Node {
        private final static int SYMBOLS = 26;
        /*
         save sub strings labels for this node
         */
        StringBuilder[] edgeLabel = new StringBuilder[SYMBOLS];

        /*
         Arrays of next child node for given label index label
         */
        Node[] children = new Node[SYMBOLS];

        /*
         it stores index of input strings for which the given labels belongs
         */
        List<Integer> list = new ArrayList<>();

        /*
         indicate weather this node is end of a word
         true if its end of word false otherwise
         */
        boolean isEnd;

        public Node(final boolean isEnd) {
            this.isEnd = isEnd;
        }

        /**
         * add new index to list
         * @param value index
         */
        public void addIndex(final int value) {
            list.add(value);
        }

        /**
         * check weather specified index present
         * @param index index
         * @return search result
         */
        public boolean isIndexExist(final Integer index) {
            return list.contains(index);
        }

        /**
         * get all available keys for this node
         * @return list of keys
         */
        public List<String> getAllKeys() {
            List<String> keys = new ArrayList<>();
            for (StringBuilder s : edgeLabel) {
                if (s != null) {
                    keys.add(s.toString());
                }
            }
            return keys;
        }

        @Override
        public String toString() {
            return "{ edgeLabel=" + Arrays.toString(edgeLabel) +
                    "\n list=" + list +
                    "\n isEnd=" + isEnd+
                    "\n children=" + Arrays.toString(children) +"}";
        }
    }

}
