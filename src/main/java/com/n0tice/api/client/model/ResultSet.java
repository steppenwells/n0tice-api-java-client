package com.n0tice.api.client.model;

import java.util.List;
import java.util.Map;

public class ResultSet {

	private final int totalMatches;
	private final int startIndex;
	private final List<Content> content;
	private final Map<String, Map<String, Integer>> refinements;
	
	public ResultSet(int totalMatches, int startIndex, List<Content> content, Map<String, Map<String, Integer>> refinements) {
		this.totalMatches = totalMatches;
		this.startIndex = startIndex;
		this.content = content;
		this.refinements = refinements;
	}

	public List<Content> getContent() {
		return content;
	}

	public int getTotalMatches() {
		return totalMatches;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public Map<String, Map<String, Integer>> getRefinements() {
		return refinements;
	}

	@Override
	public String toString() {
		return "ResultSet [content=" + content + ", refinements=" + refinements
				+ ", startIndex=" + startIndex + ", totalMatches="
				+ totalMatches + "]";
	}
	
}
