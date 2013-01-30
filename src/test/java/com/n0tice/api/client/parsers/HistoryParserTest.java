package com.n0tice.api.client.parsers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.n0tice.api.client.model.HistoryItem;

public class HistoryParserTest {

	@Test
	public void canParseHistoryItems() throws Exception {
		final HistoryParser historyParser = new HistoryParser();
		List<HistoryItem> parse = historyParser.parse(ContentLoader.loadContent("history.json"));
		
		assertEquals("auser", parse.get(0).getUser().getUsername());
		assertEquals("APPROVE", parse.get(0).getType());
	}

}
