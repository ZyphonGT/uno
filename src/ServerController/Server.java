package ServerController;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections; // For Min Max
import java.util.Random;
import java.util.Stack;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import CardModel.WildCard;
import GameModel.Game;
import GameModel.Player;
import Interfaces.GameConstants;
import Interfaces.UNOConstants;
import View.Session;
import View.UNOCard;

public class Server implements GameConstants {
	private Game game;
	private Session session;
	private Stack<UNOCard> playedCards;
	public boolean canPlay;
	private int mode;

	public Server() {

//		System.out.println("**** Inside SERVER.Server ****");

		mode = requestMode();
		game = new Game(mode);
		playedCards = new Stack<UNOCard>();

		// First Card
		UNOCard firstCard = game.getCard();
		firstCard = modifyFirstCard(firstCard);

		playedCards.add(firstCard);
		session = new Session(game, firstCard);

		game.whoseTurn();
		canPlay = true;
	}

	//return if it's 2-Player's mode or PC-mode
	private int requestMode() {

//		System.out.println("**** Inside SERVER.requestMode ****");

		Object[] options = { "No Friends", "With Friends", "Cancel" };

		int n = JOptionPane.showOptionDialog(null,
				"Choose a Game Mode to play", "Game Mode",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);

		if (n == 2)
			System.exit(1);

		return GAMEMODES[n];
	}
	
	//coustom settings for the first card
	private UNOCard modifyFirstCard(UNOCard firstCard) {

//		System.out.println("**** Inside SERVER.modifyFirstCard ****");

		firstCard.removeMouseListener(CARDLISTENER);

		/*											If get WildCard, then randomize color <-- OLD, should avoid wildCard in the first place
		if (firstCard.getType() == WILD) {

			int random = new Random().nextInt() % 4;
			try {
				((WildCard) firstCard).useWildColor(UNO_COLORS[Math.abs(random)]);
			} catch (Exception ex) {
				System.out.println("something wrong with modifyFirstcard");
			}
		}
		*/

		while(firstCard.getType() == WILD) {
			//System.out.println("GOT A WILD CARD, Rerolling...");
			firstCard  = game.getCard();
		}

		return firstCard;

	}
	
	//return Main Panel
	public Session getSession() {
		return this.session;
	}
	
	
	//request to play a card
	public void playThisCard(UNOCard clickedCard) {

//		System.out.println("**** Inside SERVER.playThisCard ****" + clickedCard.getValue());

		// Check player's turn
		if (!isHisTurn(clickedCard)) {
			infoPanel.setError("It's not your turn");
			infoPanel.repaint();
		} else {											// Player clicked a Card

			// Card validation
			if (isValidMove(clickedCard)) {

				clickedCard.removeMouseListener(CARDLISTENER);
				playedCards.add(clickedCard);
				game.removePlayedCard(clickedCard);

				// function cards ??
				switch (clickedCard.getType()) {
				case ACTION:
					//System.out.println("ACTION CARD PLAYED");
					performAction(clickedCard);
					break;
				case WILD:
					//System.out.println("WILD CARD PLAYED");
					performWild((WildCard) clickedCard);
					break;
				default:
					//System.out.println("NUMBER CARD PLAYED");
					break;
				}

				game.switchTurn();
				session.updatePanel(clickedCard);
				checkResults();
			} else {
				infoPanel.setError("invalid move");
				infoPanel.repaint();
			}
			
		}
		
		
		
		if(mode==vsPC && canPlay){
			if(game.isPCsTurn()){
				game.playPC(peekTopCard());
			}
		}
	}

	//Check if the game is over
	private void checkResults() {

//		System.out.println("**** Inside SERVER.checkResult ****");

		if (game.isOver()) {
			canPlay = false;
			infoPanel.updateText("GAME OVER");
		}

	}
	
	//check player's turn
	public boolean 	isHisTurn(UNOCard clickedCard) {

//		System.out.println("**** Inside SERVER.isHisTurn ****");

		for (Player p : game.getPlayers()) {
			if (p.hasCard(clickedCard) && p.isMyTurn())
				return true;
		}
		return false;
	}

