package com.cloudspokes.dynamodb.domain;

public class Creative {
	public static Creative fillerCreative() {
		return new Creative("filler", "filler", 0);
	}
	
	final String crid;
	final String text;
	final long rate;
	
	public Creative(String crid, String text, long rate) {
		this.crid = crid;
		this.text = text;
		this.rate = rate;
	}

	@Override
	public String toString() {
		return "Creative [crid=" + crid + ", text=" + text + ", rate=" + rate
				+ "]";
	}
}
