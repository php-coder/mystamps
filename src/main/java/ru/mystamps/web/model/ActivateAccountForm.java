package ru.mystamps.web.model;

import lombok.Getter;
import lombok.Setter;

public class ActivateAccountForm {
	@Getter @Setter String login;
	@Getter @Setter String name;
	@Getter @Setter String password;
	@Getter @Setter String passwordConfirm;
	@Getter @Setter String activationKey;
}
