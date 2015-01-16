/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import ru.mystamps.web.entity.Category;
import ru.mystamps.web.entity.Collection;
import ru.mystamps.web.entity.Country;
import ru.mystamps.web.entity.Series;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.service.dto.AddSeriesDto;
import ru.mystamps.web.service.dto.SeriesInfoDto;
import ru.mystamps.web.service.dto.SitemapInfoDto;

@SuppressWarnings("PMD.TooManyMethods")
public interface SeriesService {
	Series add(AddSeriesDto dto, User user, boolean userCanAddComments);
	long countAll();
	long countAllStamps();
	long countSeriesOf(Collection collection);
	long countStampsOf(Collection collection);
	int countByMichelNumber(String michelNumberCode);
	int countByScottNumber(String scottNumberCode);
	int countByYvertNumber(String yvertNumberCode);
	int countByGibbonsNumber(String gibbonsNumberCode);
	Iterable<SeriesInfoDto> findBy(Category category, String lang);
	Iterable<SeriesInfoDto> findBy(Country country, String lang);
	Iterable<SeriesInfoDto> findBy(Collection collection, String lang);
	Iterable<SeriesInfoDto> findRecentlyAdded(int quantity, String lang);
	Iterable<SitemapInfoDto> findAllForSitemap();
}
