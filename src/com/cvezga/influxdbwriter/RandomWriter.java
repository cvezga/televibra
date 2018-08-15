package com.cvezga.influxdbwriter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomWriter {

	private static final Random r = new Random();

	public static void main(String[] args) throws Exception {

		// http://localhost:8086/write?db=statsdemo' --data-binary 'cpu,host=serverA
		// value=1111

		List<String> list = new ArrayList<>();

		for (int i = 0; i < 1000000; i++) {
			int value = r.nextInt(500);
			list.add(value + "|" + System.currentTimeMillis());
			if (list.size() > 100) {
				sendPost(list);
			}

			Thread.sleep(100);
		}

	}

	private static void sendPost(List<String> data) throws Exception {

		String url = "http://localhost:8086/write?db=statsdemo";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		// con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		StringBuffer sb = new StringBuffer();

		for (String d : data) {
			String[] v = d.split("\\|");
			String urlParameters = "cpu,host=serverA value=" + v[0] + " " + v[1];
			if(sb.length()>0) sb.append("\n");
			sb.append(urlParameters);
		}
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(sb.toString());
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + sb.toString());
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());

	}
}
