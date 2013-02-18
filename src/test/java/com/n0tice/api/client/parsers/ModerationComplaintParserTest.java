package com.n0tice.api.client.parsers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class ModerationComplaintParserTest {
	
	@Test
	public void canParseModerationActions() throws Exception {
		final ModerationComplaintParser parser = new ModerationComplaintParser();
		
		final List<String> actions = parser.parseModerationActions(ContentLoader.loadContent("actions.json"));

		assertEquals(3, actions.size());
		assertEquals("DEFER", actions.get(2));
	}

}
