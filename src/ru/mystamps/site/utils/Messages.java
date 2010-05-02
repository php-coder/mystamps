package ru.mystamps.site.utils;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class Messages {
	
	/**
	 * Get message translation from application bundle for current locale.
	 *
	 * @param FacesContext context
	 * @param String message
	 **/
	public static String getTranslation(FacesContext context, String message) {
		
		String bundleName = context.getApplication().getMessageBundle();
		Locale locale = context.getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		final String translatedMessage =
			ResourceBundle.getBundle(bundleName, locale, loader).getString(message);
		
		return translatedMessage;
	}
	
}
