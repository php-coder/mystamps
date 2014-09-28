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
package ru.mystamps.web.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "collections")
@Getter
@Setter
@ToString
public class Collection {
	
	public static final int SLUG_LENGTH = User.NAME_LENGTH;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User owner;
	
	@ManyToMany
	@JoinTable(joinColumns = @JoinColumn(name = "collection_id"))
	private Set<Series> series;
	
	@Column(length = SLUG_LENGTH, nullable = false)
	private String slug;
	
}
