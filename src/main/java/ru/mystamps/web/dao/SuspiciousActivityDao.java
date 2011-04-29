package ru.mystamps.web.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import ru.mystamps.web.entity.SuspiciousActivity;

@Repository
public class SuspiciousActivityDao {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public void add(final SuspiciousActivity activity) {
		entityManager.persist(activity);
	}
	
}
