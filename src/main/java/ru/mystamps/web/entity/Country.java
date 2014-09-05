/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.entity;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import ru.mystamps.web.util.LocaleUtils;

@Entity
@Table(name = "countries")
@Getter
@Setter
@EqualsAndHashCode(exclude = "metaInfo")
public class Country implements LocalizedEntity {
	
	public static final int NAME_LENGTH = 50;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(length = NAME_LENGTH, unique = true, nullable = false)
	private String name;
	
	@Column(name = "name_ru", length = NAME_LENGTH, nullable = false, unique = true)
	private String nameRu;
	
	@Embedded
	private MetaInfo metaInfo; // NOPMD
	
	public Country() {
		metaInfo = new MetaInfo();
	}
	
	@Override
	public String toString() {
		return String.valueOf(id);
	}
	
	public String toLongString() {
		return new StringBuilder()
			.append("Country(id=")
			.append(id)
			.append(", name=")
			.append(name)
			.append(", nameRu=")
			.append(nameRu)
			.append(')')
			.toString();
	}
	
	@Override
	public String getLocalizedName(Locale locale) {
		return LocaleUtils.getLocalizedName(locale, this);
	}
	
}
