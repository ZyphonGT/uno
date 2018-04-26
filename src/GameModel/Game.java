package GameModel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

import javax.swing.JOptionPane;

import CardModel.*;
import Interfaces.GameConstants;
import ServerController.Server;
import View.UNOCard;

public class Game implements GameConstants {

    private static Robot robot = null ;

    private Player[] players;
	private boolean isOver;
	private int GAMEMODE;
	
	private PC pc;
	private Dealer dealer;
	private Stack<UNOCard> cardStack;

	private Server server;


	
	
	public Game(int mode){		//
//        System.out.println("**** Inside GAME.Game() ****");

        try {
            robot = new Robot();
        } catch (AWTException err) {
            err.printStackTrace();
        }

        GAMEMODE = mode;
		
		//Create players
		String name = (GAMEMODE==MANUAL) ? JOptionPane.showInputDialog("Player 1") : "PC";	
		String name2 = JOptionPane.showInputDialog("Player 2");
		
		if(GAMEMODE==vsPC)
			pc = new PC();

		Player player1 = (GAMEMODE==vsPC) ? pc : new Player(name);

		Player player2 = new Player(name2);

		player2.toggleTurn();				//Initially, player2's turn
			
		players = new Player[]{player1, player2};

		//Create Dealer
		dealer = new Dealer();
		cardStack = dealer.shuffle();
		dealer.spreadOut(players);
		
		isOver = false;
	}

    public void setServer(Server x) {
        server = x;
    }

	public Player[] getPlayers() {
		return players;
	}

	public UNOCard getCard() {
		return dealer.getCard();
	}
	
	public void removePlayedCard(UNOCard playedCard) {

//        System.out.println("**** Inside GAME.removePlayedCard ****");

        for (Player p : players) {
			if (p.hasCard(playedCard)){
				p.removeCard(playedCard);
				
				if (p.getTotalCards() == 1 && !p.getSaidUNO()) {
					infoPanel.setError(p.getName() + " Forgot to say UNO");
					p.obtainCard(getCard());
					p.obtainCard(getCard());
				}else if(p.getTotalCards()>2){
					p.setSaidUNOFalse();
				}
			}			
		}
	}
	
	//give player a card
	public void drawCard(UNOCard topCard) {

//        System.out.println("**** Inside GAME.drawCard ****");

        boolean canPlay = false;

		for (Player p : players) {
			if (p.isMyTurn()) {

//                System.out.println("Inside drawCard, drawing = " +p.getName());

                System.out.print("Cards from " + p.getTotalCards() + " to ");
                System.out.println(p.getTotalCards()+1);

                UNOCard newCard = getCard();
				p.obtainCard(newCard);

				System.out.println("Drawn Card : " + newCard.getValue() + " of " + newCard.getColor());

				canPlay = canPlay(topCard, newCard);
				break;
			}
		}

		if (!canPlay) {
            System.out.println("Drew new card, still can't play");
            switchTurn();
        }
	}

	public void switchTurn() {

//        System.out.println("**** Inside GAME.switchTurn ****");

		for (Player p : players) {
			p.toggleTurn();
		}
		whoseTurn();
	}
	
	//Draw cards x times
	public void drawPlus(int times) {

//        System.out.println("**** Inside GAME.drawPlus ****");

		for (Player p : players) {
			if (!p.isMyTurn()) {
				for (int i = 1; i <= times; i++)
					p.obtainCard(getCard());
			}
		}
	}
	
	//response whose turn it is
	public void whoseTurn() {

//        System.out.println("**** Inside GAME.whoseTurn ****");

		for (Player p : players) {
			if (p.isMyTurn()){
				infoPanel.updateText(p.getName() + "'s Turn");
				System.out.println("\n--------------- "+p.getName() + "'s Turn ---------------");
			}
		}
		infoPanel.setDetail(playedCardsSize(), remainingCards());
		infoPanel.repaint();
	}
	
	//return if the game is over
	public boolean isOver() {

//        System.out.println("**** Inside GAME.isOver ****");

		if(cardStack.isEmpty()){
			isOver= true;
			return isOver;
		}
		
		for (Player p : players) {
			if (!p.hasCards()) {
				isOver = true;
				break;
			}
		}


        System.out.println("Checking Enemy Cards...");
        getPlayers()[0].setEnemyCardsCount(getPlayers()[1].getTotalCards());     // Tell PC about the amount of player cards.
        getPlayers()[0].setEnemyCards(getPlayers()[1].getAllCards());            // Send Player's Card List to PC

		return isOver;
	}

	public int remainingCards() {
		return cardStack.size();
	}

	public int[] playedCardsSize() {
		int[] nr = new int[2];
		int i = 0;
		for (Player p : players) {
			nr[i] = p.totalPlayedCards();
			i++;
		}
		return nr;
	}

	//Check if this card can be played
	private boolean canPlay(UNOCard topCard, UNOCard newCard) {

//        System.out.println("**** Inside GAME.canPlay ****");

		// Color or value matches
		if (topCard.getColor().equals(newCard.getColor())
				|| topCard.getValue().equals(newCard.getValue()))
			return true;
		// if chosen wild card color matches
		else if (topCard.getType() == WILD)
			return ((WildCard) topCard).getWildColor().equals(newCard.getColor());

		// suppose the new card is a wild card
		else if (newCard.getType() == WILD)
			return true;

		// else
		return false;
	}

	//Check whether the player said or forgot to say UNO
	public void checkUNO() {

//        System.out.println("**** Inside GAME.checkUNO ****");

		for (Player p : players) {
			if (p.isMyTurn()) {
				if (p.getTotalCards() == 1 && !p.getSaidUNO()) {
					infoPanel.setError(p.getName() + " Forgot to say UNO");
					p.obtainCard(getCard());
					p.obtainCard(getCard());
				}
			}
		}		
	}

	public void setSaidUNO() {

//        System.out.println("**** Inside GAME.setSaidUNO ****");

		for (Player p : players) {
			if (p.isMyTurn()) {
				if (p.getTotalCards() == 2) {
					p.saysUNO();
					infoPanel.setError(p.getName() + " said UNO");
				}
			}
		}
	}
	
	public boolean isPCsTurn(){

//        System.out.println("**** Inside GAME.isPCsTurn ****");

		if(pc.isMyTurn()){
			return true;
		}
		return false;
	}

	//if it's PC's turn, play it for pc
	public void playPC(UNOCard topCard) {

//        System.out.println("**** Inside GAME.playPC ****");

		if (pc.isMyTurn()) {
			boolean done = pc.play(topCard);
			
			if(!done) {
                System.out.println("NO LEGAL MOVE !!! going to drawCard...");
//                drawCard(topCard);      // Should be clicking the button

//                robot.mouseMove(943,185);
//
//                robot.delay(5);
//                robot.mousePress(MouseEvent.BUTTON1_MASK);
//                robot.mouseRelease(MouseEvent.BUTTON1_MASK);

                  server.requestCard();
            }
        }
	}

}
