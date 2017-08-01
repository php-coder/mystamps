/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.service.CronService;
import ru.mystamps.web.service.MailService;

/**
 * @author Maxim Shestakov
 */
@Controller
@RequiredArgsConstructor
public class ReportController {
	private static final Logger LOG = LoggerFactory.getLogger(ReportController.class);
	
	private final MailService mailService;
	private final CronService cronService;
	
	@RequestMapping(Url.DAILY_STATISTICS)
	public void showDailyReport(HttpServletResponse response) {
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		
		try {
			PrintWriter writer = response.getWriter();
			String stats = mailService.getTextOfDailyStatisticsMail(
				cronService.getDailyStatistics()
			);
			writer.println(stats);
		} catch (IOException ex) {
			LOG.error("Can't get daily report", ex.getMessage());
		}
	}
	
}
