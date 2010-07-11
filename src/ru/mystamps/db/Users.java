package ru.mystamps.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import ru.mystamps.site.beans.UserBean;

public class Users {
	private Logger log = null;
	private DataSource ds = null;
	
	/**
	 * @see add()
	 **/
	private static final String addUserQuery =
			"INSERT INTO users(" +
				"login, " +
				"name, " +
				"email, " +
				"registered_at, " +
				"activated_at, " +
				"hash, " +
				"salt) " +
			"SELECT " +
				"?, " +
				"?, " +
				"ua.email, " +
				"ua.registered_at, " +
				"NOW(), " +
				"SHA1(?), " +
				"? " +
				"FROM users_activation AS ua " +
				"WHERE ua.act_key = ?";
	
	/**
	 * @see auth()
	 **/
	private static final String getUserIdByCredentialsQuery =
				"SELECT id " +
				"FROM users " +
				"WHERE login = ? AND hash = SHA1(CONCAT(salt, ?))";
	
	/**
	 * @see getUserById()
	 **/
	private static final String getUserByIdQuery =
				"SELECT login, name " +
				"FROM users " +
				"WHERE id = ?";
	
	/**
	 * @see loginExists()
	 **/
	private static final String checkUserQuery =
			"SELECT COUNT(*) " +
			"FROM users " +
			"WHERE login = ?";
	
	/**
	 * @throws NamingException
	 **/
	public Users()
		throws NamingException {
		
		log = Logger.getRootLogger();
		
		Context env = null;
		env = (Context)new InitialContext().lookup("java:comp/env");
		ds = (DataSource)env.lookup("jdbc/mystamps");
	}
	
	/**
	 * Generate password salt.
	 * @todo implement it
	 **/
	private String generateSalt() {
		return "salt";
	}
	
	/**
	 * Add record about user.
	 *
	 * @param String login user's login
	 * @param String password user's password
	 * @param String name user's name
	 * @param String actKey activation key
	 * @throws SQLException
	 **/
	public void add(final String login, final String password, final String name, final String actKey)
		throws SQLException {
		
		Connection conn = ds.getConnection();
		
		final String salt = generateSalt();
		final String hash = salt + password;
		
		try {
			PreparedStatement stat = conn.prepareStatement(addUserQuery);
			stat.setString(1, login);
			stat.setString(2, name);
			stat.setString(3, hash);
			stat.setString(4, salt);
			stat.setString(5, actKey);
			stat.executeUpdate();
			
		} finally {
			conn.close();
		}
	}
	
	/**
	 * Get user id based on login/password pair.
	 * @param String login user's login
	 * @param String password user's password
	 * @throws SQLException
	 **/
	public Long auth(final String login, final String password)
		throws SQLException {
		
		Connection conn = ds.getConnection();
		Long userId = null;
		
		try {
			PreparedStatement stat = conn.prepareStatement(getUserIdByCredentialsQuery);
			stat.setString(1, login);
			stat.setString(2, password);
			
			ResultSet rs = stat.executeQuery();
			
			if (rs.next()) {
				userId = rs.getLong("id");
				log.debug("auth(" + login + ", " + password + ") = " + userId + ": SUCCESS");
				
			} else {
				log.debug("auth(" + login + ", " + password + "): FAIL");
			}
		
		} finally {
			conn.close();
		}
		
		return userId;
	}
	
	/**
	 * Get bean with info about user based on uid.
	 * @param Long userId uid
	 * @return null if user not exists or userId is null
	 **/
	public UserBean getUserById(final Long userId)
		throws SQLException {
		
		if (userId == null) {
			return null;
		}
		
		Connection conn = ds.getConnection();
		UserBean user = null;
		
		try {
			PreparedStatement stat = conn.prepareStatement(getUserByIdQuery);
			stat.setLong(1, userId);
			
			ResultSet rs = stat.executeQuery();
			
			if (rs.next()) {
				user = new UserBean();
				user.setUid(userId);
				user.setLogin(rs.getString("login"));
				user.setName(rs.getString("name"));
				log.debug("getUserById(" + userId + "): " + user.getLogin() + "/" + user.getName());
				
			} else {
				log.error("getUserById(" + userId + "): cannot get user!");
			}
		
		} finally {
			conn.close();
		}
		
		return user;
	}
	
	/**
	 * Check if login already exists.
	 * @param String login user's login
	 * @throws SQLException
	 **/
	public boolean loginExists(final String login)
		throws SQLException {
		
		Connection conn = ds.getConnection();
		boolean result = false;
		
		try {
			PreparedStatement stat = conn.prepareStatement(checkUserQuery);
			stat.setString(1, login);
			
			ResultSet rs = stat.executeQuery();
			
			if (rs.next()) {
				int usersCounter = rs.getInt(1);
				if (usersCounter > 0) {
					result = true;
				}
				log.debug("found " + usersCounter + " for login " + login);
				
			} else {
				log.warn("loginExists(" + login +"): next() return false");
			}
		
		} finally {
			conn.close();
		}
		
		return result;
	}
	
}
