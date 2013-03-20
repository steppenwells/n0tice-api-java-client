package com.n0tice.api.client.model;


public class User {
	
	private String username;
	private String displayName;
	private String bio;
	private Image profileImage;
	private Integer noticeboards;
	private Integer followedNoticeboards;
	private Integer followedUsers;
	private String profileUrl;
	
	public User(String username) {
		this.username = username;
	}

	public User(String username, String displayName, String bio, Image profileImage, Integer noticeboards, Integer followedNoticeboards, Integer followedUsers, String profileUrl) {
		this.username = username;
		this.displayName = displayName;
		this.bio = bio;
		this.profileImage = profileImage;
		this.noticeboards = noticeboards;
		this.followedNoticeboards = followedNoticeboards;
		this.followedUsers = followedUsers;
		this.profileUrl = profileUrl;
	}
	
	public String getUsername() {
		return username;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public String getBio() {
		return bio;
	}

	public Image getProfileImage() {
		return profileImage;
	}
	
	public Integer getNoticeboards() {
		return noticeboards;
	}
	
	public Integer getFollowedNoticeboards() {
		return followedNoticeboards;
	}
	
	public Integer getFollowedUsers() {
		return followedUsers;
	}
	
	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [bio=" + bio + ", displayName=" + displayName
				+ ", followedNoticeboards=" + followedNoticeboards
				+ ", followedUsers=" + followedUsers + ", noticeboards="
				+ noticeboards + ", profileImage=" + profileImage
				+ ", profileUrl=" + profileUrl + ", username=" + username + "]";
	}
	
}
