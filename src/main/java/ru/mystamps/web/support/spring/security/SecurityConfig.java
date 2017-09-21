/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Collections;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

import org.springframework.boot.web.filter.OrderedRequestContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
// CheckStyle: ignore LineLength for next 1 line
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// CheckStyle: ignore LineLength for next 1 line
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import ru.mystamps.web.Url;
import ru.mystamps.web.config.ServicesConfig;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ServicesConfig servicesConfig;
	
	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/static/**", "/public/**");
	}
	
	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.mvcMatchers(Url.ADD_CATEGORY_PAGE).hasAuthority(StringAuthority.CREATE_CATEGORY)
				.mvcMatchers(Url.ADD_COUNTRY_PAGE).hasAuthority(StringAuthority.CREATE_COUNTRY)
				.mvcMatchers(Url.ADD_PARTICIPANT_PAGE).hasAuthority(StringAuthority.ADD_PARTICIPANT)
				.mvcMatchers(Url.ADD_SERIES_PAGE).hasAuthority(StringAuthority.CREATE_SERIES)
				.mvcMatchers(Url.SITE_EVENTS_PAGE).hasAuthority(StringAuthority.VIEW_SITE_EVENTS)
				.mvcMatchers(Url.SUGGEST_SERIES_COUNTRY).hasAuthority(StringAuthority.CREATE_SERIES)
				.mvcMatchers(Url.DAILY_STATISTICS).hasAuthority(StringAuthority.VIEW_DAILY_STATS)
				.regexMatchers(HttpMethod.POST, "/series/[0-9]+")
					.hasAnyAuthority(
						StringAuthority.UPDATE_COLLECTION,
						StringAuthority.ADD_IMAGES_TO_SERIES
					)
				.regexMatchers(HttpMethod.POST, Url.ADD_SERIES_ASK_PAGE.replace("{id}", "[0-9]+"))
					.hasAuthority(StringAuthority.ADD_SERIES_SALES)
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
				.accessDeniedHandler(getAccessDeniedHandler())
				// This entry point handles when you request a protected page and you are
				// not yet authenticated
				.authenticationEntryPoint(new Http403ForbiddenEntryPoint())
				.and()
			.csrf()
				// Allow unsecured requests to H2 consoles.
				.ignoringAntMatchers("/console/**")
				.and()
			.rememberMe()
				// TODO: GH #27
				.disable()
			.headers()
				.defaultsDisabled() // TODO
				.contentSecurityPolicy(
					// default policy prevents loading resources from any source
					"default-src 'none'; "
					// 'self' is required for: our own CSS files
					// 'https://cdn.rawgit.com' is required for: languages.min.css (TODO: GH #246)
					// 'https://www.gstatic.com' is required for: Google Charts on collection page.
					// 'sha256-Dpm...' is required for: 'box-shadow: none; border: 0px;' inline CSS
					// that are using on /series/add and /series/{id} pages.
					// 'sha256-/kX...' is required for: 'overflow: hidden;' inline CSS that is using
					// bg Google Charts on collection page.
					+ "style-src 'self' https://cdn.rawgit.com https://www.gstatic.com "
						+ "'sha256-DpmxvnMJIlwkpmmAANZYNzmyfnX2PQCBDO4CB2BFjzU=' "
						+ "'sha256-/kXZODfqoc2myS1eI6wr0HH8lUt+vRhW8H/oL+YJcMg='; "
					// 'self' is required for: our own JS files
					// 'unsafe-inline' is required for: jquery.min.js (that is using code inside of
					// event handlers. We can't use hashing algorithms because they aren't supported
					// for handlers. In future, we should get rid of jQuery or use
					// 'unsafe-hashed-attributes' from CSP3. Details:
					// https://github.com/jquery/jquery/blob/d71f6a53927ad02d/jquery.js#L1441-L1447
					// and https://w3c.github.io/webappsec-csp/#unsafe-hashed-attributes-usage)
					// 'unsafe-eval' is required for: loader.js (for Google Charts)
					// 'https://www.gstatic.com' is required for: Google Charts on collection page.
					+ "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://www.gstatic.com; "
					// 'self' is required for: AJAX requests from our scripts (country suggestions)
					+ "connect-src 'self'; "
					// 'self' is required for: uploaded images and its previews
					// 'https://cdn.rawgit.com' is required for: languages.png (TODO: GH #246)
					// 'https://raw.githubusercontent.com' is required for: languages.png
					+ "img-src 'self' https://cdn.rawgit.com https://raw.githubusercontent.com; "
					// 'self' is required for: glyphicons-halflings-regular.woff2 from bootstrap
					+ "font-src 'self'; "
					+ "report-uri https://mystamps.report-uri.io/r/default/csp/reportOnly"
				).reportOnly();
	}
	
	// Used in ServicesConfig.getUserService()
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ApplicationListener<AuthenticationFailureBadCredentialsEvent> getApplicationListener() {
		return new AuthenticationFailureListener(servicesConfig.getSiteService());
	}
	
	@Bean
	public AccessDeniedHandler getAccessDeniedHandler() {
		return new LogCsrfEventAndShow403PageForAccessDenied(
			servicesConfig.getSiteService(),
			Url.FORBIDDEN_PAGE
		);
	}
	
	@Bean
	public AuthenticationProvider getAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(getPasswordEncoder());
		provider.setUserDetailsService(getUserDetailsService());
		provider.setMessageSource(messageSource);
		return provider;
	}
	
	// By default RequestContextFilter is created. Override it with its ordered version.
	// Note that name is important here
	@Bean(name = "requestContextFilter")
	public Filter getOrderedRequestContextFilter() {
		return new OrderedRequestContextFilter();
	}
	
	// Bean name will be shown in logs
	@Bean(name = "resetLocaleFilter")
	public FilterRegistrationBean getResetLocaleFilter(
		@Qualifier("requestContextFilter") Filter filter) {
		
		FilterRegistrationBean bean = new FilterRegistrationBean(
			new SessionLocaleResolverAwareFilter()
		);
		
		// SessionLocaleResolverAwareFilter should be invoked after RequestContextFilter
		// to overwrite locale in LocaleContextHolder
		OrderedRequestContextFilter requestContextFilter = (OrderedRequestContextFilter)filter;
		bean.setOrder(requestContextFilter.getOrder() + 1);
		
		// url pattern should match HttpSecurity.formLogin().loginProcessingUrl()
		bean.setUrlPatterns(Collections.singletonList(Url.LOGIN_PAGE));
		
		return bean;
	}
	
	private UserDetailsService getUserDetailsService() {
		return new CustomUserDetailsService(servicesConfig.getUserService());
	}
	
}
