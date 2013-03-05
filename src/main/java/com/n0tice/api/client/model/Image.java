package com.n0tice.api.client.model;

public class Image {
	
	private final String id;
	private final String small;
	private final String medium;
	private final String large;
	private final String extraLarge;
	private final String mediumlandscape;
	private final String mediumoriginalaspectdouble;
	private final String orientation;
	
	public Image(String id, String small, String medium, String large, String extraLarge, String mediumlandscape, String mediumoriginalaspectdouble, String orientation) {
		this.id = id;
		this.small = small;
		this.medium = medium;
		this.large = large;
		this.extraLarge = extraLarge;
		this.mediumlandscape = mediumlandscape;
		this.mediumoriginalaspectdouble = mediumoriginalaspectdouble;		
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
	
	public String getMediumoriginalaspectdouble() {
		return mediumoriginalaspectdouble;
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
		return "Image [extraLarge=" + extraLarge + ", id=" + id + ", large="
				+ large + ", medium=" + medium + ", mediumlandscape="
				+ mediumlandscape + ", mediumoriginalaspectdouble="
				+ mediumoriginalaspectdouble + ", orientation=" + orientation
				+ ", small=" + small + "]";
	}
	
}
