package com.cvezga.influxdbwriter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

public class ImageWriterRunnable implements Runnable {

	private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
	
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

				if (queue.size() < this.ndtos) {

					Thread.sleep(10);
					continue;
				}

				System.out.println("ImageWriterRunnable processing dps");
				count++;
				if (count > 20) {
					count = 1;
				}

				BufferedImage image = new BufferedImage(620, 200, BufferedImage.TYPE_3BYTE_BGR);

				Graphics g = image.getGraphics();
				g.setColor(Color.BLUE);
				int x1 = 0;
				int y1 = 0;
				int x2 = 0;
				int y2 = 0;
				int dp = 0;
				while (dp < this.ndtos) {
					dp++;
					String entry = queue.remove();
					//System.out.println(dp+":"+entry);
					String[] data = entry.split("\\|");
					y2 = 200 - Integer.parseInt(data[1].trim());
					x2++;
					g.drawLine(x1, y1, x2, y2);
					x1 = x2;
					y1 = y2;

				}

				System.out.println("ImageWriterRunnable writing image...");
				ImageIO.write(image, "png",
						new File("/home/ec2-user/apache-tomcat-8.5.32/webapps/sensors/images/image-" + count + ".png"));

				System.out.println("ImageWriterRunnable image write done.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void write(String time, String value) {
		queue.add(time + "|" + value);
	}

}
