package ru.mystamps.web.validation;

public class ValidationRules {
	
	public static final int LOGIN_MIN_LENGTH = 2;
	public static final int LOGIN_MAX_LENGTH = 15;
	public static final String LOGIN_REGEXP = "[-_a-zA-Z0-9]+";
	
	public static final int NAME_MAX_LENGTH = 100;
	public static final String NAME_REGEXP1 = "[- \\p{Alpha}]+";
	public static final String NAME_REGEXP2 = "[ \\p{Alpha}][- \\p{Alpha}]+[ \\p{Alpha}]";
	
	public static final int PASSWORD_MIN_LENGTH = 4;
	public static final String PASSWORD_REGEXP = "[-_a-zA-Z0-9]+";
	
	public static final int EMAIL_MAX_LENGTH = 255;
	
	public static final int ACT_KEY_LENGTH = 10;
	public static final String ACT_KEY_REGEXP = "[0-9a-z]+";
	
}

