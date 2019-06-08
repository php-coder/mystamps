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
package ru.mystamps.web.feature.series.importing;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import ru.mystamps.web.dao.dto.Currency;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.category.CategoryValidation;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.feature.participant.ParticipantService;
import ru.mystamps.web.validation.ValidationRules;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
public class SeriesInfoExtractorServiceImpl implements SeriesInfoExtractorService {
	
	// Related to RELEASE_YEAR_REGEXP and used in unit tests.
	protected static final int MAX_SUPPORTED_RELEASE_YEAR = 2099;
	
	// Regular expression matches release year of the stamps (from 1840 till 2099).
	private static final Pattern RELEASE_YEAR_REGEXP =
		Pattern.compile("(?<year>18[4-9][0-9]|19[0-9]{2}|20[0-9]{2})г?");
	
	// Regular expression matches number of the stamps in a series (from 1 to 99).
	private static final Pattern NUMBER_OF_STAMPS_REGEXP = Pattern.compile(
		"(?<quantity>[1-9][0-9]?)( (беззубцовые|зубцовых))? (мар(ок|ки)|блоков)",
		Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
	);
	
	// Regular expression matches range of Michel catalog numbers (from 1 to 9999).
	private static final Pattern MICHEL_NUMBERS_REGEXP =
		Pattern.compile("#[ ]?(?<begin>[1-9][0-9]{0,3})-(?<end>[1-9][0-9]{0,3})");
	
	// CheckStyle: ignore LineLength for next 4 lines
	private static final Pattern VALID_CATEGORY_NAME_EN = Pattern.compile(CategoryValidation.CATEGORY_NAME_EN_REGEXP);
	private static final Pattern VALID_CATEGORY_NAME_RU = Pattern.compile(CategoryValidation.CATEGORY_NAME_RU_REGEXP);
	private static final Pattern VALID_COUNTRY_NAME_EN  = Pattern.compile(ValidationRules.COUNTRY_NAME_EN_REGEXP);
	private static final Pattern VALID_COUNTRY_NAME_RU  = Pattern.compile(ValidationRules.COUNTRY_NAME_RU_REGEXP);
	
	// Max number of candidates that will be used in the SQL query within IN() statement.
	private static final long MAX_CANDIDATES_FOR_LOOKUP = 50;
	
	private final Logger log;
	private final CategoryService categoryService;
	private final CountryService countryService;
	private final ParticipantService participantService;
	
	// @todo #803 SeriesInfoExtractorServiceImpl.extract(): add unit tests
	@Override
	@Transactional(readOnly = true)
	public SeriesExtractedInfo extract(String pageUrl, RawParsedDataDto data) {
		List<Integer> categoryIds = extractCategory(data.getCategoryName());
		List<Integer> countryIds = extractCountry(data.getCountryName());
		Integer releaseYear = extractReleaseYear(data.getReleaseYear());
		Integer quantity = extractQuantity(data.getQuantity());
		Boolean perforated = extractPerforated(data.getPerforated());
		Set<String> michelNumbers = extractMichelNumbers(data.getMichelNumbers());
		Integer sellerId = extractSeller(pageUrl, data.getSellerName(), data.getSellerUrl());
		Integer sellerGroupId = extractSellerGroup(sellerId, data.getSellerUrl());
		String sellerName = extractSellerName(sellerId, data.getSellerName());
		String sellerUrl = extractSellerUrl(sellerId, data.getSellerUrl());
		BigDecimal price = extractPrice(data.getPrice());
		String currency = extractCurrency(data.getCurrency());
		
		return new SeriesExtractedInfo(
			categoryIds,
			countryIds,
			releaseYear,
			quantity,
			perforated,
			michelNumbers,
			sellerId,
			sellerGroupId,
			sellerName,
			sellerUrl,
			price,
			currency
		);
	}
	
	/* default */ List<Integer> extractCategory(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return Collections.emptyList();
		}
		
		log.debug("Determining category from a fragment: '{}'", fragment);
		
		String[] names = StringUtils.split(fragment, "\n\t ,.");
		List<String> candidates = Arrays.stream(names)
			.filter(SeriesInfoExtractorServiceImpl::validCategoryName)
			.distinct()
			.limit(MAX_CANDIDATES_FOR_LOOKUP)
			.collect(Collectors.toList());
		
		log.debug("Possible candidates: {}", candidates);
		
		List<Integer> categories = categoryService.findIdsByNames(candidates);
		log.debug("Found categories: {}", categories);
		if (!categories.isEmpty()) {
			return categories;
		}
		
