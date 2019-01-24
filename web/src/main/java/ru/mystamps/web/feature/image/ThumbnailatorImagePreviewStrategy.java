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
package ru.mystamps.web.feature.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

public class ThumbnailatorImagePreviewStrategy implements ImagePreviewStrategy {
	
	private static final int WIDTH  = 250;
	private static final int HEIGHT = 250;
	
	// The value could be between 0.0 and 1.0 where 0.0 indicates the minimum quality
	// and 1.0 indicates the maximum quality.
	private static final double QUALITY = 0.5;
	
	@Override
	public byte[] createPreview(byte[] image) {
		try {
			ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
			
			Thumbnails.of(new ByteArrayInputStream(image))
				.size(WIDTH, HEIGHT)
				.outputFormat("JPEG")
				.outputQuality(QUALITY)
				.toOutputStream(resultStream);
			return resultStream.toByteArray();
			
		} catch (IOException
			| IllegalArgumentException
			| IllegalStateException
			| NullPointerException ex) { // NOPMD: AvoidCatchingNPE (Thumbnails.of() could throw it)
			throw new CreateImagePreviewException("Can't create preview for an image", ex);
		}
	}
	
}
