package ru.mystamps.site.beans;

import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import ru.mystamps.db.Users;
import ru.mystamps.db.UsersActivation;
import ru.mystamps.site.utils.Messages;

public class ActivateBean {
	
	private UIInput loginInput;
	
	private String name;
	private String password;
	private String actKey;
	
	private Logger log = null;
	
	public ActivateBean() {
		log = Logger.getRootLogger();
	}
	
	public void setLoginInput(UIInput loginInput) {
		this.loginInput = loginInput;
	}
	
	public UIInput getLoginInput() {
		return loginInput;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setActKey(String actKey) {
		this.actKey = actKey;
	}
	
	public String getActKey() {
		return actKey;
	}
	
	/**
	 * Check that login and password mismath.
	 *
	 * @param FacesContext context
	 * @param UIComponent component
	 * @param Object value
	 * @throws ValidatorException
	 **/
	public void validatePasswordLoginMismatch(FacesContext context,
			UIComponent component, Object value) {
		
		// don't continue if login field not set or not valid
		if (! loginInput.isValid()) {
			return;
		}
		
		final String login = (String)loginInput.getLocalValue();
		final String password = (String)value;
		
		if (login == null) {
			log.warn("validatePasswordLoginMismatch(): value of login field is null!");
		}
		
		if (password == null) {
			log.warn("validatePasswordLoginMismatch(): value of password field is null!");
		}
		
		try {
			if (login.equals(password)) {
				String msg = Messages.getTranslation(context, "tv_password_login_match");
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
				throw new ValidatorException(message);
			}
		} catch (NullPointerException ex) {
			log.error("validatePasswordLoginMismatch() exception: " + ex.getMessage());
		}
	}
	
	/**
	 * @todo use transactions
	 **/
	public String activateUser()
		throws SQLException, NamingException {
		
		String login = (String)loginInput.getValue();
		
		// use login as name if name is not provided
		if (name.equals("")) {
			name = login;
		}
		
		log.debug("Activate user '" + login + "' (" + name + ") with password '" + password + "' (key = " + actKey + ")");
		
		Users users = new Users();
		users.add(login, password, name, actKey);
		
		UsersActivation activationiRequests = new UsersActivation();
		activationiRequests.del(actKey);
		
		return "activation_successful";
	}
	
}

