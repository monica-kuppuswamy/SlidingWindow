package com.src.slidingwindow;

/**
 * @author Monica Padmanabhan Kuppuswamy
 * @author Prayas Rode
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

public class Data {
	
	public static int windowSize;
	
	public static void setWindowSize(int size) {
		Data.windowSize = size;
	}
	
	public static int getWindowSize(){
		return Data.windowSize;
	}
	
	/**
	 * Generates a random 16 bit binary data
	 * @return The 16 bit binary string
	 */
	public static String getBinaryData() {
		String binaryString = " ";
		Random r = new Random();
		int data = r.nextInt(65536);
		binaryString = Integer.toString(data, 2);
		if (binaryString.length() > 16) {
			binaryString = binaryString.substring(0, 15);
		} else {
			int length = 16 - binaryString.length();
			for (int i = 0; i < length; i++) {
				binaryString = "0" + binaryString;
			}
		}
		return binaryString;
	}
	
	/**
	 * Performs one complement for a binary data
	 * @param data Binary data to take ones complement to get the checksum
	 * @return Binary string which contains the checksum value
	 */
	public static String takeOnesComplement(String data) {
		StringBuilder binaryChecksum = new StringBuilder();
		for(char c: data.toCharArray()) {
			switch(c) {
				case '0':
					binaryChecksum.append('1');
				break;
				case '1':
					binaryChecksum.append('0');
				break;
			}
		}
		return binaryChecksum.toString();
	}
	
	/**
	 * Performs the operation of calculating checksum splitting 16 bit binary data into two segments
	 * of 8 bit data each and performing ones complement to obtain the checksum.
	 */
	public static String sumData(String binaryData) {
		int data1 = Integer.parseInt(binaryData.substring(0, 8), 2);
		int data2 = Integer.parseInt(binaryData.substring(8, 16), 2);
		int sum = data1 + data2;
		String resultBinary = Integer.toString(sum, 2);
		
		// If there is a carry wrap around and add
		if (resultBinary.length() > 8 && resultBinary.charAt(0) == '1') {
			data1 = Integer.parseInt(resultBinary.substring(1, 9), 2);
			data2 = Integer.parseInt(resultBinary.substring(0, 1), 2);
			sum = data1 + data2;
			resultBinary = Integer.toString(sum, 2);
			
			if (resultBinary.length() < 8) {
				int length = 8 - resultBinary.length();
				for (int i = 0; i < length; i++) {
					resultBinary = "0" + resultBinary;
				}
			}
		} else {
			int length = 8 - resultBinary.length();
			for (int i = 0; i < length; i++) {
				resultBinary = "0" + resultBinary;
			}
		}
		return resultBinary;
	}
	
	public static String changeBit(String data) {
		char[] charData = data.toCharArray();
		
		Random m = new Random();
		int bitErrorIndex = m.nextInt(16);
		if (charData[bitErrorIndex] == '0') {
			charData[bitErrorIndex] = '1';
		} else {
			charData[bitErrorIndex] = '0';
		}
		return String.valueOf(charData);
	}
	
	/**
	 * Converts an object to byte
	 * @param o The object which has to converted to byte
	 * @return The byte equivalent for the object passed
	 * @throws IOException
	 */
	public static byte[] toBytes(Object o) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
		objStream.writeObject(o);
		return byteStream.toByteArray();
	}
	
	/**
	 * Converts a byte to Object
	 * @param b The byte which has to converted to object
	 * @return The converted data from a byte to object
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Object toObject(byte[] b) throws ClassNotFoundException, IOException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(b);
		ObjectInputStream objStream = new ObjectInputStream(byteStream);
		return objStream.readObject();
	}
	
	public static Packet makePacket(int nextSequenceNumber)
	{
		String binaryData = null;
		String binaryChecksum = null;
		
		// Generate binary data and compute checksum
		binaryData = Data.getBinaryData();
		String dataSum = Data.sumData(binaryData);
		binaryChecksum = Data.takeOnesComplement(dataSum);
		
		// Create a new packet with a new data, checksum and sequence number
		Packet pkt = new Packet(nextSequenceNumber, binaryData.getBytes(), binaryChecksum.getBytes(), windowSize);
		
		return pkt;
	}
}