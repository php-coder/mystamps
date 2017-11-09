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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SeriesInfoExtractorServiceImpl implements SeriesInfoExtractorService {
	
	// Regular expression matches release year of the stamps (from 1840 till 2099).
	private static final Pattern RELEASE_YEAR_REGEXP =
		Pattern.compile("18[4-9][0-9]|19[0-9]{2}|20[0-9]{2}");
	
	private final Logger log;
	private final CategoryService categoryService;
	private final CountryService countryService;
	
	@Override
	@Transactional(readOnly = true)
	public List<Integer> extractCategory(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return Collections.emptyList();
		}
		
		log.debug("Determining category from a fragment: '{}'", fragment);
		
		String[] candidates = StringUtils.split(fragment);
		Set<String> uniqueCandidates = new HashSet<>();
		Collections.addAll(uniqueCandidates, candidates);
		
		log.debug("Possible candidates: {}", uniqueCandidates);
		
		List<Integer> categories = categoryService.findIdsByNames(uniqueCandidates);
		log.debug("Found categories: {}", categories);
		if (!categories.isEmpty()) {
			return categories;
		}
		
		for (String candidate : uniqueCandidates) {
			log.debug("Possible candidate: '{}%'", candidate);
			categories = categoryService.findIdsWhenNameStartsWith(candidate);
			if (!categories.isEmpty()) {
				log.debug("Found categories: {}", categories);
				return categories;
			}
		}
		
		log.debug("Could not extract category from a fragment");
		
		return Collections.emptyList();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Integer> extractCountry(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return Collections.emptyList();
		}
		
		log.debug("Determining country from a fragment: '{}'", fragment);
		
		String[] candidates = StringUtils.split(fragment);
		Set<String> uniqueCandidates = new HashSet<>();
		Collections.addAll(uniqueCandidates, candidates);
		
		log.debug("Possible candidates: {}", uniqueCandidates);
		
		List<Integer> countries = countryService.findIdsByNames(uniqueCandidates);
		log.debug("Found countries: {}", countries);
		if (!countries.isEmpty()) {
			return countries;
		}
		
		for (String candidate : uniqueCandidates) {
			log.debug("Possible candidate: '{}%'", candidate);
			countries = countryService.findIdsWhenNameStartsWith(candidate);
			if (!countries.isEmpty()) {
				log.debug("Found countries: {}", countries);
				return countries;
			}
		}
		
		log.debug("Could not extract country from a fragment");
		
		return Collections.emptyList();
	}
	
	@Override
	public Integer extractReleaseYear(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return null;
		}
		
		log.debug("Determining release year from a fragment: '{}'", fragment);
		
		String[] candidates = StringUtils.split(fragment);
		for (String candidate : candidates) {
			if (!RELEASE_YEAR_REGEXP.matcher(candidate).matches()) {
				continue;
			}
			
			try {
				Integer year = Integer.valueOf(candidate);
				log.debug("Release year is {}", year);
				return year;
				
			} catch (NumberFormatException ignored) { // NOPMD: EmptyCatchBlock
				// continue with the next element
			}
		}
		
		log.debug("Could not extract release year from a fragment");
		
		return null;
	}
	
}
