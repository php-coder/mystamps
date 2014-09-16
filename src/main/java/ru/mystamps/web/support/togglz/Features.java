/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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
	
	@Label("Send mail with activation key to user")
	@EnabledByDefault
	SEND_ACTIVATION_MAIL,
	
	@Label("Show list of recently added series on index page")
	@EnabledByDefault
	SHOW_RECENT_SERIES_ON_INDEX_PAGE,
	
	@Label("Show statistics of collection on collection page")
	@EnabledByDefault
	SHOW_COLLECTION_STATISTICS,
	
	@Label("Possibility to user to add series to collection")
	@EnabledByDefault
	ADD_SERIES_TO_COLLECTION;
	
	public boolean isActive() {
		return FeatureContext.getFeatureManager().isActive(this);
	}
	
}
