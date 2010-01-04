package ru.mystamps.site.events;

public class SuspiciousEventBean {
	
	private String type = null;
	private String page = null;
	private String ip = null;
	private String refererPage = null;
	private String userAgent = null;
	
	public SuspiciousEventBean() {
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setPage(String page) {
		this.page = page;
	}
	
	public String getPage() {
		return this.page;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public void setRefererPage(String refererPage) {
		this.refererPage = refererPage;
	}
	
	public String getRefererPage() {
		return this.refererPage;
	}
	
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public String getUserAgent() {
		return this.userAgent;
	}
	
}

