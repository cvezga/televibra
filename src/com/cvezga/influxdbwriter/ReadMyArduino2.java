package com.cvezga.influxdbwriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ReadMyArduino2 {

	private static final int NDOTS = 500;

	private static InfluxWriterRunnable2 writer;
	//private static ImageWriterRunnable iwriter;
	
	public static void main(String[] args) {
		// long t1 = System.currentTimeMillis();

		boolean isLocal = args.length == 1;

		writer = new InfluxWriterRunnable2(NDOTS);
		new Thread(writer).start();

		//iwriter = new ImageWriterRunnable(NDOTS);
		//new Thread(iwriter).start();

		
		while (true) {
			try {

				read(isLocal);
				sleep(5000);

			} catch (Exception e) {
				e.printStackTrace();
				sleep(30_000);
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
		//s.setSoTimeout(10_000);

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

		if (size == NDOTS*2) {
            System.out.println("Writing to threads...");
            writeDataFile(times,  values);
			long startTime = t1 * 1000;
			int x=0;
			System.out.println(String.valueOf(startTime));
			for (int i = 0; i < NDOTS; i++) {
				startTime += times[i];
				x+=times[i];
				System.out.println(x/1000.0+","+ String.valueOf(values[i]));
			//	iwriter.write(String.valueOf(startTime), String.valueOf(values[i]));
				writer.write(String.valueOf(startTime), String.valueOf(values[i]));
			}

		}

	}
	
	private static void writeDataFile(int[] times, int[] values) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/ec2-user/apache-tomcat-8.5.32/webapps/sensors/data_temp.csv"));
			bw.write("Microseconds,X\n");
			float x=0;
			for (int i = 0; i < NDOTS; i++) {
				x+=times[i]/1000.00;
				bw.write(x+","+ String.valueOf(values[i])+"\n");
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
