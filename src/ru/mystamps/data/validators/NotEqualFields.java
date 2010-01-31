package ru.mystamps.data.validators;

import javax.servlet.ServletRequest;

public class NotEqualFields extends EqualFields {
	
	public NotEqualFields() {
		this("Fields should have different values");
	}
	
	public NotEqualFields(String message) {
		super(message);
	}
	
	@Override
	public boolean isValid(String data, ServletRequest request) {
		return !super.isValid(data, request);
	}
	
}

