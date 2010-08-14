package ru.mystamps.site.beans;

import java.sql.SQLException;

import javax.naming.NamingException;

import lombok.Getter;
import lombok.Setter;

import ru.mystamps.db.UsersActivation;

public class RegisterBean {
	
	@Getter @Setter private String email;
	
	/**
	 * @throws SQLException
	 * @throws NamingException
	 **/
	public String sendActivationKey()
		throws SQLException, NamingException {
		
		final UsersActivation activationRequests = new UsersActivation();
		activationRequests.add(email);
		
		return "activation_sent";
	}
	
	
}

