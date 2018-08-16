package com.cvezga.influxdbwriter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadMyArduino2 {

	private static final int NDOTS = 500;

	public static void main(String[] args) {
		// long t1 = System.currentTimeMillis();

		boolean isLocal = args.length == 1;

		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		

		SensorReaderRunnable sensorReader = new SensorReaderRunnable(ip, port, NDOTS);

		ExecutorService executor = Executors.newSingleThreadExecutor();

		executor.execute(sensorReader);

		while (true) {
			try {

				sleep(100);

			} catch (Exception e) {
				e.printStackTrace();
				sleep(10_000);
				sensorReader = new SensorReaderRunnable(ip, 5001, NDOTS);

				executor = Executors.newSingleThreadExecutor();

				executor.execute(sensorReader);
			}
		}

	}

	private static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
