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
package ru.mystamps.web.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Calendar.JANUARY;

import org.apache.commons.lang3.Validate;

import ru.mystamps.web.dao.CountryDao;
import ru.mystamps.web.dao.GibbonsCatalogDao;
import ru.mystamps.web.dao.MichelCatalogDao;
import ru.mystamps.web.dao.ScottCatalogDao;
import ru.mystamps.web.dao.SeriesDao;
import ru.mystamps.web.dao.YvertCatalogDao;
import ru.mystamps.web.entity.GibbonsCatalog;
import ru.mystamps.web.entity.MichelCatalog;
import ru.mystamps.web.entity.ScottCatalog;
import ru.mystamps.web.entity.Series;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.YvertCatalog;
import ru.mystamps.web.model.AddSeriesForm;
import ru.mystamps.web.util.CatalogUtils;

@Service
public class SeriesService {
	
	@Inject
	private CountryDao countryDao;
	
	@Inject
	private SeriesDao seriesDao;
	
	@Inject
	private MichelCatalogDao michelCatalogDao;
	
	@Inject
	private ScottCatalogDao scottCatalogDao;
	
	@Inject
	private YvertCatalogDao yvertCatalogDao;
	
	@Inject
	private GibbonsCatalogDao gibbonsCatalogDao;
	
	@Inject
	private ImageService imageService;
	
	@Inject
	private UserService userService;
	
	@Transactional
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public Series add(final AddSeriesForm form) {
		Validate.isTrue(form != null, "Series info must be non null");
		Validate.isTrue(form.getQuantity() != null, "Stamps quantity must be non null");
		Validate.isTrue(
			form.getPerforated() != null,
			"Stamps perforated property must be non null"
		);
		
		final Series series = new Series();
		
		if (form.getCountry() != null) {
			series.setCountry(countryDao.findOne(form.getCountry()));
		}
		
		if (form.getYear() != null) {
			final Calendar releaseDate = GregorianCalendar.getInstance();
			releaseDate.clear();
			releaseDate.set(form.getYear(), JANUARY, 1);
			
			series.setReleasedAt(releaseDate.getTime());
		}
		
		series.setQuantity(form.getQuantity());
		series.setPerforated(form.getPerforated());
		
		final Set<MichelCatalog> michelNumbers =
			CatalogUtils.fromString(form.getMichelNumbers(), MichelCatalog.class);
		if (!michelNumbers.isEmpty()) {
			michelCatalogDao.save(michelNumbers);
			series.setMichel(michelNumbers);
		}
		
		final Set<ScottCatalog> scottNumbers =
			CatalogUtils.fromString(form.getScottNumbers(), ScottCatalog.class);
		if (!scottNumbers.isEmpty()) {
			scottCatalogDao.save(scottNumbers);
			series.setScott(scottNumbers);
		}
		
		final Set<YvertCatalog> yvertNumbers =
			CatalogUtils.fromString(form.getYvertNumbers(), YvertCatalog.class);
		if (!yvertNumbers.isEmpty()) {
			yvertCatalogDao.save(yvertNumbers);
			series.setYvert(yvertNumbers);
		}
		
		final Set<GibbonsCatalog> gibbonsNumbers =
			CatalogUtils.fromString(form.getGibbonsNumbers(), GibbonsCatalog.class);
		if (!gibbonsNumbers.isEmpty()) {
			gibbonsCatalogDao.save(gibbonsNumbers);
			series.setGibbons(gibbonsNumbers);
		}
		
		final String imageUrl = imageService.save(form.getImage());
		Validate.validState(imageUrl != null, "Image url must be non null");
		Validate.validState(imageUrl.length() <= Series.IMAGE_URL_LENGTH, "Too long image path");
		
		series.setImageUrl(imageUrl);
		
		if (form.getComment() != null) {
			Validate.isTrue(
				!form.getComment().trim().isEmpty(),
				"Comment must be non empty"
			);
			
			series.setComment(form.getComment());
		}
		
		final Date now = new Date();
		series.setCreatedAt(now);
		series.setUpdatedAt(now);
		
		final User currentUser = userService.getCurrentUser();
		Validate.validState(currentUser != null, "Current user must be non null");
		series.setCreatedBy(currentUser);
		series.setUpdatedBy(currentUser);
		
		return seriesDao.save(series);
	}
	
	@Transactional(readOnly = true)
	public Series findById(final Integer id) {
		Validate.isTrue(id != null, "Id should be non null");
		return seriesDao.findOne(id);
	}
	
}
