package com.src.slidingwindow;

/**
 * @author Monica Padmanabhan Kuppuswamy
 * @author Prayas Rode
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.*;
import java.util.ArrayList;

class Receiver
{
	public static double lossPacketProbability = 0.0;
	public static String protocolName;
	
	public static void main(String args[]) throws Exception
	{		
		String filePath = (System.getProperty("user.dir")).toString() + "\\input file\\SenderInput.txt";
		File f = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(f));
		protocolName = br.readLine();
		br.close();
		
		int portNumber = Integer.parseInt(args[0]);
		DatagramSocket serverSocket = new DatagramSocket(portNumber);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		int expectedAck = 1;
		ArrayList<Acknowledgement> sentAcks = new ArrayList<Acknowledgement>();
		
		while(true)
		{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			Packet rcvPacket = (Packet) Data.toObject(receivePacket.getData());
			
			/**
			 * Calculating checksum at the receivers side
			 */
			String packetChecksum = Data.sumData(new String(rcvPacket.getData()));
			String resultCheckSum = Data.sumData(packetChecksum + new String(rcvPacket.getChecksum()));
			boolean bitError = false;
			
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			
			if(Math.random() > lossPacketProbability) {
				
				int seqNo = -1;
				if (protocolName.equalsIgnoreCase("GBN")) {
					if (rcvPacket.getSequenceNumber() > rcvPacket.getWindowSize() + 1) {
						seqNo = rcvPacket.getSequenceNumber() % (rcvPacket.getWindowSize() + 1);
					} else {
						seqNo = rcvPacket.getSequenceNumber();
					}
				} else {
					if (rcvPacket.getSequenceNumber() > rcvPacket.getWindowSize() * 2) {
						seqNo = rcvPacket.getSequenceNumber() % (rcvPacket.getWindowSize() * 2);
					} else {
						seqNo = rcvPacket.getSequenceNumber();
					}
				}
				System.out.println("RECEIVED PACKET NO:" + rcvPacket.getSequenceNumber() + " SEQ NO: " + seqNo);
				
				Acknowledgement ack = null;
				
				/**
				 * Sending Acknowledgement based on where the protocol is Go-Back-N or SR
				 */
				if (protocolName.equalsIgnoreCase("GBN")) {
					if (rcvPacket.getSequenceNumber() == expectedAck && Integer.parseInt(resultCheckSum) == 11111111) {
						ack = new Acknowledgement(expectedAck);
						sentAcks.add(ack);
						expectedAck++;
					} else {
						System.out.println("***BIT ERROR DETECTED***");
						ack = new Acknowledgement(sentAcks.get(sentAcks.size() - 1).getAckNumber());
					}
				} else {
					
					// For SR
					if (Integer.parseInt(resultCheckSum) == 11111111) {
						ack = new Acknowledgement(rcvPacket.getSequenceNumber());
					} else {
						System.out.println("***BIT ERROR DETECTED***");
						bitError = true;
					}
				}

				if (!bitError) {
					sendData = Data.toBytes(ack);
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					
					// Print sent acknowledgement number from receiver to sender
					int ackNo = -1;
					if (protocolName.equalsIgnoreCase("GBN")) {
						if (ack.getAckNumber() > rcvPacket.getWindowSize() + 1) {
							ackNo = ack.getAckNumber() % (rcvPacket.getWindowSize() + 1);
						} else {
							ackNo = ack.getAckNumber();
						}
					} else {
						if (ack.getAckNumber() > rcvPacket.getWindowSize() * 2) {
							ackNo = ack.getAckNumber() % (rcvPacket.getWindowSize() * 2);
						} else {
							ackNo = ack.getAckNumber();
						}
					}
					System.out.println("ACK SENT: " + ackNo);
					System.out.println("\n");
					serverSocket.send(sendPacket);
				} else {
					sendData = ("Bit Error").getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					serverSocket.send(sendPacket);
				}

			} else {
				System.out.println("***PACKET LOSS***");
				System.out.println("\n");
				String response = "Packet Loss";
				sendData = response.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
			}
		}
	}
}