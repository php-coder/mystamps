/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.junit.Test;

import static io.qala.datagen.RandomShortApi.bool;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsArrayContaining.hasItemInArray;

public class ContentSecurityPolicyHeaderWriterTest {
	
	private static final int NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES = 6;
	private static final int NUMBER_OF_DIRECTIVES_ON_ADD_SERIES_PAGE = 7;
	private static final int NUMBER_OF_DIRECTIVES_ON_H2_CONSOLE_PAGE = 7;
	
	//
	// Tests for writeHeaders()
	//
	
	@Test
	public void writeContentSecurityPolicyHeader() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(bool(), bool());
		
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		writer.writeHeaders(request, response);
		
		String header = response.getHeader("Content-Security-Policy-Report-Only");
		assertThat(header, is(notNullValue()));
		assertThat(header.split(";"), is(arrayWithSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES)));
	}
	
	//
	// Tests for constructDirectives()
	//
	
	@Test
	public void onIndexPageWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(true, bool());
		String[] directives = writer.constructDirectives("/").split(";");
		
		assertThat(directives, hasItemInArray("default-src 'none'"));
		
		assertThat(
			directives,
			hasItemInArray(
				"img-src "
					+ "https://cdn.rawgit.com "
					+ "https://raw.githubusercontent.com "
					+ "'self'"
			)
		);
		
		assertThat(directives, hasItemInArray("font-src 'self'"));
		
		assertThat(
			directives,
			hasItemInArray("report-uri https://mystamps.report-uri.com/r/d/csp/reportOnly")
		);
		
		assertThat(directives, hasItemInArray("style-src https://cdn.rawgit.com 'self'"));
		assertThat(directives, hasItemInArray("script-src 'unsafe-inline' 'self'"));
		
		assertThat(directives, is(arrayWithSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES)));
	}
	
	@Test
	public void onIndexPageWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer
			= new ContentSecurityPolicyHeaderWriter(false, bool());
		String[] directives = writer.constructDirectives("/").split(";");
		
		assertThat(directives, hasItemInArray("default-src 'none'"));
		
		assertThat(
			directives,
			hasItemInArray(
				"img-src "
					+ "https://cdn.rawgit.com "
					+ "https://raw.githubusercontent.com "
					+ "https://stamps.filezz.ru"
			)
		);
		
		assertThat(directives, hasItemInArray("font-src https://maxcdn.bootstrapcdn.com"));
		
		assertThat(
			directives,
			hasItemInArray("report-uri https://mystamps.report-uri.com/r/d/csp/reportOnly")
		);
		
		assertThat(
			directives,
			hasItemInArray(
				"style-src "
					+ "https://cdn.rawgit.com "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com"
			)
		);
		
		assertThat(
			directives,
			hasItemInArray(
				"script-src "
					+ "'unsafe-inline' "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com "
					+ "https://yandex.st"
			)
		);
		
		assertThat(directives, is(arrayWithSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES)));
	}
	
	@Test
	public void onCollectionInfoPageWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(true, bool());
		String[] directives = writer.constructDirectives("/collection/user").split(";");
		
		// test only the directives that differ from the index page
		assertThat(
			directives,
			hasItemInArray(
				"style-src "
					+ "https://cdn.rawgit.com "
					+ "'self' "
					+ "https://www.gstatic.com "
					+ "'sha256-/kXZODfqoc2myS1eI6wr0HH8lUt+vRhW8H/oL+YJcMg='"
			)
		);
		
		assertThat(
			directives,
			hasItemInArray(
				"script-src "
					+ "'unsafe-inline' "
					+ "'self' "
					+ "'unsafe-eval' "
					+ "https://www.gstatic.com"
			)
		);
		
		// hope that all other directives are the same as on the index page
		assertThat(directives, is(arrayWithSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES)));
	}
	
	@Test
	public void onCollectionInfoPageWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(false, bool());
		String[] directives = writer.constructDirectives("/collection/user").split(";");
		
		// test only the directives that differ from the index page
		assertThat(
			directives,
			hasItemInArray(
				"style-src "
					+ "https://cdn.rawgit.com "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com "
					+ "https://www.gstatic.com "
					+ "'sha256-/kXZODfqoc2myS1eI6wr0HH8lUt+vRhW8H/oL+YJcMg='"
			)
		);
		
		assertThat(
			directives,
			hasItemInArray(
				"script-src "
					+ "'unsafe-inline' "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com "
					+ "https://yandex.st "
					+ "'unsafe-eval' "
					+ "https://www.gstatic.com"
			)
		);
		
		// hope that all other directives are the same as on the index page
		assertThat(directives, is(arrayWithSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES)));
	}
	
	@Test
	public void onSeriesAddImagePageWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(true, bool());
		
		for (String page : new String[]{"/series/11", "/series/12/ask", "/series/13/image"}) {
			String[] directives = writer.constructDirectives(page).split(";");

			// test only a directive that is differ from the index page
			assertThat(
				directives,
				hasItemInArray(
					"style-src "
						+ "https://cdn.rawgit.com "
						+ "'self' "
						+ "'sha256-DpmxvnMJIlwkpmmAANZYNzmyfnX2PQCBDO4CB2BFjzU='"
				)
			);

			// hope that all other directives are the same as on the index page
			assertThat(directives, is(arrayWithSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES)));
		}
	}
	
	@Test
	public void onSeriesAddImagePageWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(false, bool());
		
		for (String page : new String[]{"/series/11", "/series/12/ask", "/series/13/image"}) {
			String[] directives = writer.constructDirectives(page).split(";");

			// test only a directive that is differ from the index page
			assertThat(
				directives,
				hasItemInArray(
					"style-src "
						+ "https://cdn.rawgit.com "
						+ "https://stamps.filezz.ru "
						+ "https://maxcdn.bootstrapcdn.com "
						+ "'sha256-DpmxvnMJIlwkpmmAANZYNzmyfnX2PQCBDO4CB2BFjzU='"
				)
			);

			// hope that all other directives are the same as on the index page
			assertThat(directives, is(arrayWithSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES)));
		}
	}
	
	@Test
	public void onSeriesAddPageWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(true, bool());
		String[] directives = writer.constructDirectives("/series/add").split(";");
		
		// test only the directives that differ from the index page
		assertThat(
			directives,
			hasItemInArray(
				"style-src "
					+ "https://cdn.rawgit.com "
					+ "'self' "
					+ "'sha256-DpmxvnMJIlwkpmmAANZYNzmyfnX2PQCBDO4CB2BFjzU=' "
					+ "https://cdnjs.cloudflare.com"
			)
		);
		
		assertThat(
			directives,
			hasItemInArray(
				"script-src "
					+ "'unsafe-inline' "
					+ "'self' "
					+ "https://cdnjs.cloudflare.com"
			)
		);
		
		assertThat(directives, hasItemInArray("connect-src 'self'"));
		
		// hope that all other directives are the same as on the index page
		assertThat(directives, is(arrayWithSize(NUMBER_OF_DIRECTIVES_ON_ADD_SERIES_PAGE)));
	}
	
	@Test
	public void onSeriesAddPageWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(false, bool());
		String[] directives = writer.constructDirectives("/series/add").split(";");
		
		// test only the directives that differ from the index page
		assertThat(
			directives,
			hasItemInArray(
				"style-src "
					+ "https://cdn.rawgit.com "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com "
					+ "'sha256-DpmxvnMJIlwkpmmAANZYNzmyfnX2PQCBDO4CB2BFjzU=' "
					+ "https://cdnjs.cloudflare.com"
			)
		);
		
		assertThat(
			directives,
			hasItemInArray(
				"script-src "
					+ "'unsafe-inline' "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com "
					+ "https://yandex.st "
					+ "https://cdnjs.cloudflare.com"
			)
		);
		
		assertThat(directives, hasItemInArray("connect-src 'self'"));
		
		// hope that all other directives are the same as on the index page
		assertThat(directives, is(arrayWithSize(NUMBER_OF_DIRECTIVES_ON_ADD_SERIES_PAGE)));
	}
	
	@Test
	public void onH2ConsoleWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(true, true);
		String[] directives = writer.constructDirectives("/console/").split(";");
		
		// test only the directives that are differ from the index page
		assertThat(
			directives,
			hasItemInArray(
				"style-src "
					+ "https://cdn.rawgit.com"
					+ " 'self'"
					+ " 'sha256-biLFinpqYMtWHmXfkA1BPeCY0/fNt46SAZ+BBk5YUog='"
					+ " 'sha256-ZdHxw9eWtnxUb3mk6tBS+gIiVUPE3pGM470keHPDFlE='"
					+ " 'sha256-aqNNdDLnnrDOnTNdkJpYlAxKVJtLt9CtFLklmInuUAE='"
					+ " 'sha256-tIs8OfjWm8MHgPJrHv7mM4wvA/FDFcra3Pd5icRMX+k='"
					+ " 'sha256-VPm872V2JvE+vhivDg7UeH+N9a9YzzqGGow5mzY48hc='"
					+ " 'sha256-CDs+xFw5uMoNgtE5XIrz5GXgs3O+/NFkYK2IK/vKSBE='"
					+ " 'sha256-65mkwZPt4V1miqNM9CcVYkrpnlQigG9H6Vi9OM/JCgY='"
					+ " 'sha256-xSKCQeN6yeCb4HCkijkjoBFHWdJFwmwDiFa3XlZZ6Bs='"
					+ " 'sha256-JnnwE+8wsBgf/bh1qyvAsUVHBgiTioeZ1NSUKff7mOM='"
					+ " 'sha256-yBhVF062O1IGu3ZngyEhh9l561VFLsJpdSxVtbwisRY='"
					+ " 'sha256-eC+jXvbVSsG0J4zQfR5fWxxUCqpaa5DZLbINjWNCu48='"
					+ " 'sha256-rqkMEwsWwrInJqctxmIaWOCFPV+Qmym3tMHH3wtq3Y0='"
					+ " 'sha256-PGJ8tjuz2DXGgB1Sie9pW8BrxBGK6EQndbLEkXd44T8='"
			)
		);
		
		assertThat(directives, hasItemInArray("child-src 'self'"));
		
		// hope that all other directives are the same as on the index page
		assertThat(directives, is(arrayWithSize(NUMBER_OF_DIRECTIVES_ON_H2_CONSOLE_PAGE)));
	}
	
	@Test
	public void onH2ConsoleWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer =
			new ContentSecurityPolicyHeaderWriter(false, false);
		String[] directives = writer.constructDirectives("/console/").split(";");
		
		// "style-src" directive should be the same as for the index page
		assertThat(
			directives,
			hasItemInArray(
				"style-src "
					+ "https://cdn.rawgit.com "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com"
			)
		);
		
		assertThat(directives, not(hasItemInArray("child-src 'self'")));
		
		// hope that all other directives are the same as on the index page
		assertThat(directives, is(arrayWithSize(NUMBER_OF_DIRECTIVES_ON_STANDARD_PAGES)));
	}
	
}
