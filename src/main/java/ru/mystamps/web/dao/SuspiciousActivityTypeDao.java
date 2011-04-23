package ru.mystamps.web.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import ru.mystamps.web.entity.SuspiciousActivityType;

@Repository
public class SuspiciousActivityTypeDao {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public SuspiciousActivityType findByName(final String name) {
		final SuspiciousActivityType type = (SuspiciousActivityType)entityManager
			.createQuery("from SuspiciousActivityType sat where sat.name = :name")
			.setParameter("name", name)
			.getSingleResult()
			;
		
		return type;
	}
	
}
