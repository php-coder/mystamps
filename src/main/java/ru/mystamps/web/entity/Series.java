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

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "series")
@Getter
@Setter
@ToString(exclude = "metaInfo")
@EqualsAndHashCode(exclude = { "metaInfo", "images" })
@SuppressWarnings("PMD.TooManyFields")
public class Series {
	
	public static final int COMMENT_LENGTH = 255;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne(optional = false)
	private Category category;
	
	@ManyToOne
	private Country country;
	
	@Column(name = "release_day")
	private Integer releaseDay;
	
	@Column(name = "release_month")
	private Integer releaseMonth;
	
	@Column(name = "release_year")
	private Integer releaseYear;
	
	@Column(nullable = false)
	private Integer quantity;
	
	@Column(nullable = false)
	private Boolean perforated;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy
	private Set<MichelCatalog> michel;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(
			name = Price.VALUE_FIELD_NAME,
			column = @Column(name = "michel_price")
		),
		@AttributeOverride(
			name = Price.CURRENCY_FIELD_NAME,
			column = @Column(name = "michel_currency", length = Price.MAX_CURRENCY_LEGNTH)
		)
	})
	private Price michelPrice;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy
	private Set<ScottCatalog> scott;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(
			name = Price.VALUE_FIELD_NAME,
			column = @Column(name = "scott_price")
		),
		@AttributeOverride(
			name = Price.CURRENCY_FIELD_NAME,
			column = @Column(name = "scott_currency", length = Price.MAX_CURRENCY_LEGNTH)
		)
	})
	private Price scottPrice;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy
	private Set<YvertCatalog> yvert;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(
			name = Price.VALUE_FIELD_NAME,
			column = @Column(name = "yvert_price")
		),
		@AttributeOverride(
			name = Price.CURRENCY_FIELD_NAME,
			column = @Column(name = "yvert_currency", length = Price.MAX_CURRENCY_LEGNTH)
		)
	})
	private Price yvertPrice;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy
	private Set<GibbonsCatalog> gibbons;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(
			name = Price.VALUE_FIELD_NAME,
			column = @Column(name = "gibbons_price")
		),
		@AttributeOverride(
			name = Price.CURRENCY_FIELD_NAME,
			column = @Column(name = "gibbons_currency", length = Price.MAX_CURRENCY_LEGNTH)
		)
	})
	private Price gibbonsPrice;
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = "image_id"))
	@OrderBy
	private Set<Image> images;
	
	@Column(length = COMMENT_LENGTH)
	private String comment;
	
	@Embedded
	private MetaInfo metaInfo; // NOPMD
	
	public Series() {
		metaInfo = new MetaInfo();
	}
	
	public void addImage(Image image) {
		if (images == null) {
			images = new LinkedHashSet<>();
		}
		
		images.add(image);
	}
	
}
