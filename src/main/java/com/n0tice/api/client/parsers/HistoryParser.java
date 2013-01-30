package com.n0tice.api.client.parsers;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.n0tice.api.client.exceptions.ParsingException;
import com.n0tice.api.client.model.HistoryItem;

public class HistoryParser {

	private static DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTimeNoMillis().withOffsetParsed();
	
	private final UserParser userParser;
	
	public HistoryParser() {
		this.userParser = new UserParser();
	}
	
	public List<HistoryItem> parse(String json) throws ParsingException {
		final List<HistoryItem> historyItems = new ArrayList<HistoryItem>();
		try {
			JSONArray historyItemsJSON = new JSONArray(json);
			for (int i = 0; i < historyItemsJSON.length(); i++) {
				final JSONObject historyItem = historyItemsJSON.getJSONObject(i);
				historyItems.add(new HistoryItem(userParser.jsonToUser(historyItem.getJSONObject("user")),
						historyItem.getString("type"),
						parseDate(historyItem.getString("date")).toDate()));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ParsingException();
		}

		return historyItems;
	}

	// TODO duplication
	private DateTime parseDate(String dateString) {
		return dateFormatter.parseDateTime(dateString);
	}
	
}
