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
package ru.mystamps.web.support.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {
	
	@Label("/site/index: show list of recently added series")
	@EnabledByDefault
	SHOW_RECENT_SERIES_ON_INDEX_PAGE,
	
	@Label("/site/index: show list of recently created collections")
	@EnabledByDefault
	SHOW_RECENT_COLLECTIONS_ON_INDEX_PAGE,

	@Label("/site/index: show search panel")
	@EnabledByDefault
	SHOW_SEARCH_PANEL_ON_INDEX_PAGE,
	
	@Label("/site/index: show link to list of categories")
	@EnabledByDefault
	LIST_CATEGORIES,
	
	@Label("/site/index: show link to list of countries")
	@EnabledByDefault
	LIST_COUNTRIES,
	
	@Label("/series/{id}: possibility to user to add series to collection")
	@EnabledByDefault
	ADD_SERIES_TO_COLLECTION,
	
	@Label("/series/{id}: possibility of user to add additional images to series")
	@EnabledByDefault
	ADD_ADDITIONAL_IMAGES_TO_SERIES,
	
	@Label("/series/{id}: show series purchases and sales")
	@EnabledByDefault
	SHOW_PURCHASES_AND_SALES,
	
	@Label("/series/{id}: show images preview")
	@EnabledByDefault
	SHOW_IMAGES_PREVIEW,
	
	@Label("/series/{id}: possibility of user to add series purchases and sales")
	@EnabledByDefault
	ADD_PURCHASES_AND_SALES,
	
	@Label("/collection/{slug}: show statistics of collection")
	@EnabledByDefault
	SHOW_COLLECTION_STATISTICS,
	
	@Label("/collection/{slug}: show charts on collection page")
	@EnabledByDefault
	SHOW_COLLECTION_CHARTS,
	
	@Label("Send mail with activation key to user")
	@EnabledByDefault
	SEND_ACTIVATION_MAIL,
	
	@Label("View site events")
	@EnabledByDefault
	VIEW_SITE_EVENTS,

	@Label("/series/add: show link with auto-suggestions")
	@EnabledByDefault
	SHOW_SUGGESTION_LINK;
	
	public boolean isActive() {
		return FeatureContext.getFeatureManager().isActive(this);
	}
	
}
