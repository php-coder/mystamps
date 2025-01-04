/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.image;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
	ImageInfoDto save(MultipartFile file);
	void replace(Integer imageId, MultipartFile file);
	ImageDto get(Integer imageId);
	ImageDto getOrCreatePreview(Integer imageId);
	void addToSeries(Integer seriesId, Integer imageId);
	List<Integer> findBySeriesId(Integer seriesId, boolean hidden);
	void removeIfPossible(ImageInfoDto imageInfo);
}
