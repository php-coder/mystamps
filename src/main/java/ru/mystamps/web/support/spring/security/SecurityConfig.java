/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.support.spring.security;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpMethod;

import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.RequestMatcher;

import ru.mystamps.web.config.ServicesConfig;
import ru.mystamps.web.Url;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Inject
	private MessageSource messageSource;
	
	@Inject
	private ServicesConfig servicesConfig;
	
	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/static/**");
	}
	
	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers(Url.ADD_CATEGORY_PAGE).hasAuthority("CREATE_CATEGORY")
				.antMatchers(Url.ADD_COUNTRY_PAGE).hasAuthority("CREATE_COUNTRY")
				.antMatchers(Url.ADD_SERIES_PAGE).hasAuthority("CREATE_SERIES")
				.regexMatchers(HttpMethod.POST, "/series/[0-9]+")
					.hasAnyAuthority("UPDATE_COLLECTION", "ADD_IMAGES_TO_SERIES")
				.anyRequest().permitAll()
				.and()
			.formLogin()
				.loginPage(Url.AUTHENTICATION_PAGE)
				.usernameParameter("login")
				.passwordParameter("password")
				.loginProcessingUrl(Url.LOGIN_PAGE)
				.failureUrl(Url.AUTHENTICATION_PAGE + "?failed")
				.defaultSuccessUrl(Url.INDEX_PAGE, true)
				.permitAll()
				.and()
			.logout()
				.logoutUrl(Url.LOGOUT_PAGE)
				.logoutSuccessUrl(Url.INDEX_PAGE)
				.invalidateHttpSession(true)
				.permitAll()
				.and()
			.exceptionHandling()
				.accessDeniedPage(Url.UNAUTHORIZED_PAGE)
				// This entry point handles when you request a protected page and you are
				// not yet authenticated (defaults to Http403ForbiddenEntryPoint)
				.authenticationEntryPoint(new Http401UnauthorizedEntryPoint())
				.and()
			.csrf()
				// Allow unsecured requests to Togglz and H2 consoles.
				// See also: https://github.com/togglz/togglz/issues/119
				// See also: src/main/java/ru/mystamps/web/support/h2/H2Config.java
				// See also: src/main/java/ru/mystamps/web/support/togglz/TogglzConfig.java
				.requireCsrfProtectionMatcher(
					new AllExceptUrlsStartedWith(Url.TOGGLZ_CONSOLE_PAGE, "/console")
				)
			.and()
			.rememberMe()
				// TODO: GH #27
				.disable()
			.headers()
				// TODO
				.disable();
	}
	
	@Inject
	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(getAuthenticationProvider());
	}

	// Used in ServicesConfig.getUserService()
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ApplicationListener<AuthenticationFailureBadCredentialsEvent> getApplicationListener() {
		return new AuthenticationFailureListener();
	}
	
	private UserDetailsService getUserDetailsService() {
		return new CustomUserDetailsService(servicesConfig.getUserService());
	}
	
	private AuthenticationProvider getAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(getPasswordEncoder());
		provider.setUserDetailsService(getUserDetailsService());
		provider.setMessageSource(messageSource);
		return provider;
	}
	
	private static class AllExceptUrlsStartedWith implements RequestMatcher {
		
		private static final String[] ALLOWED_METHODS =
			new String[] {"GET", "HEAD", "TRACE", "OPTIONS"};
		
		private final String[] allowedUrls;
		
		public AllExceptUrlsStartedWith(String... allowedUrls) {
			this.allowedUrls = allowedUrls;
		}
		
		@Override
		public boolean matches(HttpServletRequest request) {
			// replicate default behavior (see CsrfFilter.DefaultRequiresCsrfMatcher class)
			String method = request.getMethod();
			for (String allowedMethod : ALLOWED_METHODS) {
				if (allowedMethod.equals(method)) {
					return false;
				}
			}
			
			// apply our own exceptions
			String uri = request.getRequestURI();
			for (String allowedUrl : allowedUrls) {
				if (uri.startsWith(allowedUrl)) {
					return false;
				}
			}
			
			return true;
		}
		
	}
	
}
