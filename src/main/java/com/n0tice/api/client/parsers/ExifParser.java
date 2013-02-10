package com.n0tice.api.client.parsers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

public class ExifParser {

	public Map<String, Map<String, String>> parse(String json) {		
		try {
			Map<String, Map<String, String>> directories = new HashMap<String, Map<String, String>>();
			final JSONObject exifJSON = new JSONObject(json);
			final Iterator<String> keys = exifJSON.keys();
			while(keys.hasNext()) {
				final String directoryName = keys.next();
				final HashMap<String, String> directory = new HashMap<String, String>();
				JSONObject directoryJson = exifJSON.getJSONObject(directoryName);
				Iterator<String> directoryKeys = directoryJson.keys();
				while(directoryKeys.hasNext()) {
					final String fieldName = directoryKeys.next();
					directory.put(fieldName, (String) directoryJson.getString(fieldName));
				}
				directories.put(directoryName, directory);
			}
			return directories;
			
		} catch (JSONException e) {
			throw new ParseException();
		}
	}

}
