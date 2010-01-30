package ru.mystamps.data.validators;

public abstract class AbstractValidator implements Validator {
	
	private String message;
	
	/**
	 * Check if argument is not null and not empty.
	 *
	 * @throws IllegalArgumentException when argument is null or empty
	 **/
	protected void checkNotEmptyArg(String argument) {
		
		if (argument == null) {
			throw new IllegalArgumentException("Argument is null!");
		}
		
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

