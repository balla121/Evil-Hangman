import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class actually runs the Hangman game. It handles all interactions with the player.
 * That includes getting players inputs and presenting the results of the game.
 * This way the user sees all interactions, but all game implementation is packed into another class.
 * Created by Qi_He on Feb/27/16.
 */
public class PlayHangman {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int numOfLetter = 0;
        int numOfGuess = 0;
        int runningTotal = 0;
        ArrayList<String> dictionary = new ArrayList<String>();
        char guess;
        int superHard = 0;

        // Here have to throw an error if dictionary file is not found.
        try(Scanner fileReader = new Scanner(new File("dictionary.txt"))){
            while(fileReader.hasNextLine()){
                dictionary.add(fileReader.nextLine());
            }
        }catch(FileNotFoundException e){
                e.printStackTrace();
        }

        System.out.println("To play, first specify the word length.");
        System.out.println("Please enter an integer larger than 1 and smaller than 20: ");
        numOfLetter = getIntInput(sc, 2, 19);

        System.out.println("Now enter the number of guesses - an integer larger than 0 and smaller than 15: ");
        numOfGuess = getIntInput(sc, 1, 14);

        System.out.println("Now do you want to try super hard mode? 0 for no, 1 for yes");
        superHard = getIntInput(sc, 0, 1);

        // This sets up the game.
        Hangman game = new Hangman(numOfLetter, numOfGuess, dictionary, superHard==1);

        System.out.println("Do you want a running total of words? 1 for yes, 0 for no. ");
        runningTotal = getIntInput(sc, 0, 1);

        System.out.println("Game on. Please continuously enter letters to play.");
        System.out.println("Currently the word looks like: ");
        System.out.println(game.showCurrentBoard());

        if (runningTotal == 1){
            System.out.println("# of words in word list: " + game.wordListSize());
        }

        // Each loop prompt a user's guess and present the results
        // Loop won't end unless the game is over
        while(!game.gameOver()){
            System.out.println("Now enter a letter: ");
            guess = getLetterInput(sc);
            if(game.play(guess)){
                System.out.println("Your guess was right!");
            }else{
                System.out.println("Wrong letter!");
            }

            System.out.println("Now the word looks like: ");
            System.out.println(game.showCurrentBoard());

            System.out.println("# of guesses left: "+ game.getNumOfGuess());

            if (runningTotal == 1){
                System.out.println("# of words in word list: " + game.wordListSize());
            }

            System.out.println("Letters already guessed: ");
            System.out.println(game.printGuessedChar());

        }

        if (game.playerWon()){
            System.out.println("You won!!!");
        }else{
            System.out.println("Game Over. You lost.");
        }

        // We can get the final answer if the game has ended
        // If we attempt to cheat, getting the final answer will return a null
        System.out.println("Final answer is: ");
        System.out.println(game.getFinalAnswer());


        sc.close();
    }

    /**
     * Helper method to get user's integer input. It loops until user enters a valid input
     * @param sc Scanner we pass in to read user input
     * @param min Minimum integer value (inclusive)
     * @param max Maximum integer value (inclusive)
     * @return user's valid integer input
     */
    private static int getIntInput(Scanner sc, int min, int max){
        int intInput = 0;
        while(true) {
            if (sc.hasNextInt()) {
                intInput = sc.nextInt();
                System.out.println("Number entered is: " + intInput);
                if (intInput > max) {
                    System.out.println("Too many. Please re-enter: ");
                }else if (intInput < min){
                    System.out.println("Too few letters. Please re-enter: ");
                }else{
                    return intInput;
                }
            }else{
                System.out.println("Input must be an integer. Please re-enter: ");
                sc.next();
            }
        }
    }

    /**
     * Helper method to get user's letter input. It loops until user enters a valid input
     * @param sc Scanner we pass in to read user input
     * @return user's valid input as a letter
     */
    private static char getLetterInput(Scanner sc){
        while(true){
            if (sc.hasNext()){
                String temp = sc.next();
                if (temp.length()==1 && Character.isLetter(temp.charAt(0))){
                    System.out.println("Your letter is: " + temp.charAt(0));
                    return temp.charAt(0);
                }
            }
            System.out.println("Input is invalid. Please enter one letter: ");
        }
    }
}
