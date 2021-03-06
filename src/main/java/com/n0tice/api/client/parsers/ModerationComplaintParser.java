package com.n0tice.api.client.parsers;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.n0tice.api.client.exceptions.ParsingException;
import com.n0tice.api.client.model.ModerationComplaint;

public class ModerationComplaintParser {
	
	private static DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTimeNoMillis().withOffsetParsed();
	
	private final UserParser userParser;
	
	public ModerationComplaintParser() {
		this.userParser = new UserParser();
	}
	
	public List<ModerationComplaint> parse(String json) throws ParsingException {
		final List<ModerationComplaint> complaints = Lists.newArrayList();
		try {
			JSONArray complaintsJSON = new JSONArray(json);
			for (int i = 0; i < complaintsJSON.length(); i++) {
				JSONObject complaintJSON = complaintsJSON.getJSONObject(i);
				int id = complaintJSON.getInt("id");
				String status = complaintJSON.getString("status");
				complaints.add(new ModerationComplaint(userParser.jsonToUser(complaintJSON.getJSONObject("user")), parseDate(complaintJSON.getString("date")).toDate(),
						complaintJSON.getString("type"), complaintJSON.has("notes") ? complaintJSON.getString("notes") : null, id, status));
			}			
			
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ParsingException();
		}
		return complaints;
	}
	
	public List<String> parseModerationActions(String json) throws ParsingException {		
		final List<String> actions = Lists.newArrayList();
		try {
			JSONArray actionsJSON = new JSONArray(json);
			for (int i = 0; i < actionsJSON.length(); i++) {
				actions.add((String) actionsJSON.get(i));
			}	
			return actions;
		} catch (JSONException e) {
			throw new ParsingException();
		}
	}

	private DateTime parseDate(String dateString) {
		return dateFormatter.parseDateTime(dateString);
	}

}
