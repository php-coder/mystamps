package ru.mystamps.site.beans;

import ru.mystamps.db.UsersActivation;

public class RegisterBean {
	
	private String email;
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}
	
	public RegisterBean() {
	}
	
	public String sendActivationKey() {
		
		UsersActivation activation = new UsersActivation();
		activation.add(email);
		
		return "activation_sent";
	}
	
	
}

