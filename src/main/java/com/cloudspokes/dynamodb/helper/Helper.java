package com.cloudspokes.dynamodb.helper;

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
	
	static AWSKey awsKey() {
		Properties p = new Properties();
		InputStream r = ClassLoader.getSystemClassLoader().getResourceAsStream("aws_key.properties");
		try {
			try {
				p.load(r);
				return new AWSKey(p.getProperty("access_key"), p.getProperty("secret_key"));
			} finally {
				r.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	static DynamoDBMapper mapper;
	
	public static DynamoDBMapper mapper() {
		if (mapper != null) return mapper;
		AWSKey key = awsKey();
		mapper = mapper(key.accessKey, key.secretKey);
		return mapper;
	}

	private static DynamoDBMapper mapper(String accessKey, String secretKey) {
		BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
		AmazonDynamoDBClient client = new AmazonDynamoDBClient(creds);
		client.setEndpoint("https://dynamodb.ap-northeast-1.amazonaws.com");
		return new DynamoDBMapper(client);
	}

	public static void deleteAllLoans(DynamoDBMapper dynamoDB) throws AmazonServiceException {
		deleteAll(dynamoDB, Loan.class);
	}
	
	public static void deleteAll(DynamoDBMapper dynamoDB, Class<?> dynamoDBTableClass) throws AmazonServiceException {
		PaginatedScanList<?> scan = dynamoDB.scan(dynamoDBTableClass,
				new DynamoDBScanExpression());
		for (Object l : scan) {
			dynamoDB.delete(l);
		}
	}
}
