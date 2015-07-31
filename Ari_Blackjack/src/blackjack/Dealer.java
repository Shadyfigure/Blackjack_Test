package blackjack;

//largely unnecessary class given how I set up the game. 
//But it would make any future enhancements a bit easier
//and I wasn't sure how far I was going to take this.
//Made sure I had room to play.
//extends player because the dealer needs all that stuff too.
public class Dealer extends Player
{
	public Deck deck;
	
	public Dealer()
	{
		super();
		
		deck = new Deck();
	}
	
	public void runAI()
	{
		if(this.getScore() < 17)
		{
			this.hit(deal());
		}
	}
	
	public void shuffle()
	{
		deck.shuffle();
	}
	
	public int deal()
	{
		return deck.draw();
	}
}
