package com.n0tice.api.client.model;

import java.util.Date;
import java.util.Set;

public class Noticeboard {
	
	private final String domain;
	private final String name;
	private final String description;
	private final Image background;
	private final Image cover;
	private final Date endDate;
	private final Group group;
	private final Set<MediaType> supportedMediaTypes;

	public Noticeboard(String domain, String name, String description, Image background, Image cover, Date endDate, Group group, Set<MediaType> supportedMediaTypes) {
		this.domain = domain;
		this.name = name;
		this.description = description;
		this.background = background;
		this.cover = cover;
		this.endDate = endDate;
		this.group = group;
		this.supportedMediaTypes = supportedMediaTypes;
	}

	public String getDomain() {
		return domain;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public Image getBackground() {
		return background;
	}

	public Image getCover() {
		return cover;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public Group getGroup() {
		return group;
	}
	
	public Set<MediaType> getSupportedMediaTypes() {
		return supportedMediaTypes;
	}

	@Override
	public String toString() {
		return "Noticeboard [domain=" + domain + ", name=" + name
				+ ", description=" + description + ", background=" + background
				+ ", cover=" + cover + ", endDate=" + endDate + ", group="
				+ group + ", supportedMediaTypes=" + supportedMediaTypes + "]";
	}
	
}
