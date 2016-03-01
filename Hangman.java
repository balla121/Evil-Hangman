import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * This is the game implementation of Hangman.
 * It does not prompt user for inputs, and hides the guessing process.
 * All iterations are done via passed in parameters
 * Created by Qi_He on Feb/28/16.
 */
public class Hangman {
    private int numOfLetter = 0;
    private int numOfGuess = 0;
    private ArrayList<String> wordList;
    private String output;
    private String finalAnswer;
    private ArrayList<Character> guessed = new ArrayList<>();
    private boolean superEvil;


    /**
     * Constructor sets up the number of letters and guesses, and take in the dictionary
     * Initially set the word guessing status as "[_, _, ... _, _]"
     * Then set the game's own word list as words of given length in the dictionary
     *
     * @param numOfLetter in the target word. This filters the words in the dictionary to be considered.
     * @param numOfGuess  allowed for user.
     * @param dictionary  includes all valid words.
     */
    public Hangman(int numOfLetter, int numOfGuess, ArrayList<String> dictionary, boolean superEvil) {
        this.numOfLetter = numOfLetter;
        this.numOfGuess = numOfGuess;
        wordList = new ArrayList<>();

        // Set initial output: "_" for all possible letter positions
        char[] outputArray = new char[numOfLetter];
        for (int i = 0; i < numOfLetter; i++) {
            outputArray[i] = '_';
        }

        this.superEvil = superEvil;

        output = new String(outputArray);

        // Simply adding all words in dictionary with appropriate length to our own word list.
        wordList.addAll(dictionary.stream().filter(word -> word.length() == numOfLetter).collect(Collectors.toList()));
    }


    /**
     * Each play takes in one guess and implement the word families using HashMap,
     * as suggested in the HW writeup.
     * Each key to HashMap is basically a string (e.g. "_a_" for "bad" after guessing "a").
     * Words that contain guessed letter will have such letter shown,
     * and we use '_' for unknown locations
     *
     * @param guess User's guess letter
     * @return whether the user got the guess right. true if guess was correct.
     */
    public boolean play(char guess) {
        String temp = output;
        boolean guessRight = false;
        HashMap<String, ArrayList<String>> wordChoices = new HashMap<>();

        if (gameOver()) {
            System.out.println("Game Over");
            return playerWon();
        }

        // Parse all words in word list to create keys for the hash map
        for (String word : wordList) {
            char[] key = new char[numOfLetter];

            for (int index = 0; index < numOfLetter; index++) {
                if (word.charAt(index) == guess) {
                    key[index] = guess;
                } else {
                    key[index] = output.charAt(index);
                }
            }

            String keyString = new String(key);

            addWordChoice(keyString, word, wordChoices);
        }

        // Implementing the last guess situation as in write up
        if (numOfGuess == 1) {
            // if output is present in HashMap, then there are words available without the guessed letter
            if (wordChoices.keySet().contains(output)) {

                // Then simply do the update and return false. Game over!
                wordList = new ArrayList<>(wordChoices.get(output));
                numOfGuess--;
                finalAnswer = wordChoices.get(output).get(0);
                guessed.add(guess);
                return false;
            }
        }

        // If not in above situation:

        for (String keyString : wordChoices.keySet()) {
            if (!wordChoices.keySet().contains(temp)) {
                temp = keyString;
            }

            // Super evil treatment
            if (superEvil) {

                if (getEvilWeighting(wordChoices.get(keyString)) > getEvilWeighting(wordChoices.get(temp))) {
                    temp = keyString;
                }

            } else {

                // Standard treatment
                // Then go through all word families to find the one with the most possible words
                if (wordChoices.get(keyString).size() > wordChoices.get(temp).size()) {
                    temp = keyString;
                }
            }
        }

        // handle cases where the word list is empty (if case we change the dictionary)
        if (wordChoices.keySet().contains(temp)) {

            // Shallow copy of the max value in word choices
            wordList = new ArrayList<>(wordChoices.get(temp));
            guessRight = !temp.equals(output);

            if (!guessRight) numOfGuess--;

            output = temp;
            finalAnswer = wordChoices.get(output).get(0);
            guessed.add(guess);
            return guessRight;

        } else {

            // when word list is empty, just return false...
            wordList = new ArrayList<>();
            guessed.add(guess);
            return false;
        }

    }


