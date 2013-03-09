package com.n0tice.api.client.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class SearchQuery {

	private String q = null;
	private Integer page = null;
	private Integer limit = null;
	private List<String> tags = new ArrayList<String>();
	private String type = null;
	private List<String> noticeBoards = new ArrayList<String>();
	private String location = null;
	private String user = null;
	private Double latitude;
	private Double longitude;
	private Double radius;
	private String country;
	private String via;
	private Integer maximumFlags;
	private Integer minimumFlags;
	private DateTime startingAfter;
	private DateTime endingAfter;
	private Boolean hasImages;
	private Boolean awaitingModeration;
	private String moderationStatus;
	private String order;
	private String noticeBoardOwnedBy;
	private Integer refinementCount;
	
	public SearchQuery q(String q) {
		this.q = q;
		return this;
	}
	
	public SearchQuery limit(Integer limit) {
		this.limit = limit;
		return this;
	}
	
	public SearchQuery tag(String tag) {
		tags.add(tag);
		return this;
	}

	public List<String> getTags() {
		return tags;
	}

	public SearchQuery page(int page) {
		this.page = page;
		return this;
	}

	public SearchQuery type(String type) {
		this.type = type;
		return this;
	}
	
	public SearchQuery user(String user) {
		this.user = user;
		return this;
	}
	
	public SearchQuery latitude(double latitude) {
		this.latitude = latitude;
		return this;
	}
	
	public SearchQuery longitude(double longitude) {
		this.longitude = longitude;
		return this;
	}
	
	public SearchQuery radius(double radius) {
		this.radius = radius;
		return this;
	}
	
	public SearchQuery country(String country) {
		this.country = country;
		return this;
	}
	
	public SearchQuery via(String via) {
		this.via = via;
		return this;
	}
	
	public SearchQuery maximumFlags(int maximumFlags) {
		this.maximumFlags = maximumFlags;
		return this;
	}
	
	public SearchQuery minimumFlags(int minimumFlags) {
		this.minimumFlags = minimumFlags;
		return this;
	}
	
	public SearchQuery noticeBoard(String noticeBoard) {
		noticeBoards.add(noticeBoard);
		return this;
	}
	
	public SearchQuery noticeBoardOwnedBy(String noticeBoardOwnedBy) {
		this.noticeBoardOwnedBy = noticeBoardOwnedBy;
		return this;
	}
	
	public SearchQuery location(String location) {
		this.location = location;
		return this;
	}
	
	public SearchQuery startingAfter(DateTime startingAfter) {
		this.startingAfter = startingAfter;
		return this;
	}
	
	public SearchQuery endingAfter(DateTime endingAfter) {
		this.endingAfter = endingAfter;
		return this;
	}
	
	public SearchQuery hasImages(boolean hasImages) {
		this.hasImages = hasImages;
		return this;
	}
	
	public SearchQuery awaitingModeration(boolean awaitingModeration) {
		this.awaitingModeration = awaitingModeration;
		return this;
	}
	
	public SearchQuery moderationStatus(String moderationStatus) {
		this.moderationStatus = moderationStatus;
		return this;
	}
	
	public SearchQuery order(String order) {
		this.order = order;
		return this;
	}
	
	public SearchQuery refinementCount(int refinementCount) {
		this.refinementCount = refinementCount;
		return this;
	}
	
	public String getType() {
		return type;
	}

	public List<String> getNoticeBoards() {
		return noticeBoards;
	}

	public String getQ() {
		return q;
	}

	public Integer getPage() {
		return page;
	}

	public Integer getLimit() {
		return limit;
	}

	public String getLocation() {
		return location;
	}

	public String getUser() {
		return user;
	}

	public Double getLatitude() {
		return latitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}

	public Double getRadius() {
		return radius;
	}
	
	public String getCountry() {
		return country;
	}

	public String getVia() {
		return via;
	}
	
	public Integer getMaximumFlags() {
		return maximumFlags;
	}
	
	public Integer getMinimumFlags() {
		return minimumFlags;
	}

	public DateTime getStartingAfter() {
		return startingAfter;
	}

	public DateTime getEndingAfter() {
		return endingAfter;
	}

	public Boolean getHasImages() {
		return hasImages;
	}
	
	public Boolean getAwaitingModeration() {
		return awaitingModeration;
	}
	
	public String getModerationStatus() {
		return moderationStatus;
	}
	
	public String getOrder() {
		return order;
	}
	
	public String getNoticeBoardOwnedBy() {
		return noticeBoardOwnedBy;
	}
	
	public Integer getRefinementCount() {
		return refinementCount;
	}

	@Override
	public String toString() {
		return "SearchQuery [q=" + q + ", page=" + page + ", limit=" + limit
				+ ", tags=" + tags + ", type=" + type + ", noticeBoards="
				+ noticeBoards + ", location=" + location + ", user=" + user
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", radius=" + radius + ", country=" + country + ", via="
				+ via + ", maximumFlags=" + maximumFlags + ", minimumFlags="
				+ minimumFlags + ", startingAfter=" + startingAfter
				+ ", endingAfter=" + endingAfter + ", hasImages=" + hasImages
				+ ", awaitingModeration=" + awaitingModeration
				+ ", moderationStatus=" + moderationStatus + ", order=" + order
				+ ", noticeBoardOwnedBy=" + noticeBoardOwnedBy
				+ ", refinementCount=" + refinementCount + "]";
	}
	
}
