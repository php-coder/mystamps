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
