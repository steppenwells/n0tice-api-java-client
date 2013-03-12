package com.n0tice.api.client.parsers;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.n0tice.api.client.model.MediaType;
import com.n0tice.api.client.model.Noticeboard;
import com.n0tice.api.client.model.NoticeboardResultSet;

public class NoticeboardParserTest {

	private NoticeboardParser noticeboardParser;
	
	@Before
	public void setup() {
		noticeboardParser = new NoticeboardParser();
	}

	@Test
	public void canParseDetailsFromNoticeboard() throws Exception {
		final Noticeboard noticeboard = noticeboardParser.parseNoticeboardResult(new JSONObject(ContentLoader.loadContent("noticeboard.json")));
		
		assertEquals("Test", noticeboard.getName());
		assertEquals("test", noticeboard.getDomain());
		assertEquals("Test board", noticeboard.getDescription());
		assertTrue(noticeboard.isModerated());

		assertEquals("http://n0tice-devstatic.s3.amazonaws.com/images/noticeboards/backgrounds/large/41717efdefc22bd4.jpg", noticeboard.getBackground().getLarge());
		assertEquals(2, noticeboard.getSupportedMediaTypes().size());
		assertTrue(noticeboard.getSupportedMediaTypes().contains(MediaType.TEXT));
		assertTrue(noticeboard.getSupportedMediaTypes().contains(MediaType.IMAGE));
		assertEquals(23, noticeboard.getContributors());
		assertEquals(49, noticeboard.getContributions());
	}
	
	@Test
	public void canParseNoticeboardSearchResults() throws Exception {
		final NoticeboardResultSet results = noticeboardParser.parseNoticeboardSearchResults(ContentLoader.loadContent("noticeboardSearchResults.json"));
		
		assertEquals(9186, results.getTotalMatches());
		assertEquals(20, results.getNoticeboards().size());
		assertEquals("berkeley", results.getNoticeboards().get(0).getDomain());
	}

}
