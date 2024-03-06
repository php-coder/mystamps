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
package ru.mystamps.web.feature.series.importing

import org.slf4j.helpers.NOPLogger
import org.springframework.context.ApplicationEventPublisher
import ru.mystamps.web.feature.participant.ParticipantService
import ru.mystamps.web.feature.series.SeriesService
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportService
import ru.mystamps.web.feature.series.sale.SeriesSalesService
import ru.mystamps.web.tests.Random
import spock.lang.Specification

class SeriesImportServiceImplTest extends Specification {
	
	private final SeriesImportDao seriesImportDao = Mock()
	private final SeriesService seriesService = Mock()
	private final SeriesSalesService seriesSalesService = Mock()
	private final SeriesSalesImportService seriesSalesImportService = Mock()
	private final ParticipantService participantService = Mock()
	private final ApplicationEventPublisher eventPublisher = Mock()
	
	private SeriesImportService service
	private RequestSeriesImportForm form
	
	def setup() {
		service = new SeriesImportServiceImpl(
			NOPLogger.NOP_LOGGER,
			seriesImportDao,
			seriesService,
			seriesSalesService,
			seriesSalesImportService,
			participantService,
			eventPublisher
		)
		form = new RequestSeriesImportForm()
	}
	
	//
	// Tests for addRequest()
	//
	
	def 'addRequest() should throw exception if url is incorrect'() {
		given:
			form.setUrl('http://example.org/текст c пробелами')
		when:
			service.addRequest(form, Random.userId())
		then:
			RuntimeException ex = thrown()
		and:
			ex?.cause?.class == URISyntaxException
	}
	
	def 'addRequest() should save url in the encoded form'() {
		given:
			String url = 'http://example.org/текст_на_русском'
			String expectedUrl = 'http://example.org/%D1%82%D0%B5%D0%BA%D1%81%D1%82_%D0%BD%D0%B0_%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%BE%D0%BC'
		and:
			form.setUrl(url)
		when:
			service.addRequest(form, Random.userId())
		then:
			1 * seriesImportDao.add({ ImportSeriesDbDto request ->
				assert request?.url == expectedUrl
				return true
			}) >> Random.id()
	}
	
	def 'addRequest() should not encode url if it is already encoded'() {
		given:
			String expectedUrl = 'http://example.org/%D1%82%D0%B5%D0%BA%D1%81%D1%82_%D0%BD%D0%B0_%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%BE%D0%BC'
		and:
			form.setUrl(expectedUrl)
		when:
			service.addRequest(form, Random.userId())
		then:
			1 * seriesImportDao.add({ ImportSeriesDbDto request ->
				assert request?.url == expectedUrl
				return true
			}) >> Random.id()
	}
	
}
