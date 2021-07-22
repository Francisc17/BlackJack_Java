package pt.ipc.estgoh.projetoFinal.model;

import java.io.Serializable;

public class Log implements Serializable {

	private String message;
	private long time;

	public Log(String aMessage, long aTime) {
		this.message = aMessage;
		this.time = aTime;
	}

	public String getMesage() {
		return this.message;
	}

	public long getTime() {
		return this.time;
	}

}