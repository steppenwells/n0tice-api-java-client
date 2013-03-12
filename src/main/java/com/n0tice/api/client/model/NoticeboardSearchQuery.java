package com.n0tice.api.client.model;


public class NoticeboardSearchQuery {

	private String q = null;
	private Integer page = null;
	private String noticeBoardOwnedBy = null;
	private Boolean alwaysOpen = null;
	private Boolean open = null;
	private Boolean closed = null;

	public NoticeboardSearchQuery q(String q) {
		this.q = q;
		return this;
	}
	
	public NoticeboardSearchQuery page(Integer page) {
		this.page = page;
		return this;
	}
	
	public NoticeboardSearchQuery noticeboardOwnedBy(String noticeboardOwnedBy) {
		this.noticeBoardOwnedBy = noticeboardOwnedBy;
		return this;
	}
	
	public NoticeboardSearchQuery alwaysOpen() {
		this.alwaysOpen = true;
		return this;
	}
	
	public NoticeboardSearchQuery open() {
		this.open = true;
		return this;
	}
	
	public NoticeboardSearchQuery closed() {
		this.closed = true;
		return this;
	}

	public String getQ() {
		return q;
	}

	public Integer getPage() {
		return page;
	}

	public String getNoticeBoardOwnedBy() {
		return noticeBoardOwnedBy;
	}

	public Boolean getAlwaysOpen() {
		return alwaysOpen;
	}

	public Boolean getOpen() {
		return open;
	}

	public Boolean getClosed() {
		return closed;
	}

	@Override
	public String toString() {
		return "NoticeboardSearchQuery [q=" + q + ", page=" + page
				+ ", noticeBoardOwnedBy=" + noticeBoardOwnedBy
				+ ", alwaysOpen=" + alwaysOpen + ", open=" + open + ", closed="
				+ closed + "]";
	}
	
}
