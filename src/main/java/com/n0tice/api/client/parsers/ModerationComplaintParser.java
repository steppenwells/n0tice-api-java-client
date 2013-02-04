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
				complaints.add(new ModerationComplaint(userParser.jsonToUser(complaintJSON.getJSONObject("user")), parseDate(complaintJSON.getString("date")).toDate(),
						complaintJSON.getString("type"), complaintJSON.has("notes") ? complaintJSON.getString("notes") : null));
			}			
			
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ParsingException();
		}
		return complaints;
	}

	private DateTime parseDate(String dateString) {
		return dateFormatter.parseDateTime(dateString);
	}

}
