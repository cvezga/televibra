package com.cvezga.influxdbwriter;

public class SensorData {

	private long[] times;
	private int[] values;

	private int ptr = 0;

	public SensorData(int len) {
		this.times = new long[len];
		this.values = new int[len];
	}

	public void add(long time, int value) {
		if (ptr < times.length) {
			this.times[ptr] = time;
			this.values[ptr] = value;
			ptr++;
		}
	}
	
	public boolean isInitialized() {
		return ptr<times.length;
	}

	public int getLength() {
		 
		return this.times.length;
	}

	public int getValue(int i) {
		return this.values[i];
	}

	public long getTime(int i) {
		return this.times[i];
	}

}