	//check if it is a valid card compared to topCard
	public boolean isValidMove(UNOCard playedCard) {

//		System.out.println("**** Inside SERVER.isValidMove ****");

		UNOCard topCard = peekTopCard();

		String colorOfPlayedCard = "";

		if(playedCard.getColor() == UNOConstants.RED){
			colorOfPlayedCard = "Red";
		} else if(playedCard.getColor() == UNOConstants.BLUE) {
			colorOfPlayedCard = "Blue";
		} else if(playedCard.getColor() == UNOConstants.GREEN) {
			colorOfPlayedCard = "Green";
		} else if(playedCard.getColor() == UNOConstants.YELLOW) {
			colorOfPlayedCard = "Yellow";
		} else if(playedCard.getColor() == UNOConstants.BLACK) {
			colorOfPlayedCard = "Wild Card";
		}

		if (playedCard.getColor().equals(topCard.getColor())
				|| playedCard.getValue().equals(topCard.getValue())) {
			System.out.println("Played Card : " + playedCard.getValue() + " of " + colorOfPlayedCard);
			return true;
		}

		else if (playedCard.getType() == WILD) {
			System.out.println("Played Card : " + playedCard.getValue() + " of " + colorOfPlayedCard);
			return true;
		} else if (topCard.getType() == WILD) {
			Color color = ((WildCard) topCard).getWildColor();
			if (color.equals(playedCard.getColor())) {
				System.out.println("Played Card : " + playedCard.getValue() + " of " + colorOfPlayedCard);
				return true;
			}
		}
		return false;
	}

	// ActionCards
	private void performAction(UNOCard actionCard) {

//		System.out.println("**** Inside SERVER.performAction ****");

		// Draw2PLUS
		if (actionCard.getValue().equals(DRAW2PLUS))
			game.drawPlus(2);
		else if (actionCard.getValue().equals(REVERSE))
			game.switchTurn();
		else if (actionCard.getValue().equals(SKIP))
			game.switchTurn();
	}

	private void performWild(WildCard functionCard) {

//		System.out.println("**** Inside SERVER.performWild ****");

		///		WILD	RED		BLUE	GREEN	YELLOW
		///		0		1		2		3		4
		//System.out.println(game.whoseTurn());
		if(mode==1 && game.isPCsTurn()){								// JIKA BOT YANG MEMAINKAN WILDCARD

			/*	***OLD Random-Based Color Pick***

			int random = new Random().nextInt() % 4;
			functionCard.useWildColor(UNO_COLORS[Math.abs(random)]);
			*/

			int colors[] = game.getPlayers()[0].getCardColor();
			int maxIndex = 1;
			for(int i = 2; i<=4;i++) {
				if(colors[maxIndex] < colors[i]) {
					System.out.println(maxIndex + " beaten by " + i);
					maxIndex = i;
				}
			}
			System.out.println("AI picked " + UNO_COLORS[maxIndex-1]);
			functionCard.useWildColor(UNO_COLORS[maxIndex-1]); // Because UNO Colors starts from 0

		}
		else{
			
			ArrayList<String> colors = new ArrayList<String>();
			colors.add("RED");
			colors.add("BLUE");
			colors.add("GREEN");
			colors.add("YELLOW");
			
			String chosenColor = (String) JOptionPane.showInputDialog(null,
					"Choose a color", "Wild Card Color",
					JOptionPane.DEFAULT_OPTION, null, colors.toArray(), null);
	
			functionCard.useWildColor(UNO_COLORS[colors.indexOf(chosenColor)]);
		}
		
		if (functionCard.getValue().equals(W_DRAW4PLUS))
			game.drawPlus(4);
	}


	// Player Drawing Card
	public void requestCard() {

//		System.out.println("**** Inside SERVER.requestCard ****");

		game.drawCard(peekTopCard());

		session.refreshPanel();
		
		if(mode==vsPC && canPlay){
			if(game.isPCsTurn())
				game.playPC(peekTopCard());
		}
		
		session.refreshPanel();
	}

	public UNOCard peekTopCard() {

//		System.out.println("**** Inside SERVER.peekTopCard ****");

		game.setServer(this);

		return playedCards.peek();
	}

	public void submitSaidUNO() {
		game.setSaidUNO();
	}
}
