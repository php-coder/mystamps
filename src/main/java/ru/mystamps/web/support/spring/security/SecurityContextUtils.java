package ru.mystamps.web.support.spring.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;

public final class SecurityContextUtils {
	
	private SecurityContextUtils() {
	}
	
	public static boolean hasAuthority(HttpServletRequest request, String authority) {
		return new SecurityContextHolderAwareRequestWrapper(request, null).isUserInRole(authority);
	}
	
}
