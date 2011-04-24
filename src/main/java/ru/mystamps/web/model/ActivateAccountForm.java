package ru.mystamps.web.model;

import lombok.Getter;
import lombok.Setter;

public class ActivateAccountForm {
	@Getter @Setter private String login;
	@Getter @Setter private String name;
	@Getter @Setter private String password;
	@Getter @Setter private String passwordConfirm;
	@Getter @Setter private String activationKey;
}
