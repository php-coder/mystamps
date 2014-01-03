/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.support.hibernate;

import java.sql.Types;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * Customized version of MySQL5InnoDBDialect.
 *
 * Allows to use {@code bit} type in database for {@code boolean} members.
 *
 * @see <a href="http://stackoverflow.com/q/8667965">Question at StackOverflow</a>
 * @see <a href="https://hibernate.atlassian.net/browse/HHH-6935">Issue in Hibernate's JIRA</a>
 **/
public class MySql5InnoDbDialect extends MySQL5InnoDBDialect {
	
	public MySql5InnoDbDialect() {
		super();
		registerColumnType(Types.BOOLEAN, "bit");
	}
	
}

