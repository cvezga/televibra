package com.cvezga.influxdbwriter;

import java.awt.SecondaryLoop;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketTest {
	
	static OutputStream os;
	static InputStream is;
	
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket s = new Socket("201.199.29.152", 6000);
		
		os = s.getOutputStream();
		is = s.getInputStream();
		
		send("C1","S1");
		
		send("C3","S3");
		
		sleep(100);
		
		send("C4","S4");
		
		send("C2","S2");
		
		send("C5","S5");
		
		s.close();
	}
	
	private static void send(String cmd, String waitFor) throws IOException {
		System.out.println("\n-----------------------");
		System.out.println("Sending cmd: "+cmd);
		long t = System.currentTimeMillis();
        os.write((cmd+"\r").getBytes());
		os.flush();
	
		System.out.println("Reading...");
		
		StringBuilder sb = new StringBuilder();
		
		byte[] buffer = new byte[512];
		int len;
		while((len=is.read(buffer))>0) {
			String res = new String(buffer,0,len);
			//System.out.print(res);
			sb.append(res);
			
			if(sb.toString().indexOf(waitFor)>-1) {
				break;
			}
		}
		System.out.println("Command "+cmd+" respose = "+sb.toString());
		System.out.println("Tooke:" + (System.currentTimeMillis()-t)+" ms");
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
