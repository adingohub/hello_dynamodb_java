package com.cloudspokes.dynamodb.helper;

public class MyTimer {
	private Long time;
	
	public MyTimer() {
		time = System.currentTimeMillis();
	}
	
	public void proceed(Long diff) {
		this.time = time + diff;
	}
	
	public Long now() {
		return time;
	}
}
