/*
 * Copyright (C) 2009-2024 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.mystamps.web.common.EntityWithParentDto;
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.common.SitemapInfoDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Implementation that delegates calls to a category service.
 */
public class ApiCategoryService implements CategoryService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiCategoryService.class);
	
	private final RestTemplate restTemplate;
	
	// Endpoints
	private final String countAllCategories;
	
	public ApiCategoryService(RestTemplateBuilder restTemplateBuilder, Environment env) {
		String serviceHost = env.getRequiredProperty("service.category.host");
		
		this.restTemplate = restTemplateBuilder
			.rootUri(serviceHost)
			.build();
		
		this.countAllCategories = env.getRequiredProperty("service.category.count_all");
	}
	
	@Override
	public String add(AddCategoryDto dto, Integer userId) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<Integer> findIdsByNames(List<String> names) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<Integer> findIdsWhenNameStartsWith(String name) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<LinkEntityDto> findAllAsLinkEntities(String lang) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<SitemapInfoDto> findAllForSitemap() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<EntityWithParentDto> findCategoriesWithParents(String lang) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public LinkEntityDto findOneAsLinkEntity(String slug, String lang) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public long countAll() {
		LOG.debug("GET {}", countAllCategories);
		
		ResponseEntity<Long> response = restTemplate.getForEntity(
			countAllCategories,
			Long.class
		);
		
		Long result = response.getBody();
		
		LOG.debug("Result: {} => {}", response.getStatusCodeValue(), result);
		
		return result;
	}
	
	@Override
	public long countCategoriesOf(Integer collectionId) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public long countBySlug(String slug) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public long countByName(String name) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public long countByNameRu(String name) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public long countAddedSince(Date date) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public long countUntranslatedNamesSince(Date date) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Map<String, Integer> getStatisticsOf(Integer collectionId, String lang) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String suggestCategoryForUser(Integer userId) {
		throw new UnsupportedOperationException();
	}
	
}
