package com.cvezga.influxdbwriter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

public class InfluxWriterRunnable implements Runnable {

	private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(150);

	@Override
	public void run() {

		while (true) {
			if (queue.size() < 10) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			try {
				StringBuffer sb = new StringBuffer();
				int count=0;
				while (queue.size() > 0) {

					String entry = queue.take();
					String[] data = entry.split("\\|");
					String x=data[0].trim();
					String y=data[1].trim();
					String z=data[2].trim();
					String ts=data[3].trim();
					String urlParameters = "sensor,name=CesarCell1 x=" + x + ",y=" + y + ",z=" + z+" "+ts+"000000";
					sb.append(urlParameters).append("\n");
				    count++;
				    if(count>99) break;
				}
				
				sendPost(sb.toString());
				//System.out.println(sb.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void write(String data) {
		queue.add(data+System.currentTimeMillis());
	}

	private static void sendPost(String dps) throws Exception {

		String url = "http://localhost:8086/write?db=statsdemo";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		// con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Content-Length", ""+dps.length());

		String urlParameters = dps;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
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

}
