package ru.mystamps.web.model;

import lombok.Getter;
import lombok.Setter;

public class AuthAccountForm {
	@Getter @Setter private String login;
	@Getter @Setter private String password;
}
