/*
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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

import org.assertj.core.api.WithAssertions;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.togglz.junit.TogglzRule;
import ru.mystamps.web.feature.site.SiteUrl;
import ru.mystamps.web.support.togglz.Features;
import ru.mystamps.web.tests.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static io.qala.datagen.RandomShortApi.bool;

public class ContentSecurityPolicyHeaderWriterTest implements WithAssertions {
	
	private static final int NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES = 6;
	private static final int NUMBER_OF_DIRECTIVES_ON_ADD_SERIES_PAGE = 7;
	private static final int NUMBER_OF_DIRECTIVES_ON_INFO_SERIES_PAGE = 7;
	private static final int NUMBER_OF_DIRECTIVES_ON_H2_CONSOLE_PAGE = 7;
	private static final H2ConsoleProperties h2ConsoleProperties = new H2ConsoleProperties();

	@Rule
	public TogglzRule togglz = TogglzRule.allEnabled(Features.class);
	
	@BeforeClass
	public static void setupClass() {
		h2ConsoleProperties.setPath("/console/");
	}
	
	//
	// Tests for writeHeaders()
	//
	
	@Test
	public void writeContentSecurityPolicyHeader() {
		// given
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(bool(), bool(), bool(), Random.host(), h2ConsoleProperties);
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		// when
		writer.writeHeaders(request, response);
		// then
		String header = response.getHeader("Content-Security-Policy-Report-Only");
		assertThat(header).isNotNull();
		assertThat(header.split(";")).hasSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES);
		
		// when
		togglz.disable(Features.CSP_REPORT_ONLY);
		writer.writeHeaders(request, response);
		// then
		header = response.getHeader("Content-Security-Policy");
		assertThat(header).isNotNull();
		assertThat(header.split(";")).hasSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES);
	}
	
	//
	// Tests for constructDirectives()
	//
	
	@Test
	public void onIndexPageWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(false, true, bool(), SiteUrl.SITE, h2ConsoleProperties);
		String[] directives = writer.constructDirectives("/").split(";");
		
		assertThat(directives)
			.contains(
				"default-src 'none'",
				"img-src https://cdn.jsdelivr.net 'self'",
				"font-src 'self'",
				"report-uri http://127.0.0.1:8080/site/csp/reports",
				"style-src 'report-sample' https://cdn.jsdelivr.net 'self'",
				"script-src 'report-sample' 'unsafe-inline' 'self'"
			)
			.hasSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES);
	}
	
	@Test
	public void onIndexPageWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer
			= new ContentSecurityPolicyHeaderWriter(true, false, bool(), SiteUrl.PUBLIC_URL, h2ConsoleProperties);
		String[] directives = writer.constructDirectives("/").split(";");
		
		assertThat(directives)
			.contains(
				"default-src 'none'",
				"img-src https://cdn.jsdelivr.net https://stamps.filezz.ru",
				"font-src https://maxcdn.bootstrapcdn.com",
				"report-uri https://my-stamps.ru/site/csp/reports"
			)
			.contains(
				"style-src "
					+ "'report-sample' "
					+ "https://cdn.jsdelivr.net "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com"
			)
			.contains(
				"script-src "
					+ "'report-sample' "
					+ "'unsafe-inline' "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com "
					+ "https://yandex.st"
			)
			.hasSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES);
	}
	
	@Test
	public void onCollectionInfoPageWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(false, true, bool(), Random.host(), h2ConsoleProperties);
		String[] directives = writer.constructDirectives("/collection/user").split(";");
		
		// test only the directives that differ from the index page
		assertThat(directives)
			.contains(
				"style-src "
					+ "'report-sample' "
					+ "https://cdn.jsdelivr.net "
					+ "'self' "
					+ "https://www.gstatic.com "
					+ "'sha256-/kXZODfqoc2myS1eI6wr0HH8lUt+vRhW8H/oL+YJcMg='"
			)
			.contains(
				"script-src "
					+ "'report-sample' "
					+ "'unsafe-inline' "
					+ "'self' "
					+ "https://www.gstatic.com"
			)
			// hope that all other directives are the same as on the index page
			.hasSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES);
	}
	
	@Test
	public void onCollectionInfoPageWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(true, false, bool(), Random.host(), h2ConsoleProperties);
		String[] directives = writer.constructDirectives("/collection/user").split(";");
		
		// test only the directives that differ from the index page
		assertThat(directives)
			.contains(
				"style-src "
					+ "'report-sample' "
					+ "https://cdn.jsdelivr.net "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com "
					+ "https://www.gstatic.com "
					+ "'sha256-/kXZODfqoc2myS1eI6wr0HH8lUt+vRhW8H/oL+YJcMg='"
			)
			.contains(
				"script-src "
					+ "'report-sample' "
					+ "'unsafe-inline' "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com "
					+ "https://yandex.st "
					+ "https://www.gstatic.com"
			)
			// hope that all other directives are the same as on the index page
			.hasSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES);
	}
	
	@Test
	public void onSeriesAddImagePageWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(false, true, bool(), Random.host(), h2ConsoleProperties);
		
		for (String page : new String[]{"/series/11", "/series/12/ask", "/series/13/image"}) {
			String[] directives = writer.constructDirectives(page).split(";");

			// test only the directives that are differ from the index page
			assertThat(directives)
				.contains(
					"style-src "
						+ "'report-sample' "
						+ "https://cdn.jsdelivr.net "
						+ "'self' "
						+ "'sha256-DpmxvnMJIlwkpmmAANZYNzmyfnX2PQCBDO4CB2BFjzU='"
				)
				.contains("connect-src 'self'")
				// hope that all other directives are the same as on the index page
				.hasSize(NUMBER_OF_DIRECTIVES_ON_INFO_SERIES_PAGE);
		}
	}
	
	@Test
	public void onSeriesAddImagePageWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(true, false, bool(), Random.host(), h2ConsoleProperties);
		
		for (String page : new String[]{"/series/11", "/series/12/ask", "/series/13/image"}) {
			String[] directives = writer.constructDirectives(page).split(";");

			// test only the directives that are differ from the index page
			assertThat(directives)
				.contains(
					"style-src "
						+ "'report-sample' "
						+ "https://cdn.jsdelivr.net "
						+ "https://stamps.filezz.ru "
						+ "https://maxcdn.bootstrapcdn.com "
						+ "'sha256-DpmxvnMJIlwkpmmAANZYNzmyfnX2PQCBDO4CB2BFjzU='"
				)
				.contains(
					"script-src "
						+ "'report-sample' "
						+ "'unsafe-inline' "
						+ "https://stamps.filezz.ru "
						+ "https://maxcdn.bootstrapcdn.com "
						+ "https://yandex.st "
						+ "https://unpkg.com"
				)
				.contains("connect-src 'self'")
				// hope that all other directives are the same as on the index page
				.hasSize(NUMBER_OF_DIRECTIVES_ON_INFO_SERIES_PAGE);
		}
	}
	
	@Test
	public void onSeriesAddPageWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(false, true, bool(), Random.host(), h2ConsoleProperties);
		String[] directives = writer.constructDirectives("/series/add").split(";");
		
		// test only the directives that differ from the index page
		assertThat(directives)
			.contains(
				"style-src "
					+ "'report-sample' "
					+ "https://cdn.jsdelivr.net "
					+ "'self' "
					+ "'sha256-DpmxvnMJIlwkpmmAANZYNzmyfnX2PQCBDO4CB2BFjzU=' "
					+ "https://cdnjs.cloudflare.com"
			)
			.contains(
				"script-src "
					+ "'report-sample' "
					+ "'unsafe-inline' "
					+ "'self' "
					+ "https://cdnjs.cloudflare.com"
			)
			.contains("connect-src 'self'")
			// hope that all other directives are the same as on the index page
			.hasSize(NUMBER_OF_DIRECTIVES_ON_ADD_SERIES_PAGE);
	}
	
	@Test
	public void onSeriesAddPageWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(true, false, bool(), Random.host(), h2ConsoleProperties);
		String[] directives = writer.constructDirectives("/series/add").split(";");
		
		// test only the directives that differ from the index page
		assertThat(directives)
			.contains(
				"style-src "
					+ "'report-sample' "
					+ "https://cdn.jsdelivr.net "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com "
					+ "'sha256-DpmxvnMJIlwkpmmAANZYNzmyfnX2PQCBDO4CB2BFjzU=' "
					+ "https://cdnjs.cloudflare.com"
			)
			.contains(
				"script-src "
					+ "'report-sample' "
					+ "'unsafe-inline' "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com "
					+ "https://yandex.st "
					+ "https://cdnjs.cloudflare.com"
			)
			.contains("connect-src 'self'")
			// hope that all other directives are the same as on the index page
			.hasSize(NUMBER_OF_DIRECTIVES_ON_ADD_SERIES_PAGE);
	}
	
	@Test
	public void onH2ConsoleWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(false, true, true, Random.host(), h2ConsoleProperties);
		String[] directives = writer.constructDirectives("/console/").split(";");
		
		// test only the directives that are differ from the index page
		assertThat(directives).
			contains(
				"style-src "
					+ "'report-sample' "
					+ "https://cdn.jsdelivr.net"
					+ " 'self'"
					+ " 'sha256-biLFinpqYMtWHmXfkA1BPeCY0/fNt46SAZ+BBk5YUog='"
					+ " 'sha256-aqNNdDLnnrDOnTNdkJpYlAxKVJtLt9CtFLklmInuUAE='"
					+ " 'sha256-tIs8OfjWm8MHgPJrHv7mM4wvA/FDFcra3Pd5icRMX+k='"
					+ " 'sha256-VPm872V2JvE+vhivDg7UeH+N9a9YzzqGGow5mzY48hc='"
					+ " 'sha256-CDs+xFw5uMoNgtE5XIrz5GXgs3O+/NFkYK2IK/vKSBE='"
					+ " 'sha256-JnnwE+8wsBgf/bh1qyvAsUVHBgiTioeZ1NSUKff7mOM='"
					+ " 'sha256-yBhVF062O1IGu3ZngyEhh9l561VFLsJpdSxVtbwisRY='"
					+ " 'sha256-RZ7vfNSfdJtvDeBSz2SI5g3wroaD1A1SzsDb04Yw9V0='"
					+ " 'sha256-PGJ8tjuz2DXGgB1Sie9pW8BrxBGK6EQndbLEkXd44T8='"
			)
			.contains("child-src 'self'")
			// hope that all other directives are the same as on the index page
			.hasSize(NUMBER_OF_DIRECTIVES_ON_H2_CONSOLE_PAGE);
	}
	
	@Test
	public void onH2ConsoleWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(true, false, false, Random.host(), h2ConsoleProperties);
		String[] directives = writer.constructDirectives("/console/").split(";");
		
		assertThat(directives)
			// "style-src" directive should be the same as for the index page
			.contains(
				"style-src "
					+ "'report-sample' "
					+ "https://cdn.jsdelivr.net "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com"
			)
			.doesNotContain("child-src 'self'")
			// hope that all other directives are the same as on the index page
			.hasSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES);
	}
	
}
