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
package ru.mystamps.web.feature.participant;

import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.RequiredArgsConstructor;

/**
 * Spring configuration that is required for transaction participants (buyers and sellers).
 *
 * The beans are grouped into two classes to make possible to register a controller
 * and the services in the separated application contexts.
 */
@Configuration
public class ParticipantConfig {
	
	@RequiredArgsConstructor
	public static class Controllers {
		
		private final ParticipantService participantService;
		
		@Bean
		public ParticipantController participantController() {
			return new ParticipantController(participantService);
		}
		
	}
	
	@RequiredArgsConstructor
	public static class Services {
		
		private final NamedParameterJdbcTemplate jdbcTemplate;
		
		@Bean
		public ParticipantService participantService(ParticipantDao participantDao) {
			return new ParticipantServiceImpl(
				LoggerFactory.getLogger(ParticipantServiceImpl.class),
				participantDao
			);
		}
		
		@Bean
		public ParticipantDao participantDao() {
			return new JdbcParticipantDao(jdbcTemplate);
		}
		
	}
	
}
