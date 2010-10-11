package ru.mystamps.web.entity;

import lombok.Getter;

public class User {
	@Getter private final Long uid;
	@Getter private final String name;
	@Getter private final String login;
	
	public User(final Long uid, final String name, final String login) {
		this.uid = uid;		// TODO: should be > 0
		this.name = name;	// TODO: should be not null and not empty
		this.login = login;	// TODO: should be not null and not empty
	}
	
}
