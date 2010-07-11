package ru.mystamps.site.beans;

public class UserBean {
	private Long uid     = - 1L;
	private String name  = null;
	private String login = null;
	
	public void setUid(Long uid) {
		this.uid = uid;
	}
	
	public Long getUid() {
		return uid;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getLogin() {
		return login;
	}
	
	public boolean isLogged() {
		return (name != null && login != null && uid > 0);
	}
	
}
