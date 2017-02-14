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
package ru.mystamps.web.feature.site;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import ru.mystamps.web.feature.account.UserService;
import ru.mystamps.web.feature.account.UsersActivationFullDto;
import ru.mystamps.web.feature.account.UsersActivationService;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.collection.CollectionService;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.feature.report.AdminDailyReport;
import ru.mystamps.web.feature.series.SeriesService;
import ru.mystamps.web.feature.site.SiteDb.SuspiciousActivityType;
import ru.mystamps.web.support.spring.security.HasAuthority;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class CronServiceImpl implements CronService {
	private static final String EVERY_DAY_AT_00_00 = "0 0 0 * * *";
	private static final String EVERY_DAY_AT_00_30 = "0 30 0 * * *";
	
	private final Logger log;
	private final CategoryService categoryService;
	private final CountryService countryService;
	private final CollectionService collectionService;
	private final SeriesService seriesService;
	private final SuspiciousActivityService suspiciousActivityService;
	private final UserService userService;
	private final UsersActivationService usersActivationService;
	private final MailService mailService;

	@Override
	@Scheduled(cron = EVERY_DAY_AT_00_00)
	@Transactional(readOnly = true)
	public void sendDailyStatistics() {
		mailService.sendDailyStatisticsToAdmin(getDailyReport());
	}
	
	@Override
	@PreAuthorize(HasAuthority.VIEW_DAILY_STATS)
	public AdminDailyReport getDailyReport() {
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
		//report.setUpdatedCollectionsCounter(collectionService.countUpdatedSince(yesterday));
		report.setRegistrationRequestsCounter(usersActivationService.countCreatedSince(yesterday));
		report.setRegisteredUsersCounter(userService.countRegisteredSince(yesterday));
		
		long notFoundCounter = suspiciousActivityService.countByTypeSince(
			SuspiciousActivityType.PAGE_NOT_FOUND,
			yesterday
		);
		report.setNotFoundCounter(notFoundCounter);
		
		long failedAuthCounter = suspiciousActivityService.countByTypeSince(
			SuspiciousActivityType.AUTHENTICATION_FAILED,
			yesterday
		);
		report.setFailedAuthCounter(failedAuthCounter);
		
		long missingCsrfCounter = suspiciousActivityService.countByTypeSince(
			SuspiciousActivityType.MISSING_CSRF_TOKEN,
			yesterday
		);
		report.setMissingCsrfCounter(missingCsrfCounter);
		
		long invalidCsrfCounter = suspiciousActivityService.countByTypeSince(
			SuspiciousActivityType.INVALID_CSRF_TOKEN,
			yesterday
		);
		report.setInvalidCsrfCounter(invalidCsrfCounter);
		
		return report;
	}
	
	@Override
	@Scheduled(cron = EVERY_DAY_AT_00_30)
	@Transactional
	public void purgeUsersActivations() {
		List<UsersActivationFullDto> expiredActivations =
			usersActivationService.findOlderThan(PURGE_AFTER_DAYS);
		
		Validate.validState(expiredActivations != null, "Expired activations must be non null");
		
		if (expiredActivations.isEmpty()) {
			log.info("Expired activations were not found");
			return;
		}
		
		for (UsersActivationFullDto activation : expiredActivations) {
			log.info(
				"Delete expired activation (key: {}, email: {}, created: {})",
				activation.getActivationKey(),
				activation.getEmail(),
				activation.getCreatedAt()
			);
			
			usersActivationService.remove(activation.getActivationKey());
		}
	}
	
}
