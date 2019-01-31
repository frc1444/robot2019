package com.first1444.frc.robot2019;

public class Test {
	public static void main(String[] args){
		final long start = System.nanoTime();
		final double b = 360;
		for(double i = 0; i < 10000; i++){
			final double result = i % b;
		}
		System.out.println("Took " + (System.nanoTime() - start));
	}
}
