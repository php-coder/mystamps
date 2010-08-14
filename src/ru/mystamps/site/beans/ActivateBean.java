package ru.mystamps.site.beans;

import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import javax.naming.NamingException;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ru.mystamps.db.Users;
import ru.mystamps.db.UsersActivation;
import ru.mystamps.site.utils.Messages;

public class ActivateBean {
	
	@Getter @Setter private UIInput loginInput;
	@Getter @Setter private String name;
	@Getter @Setter private String password;
	@Getter @Setter private String actKey;
	
	private final Logger log = Logger.getRootLogger();
	
	/**
	 * Check that login and password mismath.
	 *
	 * @param FacesContext context
	 * @param UIComponent component
	 * @param Object value
	 * @throws ValidatorException
	 **/
	public void validatePasswordLoginMismatch(
			final FacesContext context,
			final UIComponent component,
			final Object value) {
		
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
				final String msg =
					Messages.getTranslation(context, "tv_password_login_match");
				final FacesMessage message =
					new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
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
		
		final String login = (String)loginInput.getValue();
		
		// use login as name if name is not provided
		if (name.equals("")) {
			name = login;
		}
		
		log.debug("Activate user '" + login + "' (" + name + ") with password '" + password + "' (key = " + actKey + ")");
		
		final Users users = new Users();
		users.add(login, password, name, actKey);
		
		final UsersActivation activationRequests = new UsersActivation();
		activationRequests.del(actKey);
		
		return "activation_successful";
	}
	
}

