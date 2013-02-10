package com.n0tice.api.client.model;

import java.util.Date;

public class HistoryItem {
	
	private User user;
	private String type;
	private Date date;
	private final String notes;

	public HistoryItem(User user, String type, Date date, String notes) {
		this.user = user;
		this.type = type;
		this.date = date;
		this.notes = notes;
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
	
	public String getNotes() {
		return notes;
	}

	@Override
	public String toString() {
		return "HistoryItem [date=" + date + ", notes=" + notes + ", type="
				+ type + ", user=" + user + "]";
	}
	
}
