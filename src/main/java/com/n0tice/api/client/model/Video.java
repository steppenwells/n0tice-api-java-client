package com.n0tice.api.client.model;

public class Video {
	
	private final String id;
	private final Image image;
	
	public Video(String id, Image image) {
		this.id = id;
		this.image = image;
	}
	
	public String getOriginal() {
		return id;
	}
	
	public Image getImage() {
		return image;
	}

	@Override
	public String toString() {
		return "Video [image=" + image + ", id=" + id + "]";
	}
	
}
