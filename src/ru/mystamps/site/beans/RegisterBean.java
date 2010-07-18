package ru.mystamps.site.beans;

import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.naming.NamingException;

import ru.mystamps.db.UsersActivation;

@ManagedBean(name="register")
@RequestScoped
public class RegisterBean {
	
	private String email;
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}
	
	/**
	 * @throws SQLException
	 * @throws NamingException
	 **/
	public String sendActivationKey()
		throws SQLException, NamingException {
		
		UsersActivation activationRequests = new UsersActivation();
		activationRequests.add(email);
		
		return "activation_sent";
	}
	
	
}

