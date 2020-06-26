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

public class Main {

    // static variables and constants only here.

    public static void main(String[] args) throws Exception {

        Scanner kb;    // input Scanner for commands
        PrintStream ps;    // output file, for student testing and grading only
        // If arguments are specified, read/write from/to files instead of Std IO.
        if (args.length != 0) {
            kb = new Scanner(new File(args[0]));
            ps = new PrintStream(new File(args[1]));
            System.setOut(ps);            // redirect output to ps
        } else {
            kb = new Scanner(System.in);// default input from Stdin
            ps = System.out;            // default output to Stdout
        }
        initialize();

        ArrayList<String> input = parse(kb);	// get input from user
        while (!input.isEmpty()) {	// returns empty arraylist if input is "/quit"
        	ArrayList<String> wl = getWordLadderBFS(input.get(0), input.get(1));	// get the BFS word ladder
        	printLadder(wl);	// print the obtained ladder

        	input = parse(kb);	// get the next input
		}
    }

    public static void initialize() {
        // initialize your static variables or constants here.
        // We will call this method before running our JUNIT tests.  So call it
        // only once at the start of main.
    }

    /**
     * @param keyboard Scanner connected to System.in
     * @return ArrayList of Strings containing start word and end word.
     * If command is /quit, return an empty ArrayList.
     */
    public static ArrayList<String> parse(Scanner keyboard) {
		ArrayList<String> ret = new ArrayList<>();

        String i1 = keyboard.next();	// the first input word
        if (i1.equals("/quit")) return ret;	// the first input will be /quit or the first word of the ladder
											// if /quit, return an empty arraylist

        String i2 = keyboard.next();	// the second input word

        ret.add(i1);
        ret.add(i2);

        return ret;
    }

	/**
	 * @param start the start of the word ladder
	 * @param end the end of the word ladder
	 * @return if it exists, a word ladder between start and end. otherwise, an ArrayList that only contains start and
	 * end.
	 */
    public static ArrayList<String> getWordLadderDFS(String start, String end) {
        // start by making both words uppercase
    	start = start.toUpperCase();
        end = end.toUpperCase();

        // Returned list should be ordered start to end.  Include start and end.
        // If ladder is empty, return list with just start and end.
        Set<String> dict = makeDictionary();

        // get the word ladder obtained by findWordLadderDFS
        ArrayList<String> ret = findWordLadderDFS(start, end, dict, new ArrayList<>());

        // if findWordLadderDFS returned null (no word ladder exists), add the start and end word
        if (ret == null) {
            ret = new ArrayList<>();
            ret.add(start);
            ret.add(end);
        }

        shortenWordLadder(ret);	// attempt to shorten the word ladder

        return ret;
    }

	/**
	 * Attempts to shorten wl by finding any non-sequential off-by-one words.
	 * @param wl the word ladder to be shortened, represented by an ArrayList
	 */
	private static void shortenWordLadder(ArrayList<String> wl) {
        int j = wl.size() - 1;	// the end index is the last word in the ladder

        for (int i = 0; i < j; i++) {	// start at the beginning
        	j = wl.size() - 1;

            while (j > i + 1) {	// while there is still distance between the start and end
                if (offByOneCharacter(wl.get(i), wl.get(j))) {	// if these words are off-by-one
                    for (int k = i + 1; k < j; k++) {	// remove all the words in-between
                        wl.remove(k);
                        k--;
                        j--;
                    }
                }
                j--;
            }

            j = wl.size() - 1;
        }
    }

	/**
	 * The helper function for getWordLadderDFS.
	 *
	 * @param start	the start of the word ladder
	 * @param end the end of the word ladder
	 * @param dict the dictionary containing all possible words
	 * @param wl the current word ladder whose next word is to be found
	 * @return a word ladder if it exists. otherwise, null.
	 */
    public static ArrayList<String> findWordLadderDFS(String start, String end, Set<String> dict, ArrayList<String> wl) {
        wl.add(start);	// start by adding start

        if (start.equals(end)) {	// if the start words and end words are equal, simply add end and return
            wl.add(end);
            return wl;
        }

        if (dict.size() == 0) return null;	// if there are no words in the dictionary, no possible word ladder

        dict.remove(start);	// remove start from the dictionary so that it is not found as a connection
		ArrayList<String> possibleRungs = findPossibleRungs(start, dict);	// find all off-by-one words from dict

        if (possibleRungs.size() == 0) return null;	// if there are no neighbors, no possible word ladder

        if (possibleRungs.contains(end)) {	// if the neighbors contains end, the end of the ladder has been found
            wl.add(end);
            return wl;
        }

        dict.removeAll(possibleRungs);	// remove all current neighbors from the dictionary to avoid looping

        for (String s : possibleRungs) {	// for each word in start's neighbors
            ArrayList<String> ret = findWordLadderDFS(s, end, dict, wl);	// find if a word ladder exists from this neighbor

            if (ret != null) return ret;	// if a word ladder was found, return
        }

        return null;	// at this point, there are no possible word ladders
	}

	/**
	 * Finds all characters in dict that are off of the String rung by exactly one character.
	 *
	 * @param rung the string whose off-by-one rungs are to be found
	 * @param dict the dictionary containing all possible words
	 * @return an ArrayList of all off-by-one rungs from the passed rung String
	 */
	public static ArrayList<String> findPossibleRungs(String rung, Set<String> dict) {
		ArrayList<String> ret = new ArrayList();

		for (String s : dict) {
			if (offByOneCharacter(rung, s)) ret.add(s);
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
    public static boolean offByOneCharacter(String x, String y) {
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
	 * @param start the start of the word ladder
	 * @param end the end of the word ladder
	 * @return if it exists, a word ladder between start and end. otherwise, an ArrayList that only contains start and
	 * end.
	 */
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
    // Initialization
    	start = start.toUpperCase();
		end = end.toUpperCase();
		Set<String> dict = makeDictionary();
		ArrayList<String> rungs = new ArrayList<String>();
		
	// Getting Word Ladder 
		Tree myTree = new Tree(start, dict);
		rungs = myTree.findLadder(end);

		if (rungs.isEmpty()) {
			rungs.add(start);
			rungs.add(end);
		}

		return rungs;
	}

	/**
	 * Prints the word ladder represented by ladder, an ArrayList. If the size is only two, then that means that a
	 * ladder could not be found, and an appropriate error message will be output instead.
	 *
	 * @param ladder the word ladder to be printed
	 */
    public static void printLadder(ArrayList<String> ladder) {
        if (ladder.size() == 2) {
            System.out.println("no word ladder can be found between " + ladder.get(0).toLowerCase() + " and " + ladder.get(1).toLowerCase() + ".");
        } else {
            int ladderLength = ladder.size() - 2;
            System.out.println("a " + ladderLength + "-rung word ladder exists between " + ladder.get(0).toLowerCase() + " and " + ladder.get(ladder.size() - 1).toLowerCase() + ".");

            for (String s : ladder) {
                System.out.println(s.toLowerCase());
            }
        }
    }

    /* Do not modify makeDictionary */
    public static Set<String> makeDictionary() {
        Set<String> words = new HashSet<String>();
        Scanner infile = null;
        try {
            infile = new Scanner(new File("five_letter_words.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Dictionary File not Found!");
            e.printStackTrace();
            System.exit(1);
        }
        while (infile.hasNext()) {
            words.add(infile.next().toUpperCase());
        }
        return words;
    }

}
