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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "series")
@Getter
@Setter
@SuppressWarnings("PMD.TooManyFields")
public class Series {
	
	public static final int IMAGE_URL_LENGTH = 255;
	public static final int COMMENT_LENGTH   = 255;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne
	private Country country;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "released_at")
	private Date releasedAt;
	
	@Column(nullable = false)
	private Integer quantity;
	
	@Column(nullable = false)
	private Boolean perforated;

	@ManyToMany(fetch = FetchType.EAGER)
	@OrderBy
	private Set<MichelCatalog> michel;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@OrderBy
	private Set<ScottCatalog> scott;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@OrderBy
	private Set<YvertCatalog> yvert;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@OrderBy
	private Set<GibbonsCatalog> gibbons;
	
	@Column(name = "image_url", length = IMAGE_URL_LENGTH)
	private String imageUrl;
	
	@Column(length = COMMENT_LENGTH)
	private String comment;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private Date createdAt;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "updated_by", nullable = false)
	private User updatedBy;
	
	@Setter(AccessLevel.PROTECTED)
	@Version
	@Column(nullable = false)
	private Long version;
	
}
