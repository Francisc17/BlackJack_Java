package pt.ipc.estgoh.projetoFinal.model;

import java.io.Serializable;

public class Card implements Serializable {

	private String name;
	private int value;


	public Card(String aName, int aValue) {
		this.name = aName;
		this.value = aValue;
	}

	public String getName() {
		return this.name;
	}

	public int getValue() {
		return this.value;
	}

    public void setValue(int aValue) {
		this.value = aValue;
    }
}