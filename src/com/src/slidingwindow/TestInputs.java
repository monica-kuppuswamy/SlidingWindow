package com.src.slidingwindow;

import java.util.Random;

public class TestInputs {

	public TestInputs() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Random m = new Random();
		int num = m.nextInt(16);
		System.out.println(num);
		
		System.out.println(Math.random());
		
		Packet pkt = Data.makePacket(1);
		
		// Sending few packets with some bit error
		if(Math.random() > 0.1) {
			String data1 = new String(pkt.getData());
			System.out.println(new String(pkt.getData()));
			data1 = Data.changeBit(data1);
			System.out.println(data1);
		}
		
		
	}

}
