/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.controller.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * Event occurs when file from import request has been downloaded and saved to database.
 */
@Getter
public class DownloadingSucceeded extends ApplicationEvent {
	private final Integer requestId;
	private final String url;
	
	public DownloadingSucceeded(Object source, Integer requestId, String url) {
		super(source);
		this.requestId = requestId;
		this.url = url;
	}
	
}
