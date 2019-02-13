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
package ru.mystamps.web.feature.series.sale;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Spring configuration that is required for using series sales/purchases in an application.
 */
@Configuration
public class SeriesSalesConfig {
	
	@RequiredArgsConstructor
	public static class Services {
		
		private final NamedParameterJdbcTemplate jdbcTemplate;
		
		@Bean
		public SeriesSalesService seriesSalesService(SeriesSalesDao seriesSalesDao) {
			return new SeriesSalesServiceImpl(
				LoggerFactory.getLogger(SeriesSalesServiceImpl.class),
				seriesSalesDao
			);
		}
		
		@Bean
		public SeriesSalesDao seriesSalesDao() {
			return new JdbcSeriesSalesDao(jdbcTemplate);
		}
		
	}
	
}
