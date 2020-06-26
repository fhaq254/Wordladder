/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * Zachary Chilton
 * zgc87
 * 16225
 * Fawadul Haq
 * fh5277
 * 16225
 * Slip days used: 0
 * Git URL: https://github.com/EE422C/project-3-wordladder-pair-67
 * Spring 2019
 */

package assignment3;

import java.lang.reflect.Array;
import java.nio.file.FileSystemNotFoundException;
import java.util.*;
import java.io.*;

public class Tree {
	private Node root;
	private Set<String> treeWords;	// A list of all the current Words in the tree
	private Set<String> dict;
	
	// Tree Constructor
	public Tree (String START, Set<String> dict) {
		root = new Node(START, null);
		root.Offby1 = new ArrayList<Node>();
		this.dict = dict;
		treeWords = new HashSet<String>();
		treeWords.add(root.word);
	}
	// Node Class
	private class Node {
		private String word;
		private Node parent;
		private ArrayList<Node> Offby1;  // all the words in the dictionary off by 1 letter
		private boolean childless;
		// Node Constructor
		public Node(String word, Node parent) {
			this.word = word;
			this.parent = parent;
			Offby1 = new ArrayList<Node>();
			childless = false;
		}
	}
	
	/**
	 * Finds a word ladder between the root word of the Tree and the given END word
	 * 
	 * @param END string of destination word in the ladder
	 * @return ArrayList<String> of words in the ladder. If List is empty, no ladder could be found.
	 */
	public ArrayList<String> findLadder(String END){
		// Initialize and prepare for level order traversal (BFS)
		makeTree();
		Queue<Node> nextNode = new LinkedList<Node>();
		Node curr = root;
		
		while(curr != null) { // Continue until all nodes are exhausted
			
			if(curr.word.equals(END)) {
				return makeLadder(curr);	// Success case
			}
			
			for(Node iter : curr.Offby1) {	// Queuing the next level's nodes
				nextNode.add(iter);
			}
			
			curr = nextNode.poll();	// Trying next node 
		}
		// If no END found, return empty ArrayList
		return new ArrayList<String>();
	}
	
	/**
	 * Completely makes the Tree from a dictionary with use of addLevel()
	 */
	private void makeTree() {
		while(addLevel()) {};
	}
	
	/**
	 * Adds 1 level of height to the Tree
	 * 
	 * @return added boolean telling whether or not a level could be added
	 * 			a level could be added when any one node could have children
	 */
	private boolean addLevel() {
		// Initialize 
		Queue<Node> nextNode = new LinkedList<Node>();
		Node curr = root;
		boolean added = false;
		
		// Level Order Traversal
		while(curr != null) {
			// Need to find empty Offby1 because that signifies the bottom level possibly
			if(curr.Offby1.isEmpty()) {	
					added |= addChildren(curr);	// If we could add children to any node, we make added true
			}
			// If there are already nodes on this level, we must get to the next, lowest level
			else {	
				for(Node iter : curr.Offby1) {
					nextNode.add(iter);
				}
			}
			curr = nextNode.poll();
		}
		
		return added;
	}
	
	/**
	 * Updates a Node's Offby1 to hold all children of that node
	 * Updates the parent value of those children
	 * 
	 * @param parent Node for which to add possible children
	 * @return added boolean based on whether any children could be added
	 */
	private boolean addChildren(Node parent) {
		// If they don't have any links, immediately return
		if(parent.childless) return false;
		
		// First find all children
		ArrayList<String> childWords = findPossibleRungsTree(parent.word, dict);
		
		// Update Offby1 to include Nodes with childWords
		for(String iter : childWords) {
			Node child = new Node(iter,parent);
			parent.Offby1.add(child);
		}
		
		// Have we added any children?
		if(parent.Offby1.isEmpty()) {
			parent.childless = true;	// Does not have any children
			return false;	// Could not add any children
		}	
		return true;
			
	}
	
	/**
	 * Finds all Words in dict that are off of the String rung by exactly one character
	 * Excludes Words that are already nodes in the tree (avoid duplicates)
	 * Updates checked Set with the parent's new children
	 *
	 * @param Origin the string whose off-by-one rungs are to be found
	 * @param dict the dictionary containing all possible words
	 * @return an ArrayList of all off-by-one rungs from the passed rung String
	 */
	private ArrayList<String> findPossibleRungsTree(String Origin, Set<String> dict) {
		ArrayList<String> ret = new ArrayList<String>();

		// Iterating through the dictionary
		for (String s : dict) {
			// Finding all Strings Off by One
			if (offByOneCharacterTree(Origin, s)) {
				// Confirming the Possible Rung is not already a node on the tree
				if(!treeWords.contains(s)) {
					treeWords.add(s);
					ret.add(s);
				}	
			}
		}
		return ret;
	}

    /**
     * Compares two Strings of the same length to find if they have a difference of exactly one character.
     *
     * @param x the first string
     * @param y the second string
     * @return a boolean based on whether or not the parameters have exactly one character of difference
     */
    private boolean offByOneCharacterTree(String x, String y) {
        if (x.length() == y.length()) {
            int charDifferences = 0;

		for (int i = 0; i < x.length(); i++) {
			if (charDifferences > 1) return false;

			if (x.charAt(i) != y.charAt(i)) charDifferences++;
		}

            return charDifferences == 1;
        } else return false;
    }
	
	
	/**
	 * Returns an ArrayList<String> of the word ladder down a tree to reach the END word
	 * Algorithm begins at END word and travels to root word, then it reverses the Array
	 * 
	 * @param END Node that contains the end word
	 * @return ladder ArrayList<String> that contains the ladder rungs between the START and END word
	 */
    private ArrayList<String> makeLadder(Node END){
		ArrayList<String> ladder = new ArrayList<String>();
		ladder.add(END.word);
		
		// Go backwards by adding all the parent's words
		Node currParent = END.parent;
		while(currParent != null) {
			ladder.add(currParent.word);
			currParent = currParent.parent;
		}
					
		return reverseList(ladder);
	}
	
	/**
	 * Returns the passed-in ArrayList<String> in reverse order
	 * 
	 * @param param ArrayList to be reversed
	 * @return ret param in reverse order
	 */
	private ArrayList<String> reverseList(ArrayList<String> param){
		ArrayList<String> ret = new ArrayList<String>();
		
		for (int i = param.size()-1; i >= 0; i--) { 
            // Reversing
            ret.add(param.get(i)); 
        }
		
		return ret;
	}
	
}
// End of Tree

