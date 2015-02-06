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
package ru.mystamps.web.entity;

import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users_activation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersActivation {
	
	public static final int ACTIVATION_KEY_LENGTH = 10;
	public static final int EMAIL_LENGTH = 255;
	public static final int LANG_LENGTH = 2;
	
	@Id
	@Column(name = "act_key", length = ACTIVATION_KEY_LENGTH)
	private String activationKey;
	
	@Column(length = EMAIL_LENGTH, nullable = false)
	private String email;
	
	@Column(length = LANG_LENGTH, nullable = false)
	private String lang;
	
	@Column(name = "created_at", nullable = false)
	private Date createdAt;
	
	public Locale getLocale() {
		return new Locale(lang);
	}
	
}
