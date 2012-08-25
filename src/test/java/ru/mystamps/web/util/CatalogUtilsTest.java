/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.testng.IObjectFactory;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;

import static org.fest.assertions.api.Assertions.assertThat;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyVararg;
import static org.mockito.Mockito.when;

import ru.mystamps.web.entity.GibbonsCatalog;
import ru.mystamps.web.entity.MichelCatalog;
import ru.mystamps.web.entity.ScottCatalog;
import ru.mystamps.web.entity.StampsCatalog;
import ru.mystamps.web.entity.YvertCatalog;

@PrepareForTest(ConstructorUtils.class)
public class CatalogUtilsTest {
	
	@ObjectFactory
	public IObjectFactory getObjectFactory() {
		return new PowerMockObjectFactory();
	}
	
	//
	// Tests for toShortForm()
	//
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void toShortFormShouldThrowExceptionIfNumbersIsNull() {
		CatalogUtils.toShortForm(null);
	}
	
	@Test
	public void toShortFormShouldReturnEmptyStringForEmptyNumbers() {
		assertThat(CatalogUtils.toShortForm(Collections.<MichelCatalog>emptySet())).isEqualTo("");
	}
	
	@Test
	public void toShortFormShouldReturnOneNumberAsIs() {
		final Set<MichelCatalog> setOfSingleNumber = Collections.singleton(new MichelCatalog("1"));
		
		assertThat(CatalogUtils.toShortForm(setOfSingleNumber)).isEqualTo("1");
	}
	
	@Test
	public void toShortFormShouldReturnTwoNumbersAsCommaSeparated() {
		final Set<MichelCatalog> setOfNumbers = new LinkedHashSet<MichelCatalog>();
		setOfNumbers.add(new MichelCatalog("1"));
		setOfNumbers.add(new MichelCatalog("2"));
		
		assertThat(CatalogUtils.toShortForm(setOfNumbers)).isEqualTo("1, 2");
	}
	
	//
	// Tests for fromString()
	//
	
	@Test
	public void fromStringShouldReturnsEmptyCollectionIfCatalogNumbersIsNull() {
		assertThat(CatalogUtils.fromString(null, MichelCatalog.class)).isEmpty();
	}
	
	@Test
	public void fromStringShouldReturnsEmptyCollectionIfCatalogNumbersIsEmpty() {
		assertThat(CatalogUtils.fromString("", MichelCatalog.class)).isEmpty();
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fromStringShouldThrowExceptionIfElementClassIsNull() {
		CatalogUtils.fromString("1", null);
	}
	
	@Test
	public void fromStringShouldReturnsOneElementIfCatalogNumbersDoesNotContainsComma() {
		assertThat(CatalogUtils.fromString("1", MichelCatalog.class)).hasSize(1);
	}
	
	@Test
	public void fromStringShouldReturnsOneElementIfCatalogNumbersContainsExtraComma() {
		assertThat(CatalogUtils.fromString("1,", MichelCatalog.class)).hasSize(1);
	}
	
	@Test
	public void fromStringShouldReturnsTwoElementIfCatalogNumbersHasTwoNumbers() {
		assertThat(CatalogUtils.fromString("1,2", MichelCatalog.class)).hasSize(2);
	}
	
	@Test(expectedExceptions = IllegalStateException.class)
	public void fromStringShouldThrowExceptionIfOneOfCatalogNumbersIsABlankString() {
		CatalogUtils.fromString("1, ", MichelCatalog.class);
	}
	
	@Test(expectedExceptions = RuntimeException.class)
	public void fromStringShouldConvertExceptionToRuntimeException() throws Exception {
		PowerMockito.mockStatic(ConstructorUtils.class);
		when(ConstructorUtils.invokeConstructor(any(Class.class), anyVararg()))
			.thenThrow(new InstantiationException("Can't initiate object"));
		
		CatalogUtils.fromString("1", NopCatalog.class);
	}
	
	@Test(expectedExceptions = RuntimeException.class)
	public void fromStringShouldThrowRuntimeExceptionAsIs() throws Exception {
		PowerMockito.mockStatic(ConstructorUtils.class);
		when(ConstructorUtils.invokeConstructor(any(Class.class), anyVararg()))
			.thenThrow(new RuntimeException("Error occurs"));
		
		CatalogUtils.fromString("1", MichelCatalog.class);
	}
	
	@Test
	public void fromStringShouldReturnsSetOfMichelNumbersForAppropriateElementClass() {
		for (final StampsCatalog catalog : CatalogUtils.fromString("1,2", MichelCatalog.class)) {
			assertThat(catalog).isExactlyInstanceOf(MichelCatalog.class);
		}
	}
	
	@Test
	public void fromStringShouldReturnsSetOfScottNumbersForAppropriateElementClass() {
		for (final StampsCatalog catalog : CatalogUtils.fromString("1,2", ScottCatalog.class)) {
			assertThat(catalog).isExactlyInstanceOf(ScottCatalog.class);
		}
	}
	
	@Test
	public void fromStringShouldReturnsSetOfYvertNumbersForAppropriateElementClass() {
		for (final StampsCatalog catalog : CatalogUtils.fromString("1,2", YvertCatalog.class)) {
			assertThat(catalog).isExactlyInstanceOf(YvertCatalog.class);
		}
	}
	
	@Test
	public void fromStringShouldReturnsSetOfGibbonsNumbersForAppropriateElementClass() {
		for (final StampsCatalog catalog : CatalogUtils.fromString("1,2", GibbonsCatalog.class)) {
			assertThat(catalog).isExactlyInstanceOf(GibbonsCatalog.class);
		}
	}
	
	class NopCatalog implements StampsCatalog {
		@Override
		public String getCode() {
			return null;
		}
	}
	
}
