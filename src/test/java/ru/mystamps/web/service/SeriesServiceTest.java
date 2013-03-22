/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import org.springframework.web.multipart.MultipartFile;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.fest.assertions.api.Assertions.assertThat;

import ru.mystamps.web.dao.CountryDao;
import ru.mystamps.web.dao.SeriesDao;
import ru.mystamps.web.entity.Country;
import ru.mystamps.web.entity.Currency;
import ru.mystamps.web.entity.GibbonsCatalog;
import ru.mystamps.web.entity.MetaInfo;
import ru.mystamps.web.entity.MichelCatalog;
import ru.mystamps.web.entity.Price;
import ru.mystamps.web.entity.ScottCatalog;
import ru.mystamps.web.entity.Series;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.YvertCatalog;
import ru.mystamps.web.model.AddSeriesForm;
import ru.mystamps.web.tests.fest.DateAssert;

@RunWith(MockitoJUnitRunner.class)
public class SeriesServiceTest {
	private static final Double ANY_PRICE = 17d;
	
	@Mock
	private CountryDao countryDao;
	
	@Mock
	private ImageService imageService;
	
	@Mock
	private SeriesDao seriesDao;
	
	@Mock
	private MultipartFile multipartFile;
	
	@Captor
	private ArgumentCaptor<Series> seriesCaptor;
	
	@Captor
	private ArgumentCaptor<MultipartFile> multipartFileCaptor;
	
	@InjectMocks
	private SeriesService service = new SeriesServiceImpl();
	
	private AddSeriesForm form;
	private User user;
	
	@Before
	public void setUp() {
		form = new AddSeriesForm();
		form.setQuantity(2);
		form.setPerforated(false);
		
		user = UserServiceTest.getValidUser();
		
		when(imageService.save(any(MultipartFile.class))).thenReturn("/fake/path/to/image");
	}
	
