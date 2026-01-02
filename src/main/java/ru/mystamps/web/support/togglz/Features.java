/*
 * Copyright (C) 2009-2026 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.support.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {
	
	@Label("/site/index: search by catalog in collection")
	@EnabledByDefault
	SEARCH_IN_COLLECTION,

	@Label("Use a category microservice for the category-related functions")
	USE_CATEGORY_MICROSERVICE,

	@Label("Use a country microservice for the country-related functions")
	USE_COUNTRY_MICROSERVICE,
	
	@Label("Use React components instead of server rendered HTML")
	USE_REACT,
	
	@Label("/site/index: feature to check that Togglz works")
	ALWAYS_DISABLED,
	
	@Label("Use Content-Security-Policy-Report-Only header instead of Content-Security-Policy")
	@EnabledByDefault
	CSP_REPORT_ONLY;
	
	public boolean isActive() {
		return FeatureContext.getFeatureManager().isActive(this);
	}
	
}
