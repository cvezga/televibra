package com.cvezga.influxdbwriter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

public class ImageWriterRunnable implements Runnable {

	private ConcurrentLinkedQueue<SensorData> queue = new ConcurrentLinkedQueue<>();
	
	 private int ndtos;	

		public ImageWriterRunnable(int ndots) {
			this.ndtos=ndots;
		}

	@Override
	public void run() {
		System.out.println("ImageWriterRunnable started...");
		int count = 0;
		while (true) {
			try {

				if (queue.size() == 0) {
					Thread.sleep(250);
					continue;
				}

				System.out.println("ImageWriterRunnable processing dps");
				
				SensorData sd = queue.remove();
				
				BufferedImage image = new BufferedImage(620, 200, BufferedImage.TYPE_3BYTE_BGR);

				Graphics g = image.getGraphics();
				g.setColor(Color.BLUE);
				int x1 = 0;
				int y1 = 0;
				int x2 = 0;
				int y2 = 0;
			
				for(int i=0; i<sd.getLength(); i++) {
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
