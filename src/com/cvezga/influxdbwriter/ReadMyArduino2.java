package com.cvezga.influxdbwriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadMyArduino2 {

	private static final int NDOTS = 500;

	private static InfluxWriterRunnable2 writer;
	private static ImageWriterRunnable iwriter;

	public static void main(String[] args) {
		// long t1 = System.currentTimeMillis();

		boolean isLocal = args.length == 1;

		writer = new InfluxWriterRunnable2(NDOTS);
		iwriter = new ImageWriterRunnable(NDOTS);

		// ExecutorService executor = Executors.newSingleThreadExecutor();
		ExecutorService executor = Executors.newFixedThreadPool(2);

		executor.execute(writer);
		executor.execute(iwriter);

		System.out.println("Reading...");

		while (true) {
			try {

				read(isLocal);
				sleep(5000);

			} catch (Exception e) {
				e.printStackTrace();
				sleep(10_000);
			}
		}

	}

	private static void read(boolean local) throws UnknownHostException, IOException {

		long t1 = System.currentTimeMillis();

		Socket s;
		if (local) {
			s = new Socket("192.168.0.102", 5001);
		} else {
			s = new Socket("186.15.10.211", 5001);
		}
		s.setSoTimeout(30_000);

		s.getOutputStream().write("\n".getBytes());
		s.getOutputStream().flush();

		int[] times = new int[NDOTS];
		int[] values = new int[NDOTS];

		int size = 0;
		InputStream is = s.getInputStream();
		while (true) {
			int data1 = is.read();
			int data2 = is.read();
			if (data2 == -1)
				break;
			int data = data1 + data2 * 256;
			if (size < NDOTS) {
				times[size] = data;
			} else {
				values[size - NDOTS] = data;
			}
			size++;

		}

		System.out.println("Size: " + size);
		System.out.println("Took: " + (System.currentTimeMillis() - t1));

		s.close();

		if (size == NDOTS * 2) {
			System.out.println("Writing to threads...");

			// writeDataFile(times, values);
			long startTime = t1 * 1000;
			int x = 0;
			System.out.println(String.valueOf(startTime));

			SensorData sd = new SensorData(NDOTS);
			for (int i = 0; i < NDOTS; i++) {
				startTime += times[i];
				x += times[i];
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

	private static void writeDataFile(int[] times, int[] values) {
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter("/home/ec2-user/apache-tomcat-8.5.32/webapps/sensors/data_temp.csv"));
			bw.write("Microseconds,X\n");
			float x = 0;
			for (int i = 0; i < NDOTS; i++) {
				x += times[i] / 1000.00;
				bw.write(x + "," + String.valueOf(values[i]) + "\n");
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
