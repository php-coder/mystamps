package ru.mystamps.site.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class TrimSpacesConverter implements Converter {
	
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return value.trim();
	}
	
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value.toString();
	}
	
}
