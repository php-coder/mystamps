package ru.mystamps.site.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

public class MaintenanceFilter implements Filter {
	
	private FilterConfig config = null;
	private Logger log = null;
	private static final String MAINTENANCE_PAGE_URL = "/maintenance.jsf";
	
	public MaintenanceFilter() {
	}
	
	@Override
	public void init(FilterConfig config) {
		this.config = config;
		this.log = Logger.getRootLogger();
	}
	
	@Override
	public void doFilter(ServletRequest request,
						ServletResponse response,
						FilterChain chain)
				throws IOException, ServletException {
		
		final String mode = config.getInitParameter("enableMaintainanceMode");
		if (mode == null || mode.equals("")) {
			throw new ServletException("enableMaintainanceMode parameter not set!");
		}
		
		final String allowedIP = config.getInitParameter("allowedIP");
		if (allowedIP == null || allowedIP.equals("")) {
			throw new ServletException("allowedIP parameter not set!");
		}
		
		if (mode.equalsIgnoreCase("yes")) {
			
			String userIP = request.getRemoteHost();
			if (userIP == null || userIP.equals("")) {
				log.warn("Cannot get user IP address");
			
			} else if (! allowedIP.equals(userIP)) {
				RequestDispatcher dispatch = request.getRequestDispatcher(MAINTENANCE_PAGE_URL);
				dispatch.forward(request, response);
				return;
			}
		}
		
		chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() {
	}
}

