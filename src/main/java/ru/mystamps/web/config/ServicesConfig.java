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
package ru.mystamps.web.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import ru.mystamps.web.feature.account.AccountConfig;
import ru.mystamps.web.feature.category.CategoryConfig;
import ru.mystamps.web.feature.collection.CollectionConfig;
import ru.mystamps.web.feature.country.CountryConfig;
import ru.mystamps.web.feature.image.ImageConfig;
import ru.mystamps.web.feature.participant.ParticipantConfig;
import ru.mystamps.web.feature.report.ReportConfig;
import ru.mystamps.web.feature.series.DownloaderService;
import ru.mystamps.web.feature.series.HttpURLConnectionDownloaderService;
import ru.mystamps.web.feature.series.SeriesConfig;
import ru.mystamps.web.feature.series.TimedDownloaderService;
import ru.mystamps.web.feature.series.importing.SeriesImportConfig;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportConfig;
import ru.mystamps.web.feature.series.sale.SeriesSalesConfig;
import ru.mystamps.web.feature.site.SiteConfig;

@Configuration
@Import({
	AccountConfig.Services.class,
	CategoryConfig.Services.class,
	CollectionConfig.Services.class,
	CountryConfig.Services.class,
	ImageConfig.Services.class,
	ParticipantConfig.Services.class,
	ReportConfig.Services.class,
	SeriesConfig.Services.class,
	SeriesImportConfig.Services.class,
	SeriesSalesConfig.Services.class,
	SeriesSalesImportConfig.Services.class,
	SiteConfig.Services.class
})
@RequiredArgsConstructor
public class ServicesConfig {
	
	private final Environment env;
	
	@Bean
	public DownloaderService getImageDownloaderService() {
		return new TimedDownloaderService(
			LoggerFactory.getLogger(TimedDownloaderService.class),
			new HttpURLConnectionDownloaderService(
				new String[]{"image/jpeg", "image/png"},
				env.getRequiredProperty("app.downloader.timeout", Integer.class)
			)
		);
	}
	
	@Bean(name = "seriesDownloaderService")
	public DownloaderService getSeriesDownloaderService() {
		return new TimedDownloaderService(
			LoggerFactory.getLogger(TimedDownloaderService.class),
			new HttpURLConnectionDownloaderService(
				new String[]{"text/html", "image/jpeg", "image/png"},
				env.getRequiredProperty("app.downloader.timeout", Integer.class)
			)
		);
	}
	
}
