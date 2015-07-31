/*
 * Welcome to Ari Check's crude game of blackjack.
 * Everything really gets started in the Blackjack.java class so head over there.
 */

package blackjack;

import org.eclipse.swt.widgets.Display;

public class Main 
{
	public static void main(String[] args)
	{		
		Display display = new Display();
		Blackjack bjack = new Blackjack(display);
		bjack.startDraw();
	}
}