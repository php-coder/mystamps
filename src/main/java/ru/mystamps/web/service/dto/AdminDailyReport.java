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
package ru.mystamps.web.service.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDailyReport {
	private Date startDate;
	private Date endDate;
	private long addedCategoriesCounter;
	private long untranslatedCategoriesCounter;
	private long addedCountriesCounter;
	private long untranslatedCountriesCounter;
	private long addedSeriesCounter;
	private long updatedSeriesCounter;
	private long updatedCollectionsCounter;
	private long registrationRequestsCounter;
	private long registeredUsersCounter;
	private long notFoundCounter;
	private long failedAuthCounter;
	private long missingCsrfCounter;
	private long invalidCsrfCounter;
	
	public long countEvents() {
		long eventsCounter = 0L;
		eventsCounter = Math.addExact(eventsCounter, notFoundCounter);
		eventsCounter = Math.addExact(eventsCounter, failedAuthCounter);
		eventsCounter = Math.addExact(eventsCounter, missingCsrfCounter);
		eventsCounter = Math.addExact(eventsCounter, invalidCsrfCounter);
		return eventsCounter;
	}
	
	public long countTotalChanges() {
		long totalChanges = 0L;
		totalChanges = Math.addExact(totalChanges, addedCategoriesCounter);
		totalChanges = Math.addExact(totalChanges, untranslatedCategoriesCounter);
		totalChanges = Math.addExact(totalChanges, addedCountriesCounter);
		totalChanges = Math.addExact(totalChanges, untranslatedCountriesCounter);
		totalChanges = Math.addExact(totalChanges, addedSeriesCounter);
		totalChanges = Math.addExact(totalChanges, updatedSeriesCounter);
		totalChanges = Math.addExact(totalChanges, updatedCollectionsCounter);
		totalChanges = Math.addExact(totalChanges, registrationRequestsCounter);
		totalChanges = Math.addExact(totalChanges, registeredUsersCounter);
		totalChanges = Math.addExact(totalChanges, countEvents());
		return totalChanges;
	}
	
}
