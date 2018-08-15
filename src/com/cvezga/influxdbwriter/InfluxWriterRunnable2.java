package com.cvezga.influxdbwriter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InfluxWriterRunnable2 implements Runnable {

	private ConcurrentLinkedQueue<SensorData> queue = new ConcurrentLinkedQueue<>();
	
    private int ndtos;	

	public InfluxWriterRunnable2(int ndots) {
		this.ndtos=ndots;
	}

	@Override
	public void run() {
		System.out.println("InfluxWriterRunnable2 started...");
		while (true) {
			try {

				if (queue.size() == 0) {
					Thread.sleep(250);
					continue;
				}

				System.out.println("InfluxWriterRunnable2 processing dps");
				StringBuffer sb = new StringBuffer();
				while (queue.size() > 0) {
					SensorData sd =  queue.remove();
					
					for(int i=0; i<sd.getLength(); i++) {
						int x = sd.getValue(i);
						long ts = sd.getTime(i);
						String urlParameters = "Arduino_Cesar,name=A01 x=" + x + " " + ts + "000\n";
						sb.append(urlParameters);
					}
					 
					

				}

				sendPost(sb.toString());

				// System.out.println(sb.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}


	private void sendPost(String dps) throws Exception {
		System.out.println("InfluxWriterRunnable2 sending post to InfluxDB ...");
		String url = "http://localhost:8086/write?db=statsdemo";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		// con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Content-Length", "" + dps.length());

		String urlParameters = dps;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		// System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		// System.out.println(response.toString());

	}

	public void queue(SensorData sd) {
		this.queue.add(sd);
		
	}

}
