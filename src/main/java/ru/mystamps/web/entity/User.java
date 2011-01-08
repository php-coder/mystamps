package ru.mystamps.web.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class User {
	@Getter private final Long uid;
	@Getter private final String name;
	@Getter private final String login;
}
