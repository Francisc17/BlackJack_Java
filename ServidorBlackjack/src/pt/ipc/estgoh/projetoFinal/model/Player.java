package pt.ipc.estgoh.projetoFinal.model;


import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class Player implements Serializable {

    private CopyOnWriteArrayList<Card> cards;
    private int chipsPlayer;
    private String name;
    private boolean isObservable;
    private int gainPoints;
    private int position;

    public Player() {
    }

    public Player(String aName) {

        this.name = aName;
        this.isObservable = false;
        this.gainPoints = 0;
        this.chipsPlayer = 10;
        this.position = -1;
        this.cards = new CopyOnWriteArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public int getGain() {
        return this.gainPoints;
    }

    public void setGain(int aGain) {
        this.gainPoints = aGain;
    }

    public void setPosition(int aPosition) {
        this.position = aPosition;
    }

    public int getPosition() {
        return this.position;
    }

    public void setChipsPlayer(int aChipsPlayer) {

        this.chipsPlayer = aChipsPlayer;
    }

    public int getChipsPlayer() {
        return this.chipsPlayer;
    }

    public boolean getIsObservable() {
        return this.isObservable;
    }

    public void setIsObservable(boolean aIsObservable) {
        this.isObservable = aIsObservable;
    }

    public void setCards(Card card) {

        if (this.cards != null)
            this.cards.add(card);
    }

    public void resetCards(){
        this.cards = new CopyOnWriteArrayList<>();
    }

    public CopyOnWriteArrayList<Card> getCards() {
        return this.cards;
    }
}