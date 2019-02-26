package com.first1444.frc.robot2019.vision;

public class LiveVisionTest {
	public static void main(String[] args){
		final PacketListener packetListener = new PacketListener(5801);
		packetListener.start();
		
		VisionInstant lastInstant = null;
		while(true){
			final VisionInstant instant = packetListener.getInstant(0);
			if(lastInstant != instant){
				lastInstant = instant;
				System.out.println(instant.toString().replaceAll(",", ",\n"));
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