		for (String candidate : candidates) {
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
	
	/* default */ List<Integer> extractCountry(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return Collections.emptyList();
		}
		
		log.debug("Determining country from a fragment: '{}'", fragment);
		
		String[] words = StringUtils.split(fragment, "\n\t ,.");
		
		Stream<String> names = Arrays.stream(words);
		
		// Generate more candidates by split their names by a hyphen.
		// For example: "Minerals-Maldives" becomes [ "Minerals", "Maldives" ]
		Stream<String> additionalNames = Arrays.stream(words)
			.filter(el -> el.contains("-"))
			.map(el -> StringUtils.split(el, '-'))
			.flatMap(Arrays::stream);
		
		List<String> candidates = Stream.concat(names, additionalNames)
			.filter(SeriesInfoExtractorServiceImpl::validCountryName)
			.distinct()
			.limit(MAX_CANDIDATES_FOR_LOOKUP)
			.collect(Collectors.toList());
		
		log.debug("Possible candidates: {}", candidates);
		
		List<Integer> countries = countryService.findIdsByNames(candidates);
		log.debug("Found countries: {}", countries);
		if (!countries.isEmpty()) {
			return countries;
		}
		
		for (String candidate : candidates) {
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
	
	/* default */ Integer extractReleaseYear(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return null;
		}
		
		log.debug("Determining release year from a fragment: '{}'", fragment);
		
		String[] candidates = StringUtils.split(fragment, " \t,");
		for (String candidate : candidates) {
			Matcher matcher = RELEASE_YEAR_REGEXP.matcher(candidate);
			if (!matcher.matches()) {
				continue;
			}
			
			try {
				Integer year = Integer.valueOf(matcher.group("year"));
				log.debug("Release year is {}", year);
				return year;
				
			} catch (NumberFormatException ignored) { // NOPMD: EmptyCatchBlock
				// continue with the next element
			}
		}
		
		log.debug("Could not extract release year from a fragment");
		
		return null;
	}
	
	/* default */ Integer extractQuantity(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return null;
		}
		
		log.debug("Determining quantity from a fragment: '{}'", fragment);
		
		Matcher matcher = NUMBER_OF_STAMPS_REGEXP.matcher(fragment);
		if (matcher.find()) {
			Integer quantity = Integer.valueOf(matcher.group("quantity"));
			if (quantity <= ValidationRules.MAX_STAMPS_IN_SERIES) {
				log.debug("Quantity is {}", quantity);
				return quantity;
			}
		}
		
		log.debug("Could not extract quantity from a fragment");
		
		return null;
	}
	
	// @todo #782 Series import: add integration test for extracting perforation flag
	/* default */ Boolean extractPerforated(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return null;
		}
		
		log.debug("Determining perforation from a fragment: '{}'", fragment);
		
		boolean withoutPerforation =
			StringUtils.containsIgnoreCase(fragment, "б/з")
			|| StringUtils.containsIgnoreCase(fragment, "беззубцовые")
			|| StringUtils.containsIgnoreCase(fragment, "б/перфорации");
		if (withoutPerforation) {
			log.debug("Perforation is false");
			return Boolean.FALSE;
		}
		
		log.debug("Could not extract perforation info from a fragment");
		
