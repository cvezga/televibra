package com.cvezga.influxdbwriter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class WriteAccelerometerData {

	
	public static void main(String[] args) throws Exception {

		// http://localhost:8086/write?db=statsdemo' --data-binary 'cpu,host=serverA
		// value=1111
		
		InfluxWriterRunnable r = new InfluxWriterRunnable();
		new Thread(r).start();

		for (int i = 0; i < 1000000; i++) {
			if(i%100==0) System.out.println(i);
			 
			URL url = new URL("http://186.15.10.211:5000/?sensor=Accelerometer");

			InputStream is = url.openStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				r.write(line);
			}

		}

		

	}

	
}
