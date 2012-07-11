package com.cloudspokes.dynamodb.domain;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

import com.cloudspokes.dynamodb.helper.Helper;
import com.cloudspokes.dynamodb.helper.MyTimer;

public class SelectorTest {
	Selector target;
	User user = new User();
	MyTimer timer = new MyTimer();
	
	Creative[] creatives = {
			new Creative("1", "creative1", 1000),
			new Creative("2", "creative2", 1500),
	};

	@Before
	public void before() {
		Helper.deleteAll(Helper.mapper(), DeliverLog.class);
		target = new Selector(timer, creatives);
	}
	
	@Test
	public void test() {
		assertThat(target.selectCreativeFor(user), is(not(nullValue()))); // creative1 or 2
		assertThat(target.selectCreativeFor(user), is(not(nullValue()))); // creative1 or 2
		
		assertThat(target.selectCreativeFor(user).text, is("filler"));
		
		timer.proceed(999l); // 999ms passed
		assertThat(target.selectCreativeFor(user).text, is("filler"));
		
		timer.proceed(1l); // 1000ms passed
		assertThat(target.selectCreativeFor(user).text, is("creative1"));
		
		timer.proceed(499l); // 1499ms passed
		assertThat(target.selectCreativeFor(user).text, is("filler"));
		
		timer.proceed(1l); // 1500ms passed
		assertThat(target.selectCreativeFor(user).text, is("creative2"));
		
		timer.proceed(499l); // 1999ms passed
		assertThat(target.selectCreativeFor(user).text, is("filler"));
		
		timer.proceed(1l); // 1000ms passed
		assertThat(target.selectCreativeFor(user).text, is("creative1"));
	}

}
