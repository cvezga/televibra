package com.cvezga.influxdbwriter;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SensorReaderRunnable implements Runnable {

	private String ip;
	private int port;
	private int ndots;

	private ExecutorService executor;
	private InfluxWriterRunnable2 writer;
	private ImageWriterRunnable iwriter;

	public SensorReaderRunnable(String ip, int port, int ndots) {
		this.ip = ip;
		this.port = port;
		this.ndots = ndots;
	}

	@Override
	public void run() {

		createWriters();

		while (true) {
			try {
				read();
				sleep(5000);
			} catch (java.net.NoRouteToHostException e) {
				System.out.println(e.getMessage());
				sleep(60000);
			} catch (Exception e) {
				e.printStackTrace();
				sleep(1000);
				createWriters();
			}

		}

	}

	private void createWriters() {
		System.out.println("Creating writers...");
		writer = new InfluxWriterRunnable2();
		iwriter = new ImageWriterRunnable();

		// ExecutorService executor = Executors.newSingleThreadExecutor();
		executor = Executors.newFixedThreadPool(2);

		executor.execute(writer);
		executor.execute(iwriter);

	}

	private void read() throws UnknownHostException, IOException {

		System.out.println("Reading...");
		long t1 = System.currentTimeMillis();

		Socket s = new Socket(this.ip, this.port);
		s.setSoTimeout(30_000);

		s.getOutputStream().write("\n".getBytes());
		s.getOutputStream().flush();

		int[] times = new int[this.ndots];
		int[] values = new int[this.ndots];

		int size = 0;
		InputStream is = s.getInputStream();
		while (true) {
			int data1 = is.read();
			int data2 = is.read();
			if (data2 == -1)
				break;
			int data = data1 + data2 * 256;
			if (size < this.ndots) {
				times[size] = data;
			} else {
				values[size - this.ndots] = data;
			}
			size++;

		}

		System.out.println("Size: " + size);
		System.out.println("Took: " + (System.currentTimeMillis() - t1));

		s.close();

		if (size == this.ndots * 2) {
			System.out.println("Writing to threads...");

			// writeDataFile(times, values);
			long startTime = t1 * 1000;

			System.out.println(String.valueOf(startTime));

			SensorData sd = new SensorData(this.ndots);
			for (int i = 0; i < this.ndots; i++) {
				startTime += times[i];

				sd.add(startTime, values[i]);
			}

			if (sd.isInitialized()) {
				writer.queue(sd);
				iwriter.queue(sd);
			} else {
				System.out.println("SendorData was not initialized!");
			}

		} else {
			System.out.println("Number of dots did not match!");
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
