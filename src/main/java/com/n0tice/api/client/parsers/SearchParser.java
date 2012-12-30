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
import com.n0tice.api.client.model.Content;
import com.n0tice.api.client.model.Image;
import com.n0tice.api.client.model.Place;
import com.n0tice.api.client.model.ResultSet;
import com.n0tice.api.client.model.Tag;
import com.n0tice.api.client.model.Update;
import com.n0tice.api.client.model.User;

public class SearchParser {

	private static final String TIME_ZONE = "timeZone";
	private static final String NAME = "name";
	private static final String SMALL = "small";
	private static final String TAGS = "tags";
	private static final String UPDATES = "updates";
	private static final String USER = "user";
	private static final String TYPE = "type";
	private static final String WEB_URL = "webUrl";
	private static final String API_URL = "apiUrl";
	private static final String PLACE = "place";
	private static final String LONGITUDE = "longitude";
	private static final String LATITUDE = "latitude";
	private static final String ID = "id";
	private static final String RESULTS = "results";
	private static final String HEADLINE = "headline";
	private static final String NOTICEBOARD = "noticeboard";
	private static final String START_DATE = "startDate";
	private static final String END_DATE = "endDate";
	private static final String INTERESTING = "interesting";
	private static final String VOTES = "votes";
	private static final String REPOSTS = "reposts";
	
	private static DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTimeNoMillis().withOffsetParsed();
	
	public ResultSet parseSearchResults(String json) throws ParsingException {
		try {
			JSONObject searchResultsJSON = new JSONObject(json);
			final int totalMatches = searchResultsJSON.getInt("numberFound");
			final int startIndex = searchResultsJSON.getInt("startIndex");
			if (searchResultsJSON.has(RESULTS)) {
				List<Content> contentItems = new ArrayList<Content>();
				
				JSONArray resultContentItems = searchResultsJSON.getJSONArray(RESULTS);
				for (int i = 0; i < resultContentItems.length(); i++) {
					JSONObject contentItem = resultContentItems.getJSONObject(i);					
					contentItems.add(jsonToContentItem(contentItem));
				}
				return new ResultSet(totalMatches, startIndex, contentItems);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ParsingException();
		}
		
		return null;
	}

	private Content jsonToContentItem(JSONObject contentItemJSON) throws JSONException {
		User user = null;
		if (contentItemJSON.has(USER)) {
			JSONObject userJSON = contentItemJSON.getJSONObject(USER);			
			String displayName = userJSON.has("displayName") ? userJSON.getString("displayName") : null;
			user = new User(userJSON.getString("username"), displayName);
		}
		
		Place place = null;
		if (contentItemJSON.has(PLACE)) {
			JSONObject placeJson = contentItemJSON.getJSONObject(PLACE);
			final String timezone = placeJson.has(TIME_ZONE) ? placeJson.getString(TIME_ZONE) : null;
			place = new Place(placeJson.getString(NAME), placeJson.getDouble(LATITUDE), placeJson.getDouble(LONGITUDE), timezone);
		}
		
		DateTime startDate = null;
		if (contentItemJSON.has(START_DATE)) {
			startDate = parseDate(contentItemJSON.getString(START_DATE));
		}
		DateTime endDate = null;
		if (contentItemJSON.has(END_DATE)) {
			endDate = parseDate(contentItemJSON.getString(END_DATE));
		}
		int interestingVotes = 0;
		if (contentItemJSON.has(VOTES)) {
			JSONObject votesJson = contentItemJSON.getJSONObject(VOTES);
			if (votesJson.has(INTERESTING)) {
				interestingVotes = votesJson.getInt(INTERESTING);
			}
		}
		
		int reposts = 0;
		if (contentItemJSON.has(REPOSTS)) {
			reposts = contentItemJSON.getInt(REPOSTS);			
		}
		
		return new Content(contentItemJSON.getString(ID), 
				contentItemJSON.getString(API_URL), 
				contentItemJSON.getString(WEB_URL), 
				contentItemJSON.getString(TYPE), 
				contentItemJSON.getString(HEADLINE), 
				place, 
				user, 
				getNoticeBoardFromJSON(contentItemJSON),
				parseDate(contentItemJSON.getString("created")),
				parseDate(contentItemJSON.getString("modified")),
				parseTags(contentItemJSON),
				parseUpdates(contentItemJSON),
				startDate,
				endDate,
				interestingVotes,
				reposts
				);
	}
	
	public Content parseReport(String json) throws ParsingException {
		try {
			JSONObject reportJSON = new JSONObject(json);			
			return jsonToContentItem(reportJSON);
			
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ParsingException();
		}
	}
	
	public User parseUserResult(String json) throws ParsingException {
		return new UserParser().parseUserProfile(json);
	}
	
	public String parseNoticeboardResult(String json) throws ParsingException {
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject.getString("domain");
		} catch (JSONException e) {
			throw new ParsingException();
		}
	}
	
	public int parseVotes(String json) throws ParsingException {
		try {
			JSONArray votesJson = new JSONArray(json);
			return votesJson.length();
			
		} catch (JSONException e) {
			throw new ParsingException();
		}
	}
	
	private String getNoticeBoardFromJSON(JSONObject contentItem) throws JSONException {
		if (contentItem.has(NOTICEBOARD)) {
			return contentItem.getString(NOTICEBOARD);
		}
		return null;
	}
	
	private DateTime parseDate(String dateString) {
		return dateFormatter.parseDateTime(dateString);
	}
	
	private List<Tag> parseTags(JSONObject contentItemJSON) throws JSONException {
		if (contentItemJSON.has(TAGS)) {
			List<Tag> tags = new ArrayList<Tag>();
			JSONArray jsonTags = contentItemJSON.getJSONArray(TAGS);
			for (int i = 0; i < jsonTags.length(); i++) {
				JSONObject jsonTag = jsonTags.getJSONObject(i);
				tags.add(new Tag(jsonTag.getString("id"),					
						jsonTag.has(NAME) ? jsonTag.getString(NAME) : null));
			}
			return tags;
		}
		return null;
	}
	
	private List<Update> parseUpdates(JSONObject contentItemJSON) throws JSONException {	// TODO Test coverage
		ArrayList<Update> updates = new ArrayList<Update>();
		if (contentItemJSON.has(UPDATES)) {
			JSONArray jsonUpdates = contentItemJSON.getJSONArray(UPDATES);
			for (int i = 0; i < jsonUpdates.length(); i++) {
				JSONObject jsonUpdate = jsonUpdates.getJSONObject(i);
				final String body = jsonUpdate.has("body") ? jsonUpdate.getString("body") : null; 
				final String link = jsonUpdate.has("link") ? jsonUpdate.getString("link") : null;
				Image image = null;
				User user = null;
				if (jsonUpdate.has("image")) {
					JSONObject imageJson = jsonUpdate.getJSONObject("image");
					image = new Image(imageJson.getString(SMALL));
				}
				if (jsonUpdate.has(USER)) {
					user = new User(jsonUpdate.getJSONObject(USER).getString("username"));
				}				
				updates.add(new Update(user, body, link, image));
			}			
		}
		return updates;
	}
	
}
