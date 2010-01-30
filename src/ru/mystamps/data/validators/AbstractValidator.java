package ru.mystamps.data.validators;

public abstract class AbstractValidator implements Validator {
	
	private String message;
	
	/**
	 * @throws IllegalArgumentException when argument is null
	 **/
	protected void checkNotNullArg(String argument) {
		
		if (argument == null) {
			throw new IllegalArgumentException("Argument is null!");
		}
	}
	
	/**
	 * Check if argument is not null and not empty.
	 *
	 * @throws IllegalArgumentException when argument is null or empty
	 **/
	protected void checkNotEmptyArg(String argument) {
		checkNotNullArg(argument);
		
		if (argument.equals("")) {
			throw new IllegalArgumentException("Argument is empty!");
		}
	}
	
	@Override
	public void setMessage(String message) {
		checkNotEmptyArg(message);
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
}

