/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.model;

import lombok.Getter;
import lombok.Setter;

public class AddStampsForm {
	@Getter @Setter private String country;
	@Getter @Setter private Integer issueDay;
	@Getter @Setter private Integer issueMonth;
	@Getter @Setter private Integer issueYear;
	@Getter @Setter private String count;
	@Getter @Setter private boolean withoutPerforation;
	@Getter @Setter private String michelNo;
	@Getter @Setter private String scottNo;
	@Getter @Setter private String yvertNo;
	@Getter @Setter private String gibbonsNo;
	@Getter @Setter private String comment;
	@Getter @Setter private String image;
}
