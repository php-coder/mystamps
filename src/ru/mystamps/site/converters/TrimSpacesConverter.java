package ru.mystamps.site.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class TrimSpacesConverter implements Converter {
	
	@Override
	public Object getAsObject(
			final FacesContext context,
			final UIComponent component,
			final String value) {
		return value.trim();
	}
	
	@Override
	public String getAsString(
			final FacesContext context,
			final UIComponent component,
			final Object value) {
		return value.toString();
	}
	
}
