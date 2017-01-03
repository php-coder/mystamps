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
package ru.mystamps.web.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.dto.UsersActivationFullDto;
import ru.mystamps.web.service.dto.AdminDailyReport;

@RequiredArgsConstructor
public class CronServiceImpl implements CronService {
	private static final String EVERY_DAY_AT_00_00 = "0 0 0 * * *";
	private static final String EVERY_DAY_AT_00_30 = "0 30 0 * * *";
	
	private static final Logger LOG = LoggerFactory.getLogger(CronServiceImpl.class);
	
	private final CategoryService categoryService;
	private final CountryService countryService;
	private final SeriesService seriesService;
	private final SuspiciousActivityService suspiciousActivityService;
	private final UserService userService;
	private final UsersActivationService usersActivationService;
	private final MailService mailService;

	@Override
	@Scheduled(cron = EVERY_DAY_AT_00_00)
	@Transactional(readOnly = true)
	public void sendDailyStatistics() {
		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		Date yesterday = DateUtils.addDays(today, -1);
		
		AdminDailyReport report = new AdminDailyReport();
		report.setStartDate(yesterday);
		report.setEndDate(today);
		report.setAddedCategoriesCounter(categoryService.countAddedSince(yesterday));
		report.setAddedCountriesCounter(countryService.countAddedSince(yesterday));
		
		long untranslatedCategories = categoryService.countUntranslatedNamesSince(yesterday);
		report.setUntranslatedCategoriesCounter(untranslatedCategories);
		
		long untranslatedCountries = countryService.countUntranslatedNamesSince(yesterday);
		report.setUntranslatedCountriesCounter(untranslatedCountries);
		
		report.setAddedSeriesCounter(seriesService.countAddedSince(yesterday));
		report.setUpdatedSeriesCounter(seriesService.countUpdatedSince(yesterday));
		report.setRegistrationRequestsCounter(usersActivationService.countCreatedSince(yesterday));
		report.setRegisteredUsersCounter(userService.countRegisteredSince(yesterday));
		
		long notFoundCounter = suspiciousActivityService.countByTypeSince(
			SiteServiceImpl.PAGE_NOT_FOUND,
			yesterday
		);
		report.setNotFoundCounter(notFoundCounter);
		
		long failedAuthCounter = suspiciousActivityService.countByTypeSince(
			SiteServiceImpl.AUTHENTICATION_FAILED,
			yesterday
		);
		report.setFailedAuthCounter(failedAuthCounter);
		
		long missingCsrfCounter = suspiciousActivityService.countByTypeSince(
			SiteServiceImpl.MISSING_CSRF_TOKEN,
			yesterday
		);
		report.setMissingCsrfCounter(missingCsrfCounter);
		
		long invalidCsrfCounter = suspiciousActivityService.countByTypeSince(
			SiteServiceImpl.INVALID_CSRF_TOKEN,
			yesterday
		);
		report.setInvalidCsrfCounter(invalidCsrfCounter);
		
		mailService.sendDailyStatisticsToAdmin(report);
	}
	
	@Override
	@Scheduled(cron = EVERY_DAY_AT_00_30)
	@Transactional
	public void purgeUsersActivations() {
		List<UsersActivationFullDto> expiredActivations =
			usersActivationService.findOlderThan(PURGE_AFTER_DAYS);
		
		Validate.validState(expiredActivations != null, "Expired activations must be non null");
		
		if (expiredActivations.isEmpty()) {
			LOG.info("Expired activations was not found.");
			return;
		}
		
		for (UsersActivationFullDto activation : expiredActivations) {
			LOG.info(
				"Delete expired activation (key: {}, email: {}, created: {})",
				activation.getActivationKey(),
				activation.getEmail(),
				activation.getCreatedAt()
			);
			
			usersActivationService.remove(activation.getActivationKey());
		}
	}
	
}
