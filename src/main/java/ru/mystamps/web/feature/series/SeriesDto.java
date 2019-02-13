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
package ru.mystamps.web.feature.series;

import lombok.Getter;
import ru.mystamps.web.dao.dto.LinkEntityDto;

import java.util.List;

public class SeriesDto {
	private final SeriesFullInfoDto info;

	@Getter
	private final CatalogInfoDto michel;

	@Getter
	private final CatalogInfoDto scott;

	@Getter
	private final CatalogInfoDto yvert;

	@Getter
	private final CatalogInfoDto gibbons;

	@Getter
	private final CatalogInfoDto solovyov;

	@Getter
	private final CatalogInfoDto zagorski;
	
	@Getter
	private final List<Integer> imageIds;
	
	@SuppressWarnings({ "checkstyle:parameternumber", "PMD.ExcessiveParameterList" })
	public SeriesDto(
		SeriesFullInfoDto info,
		List<String> michelNumbers,
		List<String> scottNumbers,
		List<String> yvertNumbers,
		List<String> gibbonsNumbers,
		List<String> solovyovNumbers,
		List<String> zagorskiNumbers,
		List<Integer> imageIds) {
		
		this.info     = info;
		this.michel   = new CatalogInfoDto(michelNumbers, info.getMichelPrice());
		this.scott    = new CatalogInfoDto(scottNumbers, info.getScottPrice());
		this.yvert    = new CatalogInfoDto(yvertNumbers, info.getYvertPrice());
		this.gibbons  = new CatalogInfoDto(gibbonsNumbers, info.getGibbonsPrice());
		this.solovyov = new CatalogInfoDto(solovyovNumbers, info.getSolovyovPrice());
		this.zagorski = new CatalogInfoDto(zagorskiNumbers, info.getZagorskiPrice());
		this.imageIds = imageIds;
	}
	
	public Integer getId() {
		return info.getId();
	}
	
	public LinkEntityDto getCategory() {
		return info.getCategory();
	}
	
	public LinkEntityDto getCountry() {
		return info.getCountry();
	}
	
	public Integer getReleaseDay() {
		return info.getReleaseDay();
	}
	
	public Integer getReleaseMonth() {
		return info.getReleaseMonth();
	}
	
	public Integer getReleaseYear() {
		return info.getReleaseYear();
	}
	
	public Integer getQuantity() {
		return info.getQuantity();
	}
	
	public Boolean getPerforated() {
		return info.getPerforated();
	}
	
	public String getComment() {
		return info.getComment();
	}
	
	public Integer getCreatedBy() {
		return info.getCreatedBy();
	}
	
}
