package ru.mystamps.data.validators;

import javax.servlet.ServletRequest;

public class EqualFields extends AbstractValidator {
	
	private String firstFieldName = null;
	private String secondFieldName = null;
	
	public EqualFields() {
		this("Fields should have equal values");
	}
	
	public EqualFields(String message) {
		setMessage(message);
	}
	
	public void setFirstFieldName(String firstFieldName) {
		checkNotEmptyArg(firstFieldName);
		this.firstFieldName = firstFieldName;
	}
	
	public String getFirstFieldName() {
		return firstFieldName;
	}
	
	public void setSecondFieldName(String secondFieldName) {
		checkNotEmptyArg(secondFieldName);
		this.secondFieldName = secondFieldName;
	}
	
	public String getSecondFieldName() {
		return secondFieldName;
	}
	
	@Override
	public boolean isValid(String data, ServletRequest request) {
		checkNotEmptyArg(firstFieldName);
		checkNotEmptyArg(secondFieldName);
		
		final String firstFieldValue = request.getParameter(firstFieldName);
		final String secondFieldValue = request.getParameter(secondFieldName);
		
		checkNotEmptyArg(firstFieldValue);
		checkNotEmptyArg(secondFieldValue);
		
		if (firstFieldValue.equals(secondFieldValue)) {
			return true;
		}
		
		return false;
	}
	
}

