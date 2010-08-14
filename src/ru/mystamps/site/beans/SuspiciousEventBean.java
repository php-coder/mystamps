package ru.mystamps.site.beans;

import java.sql.SQLException;

import javax.naming.NamingException;

import lombok.Getter;
import lombok.Setter;

import ru.mystamps.db.SuspiciousActivities;

public class SuspiciousEventBean {
	
	@Getter @Setter private String type;
	@Getter @Setter private String page;
	@Getter @Setter private String ip;
	@Getter @Setter private String refererPage;
	@Getter @Setter private String userAgent;
	
	private final SuspiciousActivities act;
	
	/**
	 * @throws NamingException
	 **/
	public SuspiciousEventBean()
		throws NamingException {
		act = new SuspiciousActivities();
	}
	
	/**
	 * Log event about suspicious activity to database.
	 * @throws SQLException
	 **/
	public String getLogResult()
		throws SQLException {
		act.logEvent(getType(), getPage(), getIp(), getRefererPage(), getUserAgent());
		return "";
	}
	
}

