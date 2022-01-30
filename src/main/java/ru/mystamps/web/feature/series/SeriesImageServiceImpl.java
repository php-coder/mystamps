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
package ru.mystamps.web.feature.series;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import ru.mystamps.web.support.spring.security.HasAuthority;

@RequiredArgsConstructor
public class SeriesImageServiceImpl implements SeriesImageService {
	
	private final Logger log;
	private final SeriesImageDao seriesImageDao;
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.HIDE_IMAGE)
	public void hideImage(Integer seriesId, Integer imageId) {
		Validate.isTrue(seriesId != null, "Series id must be non null");
		Validate.isTrue(imageId != null, "Image id must be non null");
		
		seriesImageDao.hideImage(seriesId, imageId);
		
		log.info("Series #{}: image #{} has been hidden", seriesId, imageId);
	}
	
}
