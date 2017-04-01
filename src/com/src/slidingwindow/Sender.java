package com.src.slidingwindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Sender
{	
	public static String protocolName;
	public static int noOfBitsInSeq;
	public static int windowSize;
	public static int timeOutValue;
	public static int maxSegmentSize;
	public static int totalNoOfPackets;
	public static double bitErrorProbability = 0.1;
	public static double lossAckProbability = 0.05;
	
	public static void main(String args[]) throws Exception
	{
		
		// Initialization and retrieving algorithm parameters from file
		String filePath = (System.getProperty("user.dir")).toString() + "\\input file\\" + args[0];
		List<String> algorithmParameters = new ArrayList<String>();
		File f = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = null;
		while((line = br.readLine()) != null) {
			algorithmParameters.add(line);
		}
		br.close();
		
		protocolName = algorithmParameters.get(0).trim();
		noOfBitsInSeq = Integer.parseInt(algorithmParameters.get(1).trim());
		windowSize = Integer.parseInt(algorithmParameters.get(2).trim());
		timeOutValue = Integer.parseInt(algorithmParameters.get(3).trim());
		maxSegmentSize = Integer.parseInt(algorithmParameters.get(4).trim());
		totalNoOfPackets = Integer.parseInt(args[2]);
		
		Data.setWindowSize(windowSize);
		
		DatagramSocket clientSocket = new DatagramSocket();
		int portNumber = Integer.parseInt(args[1]);
		InetAddress IPAddress = InetAddress.getByName("localhost");
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		
		int base = 1;
		int nextSequenceNumber = 1;
		int i = 1;
		ArrayList<Packet> sentPackets = new ArrayList<Packet>();
		
		while (i <= totalNoOfPackets) {
			try {
				if (nextSequenceNumber - base < windowSize) {
					
					Packet pkt = Data.makePacket(nextSequenceNumber);
					
					/**
					 * Sending few packets with some bit error with probability causing bit error is 0.1.
					 * One in every 10 packets has bit error
					 */
					if(Math.random() < bitErrorProbability) {
						String errorData = new String(pkt.getData());
						errorData = Data.changeBit(errorData);
						pkt.setData(errorData.getBytes());
					}
					
					System.out.println("Base: " + base);
					System.out.println("Next seq: " + nextSequenceNumber);
					
					// Converting the packet object to bytes to send it to receiver
					sendData = Data.toBytes(pkt);
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
					
					// Printing packet information
					System.out.println(" PACKET NUMBER: " + i + "SEQ NO: " + pkt.getSequenceNumber());
					
					// Send it to the receiver socket
					clientSocket.send(sendPacket);
					
					if (base == nextSequenceNumber) {
						clientSocket.setSoTimeout(timeOutValue);
					}
					
					// Add the packet to the sent list
					sentPackets.add(pkt);
					
					// Increment sequence number
					nextSequenceNumber++;
					
				} else {
					System.out.println("REFUSING PACKET AS IT EXCEEDS WINDOW SIZE");
				}
				
				i++;
				
				// Receiving acknowledgement for the packet
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				
				String response = new String(receivePacket.getData());
				if (response.contains("Packet Loss")) {
					System.out.println("***PACKET LOSS***");
				} else {
					
					// Simulating ACK loss with probability 0.05
					if (Math.random() < lossAckProbability) {
						System.out.println("***ACK LOSS***");
					} else {
						
						// Converting it and displaying
						Acknowledgement ack = (Acknowledgement) Data.toObject(receivePacket.getData());
						System.out.println("RECEIVED ACK: " + ack.getAckNumber());
						System.out.println("\n");
						
						if (ack.getAckNumber() == base) {
							base = ack.getAckNumber() + 1;
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Time out expired. Resending unacknowledged packets");
			}
		} 
		clientSocket.close();
	}
}