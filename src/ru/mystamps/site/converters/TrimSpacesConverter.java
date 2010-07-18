package ru.mystamps.site.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.Converter;

@FacesConverter(value="TrimSpaces")
public class TrimSpacesConverter implements Converter {
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return value.trim();
	}
	
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value.toString();
	}
	
}
