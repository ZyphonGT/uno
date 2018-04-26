package GameModel;

import java.util.LinkedList;

import View.UNOCard;

public class Player {

	private int enemyCardsCount;						//For Bot find out player's card amount
	private String name = null;
	private boolean isMyTurn = false;
	private boolean saidUNO = false;
	private LinkedList<UNOCard> myCards;

    private LinkedList<UNOCard> enemyCards;

	///     WILD    RED     BLUE    GREEN   YELLOW
	///     0       1       2       3       4
	public int[] cardColor = new int[5];
	public int[] enemyColor = new int[5];


	private int playedCards = 0;



	public Player(){
		myCards = new LinkedList<UNOCard>();
	}
	
	public Player(String player){
		setName(player);
		myCards = new LinkedList<UNOCard>();
	}

    public void setEnemyCards(LinkedList<UNOCard> enemyCards) {
        this.enemyCards = enemyCards;
    }

    public LinkedList<UNOCard> getEnemyCards() {
        return enemyCards;
    }

	public void setEnemyCardsCount(int x) {
		this.enemyCardsCount = x;
	}

	public int getEnemyCardsCount() {
		return this.enemyCardsCount;
	}

	public void setName(String newName){
		name = newName;
	}
	
	public String getName(){
		return this.name;
	}


	public void obtainCard(UNOCard card){
		myCards.add(card);
	}
	
	public LinkedList<UNOCard> getAllCards(){
		return myCards;
	}

	public int[] getCardColor(){
		return cardColor;
	}

	public int getTotalCards(){
		return myCards.size();
	}
	
	public boolean hasCard(UNOCard thisCard){
		return myCards.contains(thisCard);		
	}
	
	public void removeCard(UNOCard thisCard){

//        System.out.println("**** Inside PLAYER.removeCard ****");

		myCards.remove(thisCard);
		playedCards++;
	}
	
	public int totalPlayedCards(){
		return playedCards;
	}
	
	public void toggleTurn(){

//        System.out.println("**** Inside PLAYER.toggleTurn ****");

		isMyTurn = (isMyTurn) ? false : true;
	}
	
	public boolean isMyTurn(){
//        System.out.println("**** Inside PLAYER.isMyTurn ****");
		return isMyTurn;
	}
	
	public boolean hasCards(){
		return (myCards.isEmpty()) ? false : true;
	}
	
	public boolean getSaidUNO(){
		return saidUNO;
	}
	
	public void saysUNO(){
		saidUNO = true;
	}
	
	public void setSaidUNOFalse(){
		saidUNO = false;
	}
	
	public void setCards(){
		myCards = new LinkedList<UNOCard>();
	}
}
