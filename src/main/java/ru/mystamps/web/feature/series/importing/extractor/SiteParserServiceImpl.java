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
package ru.mystamps.web.feature.series.importing.extractor;

import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SiteParserServiceImpl implements SiteParserService {
	
	private final Logger log;
	private final SiteParserDao siteParserDao;
	
	// TODO: remove after finishing migration
	@Override
	@Transactional
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	public void add(SiteParserConfiguration cfg) {
		Validate.isTrue(cfg != null, "Site parser configuration must be non null");
		
		Integer id = siteParserDao.addParser(cfg.getName());
		log.info("Site parser #{} ({}) has been created", id, cfg.getName());
		
		for (Map.Entry<String, String> param : cfg.toMap().entrySet()) {
			String name  = param.getKey();
			if ("name".equals(name)) {
				// "name" belongs to the parser itself and has been already saved above
				continue;
			}

			String value = param.getValue();
			AddParserParameterDbDto paramDto = new AddParserParameterDbDto(id, name, value);
			
			siteParserDao.addParserParameter(paramDto);
			
			log.info("Site parser #{}: parameter '{}' has been added", id, name);
		}
	}
	
	// @todo #975 SiteParserServiceImpl.findForUrl(): add unit tests
	@Override
	@Transactional(readOnly = true)
	public SiteParser findForUrl(String url) {
		Validate.isTrue(url != null, "Url must be non null");
		
		Integer parserId = siteParserDao.findParserIdForUrl(url);
		if (parserId == null) {
			log.info("Could not find parser for '{}'", url);
			return null;
		}
		
		SiteParserConfiguration cfg = siteParserDao.findConfigurationForParser(parserId);
		if (cfg == null) {
			log.warn("Could not find configuration for parser #{}", parserId);
			return null;
		}
		
		return new JsoupSiteParser(cfg);
	}
	
}
