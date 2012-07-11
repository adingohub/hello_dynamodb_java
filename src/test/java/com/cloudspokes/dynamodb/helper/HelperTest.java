package com.cloudspokes.dynamodb.helper;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.datamodeling.PaginatedScanList;
import com.cloudspokes.dynamodb.domain.Loan;
import com.cloudspokes.dynamodb.helper.Helper.AWSKey;

public class HelperTest {
	@Test
	@Ignore
	public void helloAwsKey() throws Exception {
		AWSKey key = Helper.awsKey();
		System.out.println(key.accessKey);
		System.out.println(key.secretKey);
	}
	
	@Test
	public void helloDynamoDBMapper() throws Exception {
		DynamoDBMapper dynamoDB = Helper.createMapper();
		assertThat(dynamoDB, is(not(nullValue())));
		DynamoDBScanExpression exp = new DynamoDBScanExpression();
		Helper.deleteAllLoans(dynamoDB);

		PaginatedScanList<Loan> before = dynamoDB.scan(Loan.class, exp);
		assertThat(before.size(), is(0));

		Loan loan = new Loan();
		loan.setId(1);
		loan.setCountry("JP");
		loan.setStatus("enable");
		dynamoDB.save(loan);
		PaginatedScanList<Loan> result = dynamoDB.scan(Loan.class, exp);

		assertThat(result.size(), is(1));
		assertThat(result.get(0), is(loan));
	}
}
