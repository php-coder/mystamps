package ru.mystamps.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class UsersActivation {
	private DataSource ds = null;
	
	/**
	 * @see add()
	 **/
	private static final String addRecordQuery =
			"INSERT INTO `users_activation` " +
			"VALUES(?, NOW(), ?)";
	
	/**
	 * @see del()
	 **/
	private static final String delRecordQuery =
			"DELETE FROM `users_activation` " +
			"WHERE `act_key` = ?";
	
	/**
	 * @throws NamingException
	 **/
	public UsersActivation()
		throws NamingException {
		
		Context env = null;
		env = (Context)new InitialContext().lookup("java:comp/env");
		ds = (DataSource)env.lookup("jdbc/mystamps");
	}
	
	/**
	 * Generates activation key.
	 * @todo implement it
	 **/
	private static String generateActivationKey() {
		return "7777744444";
	}
	
	
	/**
	 * Add record about user activation.
	 *
	 * @param String email
	 * @throws SQLException
	 **/
	public void add(String email)
		throws SQLException {
		
		Connection conn = ds.getConnection();
		
		try {
			PreparedStatement stat = conn.prepareStatement(addRecordQuery);
			stat.setString(1, email);
			stat.setString(2, generateActivationKey());
			stat.executeUpdate();
			
		} finally {
			conn.close();
		}
	}
	
	/**
	 * Delete record about user's activation based on key.
	 * @param String actKey activation key
	 **/
	public void del(final String actKey)
		throws SQLException {
		
		Connection conn = ds.getConnection();
		
		try {
			PreparedStatement stat = conn.prepareStatement(delRecordQuery);
			stat.setString(1, actKey);
			stat.executeUpdate();
			
		} finally {
			conn.close();
		}
	}
	
}
