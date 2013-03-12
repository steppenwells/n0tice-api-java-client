package com.n0tice.api.client.model;

import java.util.List;

public class NoticeboardResultSet {
	
	private final long totalMatches;
	private final List<Noticeboard> noticeboards;

	public NoticeboardResultSet(long totalMatches, List<Noticeboard> noticeboards) {
		this.totalMatches = totalMatches;
		this.noticeboards = noticeboards;
	}
	
	public long getTotalMatches() {
		return totalMatches;
	}
	
	public List<Noticeboard> getNoticeboards() {
		return noticeboards;
	}
	
	@Override
	public String toString() {
		return "NoticeboardResultSet [totalMatches=" + totalMatches
				+ ", noticeboards=" + noticeboards + "]";
	}
	
}
