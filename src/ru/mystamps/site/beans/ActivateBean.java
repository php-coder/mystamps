package ru.mystamps.site.beans;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.log4j.Logger;

public class ActivateBean {
	
	private UIInput loginInput;
	
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
	
	/**
	 * Get message translation from application bundle for current locale.
	 *
	 * @param FacesContext context
	 * @param String message
	 **/
	private String getMessageTranslation(FacesContext context, String message) {
		
		String bundleName = context.getApplication().getMessageBundle();
		Locale locale = context.getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		final String translatedMessage =
			ResourceBundle.getBundle(bundleName, locale, loader).getString(message);
		
		return translatedMessage;
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
				String msg = getMessageTranslation(context, "tv_password_login_match");
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
				throw new ValidatorException(message);
			}
		} catch (NullPointerException ex) {
			log.error("validatePasswordLoginMismatch() exception: " + ex.getMessage());
		}
	}
	
}

