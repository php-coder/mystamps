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
package ru.mystamps.web.service.dto;

import java.util.List;

import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.dao.dto.SeriesFullInfoDto;

public class SeriesDto {
	private final SeriesFullInfoDto info;
	private final CatalogInfoDto michel;
	private final CatalogInfoDto scott;
	private final CatalogInfoDto yvert;
	private final CatalogInfoDto gibbons;
	private final List<Integer> imageIds;
	
	@SuppressWarnings({"checkstyle:parameternumber", "PMD.ExcessiveParameterList"})
	public SeriesDto(
		SeriesFullInfoDto info,
		List<String> michelNumbers,
		List<String> scottNumbers,
		List<String> yvertNumbers,
		List<String> gibbonsNumbers,
		List<Integer> imageIds) {
		
		this.info     = info;
		// CheckStyle: ignore LineLength for next 4 lines
		this.michel   = new CatalogInfoDto(michelNumbers, info.getMichelPrice(), info.getMichelCurrency());
		this.scott    = new CatalogInfoDto(scottNumbers, info.getScottPrice(), info.getScottCurrency());
		this.yvert    = new CatalogInfoDto(yvertNumbers, info.getYvertPrice(), info.getYvertCurrency());
		this.gibbons  = new CatalogInfoDto(gibbonsNumbers, info.getGibbonsPrice(), info.getGibbonsCurrency());
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
	
	public CatalogInfoDto getYvert() {
		return yvert;
	}
	
	public CatalogInfoDto getMichel() {
		return michel;
	}
	
	public CatalogInfoDto getScott() {
		return scott;
	}
	
	public CatalogInfoDto getGibbons() {
		return gibbons;
	}
	
	public List<Integer> getImageIds() {
		return imageIds;
	}
	
}
