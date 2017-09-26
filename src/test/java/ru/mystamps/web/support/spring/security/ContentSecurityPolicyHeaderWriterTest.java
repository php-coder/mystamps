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

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsArrayContaining.hasItemInArray;

// 6 and 7 are number of directives and not magic numbers
@SuppressWarnings("checkstyle:magicnumber")
public class ContentSecurityPolicyHeaderWriterTest {
	
	@Test
	public void onIndexPageWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer = new ContentSecurityPolicyHeaderWriter(true);
		String[] directives = writer.constructDirectives("/").split(";");
		
		assertThat(directives, hasItemInArray("default-src 'none'"));
		
		assertThat(
			directives,
			hasItemInArray(
				"img-src "
					+ "https://cdn.rawgit.com "
					+ "https://raw.githubusercontent.com 'self'"
			)
		);
		
		assertThat(directives, hasItemInArray("font-src 'self'"));
		
		assertThat(
			directives,
			hasItemInArray("report-uri https://mystamps.report-uri.io/r/default/csp/reportOnly")
		);
		
		assertThat(directives, hasItemInArray("style-src https://cdn.rawgit.com 'self'"));
		assertThat(directives, hasItemInArray("script-src 'unsafe-inline' 'self'"));
		
		assertThat(directives, is(arrayWithSize(6)));
	}
	
	@Test
	public void onIndexPageWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer = new ContentSecurityPolicyHeaderWriter(false);
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
			hasItemInArray("report-uri https://mystamps.report-uri.io/r/default/csp/reportOnly")
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
		
		assertThat(directives, is(arrayWithSize(6)));
	}
	
	@Test
	public void onCollectionInfoPageWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer = new ContentSecurityPolicyHeaderWriter(true);
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
		assertThat(directives, is(arrayWithSize(6)));
	}
	
	@Test
	public void onCollectionInfoPageWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer = new ContentSecurityPolicyHeaderWriter(false);
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
		assertThat(directives, is(arrayWithSize(6)));
	}
	
	// TODO: /series/(add|\d+|\d/(ask|image))
	
	// TODO: /series/add
	
	@Test
	public void onTogglzConsoleWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer = new ContentSecurityPolicyHeaderWriter(true);
		String[] directives = writer.constructDirectives("/togglz/").split(";");
		
		// test only a directive that is differ from the index page
		assertThat(
			directives,
			hasItemInArray(
				"style-src "
					+ "https://cdn.rawgit.com "
					+ "'self' "
					+ "'sha256-biLFinpqYMtWHmXfkA1BPeCY0/fNt46SAZ+BBk5YUog=' "
					+ "'sha256-zQDRfdePzsm4666fPPtpna61v74bryIt2Xu5qx2rn4A='"
			)
		);
		
		// hope that all other directives are the same as on the index page
		assertThat(directives, is(arrayWithSize(6)));
	}
	
	@Test
	public void onTogglzConsoleWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer = new ContentSecurityPolicyHeaderWriter(false);
		String[] directives = writer.constructDirectives("/togglz/").split(";");
		
		// test only a directive that is differ from the index page
		assertThat(
			directives,
			hasItemInArray(
				"style-src "
					+ "https://cdn.rawgit.com "
					+ "https://stamps.filezz.ru "
					+ "https://maxcdn.bootstrapcdn.com "
					+ "'sha256-biLFinpqYMtWHmXfkA1BPeCY0/fNt46SAZ+BBk5YUog=' "
					+ "'sha256-zQDRfdePzsm4666fPPtpna61v74bryIt2Xu5qx2rn4A='"
			)
		);
		
		// hope that all other directives are the same as on the index page
		assertThat(directives, is(arrayWithSize(6)));
	}
	
	@Test
	public void onH2ConsoleWithLocalResources() {
		ContentSecurityPolicyHeaderWriter writer = new ContentSecurityPolicyHeaderWriter(true);
		String[] directives = writer.constructDirectives("/console/").split(";");
		
		// test only a directive that is differ from the index page
		assertThat(directives, hasItemInArray("child-src 'self'"));
		
		// hope that all other directives are the same as on the index page
		assertThat(directives, is(arrayWithSize(7)));
	}
	
	@Test
	public void onH2ConsoleWithResourcesFromCdn() {
		ContentSecurityPolicyHeaderWriter writer = new ContentSecurityPolicyHeaderWriter(false);
		String[] directives = writer.constructDirectives("/console/").split(";");
		
		// TODO: it shouldn't be on prod actually
		// test only a directive that is differ from the index page
		assertThat(directives, hasItemInArray("child-src 'self'"));
		
		// hope that all other directives are the same as on the index page
		assertThat(directives, is(arrayWithSize(7)));
	}
	
}
