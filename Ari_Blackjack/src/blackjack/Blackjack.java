/*
 * Here's the primary Blackjack class. I apologize in advance for the Thread.sleeps because they're annoying but I wanted
 * you to be able to see what was going on.
 * 
 * Unit tests have been included into the GUI so run them as you wish.
 * 
 * I did choose to do the GUI portion as a "bonus" but I did not implement the rest of blackjack rules because, well...
 * I just don't know blackjack very well. I had to do a little research to get just what I have here. I suppose
 * I need some culturing.
 */

package blackjack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Blackjack 
{
	//primary display components
	private Display display;
	private Shell shell;
	
	//UI elements
	private Button buttonStay;
	private Button buttonHit;
	private Button buttonEnd;
	private Button buttonReset;
	private Group groupDealer;
	private Group groupPlayer;
	private Label labelPlayerCards;
	private Label labelDealerCards;
	
	//test elements
	private Group groupTest;
	private Text labelTestLog;
	private Button buttonTestDeck;
	private Button buttonShowDeck;
	private Button buttonPlayerTotal;
	private Button buttonDealerTotal;
	
	//game pieces
	public static final int BUST = -1;
	private Player player;
	private Dealer dealer;
	public static boolean gameOver;
	private boolean handOver;
	private static boolean playerTurn;
	
	//display values
	Rectangle internalBounding;
	
	public Blackjack(Display display)
	{
		this.display = display;
		this.shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		
		player = new Player();
		dealer = new Dealer();
		
		handOver = true;
		gameOver = false;
		playerTurn = false;
		
		initializeGUI();
	}
	
	//initialize the GUI elements
	public void initializeGUI()
	{
		//hard coding lots and lots of values which is bad
		//wanted to throw a quick UI together though
				
		shell.setText("Blackjack");
		Rectangle boundRect = new Rectangle(100,100,800,600);
		shell.setBounds(boundRect);
		internalBounding = shell.getClientArea();
				
		groupDealer = new Group(shell, SWT.SHADOW_IN);
		groupDealer.setText("Dealer:");
		groupDealer.setBounds(0,0,500,300);
		groupDealer.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		groupPlayer = new Group(shell, SWT.SHADOW_IN);
		groupPlayer.setText("Player:");
		groupPlayer.setBounds(0,300,500,internalBounding.height);
		
		groupTest = new Group(shell, SWT.SHADOW_IN);
		groupTest.setText("Tests:");
		groupTest.setBounds(500, 0, 300, internalBounding.height);
		
		labelDealerCards = new Label(groupDealer, SWT.NONE);
		labelDealerCards.setText("");
		labelDealerCards.pack();
		labelDealerCards.setLocation(20, 100);
		
		labelPlayerCards = new Label(groupPlayer, SWT.NONE);
		labelPlayerCards.setText("");
		labelPlayerCards.pack();
		labelPlayerCards.setLocation(20, 100);
		
		//ends the players turn
		buttonStay = new Button(groupPlayer, SWT.PUSH);
		buttonStay.setText("Stay");
		buttonStay.pack();
		buttonStay.setLocation(20,225);
		buttonStay.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if(playerTurn)
				{
					playerTurn = false;
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{				
			}
		});
		
		//asks politely for another card
		//auto fail in the event of a bust
		//otherwise the "winner" handling will happen in the main loop after the dealer takes it's turn
		buttonHit = new Button(groupPlayer, SWT.PUSH);
		buttonHit.setText("Hit");
		buttonHit.pack();
		buttonHit.setLocation(220, 225);
		buttonHit.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if(playerTurn)
				{
					if(dealer.deck.getCount() == 0)
					{
						endGame();
						return;
					}
					
					player.hit(dealer.deal());
					
					updateCurrCards();
					
					if(player.getScore() == Blackjack.BUST)
					{
						playerTurn = false;
						
						try 
						{
							Thread.sleep(1000);
						}
						catch (InterruptedException e1) 
						{
							e1.printStackTrace();
						}
						
						dealerWinsHandBust();
						
						try 
						{
							Thread.sleep(3000);
						}
						catch (InterruptedException e1) 
						{
							e1.printStackTrace();
						}
						
						handOver = true;
					}
					
					updateCurrText();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{				
			}
		});
		
		//ends the game early if you so choose
		//will tally up the winnings
		buttonEnd = new Button(groupPlayer, SWT.PUSH);
		buttonEnd.setText("End");
		buttonEnd.pack();
		buttonEnd.setLocation(320, 225);
		buttonEnd.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if(!gameOver)
				{
					endGame();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{				
			}
		});
		
		//resets the game to it's starting position
		//will only work if the game has ended
		buttonReset = new Button(groupPlayer, SWT.PUSH);
		buttonReset.setText("Reset");
		buttonReset.pack();
		buttonReset.setLocation(420, 225);
		buttonReset.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if(gameOver)
				{
					resetGame();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{				
			}
		});
		
		//tests the deck shuffling
		//will end the game
		//displays deck order in debug text log
		buttonTestDeck = new Button(groupTest, SWT.PUSH);
		buttonTestDeck.setText("Shuffle (will end)");
		buttonTestDeck.pack();
		buttonTestDeck.setLocation(10,20);
		buttonTestDeck.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				endGame();
				
				labelTestLog.setText(dealer.deck.testShuffle());
				labelTestLog.setBounds(150, 20, 150, internalBounding.height - 20);
				labelTestLog.redraw();
				labelTestLog.update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{				
			}
		});
		
		//shows the deck as it currently stands for the game
		//does not end the game
		//will show where the current top of the deck is
		buttonShowDeck = new Button(groupTest, SWT.PUSH);
		buttonShowDeck.setText("Show deck:");
		buttonShowDeck.pack();
		buttonShowDeck.setLocation(10, 70);
		buttonShowDeck.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				labelTestLog.setText(dealer.deck.printDeck());
				labelTestLog.setBounds(150, 20, 150, internalBounding.height - 20);
				labelTestLog.redraw();
				labelTestLog.update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{				
			}
		});
		
		//displays the total value of the dealer's current hand
		buttonDealerTotal = new Button(groupTest, SWT.PUSH);
		buttonDealerTotal.setText("Dealer Total");
		buttonDealerTotal.pack();
		buttonDealerTotal.setLocation(10, 130);
		buttonDealerTotal.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				labelTestLog.setText(Integer.toString(dealer.getScore()));
				labelTestLog.setBounds(150, 20, 150, internalBounding.height - 20);
				labelTestLog.redraw();
				labelTestLog.update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{				
			}
		});
		
		//displays the total value of the player's current hand
		buttonPlayerTotal = new Button(groupTest, SWT.PUSH);
		buttonPlayerTotal.setText("Player Total");
		buttonPlayerTotal.pack();
		buttonPlayerTotal.setLocation(10, 180);
		buttonPlayerTotal.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				labelTestLog.setText(Integer.toString(player.getScore()));
				labelTestLog.setBounds(150, 20, 150, internalBounding.height - 20);
				labelTestLog.redraw();
				labelTestLog.update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{				
			}
		});
		
		labelTestLog = new Text(groupTest, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		labelTestLog.setEditable(false);
		labelTestLog.setBounds(150, 20, 150, internalBounding.height - 20);

		shell.open();
	}
	
	public void startDraw()
	{
		//game loop
		while(!shell.isDisposed())
		{
			if(!display.readAndDispatch())
			{				
				display.sleep();
			}
			
			//if we're still playing and the last hand is done: draw a new set of hands
			if(handOver && !gameOver)
			{
				newHand();
			}
			
			//once the player's turn is over it's the dealer's turn
			if(!playerTurn && !gameOver && !handOver)
			{
				//if we haven't busted and we're still under 17. Pick a card and update the visuals.
				if(dealer.getScore() < 17 && dealer.getScore() != Blackjack.BUST)
				{
					//if we have no more cards just end the game
					if(dealer.deck.getCount() > 0)
					{
						dealer.runAI();
						updateCurrCards();
						
						try 
						{
							Thread.sleep(1000);
						}
						catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
					}
					else
					{
						endGame();
					}
				}
				else//otherwise it's time to look at the score
				{
					handOver = true;
					
					if(dealer.getScore() == Blackjack.BUST)
					{
						playerWinsHandBust();
					}
					else if(dealer.getScore() > player.getScore())
					{
						dealerWinsHand();
					}
					else if(dealer.getScore() < player.getScore())
					{
						playerWinsHand();
					}
					else
					{
						tieHand();
					}
				}
				
				try 
				{
					Thread.sleep(2000);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				
			}
		}
		
		display.dispose();
	}
	
	//creats a new hand for the dealer and player
	//clears the old hands
	//calculates a blackjack winner
	private void newHand()
	{
		handOver = false;
		
		dealer.clearHand();
		player.clearHand();
		
		//if we can't even hit a single time per player it won't be much of a hand
		//just ending the game here
		if(dealer.deck.getCount() <= 6)
		{
			endGame();
			return;
		}
		
		//draw some cards
		player.hit(dealer.deal());
		dealer.hit(dealer.deal());
		player.hit(dealer.deal());
		dealer.hit(dealer.deal());
		
		updateCurrCards();
		updateCurrText();
		
		//check for a blackjack and/or tie
		if(dealer.getScore() == 21 || player.getScore() == 21)
		{
			try 
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			
			if(dealer.getScore() != 21)
			{
				playerWinsHandBlackjack();
			}
			else if(player.getScore() != 21)
			{
				dealerWinsHandBlackjack();
			}
			else
			{
				tieHand();
			}
			
			try 
			{
				Thread.sleep(3000);
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			
			handOver = true;//gotta go again if we blackjack
		}
		else
		{
			playerTurn = true;
		}
	}
	
	//updates the cards based on their current hands
	//also runs the update on the text visuals
	private void updateCurrCards()
	{
		String tmp = "";
		String[] tmpArr = new String[dealer.getHandSize()];
		tmpArr = dealer.getHand();
		for(int i = 0; i < tmpArr.length; i++)
		{
			tmp = tmp + tmpArr[i] + " | ";
		}
		labelDealerCards.setText(tmp);
		
		tmp = "";
		tmpArr = new String[player.getHandSize()];
		tmpArr = player.getHand();
		for(int i = 0; i < tmpArr.length; i++)
		{
			tmp = tmp + tmpArr[i] + " | ";
		}
		labelPlayerCards.setText(tmp);
		
		updateCurrText();
	}
	
	//updates the text visuals for the hands and scoring
	private void updateCurrText()
	{
		labelPlayerCards.pack();
		labelPlayerCards.redraw();
		labelPlayerCards.update();
		labelDealerCards.pack();
		labelDealerCards.redraw();
		labelDealerCards.update();
	}
	
	//resets the game to start a new game
	private void resetGame()
	{
		gameOver = false;
		handOver = true;
		
		player = new Player();
		dealer = new Dealer();
		
		labelPlayerCards.setText("");
		labelPlayerCards.redraw();
		labelPlayerCards.update();
		
		labelDealerCards.setText("");
		labelDealerCards.redraw();
		labelDealerCards.update();
	}
	
	//ends the current game
	//displays end game score whether the deck was finished or not
	private void endGame()
	{
		handOver = true;
		gameOver = true;
		playerTurn = false;
		
		if(player.score > dealer.score)
		{
			labelPlayerCards.setText("Player wins the game! Hands Won: " + Integer.toString(player.score));
			labelDealerCards.setText("Dealer loses the game! Hands Won: " + Integer.toString(dealer.score));
		}
		else if(player.score < dealer.score)
		{
			labelPlayerCards.setText("Player loses the game! Hands Won: " + Integer.toString(player.score));
			labelDealerCards.setText("Dealer wins the game! Hands Won: " + Integer.toString(dealer.score));
		}
		else
		{
			labelPlayerCards.setText("The game is a tie! Hands Won: " + Integer.toString(player.score));
			labelDealerCards.setText("The game is a tie! Hands Won: " + Integer.toString(dealer.score));
		}
		
		updateCurrText();
	}
	
	//below tis point are just a bunch of methods for easily displaying win/lose condition text
	private void playerWinsHand()
	{
		player.score++;
		labelPlayerCards.setText("Player wins the hand! Hands Won: " + player.score);
		labelDealerCards.setText("Dealer loses the hand! Hands Won: " + dealer.score);
		
		updateCurrText();
	}
	
	private void dealerWinsHand()
	{
		dealer.score++;
		labelPlayerCards.setText("Player loses the hand! Hands Won: " + player.score);
		labelDealerCards.setText("Dealer wins the hand! Hands Won: " + dealer.score);
		
		updateCurrText();
	}
	
	private void tieHand()
	{
		labelPlayerCards.setText("It's a tie! Hands Won: " + player.score);
		labelDealerCards.setText("It's a tie! Hands Won: " + dealer.score);
		
		updateCurrText();
	}
	
	private void playerWinsHandBust()
	{
		player.score++;
		labelPlayerCards.setText("Player wins the hand! Hands Won: " + player.score);
		labelDealerCards.setText("Dealer busts! Hands Won: " + dealer.score);
		
		updateCurrText();
	}
	
	private void dealerWinsHandBust()
	{
		dealer.score++;
		labelPlayerCards.setText("Player busts! Hands Won: " + player.score);
		labelDealerCards.setText("Dealer wins the hand! Hands Won: " + dealer.score);
		
		updateCurrText();
	}
	private void playerWinsHandBlackjack()
	{
		player.score++;
		labelPlayerCards.setText("Player wins, Blackjack! Hands Won: " + player.score);
		labelDealerCards.setText("Dealer loses the hand! Hands Won: " + dealer.score);
		
		updateCurrText();
	}
	
	private void dealerWinsHandBlackjack()
	{
		dealer.score++;
		labelPlayerCards.setText("Player loses the hand! Hands Won: " + player.score);
		labelDealerCards.setText("Dealer wins, Blackjack! Hands Won: " + dealer.score);
		
		updateCurrText();
	}
}
