package com.n0tice.api.client.model;

import java.util.Date;

public class ModerationComplaint {
	
	private User user;
	private Date date;
	private String type;
	private String notes;
	
	public ModerationComplaint(User user, Date date, String type, String notes) {
		this.user = user;
		this.date = date;
		this.type = type;
		this.notes = notes;
	}

	public User getUser() {
		return user;
	}

	public Date getDate() {
		return date;
	}

	public String getType() {
		return type;
	}

	public String getNotes() {
		return notes;
	}

	@Override
	public String toString() {
		return "ModerationComplaint [date=" + date + ", notes=" + notes
				+ ", type=" + type + ", user=" + user + "]";
	}
	
}
