package com.n0tice.api.client.parsers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.n0tice.api.client.exceptions.ParsingException;
import com.n0tice.api.client.model.Content;
import com.n0tice.api.client.model.Place;
import com.n0tice.api.client.model.Tag;
import com.n0tice.api.client.model.User;

public class SearchParser {

	private static final String DISPLAY_NAME = "displayName";
	private static final String TAGS = "tags";
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
	private static final String USERNAME = "username";
	private static final String PROFILE_IMAGE = "profileImage";

	public List<Content> parseSearchResults(String json) throws ParsingException {
		try {
			JSONObject searchResultsJSON = new JSONObject(json);			
			if (searchResultsJSON.has(RESULTS)) {
				List<Content> contentItems = new ArrayList<Content>();
				
				JSONArray resultContentItems = searchResultsJSON.getJSONArray(RESULTS);
				for (int i = 0; i < resultContentItems.length(); i++) {
					JSONObject contentItem = resultContentItems.getJSONObject(i);					
					contentItems.add(jsonToContentItem(contentItem));
				}
				return contentItems;
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
			String displayName = null;
			String profileImage = null;
			if (userJSON.has(DISPLAY_NAME)) {
				displayName = userJSON.getString(DISPLAY_NAME);
			}
			if (userJSON.has(PROFILE_IMAGE)) {
				profileImage = userJSON.getString(PROFILE_IMAGE);
			}
			
			JSONObject userJson = contentItemJSON.getJSONObject(USER);
			user = new User(userJson.getString(USERNAME), displayName, profileImage);
		}
		
		Place place = null;
		if (contentItemJSON.has(PLACE)) {
			JSONObject placeJson = contentItemJSON.getJSONObject(PLACE);
			place = new Place(placeJson.getDouble(LATITUDE), placeJson.getDouble(LONGITUDE));
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
				parseTags(contentItemJSON)
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
	
	private String getNoticeBoardFromJSON(JSONObject contentItem) throws JSONException {
		if (contentItem.has(NOTICEBOARD)) {
			return contentItem.getString(NOTICEBOARD);
		}
		return null;
	}
	
	private Date parseDate(String dateString) {
		return DateTime.parse(dateString).toDate();
	}
	
	private List<Tag> parseTags(JSONObject contentItemJSON) throws JSONException {
		if (contentItemJSON.has(TAGS)) {
			List<Tag> tags = new ArrayList<Tag>();
			JSONArray jsonTags = contentItemJSON.getJSONArray(TAGS);
			for (int i = 0; i < jsonTags.length(); i++) {
				tags.add(new Tag(jsonTags.getString(i)));
			}
			return tags;
		}
		return null;
	}
	
}
