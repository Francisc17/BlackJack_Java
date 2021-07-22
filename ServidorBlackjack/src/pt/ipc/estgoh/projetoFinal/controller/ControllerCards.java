package pt.ipc.estgoh.projetoFinal.controller;

import pt.ipc.estgoh.projetoFinal.model.Card;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class ControllerCards {

    private CopyOnWriteArrayList<Card> listCards;
    private int orderHand;

    public ControllerCards() {
        this.orderHand = 0;
        listCards = new CopyOnWriteArrayList<>();
        registerCards();
        Collections.shuffle(this.listCards);

    }

    public void registerCards() {

        if (this.listCards != null) {

            this.listCards.add(new Card("s1", 11));
            this.listCards.add(new Card("s2", 2));
            this.listCards.add(new Card("s3", 3));
            this.listCards.add(new Card("s4", 4));
            this.listCards.add(new Card("s5", 5));
            this.listCards.add(new Card("s6", 6));
            this.listCards.add(new Card("s7", 7));
            this.listCards.add(new Card("s8", 8));
            this.listCards.add(new Card("s9", 9));
            this.listCards.add(new Card("s10", 10));
            this.listCards.add(new Card("sj", 10));
            this.listCards.add(new Card("sk", 10));
            this.listCards.add(new Card("sq", 10));

            this.listCards.add(new Card("d1", 11));
            this.listCards.add(new Card("d2", 2));
            this.listCards.add(new Card("d3", 3));
            this.listCards.add(new Card("d4", 4));
            this.listCards.add(new Card("d5", 5));
            this.listCards.add(new Card("d6", 6));
            this.listCards.add(new Card("d7", 7));
            this.listCards.add(new Card("d8", 8));
            this.listCards.add(new Card("d9", 9));
            this.listCards.add(new Card("d10", 10));
            this.listCards.add(new Card("dj", 10));
            this.listCards.add(new Card("dk", 10));
            this.listCards.add(new Card("dq", 10));

            this.listCards.add(new Card("h1", 11));
            this.listCards.add(new Card("h2", 2));
            this.listCards.add(new Card("h3", 3));
            this.listCards.add(new Card("h4", 4));
            this.listCards.add(new Card("h5", 5));
            this.listCards.add(new Card("h6", 6));
            this.listCards.add(new Card("h7", 7));
            this.listCards.add(new Card("h8", 8));
            this.listCards.add(new Card("h9", 9));
            this.listCards.add(new Card("h10", 10));
            this.listCards.add(new Card("hj", 10));
            this.listCards.add(new Card("hk", 10));
            this.listCards.add(new Card("hq", 10));

            this.listCards.add(new Card("c1", 11));
            this.listCards.add(new Card("c2", 2));
            this.listCards.add(new Card("c3", 3));
            this.listCards.add(new Card("c4", 4));
            this.listCards.add(new Card("c5", 5));
            this.listCards.add(new Card("c6", 6));
            this.listCards.add(new Card("c7", 7));
            this.listCards.add(new Card("c8", 8));
            this.listCards.add(new Card("c9", 9));
            this.listCards.add(new Card("c10", 10));
            this.listCards.add(new Card("cj", 10));
            this.listCards.add(new Card("ck", 10));
            this.listCards.add(new Card("cq", 10));
        }
    }

    //only for test
    public Card getSpecificCard(int position) {
	    return this.listCards.get(position);
    }

    public Card getCard() {

        if (orderHand == 52) {
            return null;
        } else {
            if (this.listCards != null && this.listCards.size() > 0) {

                Card nextCard = this.listCards.get(orderHand);

                orderHand++;

                return nextCard;
            }
        }

        return null;
    }
}