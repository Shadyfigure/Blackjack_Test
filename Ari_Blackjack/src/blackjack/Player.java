package blackjack;

import java.util.ArrayList;

public class Player 
{
	//chose an ArrayList for the hand
	//easier to expand to however many cards we end up drawing
	private ArrayList<Integer> hand;
	public int score;//how many hands we've won
	
	public Player()
	{
		hand = new ArrayList<Integer>();
		score = 0;
	}
	
	//returns the number of cards in the hand
	public int getHandSize()
	{
		return hand.size();
	}
	
	//returns a text readout of the cards in the player's hand
	//translates the hundred's place into a suit
	//translates 1 to ace, 11 to jack, 12 to queen, 13 to king
	public String[] getHand()
	{
		String[] returnArr = new String[hand.size()];
		for(int i = 0; i < hand.size(); i++)
		{
			if((hand.get(i) % 100) == 1)
			{
				returnArr[i] = "Ace";
			}
			else if((hand.get(i) % 100) == 11)
			{
				returnArr[i] = "Jack";
			}
			else if((hand.get(i) % 100) == 12)
			{
				returnArr[i] = "Queen";
			}
			else if((hand.get(i) % 100) == 13)
			{
				returnArr[i] = "King";
			}
			else
			{
				returnArr[i] = Integer.toString(hand.get(i) % 100);
			}
			
			if((hand.get(i) - (hand.get(i) % 100)) == Deck.CLUBS)
			{
				returnArr[i] = returnArr[i] + " of Clubs";
			}
			else if((hand.get(i) - (hand.get(i) % 100)) == Deck.DIAMONS)
			{
				returnArr[i] = returnArr[i] + " of Diamonds";
			}
			else if((hand.get(i) - (hand.get(i) % 100)) == Deck.HEARTS)
			{
				returnArr[i] = returnArr[i] + " of Hearts";
			}
			else if((hand.get(i) - (hand.get(i) % 100)) == Deck.SPADES)
			{
				returnArr[i] = returnArr[i] + " of Spades";
			}
		}
		
		return returnArr;
	}
	

	//adds a card to the players hand
	public int hit(int cardId)
	{
		hand.add(cardId);
		
		return getScore();
	}
	
	//returns an int corresponding to current hand value or bust value	
	public int getScore()
	{
		int returnVal = 0;
		int numAces = 0;
		
		//first lets count the cards we can
		//and separate the aces
		for(int i = 0; i < hand.size(); i++)
		{
			if((hand.get(i) % 100) == 1)
			{
				numAces++;
			}
			else if((hand.get(i) % 100) == 11 || (hand.get(i) % 100 == 12) || (hand.get(i) % 100) == 13)
			{
				returnVal += 10;
			}
			else
			{
				returnVal += (hand.get(i) % 100);
			}
		}
		
		//if we're already over 21 then no sense in conitnuing
		//return busted value
		if(returnVal > 21)
		{
			return Blackjack.BUST;
		}
		
		//if our entire hand is aces
		//we can't have two 11's because that's already a bust
		//count 11 + the number of remaining aces as 1's
		if(numAces == hand.size())
		{
			//safety in case you happen to use 12 decks and pull all aces in a row
			//...yay!
			if((11 + numAces - 1) > 21)
			{
				return Blackjack.BUST;
			}
			else
			{
				return 11 + numAces - 1;
			}
		}
		//if we simply have a non-zero number of aces
		//see if an 11 fits
		//if not just add however many aces we have as 1's
		else if (numAces != 0)
		{
			if((returnVal + 11 + numAces - 1) <= 21)
			{
				returnVal = returnVal + 11 + numAces - 1;
			}
			else if((returnVal + numAces) <= 21)
			{
				returnVal += numAces;
			}
			else
			{
				return Blackjack.BUST;
			}
		}
		
		return returnVal;
	}
	
	public void clearHand()
	{
		hand.clear();
	}
}
