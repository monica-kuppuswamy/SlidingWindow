package com.src.slidingwindow;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Packet implements Serializable{
	
	private int sequenceNumber;
	private byte[] data;
	private byte[] checksum;
	private int windowSize;
	
	public Packet(int sequenceNumber, byte[] data, byte[] checksum, int windowSize) {
		super();
		this.sequenceNumber = sequenceNumber;
		this.data = data;
		this.checksum = checksum;
		this.windowSize = windowSize;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getChecksum() {
		return checksum;
	}
	
	public void setChecksum(byte[] checksum) {
		this.checksum = checksum;
	}
	
	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

}