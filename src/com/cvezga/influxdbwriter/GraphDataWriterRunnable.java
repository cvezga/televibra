package com.cvezga.influxdbwriter;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

public class GraphDataWriterRunnable implements Runnable {

	private static final long IDLE_TIME_REPORT = 10000;

	private ConcurrentLinkedQueue<SensorData> queue = new ConcurrentLinkedQueue<>();

	public GraphDataWriterRunnable() {

	}

	@Override
	public void run() {
		System.out.println("GrapDataWriterRunnable started...");
		long lastTimeProcess = System.currentTimeMillis();
		int count = 0;
		while (true) {
			try {

				if (queue.size() == 0) {
					if (System.currentTimeMillis() - lastTimeProcess > IDLE_TIME_REPORT) {
						System.out.println("GrapDataWriterRunnable idle after " + IDLE_TIME_REPORT);
						lastTimeProcess = System.currentTimeMillis();
					}
					Thread.sleep(250);
					continue;
				}

				System.out.println("GrapDataWriterRunnable processing dps");

				SensorData sd = queue.remove();

				File file = new File("/home/ec2-user/apache-tomcat-8.5.32/webapps/sensors/graph_data.csv");
				BufferedWriter bw = new BufferedWriter( new FileWriter(file) );
                              
				bw.write("time,x\n");
				
				int x = 0;
				int y = 0;
				
				long startTime = sd.getTime(0);

				for (int i = 0; i < sd.getLength(); i++) {
					x  = (int) (sd.getTime(i) - startTime);
					y  = sd.getValue(i);

					bw.write(x+","+y+"\n");
					
				}
				
				bw.flush();
				bw.close();

			
				System.out.println("GrapDataWriterRunnable file write done.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void queue(SensorData sd) {
		this.queue.add(sd);

	}

}
