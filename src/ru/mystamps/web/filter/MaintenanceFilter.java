package ru.mystamps.web.filter;

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
	
	private FilterConfig config;
	private final Logger log = Logger.getRootLogger();
	private static final String MAINTENANCE_PAGE_URL = "/site/maintenance.htm";
	
	@Override
	public void init(final FilterConfig config) {
		this.config = config;
	}
	
	@Override
	public void doFilter(final ServletRequest request,
						final ServletResponse response,
						final FilterChain chain)
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
			
			final String userIP = request.getRemoteHost();
			if (userIP == null || userIP.equals("")) {
				log.warn("Cannot get user IP address");
			
			} else if (! allowedIP.equals(userIP)) {
				final RequestDispatcher dispatch =
					request.getRequestDispatcher(MAINTENANCE_PAGE_URL);
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

