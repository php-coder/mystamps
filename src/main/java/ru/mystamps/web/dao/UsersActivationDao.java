package ru.mystamps.web.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import ru.mystamps.web.entity.UsersActivation;

@Repository
public class UsersActivationDao {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public UsersActivation findByActivationKey(final String activationKey) {
		return entityManager.find(UsersActivation.class, activationKey);
	}
	
	public void add(final UsersActivation activation) {
		entityManager.persist(activation);
	}
	
	public void delete(final UsersActivation activation) {
		entityManager.remove(activation);
	}
	
}
