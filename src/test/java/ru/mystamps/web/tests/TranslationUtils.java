package ru.mystamps.web.tests;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class TranslationUtils {
	
	private static final String DEFAULT_BUNDLE_CLASS_NAME =
		"ru.mystamps.i18n.Messages";
	
	private static final Locale DEFAULT_BUNDLE_LOCALE =
		Locale.ENGLISH;
	
	private static final ResourceBundle bundle =
		PropertyResourceBundle.getBundle(
			DEFAULT_BUNDLE_CLASS_NAME,
			DEFAULT_BUNDLE_LOCALE
		);
	
	public static String tr(final String key) {
		return bundle.getString(key);
	}
	
	// TODO: add simple unit tests (#93)
	public static String stripHtmlTags(final String msg) {
		return msg.replaceAll("\\<.*?>", "");
	}
	
	public static String tr(final String key, final Object... args) {
		return MessageFormat.format(tr(key), args);
	}
	
}
