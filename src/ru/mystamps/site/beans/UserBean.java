package ru.mystamps.site.beans;

import lombok.Getter;
import lombok.Setter;

public class UserBean {
	@Getter @Setter private Long uid = - 1L;
	@Getter @Setter private String name;
	@Getter @Setter private String login;
	
	public boolean isLogged() {
		return (name != null && login != null && uid > 0);
	}
	
}
