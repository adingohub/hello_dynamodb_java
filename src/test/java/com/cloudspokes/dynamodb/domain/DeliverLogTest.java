package com.cloudspokes.dynamodb.domain;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;

import static com.cloudspokes.dynamodb.helper.Helper.*;

public class DeliverLogTest {
	@Before
	public void before() {
		deleteAll(mapper(), DeliverLog.class);
	}

	@Test
	public void putAndGetLog() {
		assertThat(mapper().scan(DeliverLog.class, new DynamoDBScanExpression()).size(), is(0));
		DeliverLog.put("uuid", "crid", 12345);
		assertThat(mapper().scan(DeliverLog.class, new DynamoDBScanExpression()).size(), is(1));
		DeliverLog.put("uuid2", "crid2", 22345);
		assertThat(mapper().scan(DeliverLog.class, new DynamoDBScanExpression()).size(), is(2));
		assertThat(DeliverLog.get("uuid", "crid"), is(12345l));
		assertThat(DeliverLog.get("uuid2", "crid2"), is(22345l));
	}

}
