package ru.mystamps.web.validation;

public class ValidationRules {
	
	public static final Integer LOGIN_MIN_LENGTH = 2;
	public static final Integer LOGIN_MAX_LENGTH = 15;
	public static final String LOGIN_REGEXP = "[-_a-zA-Z0-9]+";
	
	public static final Integer NAME_MAX_LENGTH = 100;
	public static final String NAME_REGEXP1 = "[- \\p{Alpha}]+";
	public static final String NAME_REGEXP2 = "[ \\p{Alpha}][- \\p{Alpha}]+[ \\p{Alpha}]";
	
	public static final Integer PASSWORD_MIN_LENGTH = 4;
	public static final String PASSWORD_REGEXP = "[-_a-zA-Z0-9]+";
	
	public static final Integer EMAIL_MAX_LENGTH = 255;
	
	public static final Integer ACT_KEY_LENGTH = 10;
	public static final String ACT_KEY_REGEXP = "[0-9a-z]+";
	
}

