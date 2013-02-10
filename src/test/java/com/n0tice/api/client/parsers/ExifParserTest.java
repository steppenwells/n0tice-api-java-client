package com.n0tice.api.client.parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

public class ExifParserTest {

	@Test
	public void canParseExifSummary() throws Exception {
		final ExifParser exifParser = new ExifParser();
		Map<String, Map<String, String>> parse = exifParser.parse(ContentLoader.loadContent("exif.json"));
		
		assertEquals(9, parse.size());
		assertNotNull(parse.get("Jpeg"));
		assertEquals("Adobe Photoshop CS5 Windows", parse.get("Exif IFD0").get("Software"));
	}

}
