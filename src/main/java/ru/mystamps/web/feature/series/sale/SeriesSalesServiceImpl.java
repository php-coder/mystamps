/*
 * Copyright (C) 2009-2022 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series.sale;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import ru.mystamps.web.support.spring.security.HasAuthority;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class SeriesSalesServiceImpl implements SeriesSalesService {
	
	private final Logger log;
	private final SeriesSalesDao seriesSalesDao;
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.ADD_SERIES_SALES)
	public void add(AddSeriesSalesDto dto, Integer seriesId, Integer userId) {
		Validate.isTrue(dto != null, "DTO must be non null");
		Validate.isTrue(dto.getSellerId() != null, "Seller id must be non null");
		Validate.isTrue(dto.getPrice() != null, "Price must be non null");
		Validate.isTrue(dto.getCurrency() != null, "Currency must be non null");
		Validate.isTrue(seriesId != null, "Series id must be non null");
		Validate.isTrue(userId != null, "Current user id must be non null");
		
		AddSeriesSalesDbDto sale = new AddSeriesSalesDbDto();
		sale.setDate(dto.getDate());
		sale.setSellerId(dto.getSellerId());
		sale.setUrl(dto.getUrl());
		sale.setPrice(dto.getPrice());
		sale.setCurrency(dto.getCurrency().toString());
		sale.setAltPrice(dto.getAltPrice());
		if (dto.getAltCurrency() != null) {
			sale.setAltCurrency(dto.getAltCurrency().toString());
		}
		sale.setBuyerId(dto.getBuyerId());
		if (dto.getCondition() != null) {
			sale.setCondition(dto.getCondition().toString());
		}
		
		Date now = new Date();
		sale.setCreatedAt(now);
		sale.setCreatedBy(userId);
		
		sale.setSeriesId(seriesId);
		
		seriesSalesDao.add(sale);
		
		log.info("Sale for series #{} has been added", seriesId);
	}
	
	/**
	 * @author Sergey Chechenev
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.VIEW_SERIES_SALES)
	public List<SeriesSaleDto> findSales(Integer seriesId) {
		Validate.isTrue(seriesId != null, "Series id must be non null");
		
		return seriesSalesDao.findSeriesSales(seriesId);
	}
	
}
