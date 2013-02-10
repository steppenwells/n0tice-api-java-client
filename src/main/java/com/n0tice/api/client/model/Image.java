package com.n0tice.api.client.model;

public class Image {
	
	private final String id;
	private final String small;
	private final String medium;
	private final String large;
	private final String extraLarge;
	private final String orientation;
	private final String mediumlandscape;
	
	public Image(String id, String small, String medium, String large, String extraLarge, String mediumlandscape, String orientation) {
		this.id = id;
		this.small = small;
		this.medium = medium;
		this.large = large;
		this.extraLarge = extraLarge;
		this.mediumlandscape = mediumlandscape;		
		this.orientation = orientation;
	}
	
	public String getId() {
		return id;
	}
	
	public String getSmall() {
		return small;
	}

	public String getMedium() {
		return medium;
	}
	
	public String getMediumlandscape() {
		return mediumlandscape;
	}

	public String getLarge() {
		return large;
	}
	
	public String getExtraLarge() {
		return extraLarge;
	}

	public String getOrientation() {
		return orientation;
	}

	@Override
	public String toString() {
		return "Image [id=" + id + ", small=" + small + ", medium=" + medium
				+ ", large=" + large + ", extraLarge=" + extraLarge
				+ ", orientation=" + orientation + ", mediumlandscape="
				+ mediumlandscape + "]";
	}
	
}
