package com.cloudspokes.dynamodb.domain;

public class User {
	public static String generateUUID() {
		return "uuid" + System.currentTimeMillis();
	}
	
	String uuid;
	
	public User() {
		this(generateUUID());
	}

	public User(String uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public String toString() {
		return "User [uuid=" + uuid + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
	
}