	//
	// Tests for add()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void addShouldThrowExceptionArgumentIsNull() {
		service.add(null, user);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addShouldThrowExceptionIfQuantityIsNull() {
		form.setQuantity(null);
		
		service.add(form, user);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addShouldThrowExceptionIfPerforatedIsNull() {
		form.setPerforated(null);
		
		service.add(form, user);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addShouldThrowExceptionWhenUserIsNull() {
		service.add(form, null);
	}
	
	@Test
	public void addShouldPassEntityToSeriesDao() {
		service.add(form, user);

		verify(seriesDao).save(any(Series.class));
	}
	
	@Test
	public void addShouldLoadAndPassCountryToSeriesDaoIfCountryPresent() {
		Country expectedCountry = CountryServiceTest.getCountry();
		
		form.setCountry(expectedCountry);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());

		assertThat(seriesCaptor.getValue().getCountry()).isNotNull();
		assertThat(seriesCaptor.getValue().getCountry()).isEqualTo(expectedCountry);
	}
	
	@Test
	public void addShouldPassDateWithSpecifiedYearToSeriesDaoIfYearPresent() {
		final int expectedYear = 2000;
		form.setYear(expectedYear);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(seriesCaptor.getValue().getReleasedAt());
		int actualYear = cal.get(Calendar.YEAR);
		
		assertThat(actualYear).isEqualTo(expectedYear);
	}
	
	@Test
	public void addShouldPassQuantityToSeriesDao() {
		final Integer expectedQuantity = 3;
		form.setQuantity(expectedQuantity);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		assertThat(seriesCaptor.getValue().getQuantity()).isEqualTo(expectedQuantity);
	}
	
	@Test
	public void addShouldPassPerforatedToSeriesDao() {
		Boolean expectedResult = true;
		form.setPerforated(expectedResult);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		assertThat(seriesCaptor.getValue().getPerforated()).isEqualTo(expectedResult);
	}

	@Test
	public void addShouldPassNullToSeriesDaoIfMichelNumbersIsNull() {
		assertThat(form.getMichelNumbers()).isNull();
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		assertThat(seriesCaptor.getValue().getMichel()).isNull();
	}
	
	@Test
	public void addShouldPassMichelNumbersToSeriesDao() {
		Set<MichelCatalog> expectedNumbers = newSet(
			new MichelCatalog("1"),
			new MichelCatalog("2")
		);
		form.setMichelNumbers(StringUtils.join(expectedNumbers, ','));
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		assertThat(seriesCaptor.getValue().getMichel()).isEqualTo(expectedNumbers);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addShouldThrowExceptionIfMichelPriceSpecifiedWithoutCurrency() {
		form.setMichelPrice(ANY_PRICE);
		form.setMichelCurrency(null);
		
		service.add(form, user);
	}
	
	@Test
	public void addShouldPassMichelPriceToSeriesDao() {
		Double expectedPrice = ANY_PRICE;
		Currency expectedCurrency = Currency.RUR;
		form.setMichelPrice(expectedPrice);
		form.setMichelCurrency(expectedCurrency);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		Price actualPrice = seriesCaptor.getValue().getMichelPrice();
		assertThat(actualPrice).isNotNull();
		assertThat(actualPrice.getValue()).isEqualTo(expectedPrice);
		assertThat(actualPrice.getCurrency()).isEqualTo(expectedCurrency);
	}
	
	@Test
	public void addShouldPassNullToSeriesDaoIfMichelPriceIsNull() {
		form.setMichelPrice(null);
		form.setMichelCurrency(null);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		Price actualPrice = seriesCaptor.getValue().getMichelPrice();
		assertThat(actualPrice).isNull();
	}
	
	@Test
	public void addShouldPassNullToSeriesDaoIfScottNumbersIsNull() {
		assertThat(form.getScottNumbers()).isNull();
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		assertThat(seriesCaptor.getValue().getScott()).isNull();
	}
	
	@Test
	public void addShouldPassScottNumbersToSeriesDao() {
		Set<ScottCatalog> expectedNumbers = newSet(
			new ScottCatalog("1"),
			new ScottCatalog("2")
		);
		form.setScottNumbers(StringUtils.join(expectedNumbers, ','));
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		assertThat(seriesCaptor.getValue().getScott()).isEqualTo(expectedNumbers);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addShouldThrowExceptionIfScottPriceSpecifiedWithoutCurrency() {
		form.setScottPrice(ANY_PRICE);
		form.setScottCurrency(null);
		
		service.add(form, user);
	}
	
	@Test
	public void addShouldPassScottPriceToSeriesDao() {
		Double expectedPrice = ANY_PRICE;
		Currency expectedCurrency = Currency.RUR;
		form.setScottPrice(expectedPrice);
		form.setScottCurrency(expectedCurrency);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		Price actualPrice = seriesCaptor.getValue().getScottPrice();
		assertThat(actualPrice).isNotNull();
		assertThat(actualPrice.getValue()).isEqualTo(expectedPrice);
		assertThat(actualPrice.getCurrency()).isEqualTo(expectedCurrency);
	}
	
	@Test
	public void addShouldPassNullToSeriesDaoIfScottPriceIsNull() {
		form.setScottPrice(null);
		form.setScottCurrency(null);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		Price actualPrice = seriesCaptor.getValue().getScottPrice();
		assertThat(actualPrice).isNull();
	}
	
	@Test
	public void addShouldPassNullToSeriesDaoIfYvertNumbersIsNull() {
		assertThat(form.getYvertNumbers()).isNull();
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		assertThat(seriesCaptor.getValue().getYvert()).isNull();
	}
	
	@Test
	public void addShouldPassYvertNumbersToSeriesDao() {
		Set<YvertCatalog> expectedNumbers = newSet(
			new YvertCatalog("1"),
			new YvertCatalog("2")
		);
		form.setYvertNumbers(StringUtils.join(expectedNumbers, ','));
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		assertThat(seriesCaptor.getValue().getYvert()).isEqualTo(expectedNumbers);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addShouldThrowExceptionIfYvertPriceSpecifiedWithoutCurrency() {
		form.setYvertPrice(ANY_PRICE);
		form.setYvertCurrency(null);
		
		service.add(form, user);
	}
	
	@Test
	public void addShouldPassYvertPriceToSeriesDao() {
		Double expectedPrice = ANY_PRICE;
		Currency expectedCurrency = Currency.RUR;
		form.setYvertPrice(expectedPrice);
		form.setYvertCurrency(expectedCurrency);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		Price actualPrice = seriesCaptor.getValue().getYvertPrice();
		assertThat(actualPrice).isNotNull();
		assertThat(actualPrice.getValue()).isEqualTo(expectedPrice);
		assertThat(actualPrice.getCurrency()).isEqualTo(expectedCurrency);
	}
	
	@Test
	public void addShouldPassNullToSeriesDaoIfYvertPriceIsNull() {
		form.setYvertPrice(null);
		form.setYvertCurrency(null);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		Price actualPrice = seriesCaptor.getValue().getYvertPrice();
		assertThat(actualPrice).isNull();
	}
	
	@Test
	public void addShouldPassNullToSeriesDaoIfGibbonsNumbersIsNull() {
		assertThat(form.getGibbonsNumbers()).isNull();
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		assertThat(seriesCaptor.getValue().getGibbons()).isNull();
	}
	
	@Test
	public void addShouldPassGibbonsNumbersToSeriesDao() {
		Set<GibbonsCatalog> expectedNumbers = newSet(
			new GibbonsCatalog("1"),
			new GibbonsCatalog("2")
		);
		form.setGibbonsNumbers(StringUtils.join(expectedNumbers, ','));
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		assertThat(seriesCaptor.getValue().getGibbons()).isEqualTo(expectedNumbers);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addShouldThrowExceptionIfGibbonsPriceSpecifiedWithoutCurrency() {
		form.setGibbonsPrice(ANY_PRICE);
		form.setGibbonsCurrency(null);
		
		service.add(form, user);
	}
	
	@Test
	public void addShouldPassGibbonsPriceToSeriesDao() {
		Double expectedPrice = ANY_PRICE;
		Currency expectedCurrency = Currency.RUR;
		form.setGibbonsPrice(expectedPrice);
		form.setGibbonsCurrency(expectedCurrency);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		Price actualPrice = seriesCaptor.getValue().getGibbonsPrice();
		assertThat(actualPrice).isNotNull();
		assertThat(actualPrice.getValue()).isEqualTo(expectedPrice);
		assertThat(actualPrice.getCurrency()).isEqualTo(expectedCurrency);
	}
	
	@Test
	public void addShouldPassNullToSeriesDaoIfGibbonsPriceIsNull() {
		form.setGibbonsPrice(null);
		form.setGibbonsCurrency(null);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		Price actualPrice = seriesCaptor.getValue().getGibbonsPrice();
		assertThat(actualPrice).isNull();
	}
	
	@Test
	public void addShouldPassImageToImageService() {
		form.setImage(multipartFile);
		service.add(form, user);
		
		verify(imageService).save(multipartFileCaptor.capture());
		assertThat(multipartFileCaptor.getValue()).isEqualTo(multipartFile);
	}
	
	@Test(expected = IllegalStateException.class)
	public void addShouldThrowExceptionIfImageUrlIsNull() {
		when(imageService.save(any(MultipartFile.class))).thenReturn(null);
		
		service.add(form, user);
	}
	
	@Test(expected = IllegalStateException.class)
	public void addShouldThrowExceptionIfImageUrlTooLong() {
		String aVeryLongPath = StringUtils.repeat("x", Series.IMAGE_URL_LENGTH + 1);
		when(imageService.save(any(MultipartFile.class))).thenReturn(aVeryLongPath);
		
		service.add(form, user);
	}
	
	@Test
	public void addShouldPassImageUrlToSeriesDao() {
		String expectedUrl = "http://example.org/example.jpg";
		when(imageService.save(any(MultipartFile.class))).thenReturn(expectedUrl);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		assertThat(seriesCaptor.getValue().getImageUrl()).isEqualTo(expectedUrl);
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addShouldThrowExceptionIfCommentIsEmpty() {
		form.setComment("  ");

		service.add(form, user);
	}
	
	@Test
	public void addShouldPassCommentToSeriesDaoIfPresent() {
		String expectedComment = "Some text";
		form.setComment(expectedComment);
		
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		assertThat(seriesCaptor.getValue().getComment()).isEqualTo(expectedComment);
	}
	
	@Test
	public void addShouldAssignCreatedAtToCurrentDate() {
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		MetaInfo metaInfo = seriesCaptor.getValue().getMetaInfo();
		assertThat(metaInfo).isNotNull();
		DateAssert.assertThat(metaInfo.getCreatedAt()).isCurrentDate();
	}
	
	@Test
	public void addShouldAssignUpdatedAtToCurrentDate() {
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		
		MetaInfo metaInfo = seriesCaptor.getValue().getMetaInfo();
		assertThat(metaInfo).isNotNull();
		DateAssert.assertThat(metaInfo.getUpdatedAt()).isCurrentDate();
	}
	
	@Test
	public void addShouldAssignCreatedByToUser() {
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		MetaInfo metaInfo = seriesCaptor.getValue().getMetaInfo();
		assertThat(metaInfo).isNotNull();
		assertThat(metaInfo.getCreatedBy()).isEqualTo(user);
	}
	
	@Test
	public void addShouldAssignUpdatedByToUser() {
		service.add(form, user);
		
		verify(seriesDao).save(seriesCaptor.capture());
		MetaInfo metaInfo = seriesCaptor.getValue().getMetaInfo();
		assertThat(metaInfo).isNotNull();
		assertThat(metaInfo.getUpdatedBy()).isEqualTo(user);
	}
	
	private static <T> Set<T> newSet(T... elements) {
		Set<T> result = new LinkedHashSet<T>();
		
		for (T element : elements) {
			result.add(element);
		}
		
		return result;
	}
	
}
