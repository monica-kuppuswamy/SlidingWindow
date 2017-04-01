package com.src.slidingwindow;

import java.net.*;
import java.util.ArrayList;

class Receiver
{
	public static double lossPacketProbability = 0.1;
	
	public static void main(String args[]) throws Exception
	{	
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
			
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			
			if(Math.random() > lossPacketProbability) {
				System.out.println("Recieved Seq Number: " + rcvPacket.getSequenceNumber());
				Acknowledgement ack;
				if (rcvPacket.getSequenceNumber() == expectedAck) {
					ack = new Acknowledgement(expectedAck);
					sentAcks.add(ack);
				} else {
					ack = new Acknowledgement(sentAcks.get(sentAcks.size() - 1).getAckNumber());
				}
				sendData = Data.toBytes(ack);
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				System.out.println("ACK Sent: " + ack.getAckNumber());
				serverSocket.send(sendPacket);
				expectedAck++;
				
				if (expectedAck > rcvPacket.getWindowSize() + 1) {
					expectedAck = 1;
				}
				
			} else {
				System.out.println("***PACKET LOSS***");
				String response = "Packet Loss";
				sendData = response.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
			}
		}
	}
}
