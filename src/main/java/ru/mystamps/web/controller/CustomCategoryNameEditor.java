package ru.mystamps.web.controller;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.mystamps.web.validation.ValidationRules.CATEGORY_REPEAT_HYPHEN_REGEXP;

/**
 * Created by Максим on 28.08.2016.
 */

@SuppressWarnings("PMD")
public class CustomCategoryNameEditor extends PropertyEditorSupport {
	
	@Override
	public void setAsText(String categoryName) throws IllegalArgumentException {
		
		Pattern regexpCheck = Pattern.compile(CATEGORY_REPEAT_HYPHEN_REGEXP);
		Matcher regexpMatch = regexpCheck.matcher(categoryName);
		
		if (!regexpMatch.matches()) {
			
			setValue(categoryName.replaceAll(CATEGORY_REPEAT_HYPHEN_REGEXP, "-"));
			
		} else {
			
			setValue(categoryName);
			
		}
	}
	
}
