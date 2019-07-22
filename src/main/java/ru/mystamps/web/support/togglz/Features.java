/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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

// @todo #1022 Togglz: remove SEND_MAIL_VIA_HTTP_API from database
public enum Features implements Feature {
	
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
	
	@Label("Send mail with activation key to user")
	@EnabledByDefault
	SEND_ACTIVATION_MAIL,
	
	@Label("/series/add: show link with auto-suggestions")
	@EnabledByDefault
	SHOW_SUGGESTION_LINK,
	
	@Label("/site/index: search by catalog in collection")
	SEARCH_IN_COLLECTION;
	
	public boolean isActive() {
		return FeatureContext.getFeatureManager().isActive(this);
	}
	
}
