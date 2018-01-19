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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.service.dto.RawParsedDataDto;
import ru.mystamps.web.service.dto.SeriesExtractedInfo;
import ru.mystamps.web.validation.ValidationRules;

@RequiredArgsConstructor
@SuppressWarnings({
	// predicate names in camel case more readable than in uppercase
	"PMD.VariableNamingConventions", "checkstyle:constantname",
	// these "||" on the same line because it's more readable
	"checkstyle:operatorwrap"
})
public class SeriesInfoExtractorServiceImpl implements SeriesInfoExtractorService {
	
	// Related to RELEASE_YEAR_REGEXP and used in unit tests.
	protected static final int MAX_SUPPORTED_RELEASE_YEAR = 2099;
	
	// Regular expression matches release year of the stamps (from 1840 till 2099).
	private static final Pattern RELEASE_YEAR_REGEXP =
		Pattern.compile("18[4-9][0-9]|19[0-9]{2}|20[0-9]{2}");
	
	// Regular expression matches number of the stamps in a series (from 1 to 99).
	private static final Pattern NUMBER_OF_STAMPS_REGEXP = Pattern.compile(
		"([1-9][0-9]?)( беззубцовые)? мар(ок|ки)",
		Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
	);
	
	// CheckStyle: ignore LineLength for next 9 lines
	private static final Pattern VALID_CATEGORY_NAME_EN = Pattern.compile(ValidationRules.CATEGORY_NAME_EN_REGEXP);
	private static final Pattern VALID_CATEGORY_NAME_RU = Pattern.compile(ValidationRules.CATEGORY_NAME_RU_REGEXP);
	private static final Pattern VALID_COUNTRY_NAME_EN  = Pattern.compile(ValidationRules.COUNTRY_NAME_EN_REGEXP);
	private static final Pattern VALID_COUNTRY_NAME_RU  = Pattern.compile(ValidationRules.COUNTRY_NAME_RU_REGEXP);
	
	private static final Predicate<String> tooShortCategoryName = name -> name.length() >= ValidationRules.CATEGORY_NAME_MIN_LENGTH;
	private static final Predicate<String> tooLongCategoryName  = name -> name.length() <= ValidationRules.CATEGORY_NAME_MAX_LENGTH;
	private static final Predicate<String> tooShortCountryName  = name -> name.length() >= ValidationRules.COUNTRY_NAME_MIN_LENGTH;
	private static final Predicate<String> tooLongCountryName   = name -> name.length() <= ValidationRules.COUNTRY_NAME_MAX_LENGTH;
	
	private static final Predicate<String> invalidCategoryName = name ->
		VALID_CATEGORY_NAME_EN.matcher(name).matches() ||
		VALID_CATEGORY_NAME_RU.matcher(name).matches();
	
	private static final Predicate<String> invalidCountryName = name ->
		VALID_COUNTRY_NAME_EN.matcher(name).matches() ||
		VALID_COUNTRY_NAME_RU.matcher(name).matches();
	
	// Max number of candidates that will be used in the SQL query within IN() statement.
	private static final long MAX_CANDIDATES_FOR_LOOKUP = 50;
	
	private final Logger log;
	private final CategoryService categoryService;
	private final CountryService countryService;
	
	// @todo #803 SeriesInfoExtractorServiceImpl.extract(): add unit tests
	@Override
	@Transactional(readOnly = true)
	public SeriesExtractedInfo extract(RawParsedDataDto data) {
		List<Integer> categoryIds = extractCategory(data.getCategoryName());
		List<Integer> countryIds = extractCountry(data.getCountryName());
		Integer releaseYear = extractReleaseYear(data.getReleaseYear());
		Integer quantity = extractQuantity(data.getQuantity());
		Boolean perforated = extractPerforated(data.getPerforated());
		
		return new SeriesExtractedInfo(
			categoryIds,
			countryIds,
			releaseYear,
			quantity,
			perforated
		);
	}
	
	// CheckStyle: ignore LineLength for next 1 line
	// @todo #821 SeriesInfoExtractorServiceImpl.extractCategory(): add unit tests for filtering invalid names
	protected List<Integer> extractCategory(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return Collections.emptyList();
		}
		
		log.debug("Determining category from a fragment: '{}'", fragment);
		
		String[] names = StringUtils.split(fragment, "\n\t ,");
		List<String> uniqueCandidates = Arrays.stream(names)
			.filter(tooShortCategoryName)
			.filter(tooLongCategoryName)
			.filter(invalidCategoryName)
			.distinct()
			.limit(MAX_CANDIDATES_FOR_LOOKUP)
			.collect(Collectors.toList());
		
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
	
	// CheckStyle: ignore LineLength for next 1 line
	// @todo #821 SeriesInfoExtractorServiceImpl.extractCountry(): add unit tests for filtering invalid names
	protected List<Integer> extractCountry(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return Collections.emptyList();
		}
		
		log.debug("Determining country from a fragment: '{}'", fragment);
		
		String[] names = StringUtils.split(fragment, "\n\t ,");
		List<String> uniqueCandidates = Arrays.stream(names)
			.filter(tooShortCountryName)
			.filter(tooLongCountryName)
			.filter(invalidCountryName)
			.distinct()
			.limit(MAX_CANDIDATES_FOR_LOOKUP)
			.collect(Collectors.toList());
		
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
	
	protected Integer extractReleaseYear(String fragment) {
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
	
	// @todo #781 SeriesInfoExtractorServiceImpl.extractQuantity(): add unit tests
	// @todo #781 SeriesInfoExtractorServiceImpl.extractQuantity() respect MAX_STAMPS_IN_SERIES
	protected Integer extractQuantity(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return null;
		}
		
		log.debug("Determining quantity from a fragment: '{}'", fragment);
		
		Matcher matcher = NUMBER_OF_STAMPS_REGEXP.matcher(fragment);
		if (matcher.find()) {
			String quantity = matcher.group(1);
			log.debug("Quantity is {}", quantity);
			return Integer.valueOf(quantity);
		}
		
		log.debug("Could not extract quantity from a fragment");
		
		return null;
	}
	
	// @todo #782 SeriesInfoExtractorServiceImpl.extractPerforated(): add unit tests
	// @todo #782 Series import: add integration test for extracting perforation flag
	protected Boolean extractPerforated(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return null;
		}
		
		log.debug("Determining perforation from a fragment: '{}'", fragment);
		
		boolean withoutPerforation = StringUtils.containsIgnoreCase(fragment, "б/з")
			|| StringUtils.containsIgnoreCase(fragment, "беззубцовые");
		if (withoutPerforation) {
			log.debug("Perforation is false");
			return Boolean.FALSE;
		}
		
		log.debug("Could not extract perforation info from a fragment");
		
		return null;
	}
	
}
