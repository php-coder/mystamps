/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;

import lombok.Getter;
import lombok.ToString;

// too many "PMD.SingularField" here and yes, I know that it's too complex :-(
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.ModifiedCyclomaticComplexity" })
@ToString
public class Pager {
	// be very careful when you're changing this value
	private static final int MAX_ITEMS = 5;
	
	private static final int MAX_ITEMS_BEFORE_CURRENT = MAX_ITEMS - 1;
	private static final int MAX_ITEMS_AFTER_CURRENT = MAX_ITEMS - 1;
	
	private static final int ITEMS_BEFORE_CURRENT = MAX_ITEMS / 2;
	private static final int ITEMS_AFTER_CURRENT = MAX_ITEMS / 2;
	
	private static final int FIRST_PAGE = 1;
	
	// this field is shown in toString() and useful when debugging unit tests
	@SuppressWarnings("PMD.SingularField")
	private final int totalRecords;
	
	// this field is shown in toString() and useful when debugging unit tests
	@SuppressWarnings("PMD.SingularField")
	private final int totalPages;
	
	// this field is shown in toString() and useful when debugging unit tests
	@SuppressWarnings({ "PMD.SingularField", "PMD.UnusedPrivateField" })
	private final int recordsPerPage;
	
	// this field is using in the view (hence its getter)
	@SuppressWarnings("PMD.SingularField")
	@Getter
	private final int currentPage;
	
	// this field is using in the view (hence its getter)
	@SuppressWarnings("PMD.SingularField")
	@Getter
	private final List<Integer> items;
	
	// this field is using in the view (hence its getter)
	@SuppressWarnings("PMD.SingularField")
	@Getter
	private final Integer prev;
	
	// this field is using in the view (hence its getter)
	@SuppressWarnings("PMD.SingularField")
	@Getter
	private final Integer next;
	
	public Pager(long totalRecords, int recordsPerPage, int currentPage) {
		Validate.isTrue(totalRecords >= 0, "Total records must be greater than or equal to zero");
		Validate.isTrue(recordsPerPage > 0, "Records per page must be greater than zero");
		Validate.isTrue(currentPage > 0, "Current page must be greater than zero");
		
		this.totalRecords = Math.toIntExact(totalRecords);
		this.totalPages = countTotalPages(this.totalRecords, recordsPerPage);
		this.recordsPerPage = recordsPerPage;
		this.currentPage = currentPage;
		this.items = createItems(this.totalRecords, recordsPerPage, currentPage, this.totalPages);
		this.prev = findPrev(currentPage);
		this.next = findNext(currentPage, this.totalPages);
	}
	
	private static int countTotalPages(int totalRecords, int recordsPerPage) {
		int totalPages = totalRecords / recordsPerPage;
		if (totalRecords % recordsPerPage > 0) {
			totalPages++;
		}
		return totalPages;
	}
	
	private static Integer findPrev(int currentPage) {
		if (currentPage == FIRST_PAGE) {
			return null;
		}
		
		return Integer.valueOf(currentPage - 1);
	}
	
	private static Integer findNext(int currentPage, int totalPages) {
		if (currentPage == totalPages) {
			return null;
		}
		
		return Integer.valueOf(currentPage + 1);
	}
	
	// I hope that we'll fix these one day
	@SuppressWarnings("PMD.NPathComplexity")
	private static List<Integer> createItems(
		int totalRecords,
		int recordsPerPage,
		int currentPage,
		int totalPages) {
		
		if (totalRecords <= recordsPerPage) {
			return Collections.singletonList(FIRST_PAGE);
		}
		
		int prevItemsCnt = 0;
		for (int i = 1; i <= MAX_ITEMS_BEFORE_CURRENT; i++) {
			int page = currentPage - i;
			if (page < FIRST_PAGE) {
				break;
			}
			prevItemsCnt++;
		}
		
		int nextItemsCnt = 0;
		for (int i = 1; i <= MAX_ITEMS_AFTER_CURRENT; i++) {
			int page = currentPage + i;
			if (page > totalPages) {
				break;
			}
			nextItemsCnt++;
		}
		
		// we've added too much to a both sides
		if (prevItemsCnt > ITEMS_BEFORE_CURRENT && nextItemsCnt > ITEMS_AFTER_CURRENT) {
			while (prevItemsCnt > ITEMS_BEFORE_CURRENT) {
				prevItemsCnt--;
			}
			while (nextItemsCnt > ITEMS_AFTER_CURRENT) {
				nextItemsCnt--;
			}
		
		// CheckStyle: ignore LineLength for next 3 lines
		// we've added too much to the beginning
		} else if (prevItemsCnt > ITEMS_BEFORE_CURRENT && nextItemsCnt <= ITEMS_AFTER_CURRENT) {
			while (prevItemsCnt > ITEMS_BEFORE_CURRENT && (prevItemsCnt + nextItemsCnt + 1) > MAX_ITEMS) {
				prevItemsCnt--;
			}
		
		// CheckStyle: ignore LineLength for next 3 lines
		// we've added too much to the end
		} else if (nextItemsCnt > ITEMS_AFTER_CURRENT && prevItemsCnt <= ITEMS_BEFORE_CURRENT) {
			while (nextItemsCnt > ITEMS_AFTER_CURRENT && (prevItemsCnt + nextItemsCnt + 1) > MAX_ITEMS) {
				nextItemsCnt--;
			}
		}
		
		List<Integer> items = new ArrayList<>();
		int fromPage = currentPage - prevItemsCnt;
		int toPage = currentPage + nextItemsCnt;
		for (int page = fromPage; page <= toPage; page++) {
			items.add(page);
		}
		
		Validate.validState(
			items.size() <= MAX_ITEMS,
			"Size of items (%s) must be <= %d when "
			+ "totalRecords = %d, recordsPerPage = %d, currentPage = %d and totalPages = %d",
			items, MAX_ITEMS, totalRecords, recordsPerPage, currentPage, totalPages
		);
		
		return items;
	}
	
}
