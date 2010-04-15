package ru.mystamps.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class UsersActivation {
	private Logger log = null;
	private DataSource ds = null;
	
	private static final String addRecordQuery =
			"INSERT INTO `users_activation` " +
			"VALUES(?, NOW(), ?)";
	
	public UsersActivation() {
		log = Logger.getRootLogger();
		
		Context env = null;
		try {
			env = (Context)new InitialContext().lookup("java:comp/env");
			ds = (DataSource)env.lookup("jdbc/mystamps");
			
		} catch (NamingException ex) {
			log.error(ex);
		}
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
	 **/
	public void add(String email) {
		
		try {
			Connection conn = ds.getConnection();
			
			try {
				PreparedStatement stat = conn.prepareStatement(addRecordQuery);
				stat.setString(1, email);
				stat.setString(2, generateActivationKey());
				stat.executeUpdate();
				
			} finally {
				conn.close();
			}
			
		} catch (SQLException ex) {
			log.error("add(): " + ex);
		}
	}
	
}
