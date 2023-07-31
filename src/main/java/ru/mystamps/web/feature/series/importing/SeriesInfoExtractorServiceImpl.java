/*
 * Copyright (C) 2009-2023 Slava Semushin <slava.semushin@gmail.com>
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
import ru.mystamps.web.common.Currency;
import ru.mystamps.web.common.LocaleUtils;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.category.CategoryValidation;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.feature.country.CountryValidation;
import ru.mystamps.web.feature.participant.ParticipantService;
import ru.mystamps.web.feature.series.SeriesValidation;
import ru.mystamps.web.feature.series.sale.SeriesCondition;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
public class SeriesInfoExtractorServiceImpl implements SeriesInfoExtractorService {
	
	// Related to RELEASE_DATE_REGEXP and used in unit tests.
	protected static final int MAX_SUPPORTED_RELEASE_YEAR = 2099;
	
	// Regular expression matches release date of the stamps.
	// Year should be in range within 1840 and 2099 inclusive.
	// CheckStyle: ignore LineLength for next 2 lines
	private static final Pattern RELEASE_DATE_REGEXP =
		Pattern.compile("((?<day>[0-9]{2})\\.(?<month>[0-9]{2})\\.)?(?<year>18[4-9][0-9]|19[0-9]{2}|20[0-9]{2})(г(од|\\.)?|\\.)?");
	
	// Regular expression matches number of the stamps in a series.
	// CheckStyle: ignore LineLength for next 2 lines
	private static final Pattern NUMBER_OF_STAMPS_REGEXP = Pattern.compile(
		"(?<quantity>[1-9][0-9]*)(-?(ти|ой|ух))?( ((без)?зубцов(ая|ы[ех])))?[ ]?(м(ар(ок|к[аи])|\\b)|(люкс[- ])?блок(а|ов)?|БЛ)",
		Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
	);
	
	// Regular expression matches range of Michel catalog numbers (from 1 to 9999).
	private static final Pattern MICHEL_NUMBERS_REGEXP =
		Pattern.compile("(#|Michel)[ ]?(?<begin>[1-9][0-9]{0,3})-(?<end>[1-9][0-9]{0,3})");
	
	// Regular expression matches prices that have a space between digits.
	private static final Pattern PRICE_WITH_SPACES = Pattern.compile("([0-9]) ([0-9])");
	
	// Regular expression that matches Belarusian ruble.
	private static final Pattern BYN_CURRENCY_REGEXP = Pattern.compile("[0-9] бел\\. руб\\.");
	
	// Regular expression that matches Rubles (Russian currency).
	// CheckStyle: ignore LineLength for next 1 line
	private static final Pattern RUB_CURRENCY_REGEXP = Pattern.compile("([0-9][ ]?р(уб|\\.)|RUB [0-9])");

	// Regular expression that matches Euro.
	private static final Pattern EUR_CURRENCY_REGEXP = Pattern.compile("€[0-9]");
	
	// Regular expression that matches Ukrainian hryvnia.
	private static final Pattern UAH_CURRENCY_REGEXP = Pattern.compile("([0-9] |\\b)грн\\b");
	
	// Regular expression that matches US dollar.
	private static final Pattern USD_CURRENCY_REGEXP = Pattern.compile("([0-9]\\$|US\\$[0-9])");
	
	// CheckStyle: ignore LineLength for next 4 lines
	private static final Pattern VALID_CATEGORY_NAME_EN = Pattern.compile(CategoryValidation.NAME_EN_REGEXP);
	private static final Pattern VALID_CATEGORY_NAME_RU = Pattern.compile(CategoryValidation.NAME_RU_REGEXP);
	private static final Pattern VALID_COUNTRY_NAME_EN  = Pattern.compile(CountryValidation.NAME_EN_REGEXP);
	private static final Pattern VALID_COUNTRY_NAME_RU  = Pattern.compile(CountryValidation.NAME_RU_REGEXP);
	
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
		Map<String, Integer> releaseDate = extractIssueDate(data.getIssueDate());
		Integer quantity = extractQuantity(data.getQuantity());
		Boolean perforated = extractPerforated(data.getPerforated());
		Set<String> michelNumbers = extractMichelNumbers(data.getMichelNumbers());
		Integer sellerId = extractSeller(pageUrl, data.getSellerName(), data.getSellerUrl());
		Integer sellerGroupId = extractSellerGroup(sellerId, data.getSellerUrl());
		String sellerName = extractSellerName(sellerId, data.getSellerName());
		String sellerUrl = extractSellerUrl(sellerId, data.getSellerUrl());
		BigDecimal price = extractPrice(data.getPrice());
		String currency = extractCurrency(data.getCurrency());
		BigDecimal altPrice = extractPrice(data.getAltPrice());
		String altCurrency = extractCurrency(data.getAltCurrency());
		SeriesCondition condition = extractCondition(data.getCondition());
		
		return new SeriesExtractedInfo(
			categoryIds,
			countryIds,
			releaseDate.get("day"),
			releaseDate.get("month"),
			releaseDate.get("year"),
			quantity,
			perforated,
			michelNumbers,
			sellerId,
			sellerGroupId,
			sellerName,
			sellerUrl,
			price,
			currency,
			altPrice,
			altCurrency,
			condition
		);
	}
	
	/* default */ List<Integer> extractCategory(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return Collections.emptyList();
		}
		
		log.debug("Determine category from '{}'", fragment);
		
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
		
		log.debug("Determine country from '{}'", fragment);
		
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
	
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	/* default */ Map<String, Integer> extractIssueDate(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return Collections.emptyMap();
		}
		
		log.debug("Determine release date from '{}'", fragment);
		
		String[] candidates = StringUtils.split(fragment, " \t,");
		for (String candidate : candidates) {
			Matcher matcher = RELEASE_DATE_REGEXP.matcher(candidate);
			if (!matcher.matches()) {
				continue;
			}
			
			try {
				Integer year = Integer.valueOf(matcher.group("year"));
				log.debug("Release year is {}", year);
				
				String day = matcher.group("day");
				String month = matcher.group("month");

				Map<String, Integer> result = new HashMap<>();
				result.put("year", year);
				
				if (day != null && month != null) {
					// @todo #1287 SeriesInfoExtractorServiceImpl.extractIssueDate():
					//  filter out invalid day/month
					result.put("day", Integer.valueOf(day));
					result.put("month", Integer.valueOf(month));
				}
				return result;
				
			} catch (NumberFormatException ignored) {
				// continue with the next element
			}
		}
		
		log.debug("Could not extract release date from a fragment");
		
		return Collections.emptyMap();
	}
	
	/* default */ Integer extractQuantity(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return null;
		}
		
		log.debug("Determine quantity from '{}'", fragment);
		
		Matcher matcher = NUMBER_OF_STAMPS_REGEXP.matcher(fragment);
		if (matcher.find()) {
			Integer quantity = Integer.valueOf(matcher.group("quantity"));
			if (quantity <= SeriesValidation.MAX_STAMPS_IN_SERIES) {
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
		
		log.debug("Determine perforation from '{}'", fragment);
		
		boolean withoutPerforation = StringUtils.containsAny(
			StringUtils.upperCase(fragment, LocaleUtils.RUSSIAN),
			"Б/З",
			"Б\\З",
			"Б.З.",
			"БЗ",
			"БЕЗ ЗУБ",
			"БЕЗЗУБЦ.",
			"БЕЗЗУБЦОВЫЙ",
			"БЕЗЗУБЦОВЫЕ",
			"БЕЗЗУБЦОВЫХ",
			"БЕЗ ПЕРФ.",
			"НЕПЕРФОРИРОВАННЫЙ",
			"Б/ПЕРФОРАЦИИ",
			"БЕЗ ПЕРФОРАЦИИ",
			"NEPERF."
		);
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
		
		log.debug("Determine michel numbers from '{}'", fragment);
		
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
			log.debug("Determine seller by site, page URL = '{}'", pageUrl);
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
		
		log.debug("Determine seller group by seller url '{}'", sellerUrl);
		
		try {
			String name = new URL(sellerUrl).getHost();
			log.debug("Determine seller group: look for a group named '{}'", name);
			
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
	
	@SuppressWarnings({
		"PMD.AvoidInstantiatingObjectsInLoops",
		"PMD.AvoidReassigningParameters",
		"PMD.NPathComplexity",
		"PMD.ModifiedCyclomaticComplexity",
		"checkstyle:parameterassignment"
	})
	/* default */ BigDecimal extractPrice(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return null;
		}
		
		log.debug("Determine price from '{}'", fragment);
		
		// "1 000" -> "1000"
		Matcher matcher = PRICE_WITH_SPACES.matcher(fragment);
		if (matcher.find()) {
			fragment = matcher.replaceAll("$1$2");
		}

		String[] prefixes = new String[]{"US$", "€"};
		String postfix = "$";
		
		String[] candidates = StringUtils.split(fragment, ' ');
		for (String candidate : candidates) {
			if (candidate.contains(",")) {
				if (candidate.contains(".")) {
					// "1,218.79" => "1218.79"
					candidate = StringUtils.remove(candidate, ',');
				} else {
					// replace comma with dot to handle 10,5 in the same way as 10.5
					candidate = StringUtils.replaceChars(candidate, ',', '.');
				}
			}
			// "10$" -> "10"
			if (candidate.endsWith(postfix) && candidate.length() > postfix.length()) {
				candidate = StringUtils.substringBeforeLast(candidate, postfix);
			}
			// "${prefix}10" -> "10"
			for (String prefix : prefixes) {
				if (candidate.startsWith(prefix) && candidate.length() > prefix.length()) {
					candidate = StringUtils.substringAfter(candidate, prefix);
					break;
				}
			}
			try {
				BigDecimal price = new BigDecimal(candidate);
				log.debug("Price is {}", price);
				// @todo #695 SeriesInfoExtractorServiceImpl.extractPrice(): filter out values <= 0
				return price;

			} catch (NumberFormatException ignored) {
				// continue with the next candidate
			}
		}
		
		log.debug("Could not extract price from a fragment");
		return null;
	}
	
	/* default */ String extractCurrency(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return null;
		}
		
		log.debug("Determine currency from '{}'", fragment);
		
		try {
			Currency currency = Enum.valueOf(Currency.class, fragment);
			log.debug("Currency is {}", currency);
			return currency.toString();
			
		} catch (IllegalArgumentException ignored) {
		}
		
		Matcher matcher = BYN_CURRENCY_REGEXP.matcher(fragment);
		if (matcher.find()) {
			log.debug("Currency is BYN");
			return Currency.BYN.toString();
		}
		
		matcher = RUB_CURRENCY_REGEXP.matcher(fragment);
		if (matcher.find()) {
			log.debug("Currency is RUB");
			return Currency.RUB.toString();
		}
		
		matcher = EUR_CURRENCY_REGEXP.matcher(fragment);
		if (matcher.find()) {
			log.debug("Currency is EUR");
			return Currency.EUR.toString();
		}
		
		matcher = UAH_CURRENCY_REGEXP.matcher(fragment);
		if (matcher.find()) {
			log.debug("Currency is UAH");
			return Currency.UAH.toString();
		}
		
		matcher = USD_CURRENCY_REGEXP.matcher(fragment);
		if (matcher.find()) {
			log.debug("Currency is USD");
			return Currency.USD.toString();
		}
		
		log.debug("Could not extract currency from a fragment");
		
		return null;
	}
	
	// @todo #1326 SeriesInfoExtractorServiceImpl.extractCondition(): add unit tests
	@SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
	/* default */ SeriesCondition extractCondition(String fragment) {
		if (StringUtils.isBlank(fragment)) {
			return null;
		}
		
		String[] candidates = StringUtils.split(
			StringUtils.upperCase(fragment, LocaleUtils.RUSSIAN),
			' '
		);
		for (String candidate : candidates) {
			SeriesCondition condition;
			switch(candidate) {
				case "CTO":
				case "MNH":
				case "MNHOG":
				case "MVLH":
					condition = SeriesCondition.valueOf(candidate);
					break;
				case "ГАШ":
				case "ГАШ.":
				case "ГАШЕНЫЙ": case "ГАШЕНАЯ": case "ГАШЕНЫЕ": case "ГАШЕНЫХ":
				case "ГАШЁНЫЙ": case "ГАШЁНАЯ": case "ГАШЁНЫЕ": case "ГАШЁНЫХ":
				case "USED":
					condition = SeriesCondition.CANCELLED;
					break;
				// written in Russian
				case "СТО":
					condition = SeriesCondition.CTO;
					break;
				case "MNH**":
				case "MNH**.":
					condition = SeriesCondition.MNH;
					break;
				default:
					continue;
			}
			log.debug("Condition is {}", condition);
			return condition;
		}
		
		log.debug("Could not extract condition from a fragment");
		
		return null;
	}
	
	private Integer extractSellerByNameAndUrl(String name, String url) {
		log.debug("Determine seller by name '{}' and url '{}'", name, url);
		
		Integer sellerId = participantService.findSellerId(name, url);
		if (sellerId == null) {
			log.debug("Could not extract seller based on name/url");
		} else {
			log.debug("Found seller: #{}", sellerId);
		}
		
		return sellerId;
	}
	
	private Integer extractSellerBySiteName(String name) {
		log.debug("Determine seller by site name '{}'", name);
		
		Integer sellerId = participantService.findSellerId(name);
		if (sellerId == null) {
			log.debug("Could not extract seller based on site name");
		} else {
			log.debug("Found seller: #{}", sellerId);
		}
		
		return sellerId;
	}
	
	private static boolean validCategoryName(String name) {
		if (name.length() < CategoryValidation.NAME_MIN_LENGTH) {
			return false;
		}
		if (name.length() > CategoryValidation.NAME_MAX_LENGTH) {
			return false;
		}
		return VALID_CATEGORY_NAME_EN.matcher(name).matches()
			|| VALID_CATEGORY_NAME_RU.matcher(name).matches();
	}
	
	private static boolean validCountryName(String name) {
		if (name.length() < CountryValidation.NAME_MIN_LENGTH) {
			return false;
		}
		if (name.length() > CountryValidation.NAME_MAX_LENGTH) {
			return false;
		}
		return VALID_COUNTRY_NAME_EN.matcher(name).matches()
			|| VALID_COUNTRY_NAME_RU.matcher(name).matches();
	}
	
}
