package com.n0tice.api.client.model;

public class Image {
	
	private final String small;
	private final String medium;
	private final String large;
	private final String extraLarge;
	private final String orientation;
	private final String mediumlandscape;
	
	public Image(String small, String medium, String large, String extraLarge, String mediumlandscape, String orientation) {
		this.small = small;
		this.medium = medium;
		this.large = large;
		this.extraLarge = extraLarge;
		this.mediumlandscape = mediumlandscape;		
		this.orientation = orientation;
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
		return "Image [extraLarge=" + extraLarge + ", large=" + large
				+ ", medium=" + medium + ", mediumlandscape=" + mediumlandscape
				+ ", orientation=" + orientation + ", small=" + small + "]";
	}
	
}
