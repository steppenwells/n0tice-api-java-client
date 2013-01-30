package com.n0tice.api.client.model;

import java.util.Date;

public class HistoryItem {
	
	private User user;
	private String type;
	private Date date;

	public HistoryItem(User user, String type, Date date) {
		this.user = user;
		this.type = type;
		this.date = date;
	}

	public User getUser() {
		return user;
	}

	public String getType() {
		return type;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public String toString() {
		return "HistoryItem [user=" + user + ", type=" + type + ", date="
				+ date + "]";
	}
	
}
