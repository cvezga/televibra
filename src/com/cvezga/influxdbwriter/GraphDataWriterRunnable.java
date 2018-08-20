package com.cvezga.influxdbwriter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

public class GraphDataWriterRunnable implements Runnable {

	private static final long IDLE_TIME_REPORT = 10_000;

	private ConcurrentLinkedQueue<SensorData> queue = new ConcurrentLinkedQueue<>();

	public ImageWriterRunnable() {

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
                                
				Graphics g = image.getGraphics();
				g.setColor(Color.BLUE);
				int x1 = 0;
				int y1 = 0;
				int x2 = 0;
				int y2 = 0;

				for (int i = 0; i < sd.getLength(); i++) {
					int x = sd.getValue(i);

					y2 = 200 - x;
					x2++;
					g.drawLine(x1, y1, x2, y2);
					x1 = x2;
					y1 = y2;

				}

				System.out.println("ImageWriterRunnable writing image...");

				count++;
				if (count > 20) {
					count = 1;
				}

				ImageIO.write(image, "png",
						new File("/home/ec2-user/apache-tomcat-8.5.32/webapps/sensors/images/image-" + count + ".png"));

				System.out.println("ImageWriterRunnable image write done.");
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