		return null;
	}
	
	// @todo #694 SeriesInfoExtractorServiceImpl: support for a single Michel number
	// @todo #694 SeriesInfoExtractorServiceImpl: support for a comma separated Michel numbers
	/* default */ Set<String> extractMichelNumbers(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return Collections.emptySet();
		}
		
		log.debug("Determining michel numbers from a fragment: '{}'", fragment);
		
		Matcher matcher = MICHEL_NUMBERS_REGEXP.matcher(fragment);
		if (matcher.find()) {
			Integer begin = Integer.valueOf(matcher.group("begin"));
			Integer end = Integer.valueOf(matcher.group("end"));
			if (begin < end) {
				Set<String> numbers = IntStream.rangeClosed(begin, end)
					.mapToObj(String::valueOf)
					.collect(Collectors.toCollection(LinkedHashSet::new));
				log.debug("Extracted michel numbers: {}", numbers);
				return numbers;
			}
		}
		
		log.debug("Could not extract michel numbers from a fragment");
		
		return Collections.emptySet();
	}
	
	/* default */ Integer extractSeller(String pageUrl, String name, String url) {
		if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(url)) {
			// CheckStyle: ignore LineLength for next 1 line
			// @todo #695 SeriesInfoExtractorServiceImpl.extractSeller(): validate name/url (length etc)
			return extractSellerByNameAndUrl(name, url);
		}
		
		if (StringUtils.isNotBlank(pageUrl)) {
			log.debug("Determining seller by site, page URL = '{}'", pageUrl);
			try {
				String siteUrl = new URL(pageUrl).getHost();
				// @todo #978 SeriesInfoExtractorServiceImpl.extractSeller(): validate name
				return extractSellerBySiteName(siteUrl);

			} catch (MalformedURLException ex) {
				log.debug("Could not extract seller: {}", ex.getMessage());
				return null;
			}
		}
		
		return null;
	}
	
	/* default */ Integer extractSellerGroup(Integer id, String sellerUrl) {
		// we need a group only for a new seller (id == null)
		if (id != null || StringUtils.isBlank(sellerUrl)) {
			return null;
		}
		
		log.debug("Determining seller group by seller url '{}'", sellerUrl);
		
		try {
			String name = new URL(sellerUrl).getHost();
			log.debug("Determining seller group: looking for a group named '{}'", name);
			
			Integer groupId = participantService.findGroupIdByName(name);
			if (groupId != null) {
				log.debug("Found seller group: #{}", groupId);
			}
			return groupId;
			
		} catch (MalformedURLException ex) {
			log.debug("Could not extract seller group: {}", ex.getMessage());
			return null;
		}
	}
	
	/* default */ String extractSellerName(Integer id, String name) {
		if (id != null) {
			return null;
		}
		
		// @todo #695 SeriesInfoExtractorServiceImpl.extractSellerName(): filter out short names
		// @todo #695 SeriesInfoExtractorServiceImpl.extractSellerName(): filter out long names
		
		// we need a name only if we couldn't find a seller in database (id == null)
		return name;
	}
	
	/* default */ String extractSellerUrl(Integer id, String url) {
		if (id != null) {
			return null;
		}

		// @todo #695 SeriesInfoExtractorServiceImpl.extractSellerUrl(): filter out non-urls
		// @todo #695 SeriesInfoExtractorServiceImpl.extractSellerUrl(): filter out too long urls
		
		// we need a url only if we couldn't find a seller in database (id == null)
		return url;
	}
	
	/* default */ BigDecimal extractPrice(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return null;
		}
		
		log.debug("Determining price from a fragment: '{}'", fragment);
		
		try {
			BigDecimal price = new BigDecimal(fragment);
			log.debug("Price is {}", price);
			// @todo #695 SeriesInfoExtractorServiceImpl.extractPrice(): filter out values <= 0
			return price;
			
		} catch (NumberFormatException ex) {
			log.debug("Could not extract price: {}", ex.getMessage());
			return null;
		}
	}
	
	/* default */ String extractCurrency(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return null;
		}
		
		log.debug("Determining currency from a fragment: '{}'", fragment);
		
		try {
			Currency currency = Enum.valueOf(Currency.class, fragment);
			log.debug("Currency is {}", currency);
			return currency.toString();
			
		} catch (IllegalArgumentException ex) {
			log.debug("Could not extract currency: {}", ex.getMessage());
			return null;
		}
	}
	
	private Integer extractSellerByNameAndUrl(String name, String url) {
		log.debug("Determining seller by name '{}' and url '{}'", name, url);
		
		Integer sellerId = participantService.findSellerId(name, url);
		if (sellerId == null) {
			log.debug("Could not extract seller based on name/url");
		} else {
			log.debug("Found seller: #{}", sellerId);
		}
		
		return sellerId;
	}
	
	private Integer extractSellerBySiteName(String name) {
		log.debug("Determining seller by site name '{}'", name);
		
		Integer sellerId = participantService.findSellerId(name);
		if (sellerId == null) {
			log.debug("Could not extract seller based on site name");
		} else {
			log.debug("Found seller: #{}", sellerId);
		}
		
		return sellerId;
	}
	
	private static boolean validCategoryName(String name) {
		if (name.length() < CategoryValidation.CATEGORY_NAME_MIN_LENGTH) {
			return false;
		}
		if (name.length() > CategoryValidation.CATEGORY_NAME_MAX_LENGTH) {
			return false;
		}
		return VALID_CATEGORY_NAME_EN.matcher(name).matches()
			|| VALID_CATEGORY_NAME_RU.matcher(name).matches();
	}
	
	private static boolean validCountryName(String name) {
		if (name.length() < ValidationRules.COUNTRY_NAME_MIN_LENGTH) {
			return false;
		}
		if (name.length() > ValidationRules.COUNTRY_NAME_MAX_LENGTH) {
			return false;
		}
		return VALID_COUNTRY_NAME_EN.matcher(name).matches()
			|| VALID_COUNTRY_NAME_RU.matcher(name).matches();
	}
	
}
