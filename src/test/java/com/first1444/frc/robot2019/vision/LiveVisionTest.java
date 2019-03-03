package com.first1444.frc.robot2019.vision;

public class LiveVisionTest {
	public static void main(String[] args){
		final PacketListener packetListener = new PacketListener(5801);
		packetListener.start();
		
		final VisionInstant[] lastInstants = new VisionInstant[2];
		while(true){
			for(int i = 0; i < 2; i++) {
				final int id = i + 1;
				final VisionInstant instant = packetListener.getInstant(id);
				if (lastInstants[i] != instant) {
					lastInstants[i] = instant;
					System.out.println(instant.toString().replaceAll(",", ",\n"));
				}
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
