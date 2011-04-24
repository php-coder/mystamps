package ru.mystamps.web.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.WebElement;

public class WebElementUtils {
	
	public static List<String> convertToListWithText(final List<WebElement> elements) {
		if (elements.isEmpty()) {
			return Collections.<String>emptyList();
		}
		
		final List<String> result = new ArrayList<String>(elements.size());
		for (final WebElement el : elements) {
			result.add(el.getText());
		}
		
		return result;
	}
	
}
