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
package ru.mystamps.web.feature.site;

@SuppressWarnings("PMD.CommentDefaultAccessModifier")
final class SiteDb {
	
	static final class SuspiciousActivity {
		static final int PAGE_URL_LENGTH     = 100;
		static final int METHOD_LENGTH       = 7;
		static final int REFERER_PAGE_LENGTH = 255;
		static final int USER_AGENT_LENGTH   = 255;
	}
	
	static final class SuspiciousActivityType {
		// see initiate-suspicious_activities_types-table changeset
		// in src/main/resources/liquibase/initial-state.xml
		static final String PAGE_NOT_FOUND        = "PageNotFound";
		static final String AUTHENTICATION_FAILED = "AuthenticationFailed";
		
		// see add-types-for-csrf-tokens-to-suspicious_activities_types-table changeset
		// in src/main/resources/liquibase/version/0.4/2016-02-19--csrf_events.xml
		static final String MISSING_CSRF_TOKEN = "MissingCsrfToken";
		static final String INVALID_CSRF_TOKEN = "InvalidCsrfToken";
	}
	
}
