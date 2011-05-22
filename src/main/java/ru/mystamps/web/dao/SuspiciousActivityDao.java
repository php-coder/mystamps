package ru.mystamps.web.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
	
	public SuspiciousActivity findByPage(final String page) {
		try {
			final SuspiciousActivity activity = (SuspiciousActivity)entityManager
				.createQuery("from SuspiciousActivity sa")
				.getSingleResult()
				;
				//.createQuery("from SuspiciousActivity sa where sa.page = :page")
				//.setParameter("page", page)
			
			return activity;
		} catch (final NoResultException ex) {
			return null;
		}
	}
	
}
