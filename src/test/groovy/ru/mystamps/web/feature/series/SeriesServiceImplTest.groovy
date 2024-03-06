/**
 * Copyright (C) 2009-2024 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series

import org.slf4j.helpers.NOPLogger
import ru.mystamps.web.feature.image.ImageInfoDto
import ru.mystamps.web.feature.image.ImageService
import ru.mystamps.web.service.TestObjects
import ru.mystamps.web.tests.Random
import spock.lang.Specification

class SeriesServiceImplTest extends Specification {
	private final ImageService imageService = Mock()
	private final SeriesDao seriesDao = Mock()
	private final StampsCatalogService michelCatalogService = Mock()
	private final StampsCatalogService scottCatalogService = Mock()
	private final StampsCatalogService yvertCatalogService = Mock()
	private final StampsCatalogService gibbonsCatalogService = Mock()
	private final StampsCatalogService solovyovCatalogService = Mock()
	private final StampsCatalogService zagorskiCatalogService = Mock()
	
	private SeriesService service
	
	def setup() {
		service = new SeriesServiceImpl(
			NOPLogger.NOP_LOGGER,
			seriesDao,
			imageService,
			michelCatalogService,
			scottCatalogService,
			yvertCatalogService,
			gibbonsCatalogService,
			solovyovCatalogService,
			zagorskiCatalogService
		)
	}
	
	//
	// Tests for add()
	//
	
	def "add() should remove image when exception occurs"() {
		given:
			AddSeriesForm form = new AddSeriesForm()
			form.setQuantity(Random.quantity())
			form.setPerforated(Random.perforated())
			form.setCategory(TestObjects.createLinkEntityDto())
		and:
			ImageInfoDto expectedImageInfo = TestObjects.createImageInfoDto()
		and:
			seriesDao.add(_ as AddSeriesDbDto) >> Random.id()
		and:
			imageService.addToSeries(_ as Integer, _ as Integer) >> { throw new IllegalStateException('oops') }
		when:
			service.add(form, Random.userId())
		then:
			imageService.save(_) >> expectedImageInfo
		and:
			1 * imageService.removeIfPossible(expectedImageInfo)
		and:
			IllegalStateException ex = thrown()
			ex.message == 'oops'
	}
	
	//
	// Tests for addImageToSeries()
	//
	
	def "addImageToSeries() should remove image when exception occurs"() {
		given:
			AddImageForm imageForm = new AddImageForm()
		and:
			ImageInfoDto expectedImageInfo = TestObjects.createImageInfoDto()
		and:
			imageService.addToSeries(_ as Integer, _ as Integer) >> { throw new IllegalStateException('oops') }
		when:
			service.addImageToSeries(imageForm, Random.id(), Random.userId())
		then:
			imageService.save(_) >> expectedImageInfo
		and:
			1 * imageService.removeIfPossible(expectedImageInfo)
		and:
			IllegalStateException ex = thrown()
			ex.message == 'oops'
	}
	
}
