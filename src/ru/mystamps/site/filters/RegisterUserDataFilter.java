package ru.mystamps.site.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import ru.mystamps.data.validators.Validator;
import ru.mystamps.data.validators.NotEmpty;
import ru.mystamps.site.ContextObject;

public class RegisterUserDataFilter implements Filter {
	
	private Logger log = null;
	
	/**
	 * Names of field which should not have empty values.
	 **/
	private static final String[] notEmptyFields = {
		"login",
		"pass1",
		"pass2",
		"email",
		"name"
	};
	
	/**
	 * Instance of NotEmpty validator.
	 **/
	private static final Validator notEmptyValidator = new NotEmpty("tv_empty_value");
	
	public RegisterUserDataFilter() {
	}
	
	@Override
	public void init(FilterConfig config) {
		this.log = Logger.getRootLogger();
	}
	
	@Override
	public void doFilter(ServletRequest request,
						ServletResponse response,
						FilterChain chain)
				throws IOException, ServletException {
		
		// if user sent data
		if (request.getParameterNames().hasMoreElements()) {
			int valuesReceivedCounter = 0;
			ContextObject contextObject = new ContextObject();
			
			// check each field
			for (String fieldName : notEmptyFields) {
				String fieldValue = request.getParameter(fieldName);
				
				// user not sent value for this field
				if (fieldValue == null) {
					continue;
				}
				
				// check value
				if (! notEmptyValidator.isValid(fieldValue)) {
					contextObject.addFailedElement(fieldName, notEmptyValidator.getMessage());
				}
				
				// don't lost user's data
				contextObject.addElement(fieldName, fieldValue);
				
				++valuesReceivedCounter;
			}
			
			if (valuesReceivedCounter != notEmptyFields.length) {
				StringBuffer msg = new StringBuffer();
				msg.append("Values received: ");
				msg.append(valuesReceivedCounter);
				msg.append(" but ");
				msg.append(notEmptyFields.length);
				msg.append(" expected!");
				log.warn(msg.toString());
			}
			
			request.setAttribute("context", contextObject);
		}
		
		chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() {
	}
}

