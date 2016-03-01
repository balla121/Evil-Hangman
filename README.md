This is an implementation of the Evil Hangman. The famous HW assignment is detailed here:

http://nifty.stanford.edu/2011/schwarz-evil-hangman/


1. Data structure
- I used ArrayList to store the dictionary simply for the ease of use. As shown below, I only need to iterate through the dictionary instead of changing a specific element. 
- Another ArrayList helps keep track of letters already guessed
- I used a HashMap of <String, ArrayList> to keep track of word families 
     - Each key to HashMap is basically a string (e.g. "_a_" for "bad" after guessing "a")
     	* Words that contain guessed letter will have such letter shown
     	* And it will show '_' for unknown locations
     - Value is an ArrayList of all possible words

2. Edge cases:
- when word list is empty, play method will return false for user's guess. But the game over method will also show that the game is over.
- If the game is not over yet, the method that checks whether the player wins will return false.
- A method returns the "secretly pre-selected" word, but only when game is over. Otherwise the method will return null.
- We must make sure dictionary.txt is present. Otherwise there will be an error (handled in try catch in main).

3. How to play:
- To play the game, we simply run PlayHangman:
	* This class handles all interactions with the player.
	* That includes getting players inputs and presenting the results of the game.
	* This way the user sees all interactions, but all game implementation is hidden.

- Simply enter the values as prompted to set up the game

- Do "SUPER HARD MODE" for extra evil stuff...

- Then each loop will prompt a user's guess and present the results, until the game is over
- We can get the final answer if the game has ended. If we attempt to cheat, getting the final answer will return a null

4. Extra Evil
- First implemented the slight improvement suggested in HW writeup:
	* When there is only 1 guess left, pick the word that can cause user to lose immediately.

- Additional implementation can only be triggered by "superEvil":
	* Because each unique char requires at least one guess to reveal, the weighting should really be on unique char in each word, instead of 1 for each word.
	* So I included an additional method that finds the number of unique chars in each word of the ArrayList, and then sum up all counts to give an ArrayList a weighting.

	* For example, if we have the following words: lily, pool, cool, risk, take, bake
		* 'l' family would have the same # of words as no 'l' (same if user inputs 'k')
		* But choosing l family leads to more repeated letters
		* Once user hits one of the repeated letter, user only needs to guess 2 more
		* But the no 'l' family requires 3 more guesses regardless

	* More importantly, words with repeated letters show more information about the word structure than only revealing one letter
		* if we show l_l_ or _oo_, the user will have a much better sense of the underlying

