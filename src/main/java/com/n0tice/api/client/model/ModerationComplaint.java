package com.n0tice.api.client.model;

import java.util.Date;

public class ModerationComplaint {
	
	private final int id;
	private final User user;
	private final Date date;
	private final String type;
	private final String notes;
	private final String status;
	
	public ModerationComplaint(User user, Date date, String type, String notes, int id, String status) {
		this.user = user;
		this.date = date;
		this.type = type;
		this.notes = notes;
		this.id = id;
		this.status = status;
	}

	public int getId() {
		return id;
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


	public String getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "ModerationComplaint [id=" + id + ", user=" + user + ", date="
				+ date + ", type=" + type + ", notes=" + notes + ", status="
				+ status + "]";
	}
	
}
