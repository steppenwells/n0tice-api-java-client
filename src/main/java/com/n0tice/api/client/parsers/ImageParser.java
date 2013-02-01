package com.n0tice.api.client.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import com.n0tice.api.client.model.Image;

public class ImageParser {
	
	private static final String SMALL = "small";
	private static final String MEDIUM = "medium";
	private static final String MEDIUMLANDSCAPE = "mediumlandscape";
	private static final String LARGE = "large";
	private static final String ORIENTATION = "orientation";
	
	public Image parseImage(JSONObject imageJson) throws JSONException {
		return new Image(
				imageJson.has(SMALL) ? imageJson.getString(SMALL) : null,
				imageJson.has(MEDIUM) ? imageJson.getString(MEDIUM) : null,
				imageJson.has(LARGE) ? imageJson.getString(LARGE) : null,
				imageJson.has(MEDIUMLANDSCAPE) ? imageJson.getString(MEDIUMLANDSCAPE) : null,
				imageJson.has(ORIENTATION) ? imageJson.getString(ORIENTATION) : null);
	}

}
