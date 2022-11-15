/*
 * Copyright (C) 2009-2022 Slava Semushin <slava.semushin@gmail.com>
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import ru.mystamps.web.feature.account.AccountUrl;
import ru.mystamps.web.feature.account.UserService;
import ru.mystamps.web.feature.category.CategoryUrl;
import ru.mystamps.web.feature.collection.CollectionUrl;
import ru.mystamps.web.feature.country.CountryUrl;
import ru.mystamps.web.feature.participant.ParticipantUrl;
import ru.mystamps.web.feature.report.ReportUrl;
import ru.mystamps.web.feature.series.SeriesUrl;
import ru.mystamps.web.feature.series.importing.SeriesImportUrl;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportUrl;
import ru.mystamps.web.feature.site.SiteService;
import ru.mystamps.web.feature.site.SiteUrl;

import javax.servlet.Filter;
import java.util.Collections;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private Environment environment;
	
	@Lazy
	@Autowired
	private SiteService siteService;
	
	@Autowired(required = false)
	private H2ConsoleProperties h2ConsoleProperties;
	
	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/static/**", "/public/**");
	}
	
	@Override
	@SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "checkstyle:linelength" })
	protected void configure(HttpSecurity http) throws Exception {
		Profiles prod = Profiles.of("prod");
		boolean useSingleHost = !environment.acceptsProfiles(prod);
		boolean useCdn = environment.getProperty("app.use-cdn", Boolean.class, Boolean.TRUE);
		
		// @todo #226 Introduce app.use-public-hostname property
		boolean usePublicHostname = environment.acceptsProfiles(prod);
		String hostname = usePublicHostname ? SiteUrl.PUBLIC_URL : SiteUrl.SITE;

		String h2ConsolePath = h2ConsoleProperties == null ? null : h2ConsoleProperties.getPath();

		// Allow unsecured requests to H2 consoles if available.
		// See also spring.h2.console.path in application-test.properties
		String[] pathsToIgnore =
			h2ConsolePath == null ? new String[]{SiteUrl.CSP_REPORTS_HANDLER}
			                      : new String[]{h2ConsolePath + "/**", SiteUrl.CSP_REPORTS_HANDLER};
		
		ContentSecurityPolicyHeaderWriter cspWriter =
			new ContentSecurityPolicyHeaderWriter(useCdn, useSingleHost, hostname, h2ConsolePath);
		
		http
			.authorizeRequests()
				.mvcMatchers(CategoryUrl.ADD_CATEGORY_PAGE).hasAuthority(StringAuthority.CREATE_CATEGORY)
				.mvcMatchers(CountryUrl.ADD_COUNTRY_PAGE).hasAuthority(StringAuthority.CREATE_COUNTRY)
				.mvcMatchers(ParticipantUrl.ADD_PARTICIPANT_PAGE).hasAuthority(StringAuthority.ADD_PARTICIPANT)
				.mvcMatchers(SeriesUrl.ADD_SERIES_PAGE).hasAuthority(StringAuthority.CREATE_SERIES)
				.mvcMatchers(HttpMethod.PATCH, SeriesUrl.INFO_SERIES_PAGE)
					.hasAnyAuthority(StringAuthority.CREATE_SERIES, StringAuthority.ADD_COMMENTS_TO_SERIES)
				.mvcMatchers(SeriesImportUrl.REQUEST_IMPORT_SERIES_PAGE).hasAuthority(StringAuthority.IMPORT_SERIES)
				.mvcMatchers(SiteUrl.SITE_EVENTS_PAGE).hasAuthority(StringAuthority.VIEW_SITE_EVENTS)
				.mvcMatchers(CategoryUrl.SUGGEST_SERIES_CATEGORY).hasAuthority(StringAuthority.CREATE_SERIES)
				.mvcMatchers(CountryUrl.SUGGEST_SERIES_COUNTRY).hasAuthority(StringAuthority.CREATE_SERIES)
				.mvcMatchers(ReportUrl.DAILY_STATISTICS).hasAuthority(StringAuthority.VIEW_DAILY_STATS)
				.mvcMatchers(CollectionUrl.ESTIMATION_COLLECTION_PAGE)
					.access(HasAuthority.ADD_SERIES_PRICE_AND_COLLECTION_OWNER_OR_VIEW_ANY_ESTIMATION)
				.regexMatchers(HttpMethod.POST, "/series/[0-9]+")
					.hasAnyAuthority(
						StringAuthority.UPDATE_COLLECTION,
						StringAuthority.ADD_IMAGES_TO_SERIES
					)
				.regexMatchers(HttpMethod.POST, SeriesUrl.ADD_SERIES_ASK_PAGE.replace("{id}", "[0-9]+"))
					.hasAuthority(StringAuthority.ADD_SERIES_SALES)
				.mvcMatchers(HttpMethod.POST, SeriesUrl.MARK_SIMILAR_SERIES)
					.hasAnyAuthority(StringAuthority.MARK_SIMILAR_SERIES)
				.mvcMatchers(HttpMethod.POST, SeriesSalesImportUrl.IMPORT_SERIES_SALES)
					.hasAuthority(StringAuthority.IMPORT_SERIES_SALES)
				.anyRequest().permitAll()
				.and()
			.formLogin(formLogin -> formLogin
				.loginPage(AccountUrl.AUTHENTICATION_PAGE)
				.usernameParameter("login")
				.passwordParameter("password")
				.loginProcessingUrl(AccountUrl.LOGIN_PAGE)
				.failureUrl(AccountUrl.AUTHENTICATION_PAGE + "?failed")
				.defaultSuccessUrl(SiteUrl.INDEX_PAGE, true)
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl(AccountUrl.LOGOUT_PAGE)
				.logoutSuccessUrl(SiteUrl.INDEX_PAGE)
				.invalidateHttpSession(true)
				.permitAll()
			)
			.exceptionHandling()
				.accessDeniedHandler(getAccessDeniedHandler())
				// This entry point handles when you request a protected page and you are
				// not yet authenticated
				.authenticationEntryPoint(new Http403ForbiddenEntryPoint())
				.and()
			.csrf()
				.ignoringAntMatchers(pathsToIgnore)
				.and()
			.rememberMe()
				// FIXME: GH #27
				.disable()
			.headers()
				.defaultsDisabled() // FIXME
				// @todo #1161 Add Feature-Policy header
				.addHeaderWriter(cspWriter);
	}
	
	// Used in AccountConfig.Services.userService()
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ApplicationListener<AuthenticationFailureBadCredentialsEvent> getApplicationListener() {
		return new AuthenticationFailureListener(siteService);
	}
	
	@Bean
	public AccessDeniedHandler getAccessDeniedHandler() {
		return new LogCsrfEventAndShow403PageForAccessDenied(siteService, SiteUrl.FORBIDDEN_PAGE);
	}
	
	@Bean
	public AuthenticationProvider getAuthenticationProvider(UserService userService) {
		
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(getPasswordEncoder());
		provider.setUserDetailsService(new CustomUserDetailsService(userService));
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
	public FilterRegistrationBean<SessionLocaleResolverAwareFilter> getResetLocaleFilter(
		@Qualifier("requestContextFilter") Filter filter) {
		
		FilterRegistrationBean<SessionLocaleResolverAwareFilter> bean =
			new FilterRegistrationBean<>(new SessionLocaleResolverAwareFilter());
		
		// SessionLocaleResolverAwareFilter should be invoked after RequestContextFilter
		// to overwrite locale in LocaleContextHolder
		OrderedRequestContextFilter requestContextFilter = (OrderedRequestContextFilter)filter;
		bean.setOrder(requestContextFilter.getOrder() + 1);
		
		// url pattern should match HttpSecurity.formLogin().loginProcessingUrl()
		bean.setUrlPatterns(Collections.singletonList(AccountUrl.LOGIN_PAGE));
		
		return bean;
	}
	
	@Bean
	public FilterRegistrationBean<UserMdcLoggingFilter> userMdcLoggingFilter() {
		FilterRegistrationBean<UserMdcLoggingFilter> bean =
			new FilterRegistrationBean<>(new UserMdcLoggingFilter());
		// the filters that need to include userId in their logs, should have the order grater than
		// Ordered.LOWEST_PRECEDENCE - 100 to get applied after us
		// CheckStyle: ignore MagicNumber for next 1 line
		bean.setOrder(Ordered.LOWEST_PRECEDENCE - 100);
		return bean;
	}

}
