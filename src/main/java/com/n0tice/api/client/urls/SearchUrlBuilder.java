package com.n0tice.api.client.urls;

import org.joda.time.format.ISODateTimeFormat;

import com.google.common.base.Joiner;
import com.n0tice.api.client.model.NoticeboardSearchQuery;
import com.n0tice.api.client.model.SearchQuery;

public class SearchUrlBuilder {

	private static final String SEARCH = "/search";
	private static final String NOTICEBOARDS = "/noticeboards";
	
	private static Joiner COMMA_JOINER = Joiner.on(",");
   
	private final String apiUrl;
	
	public SearchUrlBuilder(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	
	public String toUrl(SearchQuery searchQuery) {
		final UrlStringBuilder url = new UrlStringBuilder();
		url.append(apiUrl);
		url.append(SEARCH);
		
		if (searchQuery.getQ() != null) {
			url.appendParameter("q", searchQuery.getQ());		
		}		
		if (searchQuery.getPage() != null) {		
			url.appendParameter("page", Integer.toString(searchQuery.getPage()));
		}
		if (searchQuery.getLimit() != null) {		
			url.appendParameter("limit", Integer.toString(searchQuery.getLimit()));
		}
		if (searchQuery.getType() != null) {
			url.appendParameter("type", searchQuery.getType());		
		}
		if (!searchQuery.getNoticeBoards().isEmpty()) {
			url.appendParameter("noticeboard", COMMA_JOINER.join(searchQuery.getNoticeBoards()));
		}
		if (searchQuery.getNoticeBoardOwnedBy() != null) {
			url.appendParameter("noticeboardOwnedBy", searchQuery.getNoticeBoardOwnedBy());
		}
		if (!searchQuery.getTags().isEmpty()) {
			url.appendParameter("tags", COMMA_JOINER.join(searchQuery.getTags()));
		}
		if (searchQuery.getUser() != null) {
			url.appendParameter("user", searchQuery.getUser());
		}		
		if (searchQuery.getLocation() != null) {
			url.appendParameter("location", searchQuery.getLocation());
		}
		if (searchQuery.getLatitude() != null) {
			url.appendParameter("latitude", Double.toString(searchQuery.getLatitude()));
		}
		if (searchQuery.getLongitude() != null) {
			url.appendParameter("longitude", Double.toString(searchQuery.getLongitude()));
		}
		if (searchQuery.getRadius() != null) {
			url.appendParameter("radius", Double.toString(searchQuery.getRadius()));
		}
		if (searchQuery.getCountry() != null) {
			url.appendParameter("country", searchQuery.getCountry());
		}
		if (searchQuery.getVia() != null) {
			url.appendParameter("via", searchQuery.getVia());
		}
		if (searchQuery.getMaximumFlags() != null) {
			url.appendParameter("maximumFlags", Integer.toString(searchQuery.getMaximumFlags()));
		}
		if (searchQuery.getMinimumFlags() != null) {
			url.appendParameter("minimumFlags", Integer.toString(searchQuery.getMinimumFlags()));
		}
		if (searchQuery.getHasImages() != null) {
			url.appendParameter("hasImages", Boolean.toString(searchQuery.getHasImages()));
		}
		if (searchQuery.getAwaitingModeration() != null) {
			url.appendParameter("awaitingModeration", Boolean.toString(searchQuery.getAwaitingModeration()));
		}
		if (searchQuery.getModerationStatus() != null) {
			url.appendParameter("moderationStatus", searchQuery.getModerationStatus());
		}
		if (searchQuery.getStartingAfter() != null) {
			url.appendParameter("startingAfter", ISODateTimeFormat.dateTimeNoMillis().print(searchQuery.getStartingAfter()));
		}
		if (searchQuery.getEndingAfter() != null) {
			url.appendParameter("endingAfter", ISODateTimeFormat.dateTimeNoMillis().print(searchQuery.getEndingAfter()));
		}
		if (searchQuery.getOrder() != null) {
			url.appendParameter("order", searchQuery.getOrder());
		}
		if (searchQuery.getRefinementCount() != null) {
			url.appendParameter("refinementCount", Integer.toString(searchQuery.getRefinementCount()));
		}
		
		return url.toString();
	}

	public String toUrl(NoticeboardSearchQuery noticeboardSearchQuery) {
		final UrlStringBuilder url = new UrlStringBuilder();
		url.append(apiUrl);
		url.append(NOTICEBOARDS);
		if (noticeboardSearchQuery.getQ() != null) {
			url.appendParameter("q", noticeboardSearchQuery.getQ());
		}
		if (noticeboardSearchQuery.getPage() != null) {
			url.appendParameter("page", Integer.toString(noticeboardSearchQuery.getPage()));
		}
		if (noticeboardSearchQuery.getNoticeBoardOwnedBy() != null) {
			url.appendParameter("noticeboardOwnedBy", noticeboardSearchQuery.getNoticeBoardOwnedBy());
		}
		if (noticeboardSearchQuery.getModerated() != null) {
			url.appendParameter("moderated", Boolean.toString(noticeboardSearchQuery.getModerated()));
		}
		if (noticeboardSearchQuery.getAlwaysOpen() != null) {
			url.appendParameter("alwaysOpen", Boolean.toString(noticeboardSearchQuery.getAlwaysOpen()));
		}
		if (noticeboardSearchQuery.getOpen() != null) {
			url.appendParameter("open", Boolean.toString(noticeboardSearchQuery.getOpen()));
		}
		if (noticeboardSearchQuery.getClosed() != null) {
			url.appendParameter("closed", Boolean.toString(noticeboardSearchQuery.getClosed()));
		}
		return url.toString();
	}
	
}
