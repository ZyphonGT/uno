package GameModel;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Arrays;

import javax.sound.midi.Receiver;

import CardModel.WildCard;
import Interfaces.GameConstants;
import Interfaces.UNOConstants;
import View.UNOCard;

public class PC extends Player implements GameConstants {
/*
    ///     WILD    RED     BLUE    GREEN   YELLOW
    ///     0       1       2       3       4
    public int[] cardColor = new int[5];

*/



    public PC() {
		setName("PC");
		super.setCards();
	}

	public PC(Player player) {
	}

	//PC plays a card
	public boolean play(UNOCard topCard) {

        Arrays.fill(cardColor, 0);
        System.out.println("Enemy card : " + getEnemyCards().size());
//      System.out.println("Checking My Cards...");
        System.out.println("My Card : " + getAllCards().size());
        for(UNOCard card : getAllCards()) {
            if(card.getType() == WILD )cardColor[0]++;
            if(card.getColor() == RED )cardColor[1]++;
            if(card.getColor() == BLUE )cardColor[2]++;
            if(card.getColor() == GREEN )cardColor[3]++;
            if(card.getColor() == YELLOW )cardColor[4]++;
        }
//        System.out.println( "Number of BOT's Wildcard : "+cardColor[0]+ "\n Number of Red : "+cardColor[1]+"\n Number of Blue : "+cardColor[2]+"\n Number of Green : "+cardColor[3]+ "\n Number of Yellow : "+cardColor[4]);

		boolean done = false;

		Color color = topCard.getColor();
		String value = topCard.getValue();
		
		if(topCard.getType()==WILD){
            System.out.println("Getting Wild Card Color...");
            color = ((WildCard) topCard).getWildColor();
		}

        /**
         *  Default Strategy
         */

        if(getAllCards().size()==2) {
            this.saysUNO();
        }

		for (UNOCard card : getAllCards()) {
			if (card.getColor().equals(color) || card.getValue().equals(value)) {	// Asalkan warna atau angka sama
                System.out.println("Picked Normal Card...");   						// maka pilih kartu itu
				MouseEvent doPress = new MouseEvent(card, MouseEvent.MOUSE_PRESSED,
						System.currentTimeMillis(),
						(int) MouseEvent.MOUSE_EVENT_MASK,5,5, 1, true);

				card.dispatchEvent(doPress);
				
				MouseEvent doRelease = new MouseEvent(card, MouseEvent.MOUSE_RELEASED,
						System.currentTimeMillis(),
						(int) MouseEvent.MOUSE_EVENT_MASK,5,5, 1, true);
				card.dispatchEvent(doRelease);
				
				done = true;
				break;
			}
		}

		// if no card was found, play wild card
		if (!done) {
			for (UNOCard card : getAllCards()) {
				if (card.getType() == WILD) {
                    System.out.println("Picked Wild Card...");
                    MouseEvent doPress = new MouseEvent(card,
							MouseEvent.MOUSE_PRESSED,
							System.currentTimeMillis(),
							(int) MouseEvent.MOUSE_EVENT_MASK, 5, 5, 1, true);
					card.dispatchEvent(doPress);
					
					MouseEvent doRelease = new MouseEvent(card, MouseEvent.MOUSE_RELEASED,
							System.currentTimeMillis(),
							(int) MouseEvent.MOUSE_EVENT_MASK, 5, 5, 1, true);
					card.dispatchEvent(doRelease);
					
					done = true;
					break;
				}
			}
		}
		
		if(getTotalCards()==1 || getTotalCards()==2) {
            saysUNO();
            System.out.println("Said UNO...");
        }

        /**
        *   END OF STRATEGY
        */

        return done;
	}
}
