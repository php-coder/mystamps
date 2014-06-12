package ru.mystamps.web.entity;

import java.util.Locale;

public interface LocalizedEntity {
	String getName();
	String getNameRu();
	String getLocalizedName(Locale locale);
}
