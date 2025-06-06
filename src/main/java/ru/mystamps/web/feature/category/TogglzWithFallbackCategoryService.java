/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mystamps.web.common.EntityWithParentDto;
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.common.SitemapInfoDto;
import ru.mystamps.web.support.togglz.Features;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Implementation that delegates calls to a category service when the feature is enabled.
 *
 * When the {@code USE_CATEGORY_MICROSERVICE} feature is disabled or when a call has failed,
 * it falls back to the default implementation.
 *
 * @see ApiCategoryService
 * @see CategoryServiceImpl
 */
@RequiredArgsConstructor
public class TogglzWithFallbackCategoryService implements CategoryService {

	private static final Logger LOG =
		LoggerFactory.getLogger(TogglzWithFallbackCategoryService.class);
	
	private final ApiCategoryService apiService;
	private final CategoryServiceImpl fallbackService;
	
	@Override
	public String add(AddCategoryDto dto, Integer userId) {
		return executeOneOf(
			() ->      apiService.add(dto, userId),
			() -> fallbackService.add(dto, userId)
		);
	}
	
	@Override
	public List<Integer> findIdsByNames(List<String> names) {
		return executeOneOf(
			() ->      apiService.findIdsByNames(names),
			() -> fallbackService.findIdsByNames(names)
		);
	}
	
	@Override
	public List<Integer> findIdsWhenNameStartsWith(String name) {
		return executeOneOf(
			() ->      apiService.findIdsWhenNameStartsWith(name),
			() -> fallbackService.findIdsWhenNameStartsWith(name)
		);
	}
	
	@Override
	public List<LinkEntityDto> findAllAsLinkEntities(String lang) {
		return executeOneOf(
			() ->      apiService.findAllAsLinkEntities(lang),
			() -> fallbackService.findAllAsLinkEntities(lang)
		);
	}
	
	@Override
	public List<SitemapInfoDto> findAllForSitemap() {
		return executeOneOf(
			apiService::findAllForSitemap,
			fallbackService::findAllForSitemap
		);
	}
	
	@Override
	public List<EntityWithParentDto> findCategoriesWithParents(String lang) {
		return executeOneOf(
			() ->      apiService.findCategoriesWithParents(lang),
			() -> fallbackService.findCategoriesWithParents(lang)
		);
	}
	
	@Override
	public LinkEntityDto findOneAsLinkEntity(String slug, String lang) {
		return executeOneOf(
			() ->      apiService.findOneAsLinkEntity(slug, lang),
			() -> fallbackService.findOneAsLinkEntity(slug, lang)
		);
	}
	
	@Override
	public long countAll() {
		return executeOneOf(
			apiService::countAll,
			fallbackService::countAll
		);
	}
	
	@Override
	public long countCategoriesOf(Integer collectionId) {
		return executeOneOf(
			() ->      apiService.countCategoriesOf(collectionId),
			() -> fallbackService.countCategoriesOf(collectionId)
		);
	}
	
	@Override
	public long countBySlug(String slug) {
		return executeOneOf(
			() ->      apiService.countBySlug(slug),
			() -> fallbackService.countBySlug(slug)
		);
	}
	
	@Override
	public long countByName(String name) {
		return executeOneOf(
			() ->      apiService.countByName(name),
			() -> fallbackService.countByName(name)
		);
	}
	
	@Override
	public long countByNameRu(String name) {
		return executeOneOf(
			() ->      apiService.countByNameRu(name),
			() -> fallbackService.countByNameRu(name)
		);
	}
	
	@Override
	public long countAddedSince(Date date) {
		return executeOneOf(
			() ->      apiService.countAddedSince(date),
			() -> fallbackService.countAddedSince(date)
		);
	}
	
	@Override
	public long countUntranslatedNamesSince(Date date) {
		return executeOneOf(
			() ->      apiService.countUntranslatedNamesSince(date),
			() -> fallbackService.countUntranslatedNamesSince(date)
		);
	}
	
	@Override
	public Map<String, Integer> getStatisticsOf(Integer collectionId, String lang) {
		return executeOneOf(
			() ->      apiService.getStatisticsOf(collectionId, lang),
			() -> fallbackService.getStatisticsOf(collectionId, lang)
		);
	}
	
	@Override
	public String suggestCategoryForUser(Integer userId) {
		return executeOneOf(
			() ->      apiService.suggestCategoryForUser(userId),
			() -> fallbackService.suggestCategoryForUser(userId)
		);
	}
	
	private static <T> T executeOneOf(Callable<T> firstImpl, Callable<T> defaultImpl) {
		try {
			if (Features.USE_CATEGORY_MICROSERVICE.isActive()) {
				try {
					return firstImpl.call();
				} catch (UnsupportedOperationException ignored) {
					// the method isn't yet implemented, fallback to the default implementation
					
				} catch (RuntimeException e) {
					LOG.warn(
						"Failed to call a category service. Fallback to default implementation",
						e
					);
				}
			}
			return defaultImpl.call();
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
