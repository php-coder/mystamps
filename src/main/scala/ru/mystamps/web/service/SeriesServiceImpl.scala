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
package ru.mystamps.web.service

import java.util.{Calendar, Date, GregorianCalendar, Set}

import javax.inject.Inject

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static java.util.Calendar.JANUARY

import org.apache.commons.lang3.Validate

import ru.mystamps.web.dao.SeriesDao
import ru.mystamps.web.entity.{GibbonsCatalog, MichelCatalog, ScottCatalog, YvertCatalog}
import ru.mystamps.web.entity.{Price, Series, User}
import ru.mystamps.web.service.dto.AddSeriesDto
import ru.mystamps.web.util.CatalogUtils

@Service
class SeriesServiceImpl extends SeriesService {
	
	@Inject
	private var seriesDao: SeriesDao
	
	@Inject
	private var imageService: ImageService
	
	@Transactional
	@PreAuthorize("hasAuthority('ROLE_USER')")
	override def add(dto: AddSeriesDto, user: User): Series = {
		Validate.isTrue(dto != null, "DTO must be non null")
		Validate.isTrue(dto.getQuantity() != null, "Stamps quantity must be non null")
		Validate.isTrue(
			dto.getPerforated() != null,
			"Stamps perforated property must be non null"
		)
		Validate.isTrue(user != null, "Current user must be non null")
		
		val series: series = new Series()
		
		if (dto.getCountry() != null) {
			series.setCountry(dto.getCountry())
		}
		
		if (dto.getYear() != null) {
			val releaseDate: Calendar = GregorianCalendar.getInstance()
			releaseDate.clear()
			releaseDate.set(dto.getYear(), JANUARY, 1)
			
			series.setReleasedAt(releaseDate.getTime())
		}
		
		series.setQuantity(dto.getQuantity())
		series.setPerforated(dto.getPerforated())
		
		setMichelNumbersIfProvided(dto, series)
		setMichelPriceIfProvided(dto, series)
		
		setScottNumbersIfProvided(dto, series)
		setScottPriceIfProvided(dto, series)
		
		setYvertNumbersIfProvided(dto, series)
		setYvertPriceIfProvided(dto, series)
		
		setGibbonsNumbersIfProvided(dto, series)
		setGibbonsPriceIfProvided(dto, series)
		
		val imageUrl: String = imageService.save(dto.getImage())
		Validate.validState(imageUrl != null, "Image url must be non null")
		Validate.validState(imageUrl.length() <= Series.IMAGE_URL_LENGTH, "Too long image path")
		
		series.setImageUrl(imageUrl)
		
		if (dto.getComment() != null) {
			Validate.isTrue(
				!dto.getComment().trim().isEmpty(),
				"Comment must be non empty"
			)
			
			series.setComment(dto.getComment())
		}
		
		val now: Date = new Date()
		series.getMetaInfo().setCreatedAt(now)
		series.getMetaInfo().setUpdatedAt(now)
		
		series.getMetaInfo().setCreatedBy(user)
		series.getMetaInfo().setUpdatedBy(user)
		
		return seriesDao.save(series)
	}
	
	private def setMichelNumbersIfProvided(dto: AddSeriesDto, series: Series): Unit = {
		val michelNumbers: Set[MichelCatalog] =
			CatalogUtils.fromString(dto.getMichelNumbers(), classOf[MichelCatalog])
		if (!michelNumbers.isEmpty()) {
			series.setMichel(michelNumbers)
		}
	}
	
	private def setMichelPriceIfProvided(dto: AddSeriesDto, series: Series): Unit {
		if (dto.getMichelPrice() == null) {
			return
		}
		
		Validate.isTrue(
			dto.getMichelCurrency() != null,
			"Michel currency must be non null when price is specified"
		)
		series.setMichelPrice(new Price(dto.getMichelPrice(), dto.getMichelCurrency()))
	}
	
	private def setScottNumbersIfProvided(dto: AddSeriesDto, series: Series): Unit = {
		val scottNumbers: Set[ScottCatalog] =
			CatalogUtils.fromString(dto.getScottNumbers(), classOf[ScottCatalog])
		if (!scottNumbers.isEmpty()) {
			series.setScott(scottNumbers)
		}
	}
	
	private def setScottPriceIfProvided(dto: AddSeriesDto, series: Series): Unit = {
		if (dto.getScottPrice() == null) {
			return
		}
		
		Validate.isTrue(
			dto.getScottCurrency() != null,
			"Scott currency must be non null when price is specified"
		)
		series.setScottPrice(new Price(dto.getScottPrice(), dto.getScottCurrency()))
	}
	
	private def setYvertNumbersIfProvided(dto: AddSeriesDto, series: Series): Unit = {
		val yvertNumbers: Set[YvertCatalog] =
			CatalogUtils.fromString(dto.getYvertNumbers(), classOf[YvertCatalog])
		if (!yvertNumbers.isEmpty()) {
			series.setYvert(yvertNumbers)
		}
	}
	
	private def setYvertPriceIfProvided(dto: AddSeriesDto, series: Series): Unit = {
		if (dto.getYvertPrice() == null) {
			return
		}
		
		Validate.isTrue(
			dto.getYvertCurrency() != null,
			"Yvert currency must be non null when price is specified"
		)
		series.setYvertPrice(new Price(dto.getYvertPrice(), dto.getYvertCurrency()))
	}
	
	private def setGibbonsNumbersIfProvided(dto: AddSeriesDto, series: Series): Unit = {
		val gibbonsNumbers: Set[GibbonsCatalog] =
			CatalogUtils.fromString(dto.getGibbonsNumbers(), classOf[GibbonsCatalog])
		if (!gibbonsNumbers.isEmpty()) {
			series.setGibbons(gibbonsNumbers)
		}
	}
	
	private def setGibbonsPriceIfProvided(dto: AddSeriesDto, series: Series): Unit = {
		if (dto.getGibbonsPrice() == null) {
			return
		}
		
		Validate.isTrue(
			dto.getGibbonsCurrency() != null,
			"Gibbons currency must be non null when price is specified"
		)
		series.setGibbonsPrice(new Price(dto.getGibbonsPrice(), dto.getGibbonsCurrency()))
	}
	
}
