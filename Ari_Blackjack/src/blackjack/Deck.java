package blackjack;

import java.util.Random;

//the deck of cards
public class Deck 
{	
	public static final int CLUBS = 100;
	public static final int DIAMONS = 200;
	public static final int HEARTS = 300;
	public static final int SPADES = 400;
	
	//support for more decks :O
	private final int DECK_SIZE = 52;
	private int numOfDecks = 1;
	
	//I briefly considered using a linked list or queue but this seemed
	//to be lighter weight and easier to work with given the randomization and
	//known/fixed size.
	private int[] cardDeck = new int[DECK_SIZE * numOfDecks];
	
	private int current;
	
	public Deck()
	{
		initializeCards();
		shuffle();
		
		current = 0;
	}
	
	//returns how many cards are left in the deck
	public int getCount()
	{
		return cardDeck.length - current;
	}
	
	//draws a card from the deck
	//implements counter
	//returns -1 if no cards are left. Ended up being handled differently elsewhere but
	//left this just in case I came back to use it.
	public int draw()
	{
		if(current > cardDeck.length - 1)
		{
			Blackjack.gameOver = true;
			return -1;
		}
		else
		{
			current++;
			return cardDeck[current - 1];
		}
	}
	
	//shuffle the deck
	//using a more simple and quick algorithm for shuffling at the expense of supreme randomness
	//start at the end, pick a random number of the entire range before that number
	//swap that location with the end location and move in one position
	public void shuffle()
	{
		int tmpVal;
		for(int i = cardDeck.length - 1; i > 1; i--)
		{
			Random rand = new Random();
			tmpVal = rand.nextInt(i);
			
			//swapping values
			//probably wouldn't use this in real life unless super tight on memory
			//admittedly showing off/preempting a programming question
			cardDeck[i] = cardDeck[i] ^ cardDeck[tmpVal];
			cardDeck[tmpVal] = cardDeck[i] ^ cardDeck[tmpVal]; 
			cardDeck[i] = cardDeck[i] ^ cardDeck[tmpVal];
		}
		
		current = 0;
	}
	
	//builds the deck
	//will start them off in order
	private void initializeCards()
	{
		int counter = 0;
		
		//how many decks are we doing
		for(int k = 0; k < numOfDecks; k++)
		{
			//suit
			for(int i = 1; i <= 4; i++)
			{
				//card number (1 = ace, 13 = king)
				for(int j = 1; j <= 13; j++)
				{
					//creates an ID for the cards
					//allows us to use a simple int array to hold them all
					cardDeck[counter] = (i*100) + j;
					counter++;
				}
			}	
		}
	}
	
	//test method for building the deck
	public String testDeckBuild()
	{
		initializeCards();
		
		String tmp = "";
		for(int i = 0; i < cardDeck.length; i++)
		{
			tmp = tmp + Integer.toString(cardDeck[i]) + "\r\n";
		}
		return tmp;
		
		
	}
	
	//test method for shuffling the deck
	public String testShuffle()
	{
		shuffle();
		
		String tmp = "";
		for(int i = 0; i < cardDeck.length; i++)
		{
			tmp = tmp + Integer.toString(cardDeck[i]) + "\r\n";
		}
		
		return tmp;
	}
	
	//test method for use while the game is running
	//helps us keep track of our current position in the deck
	public String printDeck()
	{
		String tmp = "";
		for(int i = 0; i < cardDeck.length; i++)
		{
			if(i == current)
			{
				tmp = tmp + "---top\r\n";
			}
			tmp = tmp + Integer.toString(cardDeck[i]) + "\r\n";
		}
		return tmp;
	}
}
