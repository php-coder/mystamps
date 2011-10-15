/*
 * Copyright (C) 2011 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import ru.mystamps.web.entity.Country;

@Repository
public class CountryDao {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public Integer add(final Country country) {
		entityManager.persist(country);
		return country.getId();
	}
	
	public List<Country> findAll() {
		final List<Country> result = (List<Country>)entityManager
			.createQuery("from Country")
			.getResultList();
		
		return result;
	}
	
	public Country findById(final Integer id) {
		return entityManager.find(Country.class, id);
	}
	
	public Country findByName(final String name) {
		try {
			final Country country = (Country)entityManager
				.createQuery("from Country as c where c.name = :name")
				.setParameter("name", name)
				.getSingleResult();
			
			return country;
			
		} catch (final NoResultException ex) {
			return null;
		}
	}
	
}
