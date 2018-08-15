package com.cvezga.influxdbwriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ReadMyArduino {
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		//long t1 = System.currentTimeMillis();
		Socket s = new Socket("192.168.0.102",5001);
		
		s.getOutputStream().write("\n".getBytes());
		s.getOutputStream().flush();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		long t1 = System.currentTimeMillis();
		System.out.println(t1);
		
		String line;
		while((line=br.readLine())!=null) {
		   System.out.println(line);
		}
		
		System.out.println("Took: "+(System.currentTimeMillis()-t1));
	}

}
