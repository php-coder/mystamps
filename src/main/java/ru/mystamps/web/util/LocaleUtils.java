package ru.mystamps.web.util;

import java.util.Locale;

import org.apache.commons.lang3.Validate;

import ru.mystamps.web.entity.LocalizedEntity;

public final class LocaleUtils {
	
	private LocaleUtils() {
	}
	
	public static String getLanguageOrNull(Locale locale) {
		return locale != null ? locale.getLanguage() : null;
	}
	
	public static String getLocalizedName(Locale locale, LocalizedEntity entity) {
		Validate.isTrue(entity != null, "LocalizedEntity must be non null");
		
		String lang = getLanguageOrNull(locale);
		if ("ru".equals(lang)) {
			return entity.getNameRu();
		}
		
		return entity.getName();
	}
	
}
