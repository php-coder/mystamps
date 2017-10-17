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
package ru.mystamps.web.controller.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

import ru.mystamps.web.service.dto.DownloadResult;

/**
 * Event occurs when downloading of a file from import request has been failed.
 */
@Getter
public class DownloadingFailed extends ApplicationEvent {
	private final Integer requestId;
	private final String url;
	private final DownloadResult.Code code;
	
	// CheckStyle: ignore LineLength for next 1 line
	public DownloadingFailed(Object source, Integer requestId, String url, DownloadResult.Code code) {
		super(source);
		this.requestId = requestId;
		this.url = url;
		this.code = code;
	}
	
}
