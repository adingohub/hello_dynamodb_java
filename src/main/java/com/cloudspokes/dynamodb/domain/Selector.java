package com.cloudspokes.dynamodb.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.cloudspokes.dynamodb.helper.MyTimer;

public class Selector {
	private List<Creative> creatives;
	private final MyTimer timer;
	
	public Selector(MyTimer timer, Creative...creatives) {
		this.timer = timer;
		this.creatives = Arrays.asList(creatives);
	}

	public Creative selectCreativeFor(User user) {
		for (Creative c : creatives) {
			Long t = DeliverLog.get(user.uuid, c.crid);
			if (enable(c, t, timer.now())) {
				DeliverLog.put(user.uuid, c.crid, timer.now());
				return c;
			}
		}
		return Creative.fillerCreative();
	}
	
	static boolean enable(Creative c, Long t, Long now) {
		Long past = now - t;
		return past >= c.rate;
	}
	
	static Map<Creative, Long> map(User user, List<Creative> creatives) {
		HashMap<Creative, Long> m = new HashMap<Creative, Long>();
		for (Creative c : creatives) {
			Long t = DeliverLog.get(user.uuid, c.crid);
			m.put(c, t);
		}
		return m;
	}
}
