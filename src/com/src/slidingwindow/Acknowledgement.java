package com.src.slidingwindow;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Acknowledgement implements Serializable {
	
	private int ackNumber;

	public Acknowledgement(int ackNumber) {
		super();
		this.ackNumber = ackNumber;
	}

	public void setAckNumber(int ackNumber) {
		this.ackNumber = ackNumber;
	}

	public int getAckNumber() {
		return ackNumber;
	}

}
