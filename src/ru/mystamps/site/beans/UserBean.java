package ru.mystamps.site.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="user")
@SessionScoped
public class UserBean {
	private Long uid = - 1L;
	private String name;
	private String login;
	
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
