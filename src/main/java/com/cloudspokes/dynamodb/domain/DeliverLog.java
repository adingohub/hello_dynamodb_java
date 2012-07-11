package com.cloudspokes.dynamodb.domain;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;
import com.cloudspokes.dynamodb.helper.Helper;

@DynamoDBTable(tableName = "deliver_log")
public class DeliverLog {
	public static long get(String uuid, String crid) {
		DeliverLog log = Helper.mapper().load(DeliverLog.class, uuid + crid);
		if (log == null) return 0;
		return log.timestamp;
	}
	
	public static void put(String uuid, String crid, long timestamp) {
		DeliverLog log = new DeliverLog(uuid + crid, timestamp);
		Helper.mapper().save(log); // TODO overwrite if exists
	}
	
	private String key;
	private long timestamp;
	
	public DeliverLog() {
	}
	
	public DeliverLog(String key, long timestamp) {
		this.key = key;
		this.timestamp = timestamp;
	}

	@DynamoDBHashKey
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	@DynamoDBAttribute
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
