package ru.mystamps.web.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import ru.mystamps.web.entity.User;

@Repository
public class UserDao {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public void add(final User user) {
		entityManager.persist(user);
	}
	
	public User findById(final Integer id) {
		return entityManager.find(User.class, id);
	}
	
	public User findByLogin(final String login) {
		try {
			final User user = (User)entityManager
				.createQuery("from User u where u.login = :login")
				.setParameter("login", login)
				.getSingleResult()
				;
			
			return user;
		} catch (final NoResultException ex) {
			return null;
		}
	}
	
}
