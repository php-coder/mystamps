/*
 * Copyright (C) 2009-2021 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web.support.spring.mvc;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;

// CheckStyle: ignore LineLength for next 2 lines
/**
 * Converter for BigDecimal that correctly parse values with a comma separator (in addition to a point).
 */
// @todo #1513 Add integration test to check that prices accept a decimal comma
public class BigDecimalConverter implements Converter<String, BigDecimal> {
	
	@Override
	public BigDecimal convert(String source) {
		if (StringUtils.EMPTY.equals(source)) {
			return null;
		}
		
		String value = source;
		if (source.indexOf(',') >= 0) {
			// "10,5" => "10.5"
			value = source.replace(',', '.');
		}
		
		return new BigDecimal(value);
	}
	
}
