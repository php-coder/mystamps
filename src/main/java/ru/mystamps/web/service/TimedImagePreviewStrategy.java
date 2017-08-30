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
package ru.mystamps.web.service;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;

import org.slf4j.Logger;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TimedImagePreviewStrategy implements ImagePreviewStrategy {
	
	private final Logger log;
	private final ImagePreviewStrategy strategy;
	
	@Override
	public byte[] createPreview(byte[] image) {
		// Why we don't use Spring's StopWatch?
		// 1) because its javadoc says that it's not intended for production
		// 2) because we don't want to have strong dependencies on the Spring Framework
		StopWatch timer = new StopWatch();
		
		// start() and stop() may throw IllegalStateException and in this case it's possible
		// that we won't generate anything or lose already generated result. I don't want to
		// make method body too complicated by adding many try/catches and I believe that such
		// exception will never happen because it would mean that we're using API in a wrong way.
		timer.start();
		byte[] result = strategy.createPreview(image);
		timer.stop();
		
		log.debug(
			"Image preview has been generated in {} msecs: {} -> {} bytes",
			timer.getTime(),
			// let's hope that it won't throw IllegalStateException :)
			ArrayUtils.getLength(image),
			ArrayUtils.getLength(result)
		);
		
		return result;
	}
	
}
