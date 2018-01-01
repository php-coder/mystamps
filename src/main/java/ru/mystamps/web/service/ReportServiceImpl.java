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
package ru.mystamps.web.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.lang3.time.DatePrinter;
import org.apache.commons.lang3.time.FastDateFormat;

import org.springframework.context.MessageSource;

import ru.mystamps.web.service.dto.AdminDailyReport;

/**
 * @author Maxim Shestakov
 */
public class ReportServiceImpl implements ReportService {

	private final MessageSource messageSource;
	private final DatePrinter shortDatePrinter;
	private final Locale adminLang;
	
	public ReportServiceImpl(
		MessageSource messageSource,
		Locale adminLang) {
		
		this.messageSource = messageSource;
		this.adminLang = adminLang;

		this.shortDatePrinter = FastDateFormat.getInstance("dd.MM.yyyy", adminLang);
	}

	// This method should have @PreAuthorize(VIEW_DAILY_STATS) but we can't put it here because it
	// breaks MailServiceImpl.sendDailyStatisticsToAdmin() method that is being executed by cron.
	@Override
	public String prepareDailyStatistics(AdminDailyReport report) {
		String template = messageSource.getMessage("daily_stat.text", null, adminLang);
		String fromDate = shortDatePrinter.format(report.getStartDate());
		String tillDate = shortDatePrinter.format(report.getEndDate());

		Map<String, String> ctx = new HashMap<>();
		ctx.put("from_date", fromDate);
		ctx.put("to_date", tillDate);

		put(ctx, "added_countries_cnt", report.getAddedCountriesCounter());
		put(ctx, "untranslated_countries_cnt", report.getUntranslatedCountriesCounter());
		put(ctx, "added_categories_cnt", report.getAddedCategoriesCounter());
		put(ctx, "untranslated_categories_cnt", report.getUntranslatedCategoriesCounter());
		put(ctx, "added_series_cnt", report.getAddedSeriesCounter());
		put(ctx, "updated_series_cnt", report.getUpdatedSeriesCounter());
		put(ctx, "updated_collections_cnt", report.getUpdatedCollectionsCounter());
		put(ctx, "registration_requests_cnt", report.getRegistrationRequestsCounter());
		put(ctx, "registered_users_cnt", report.getRegisteredUsersCounter());
		put(ctx, "events_cnt", report.countEvents());
		put(ctx, "not_found_cnt", report.getNotFoundCounter());
		put(ctx, "failed_auth_cnt", report.getFailedAuthCounter());
		put(ctx, "missing_csrf_cnt", report.getMissingCsrfCounter());
		put(ctx, "invalid_csrf_cnt", report.getInvalidCsrfCounter());
		put(ctx, "bad_request_cnt", -1L);  // TODO: #122

		return new StrSubstitutor(ctx).replace(template);
	}

	private static void put(Map<String, String> ctx, String key, long value) {
		ctx.put(key, String.valueOf(value));
	}
}
