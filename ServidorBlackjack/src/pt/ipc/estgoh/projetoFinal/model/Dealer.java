package pt.ipc.estgoh.projetoFinal.model;

import java.util.concurrent.CopyOnWriteArrayList;

public class Dealer {

    private CopyOnWriteArrayList<Card> cardsDealer;
    private int points;


    public Dealer() {
        this.cardsDealer = new CopyOnWriteArrayList<>();
        this.points = 0;
    }

    public void setCards(Card card) {

        if (this.cardsDealer != null)
            this.cardsDealer.add(card);
    }

    public void resetCards(){
        this.cardsDealer = new CopyOnWriteArrayList<>();
    }

    public void  setPoints(int aNewPoints) {
        this.points = aNewPoints;
    }

    public CopyOnWriteArrayList<Card> getCards() {
        return this.cardsDealer;
    }

    public int getPoints() {
        return this.points;
    }

}