    /**
     * Because each unique char requires at least one guess to reveal,
     * the weighting should really be on unique char in each word,
     * instead of simply using a weight of 1 for each word.
     * This method finds the number of unique chars in each word of the ArrayList,
     * and then sum up all counts to give an ArrayList a weighting.
     * @param words ArrayList of words, to be given a new weighting
     * @return new score for given ArrayList
     */
    private int getEvilWeighting(ArrayList<String> words) {
        int weighting = 0;

        if (words == null) return 0;

        // Use a hash set to find the number of unique char in a string
        for (String word:words){
            char[] tempArray = word.toCharArray();
            HashSet<Character> uniqueChars = new HashSet<>();
            for (char c : tempArray) {

                // Also need to make sure we don't count the letters already guessed
                // We only want a proxy for how many steps the user must mess up...
                if (!guessed.contains(c)){
                    uniqueChars.add(c);
                }
            }

            // increment total weighting by the number of unique char
            weighting += uniqueChars.size();
        }

        return weighting;
    }


    /**
     * Getter for number of guesses left
     *
     * @return number of guesses left
     */
    public int getNumOfGuess() {
        return numOfGuess;
    }

    /**
     * Check if game is over. Game is over when:
     * There is no more guess left or no word in word list. Or the player already won
     *
     * @return true if game is over. false otherwise.
     */
    public boolean gameOver() {
        return numOfGuess == 0 || !output.contains("_") || wordList.isEmpty();
    }

    /**
     * Helper function to print out the current status of guesses.
     * Result string will look like "[_, _, a, _, _]"
     *
     * @return The formmated result string that shows what user has guessed right and the unknown
     */
    public String showCurrentBoard() {
        char[] formattedOutput = output.toCharArray();
        return Arrays.toString(formattedOutput);
    }

    /**
     * Helper function to appended a word to the hash map.
     * If the key does not exist in the map yet, create an ArrayList and append the word
     * Otherwise simply appended the word to the appropriate ArrayList
     * Same as default dict in python
     *
     * @param key           key string of the pair - used to find the target ArrayList in the given HashMap
     * @param word          value of the pair - to be appended to the value (ArrayList) in given HashMap
     * @param wordChoiceMap HashMap of string keys and ArrayList values.
     */
    private void addWordChoice(String key, String word, HashMap<String, ArrayList<String>> wordChoiceMap) {
        if (wordChoiceMap.get(key) == null) {
            wordChoiceMap.put(key, new ArrayList<>());
        }
        wordChoiceMap.get(key).add(word);
    }

    /**
     * Simply return how many words are in the current family, as requested
     *
     * @return how many words are in the current family
     */
    public int wordListSize() {
        return wordList.size();
    }

    /**
     * Check if the player has won.
     *
     * @return true if the player has won. If the game is not over yet, return false.
     */
    public boolean playerWon() {
        if (!gameOver()) {
            System.out.println("Game not over");
            return false;
        } else {
            return !output.contains("_");
        }
    }

    /**
     * We used gussed to store all guessed letters.
     * This method helps print those stored letters in a well formatted way.
     *
     * @return A string that shows all gussed letters.
     */
    public String printGuessedChar() {
        return guessed.toString();
    }

    /**
     * Only when game is over, this method shows the "secretly pre-selected" word.
     * Otherwise, return null to make sure user cannot cheat.
     *
     * @return the chosen word if game is over. Otherwise return null.
     */
    public String getFinalAnswer() {
        if (gameOver()) {
            return finalAnswer;
        } else {
            return null;
        }
    }


}
