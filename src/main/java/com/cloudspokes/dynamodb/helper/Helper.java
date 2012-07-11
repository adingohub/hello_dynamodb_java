package com.cloudspokes.dynamodb.helper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.datamodeling.PaginatedScanList;
import com.cloudspokes.dynamodb.domain.Loan;

public class Helper {
	public static class AWSKey {
		final String accessKey;
		final String secretKey;
		public AWSKey(String accessKey, String secretKey) {
			this.accessKey = accessKey;
			this.secretKey = secretKey;
		}
	}
	
	static AWSKey awsKey() throws IOException {
		Properties p = new Properties();
		InputStream r = ClassLoader.getSystemClassLoader().getResourceAsStream("aws_key.properties");
		try {
			p.load(r);
			return new AWSKey(p.getProperty("access_key"), p.getProperty("secret_key"));
		} finally {
			r.close();
		}
	}
	
	public static DynamoDBMapper createMapper() throws Exception {
		AWSKey key = awsKey();
		return createMapper(key.accessKey, key.secretKey);
	}

	public static DynamoDBMapper createMapper(String accessKey, String secretKey) throws Exception {
		BasicAWSCredentials creds = new BasicAWSCredentials(accessKey,
				secretKey);
		AmazonDynamoDBClient client = new AmazonDynamoDBClient(creds);
		client.setEndpoint("https://dynamodb.ap-northeast-1.amazonaws.com");
		return new DynamoDBMapper(client);
	}

	/**
	 * Deletes all items from dynamodb
	 * @param dynamoDB 
	 */
	public static void deleteAllLoans(DynamoDBMapper dynamoDB) throws AmazonServiceException {
		PaginatedScanList<Loan> scan = dynamoDB.scan(Loan.class,
				new DynamoDBScanExpression());
		for (Loan l : scan) {
			dynamoDB.delete(l);
		}
	}


}
