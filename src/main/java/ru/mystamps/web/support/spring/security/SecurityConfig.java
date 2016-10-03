/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

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
				.mvcMatchers(Url.ADD_CATEGORY_PAGE)
					.hasAuthority(StringAuthority.CREATE_CATEGORY)
				.mvcMatchers(Url.ADD_COUNTRY_PAGE)
					.hasAuthority(StringAuthority.CREATE_COUNTRY)
				.mvcMatchers(
					Url.ADD_SERIES_PAGE,
					Url.ADD_SERIES_WITH_CATEGORY_PAGE.replace("{slug}", "**"),
					Url.ADD_SERIES_WITH_COUNTRY_PAGE.replace("{slug}", "**")
				)
					.hasAuthority(StringAuthority.CREATE_SERIES)
				.mvcMatchers(Url.SITE_EVENTS_PAGE)
					.hasAuthority(StringAuthority.VIEW_SITE_EVENTS)
				.regexMatchers(HttpMethod.POST, "/series/[0-9]+")
					.hasAnyAuthority(
						StringAuthority.UPDATE_COLLECTION,
						StringAuthority.ADD_IMAGES_TO_SERIES
					)
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
				// not yet authenticated (defaults to Http403ForbiddenEntryPoint)
				.authenticationEntryPoint(new Http401UnauthorizedEntryPoint())
				.and()
			.csrf()
				// Allow unsecured requests to H2 consoles.
				.ignoringAntMatchers("/console/**")
			.and()
			.rememberMe()
				// TODO: GH #27
				.disable()
			.headers()
				// TODO
				.disable();
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
	
	private UserDetailsService getUserDetailsService() {
		return new CustomUserDetailsService(servicesConfig.getUserService());
	}
	
}
