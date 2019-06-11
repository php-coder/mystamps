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
package ru.mystamps.web.feature.site;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

final class RowMappers {
	
	private RowMappers() {
	}
	
	/**
	 * @author Sergey Chechenev
	 */
	/* default */ static SuspiciousActivityDto forSuspiciousActivityDto(ResultSet rs, int unused)
		throws SQLException {
		
		String type        = rs.getString("activity_name");
		Date occurredAt    = rs.getTimestamp("occurred_at");
		String page        = rs.getString("page");
		String method      = rs.getString("method");
		String userLogin   = rs.getString("user_login");
		String ip          = rs.getString("ip");
		String refererPage = rs.getString("referer_page");
		String userAgent   = rs.getString("user_agent");
		
		return new SuspiciousActivityDto(
			type,
			occurredAt,
			page,
			method,
			userLogin,
			ip,
			refererPage,
			userAgent
		);
	}
	
}
