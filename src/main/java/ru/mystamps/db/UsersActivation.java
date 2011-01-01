package ru.mystamps.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import lombok.Cleanup;

import org.springframework.stereotype.Repository;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

@Repository
public class UsersActivation {
	private final Logger log = Logger.getRootLogger();
	
	@Resource
	private DataSource ds;
	
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
	 * @see actKeyExists()
	 **/
	private static final String checkActKeyQuery =
			"SELECT COUNT(*) AS keys_count " +
			"FROM `users_activation` " +
			"WHERE `act_key` = ?";
	
	/**
	 * Generates activation key.
	 * @return string which contains numbers and letters in lower case
	 *         in 10 characters length
	 **/
	private static String generateActivationKey() {
		// Length of users_activation.act_key field in database equals
		// to 10 characters (see mystamps.sql)
		return RandomStringUtils.randomAlphanumeric(10).toLowerCase();
	}
	
	
	/**
	 * Add record about user activation.
	 *
	 * @param String email
	 * @throws SQLException
	 **/
	public void add(final String email)
		throws SQLException {
		
		@Cleanup
		final Connection conn = ds.getConnection();
		
		@Cleanup
		final PreparedStatement stat =
			conn.prepareStatement(addRecordQuery);
		
		stat.setString(1, email);
		/// @todo: get rid of hardcoded act key
		stat.setString(2, (email.equals("coder@rock.home") ? "7777744444" : generateActivationKey()));
		stat.executeUpdate();
	}
	
	/**
	 * Delete record about user's activation based on key.
	 * @param String actKey activation key
	 **/
	public void del(final String actKey)
		throws SQLException {
		
		@Cleanup
		final Connection conn = ds.getConnection();
		
		@Cleanup
		final PreparedStatement stat =
			conn.prepareStatement(delRecordQuery);
		
		stat.setString(1, actKey);
		stat.executeUpdate();
	}
	
	/**
	 * Check if activation key exists.
	 * @param String actKey activation key
	 * @throws SQLException
	 **/
	public boolean actKeyExists(final String actKey)
		throws SQLException {
		
		boolean result = false;
		
		@Cleanup
		final Connection conn = ds.getConnection();
		
		@Cleanup
		final PreparedStatement stat =
			conn.prepareStatement(checkActKeyQuery);
		
		stat.setString(1, actKey);

		@Cleanup
		final ResultSet rs = stat.executeQuery();

		if (rs.next()) {
			final int actKeys = rs.getInt("keys_count");
			if (actKeys > 0) {
				result = true;
			}
			log.debug("found " + actKeys + " for key " + actKey);

		} else {
			log.warn("actKeyExists(" + actKey +"): next() return false");
		}
		
		return result;
	}
	
}
