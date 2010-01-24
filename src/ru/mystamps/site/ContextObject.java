package ru.mystamps.site;

import java.util.Map;
import java.util.HashMap;

public class ContextObject {
	
	private Map<String, String> elements;
	private Map<String, String> failedElements;
	
	public ContextObject() {
		elements = new HashMap<String, String>();
		failedElements = new HashMap<String, String>();
	}
	
	public void addElement(String elementName, String elementValue) {
		elements.put(elementName, elementValue);
	}
	
	public Map<String, String> getElements() {
		return elements;
	}
	
	public void addFailedElement(String elementName, String errorMessage) {
		failedElements.put(elementName, errorMessage);
	}
	
	public Map<String, String> getFailedElements() {
		return failedElements;
	}
	
	public int getCountFailedElements() {
		return failedElements.size();
	}
	
}
