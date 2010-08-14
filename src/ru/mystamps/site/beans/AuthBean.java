package ru.mystamps.site.beans;

import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javax.naming.NamingException;

import lombok.Getter;
import lombok.Setter;

import ru.mystamps.db.Users;
import ru.mystamps.db.SuspiciousActivities;
import ru.mystamps.site.utils.Messages;

public class AuthBean {
	@Getter @Setter private String login;
	@Getter @Setter private String password;
	@Getter @Setter private UserBean user;
	
	/**
	 * @throws NamingException
	 * @throws SQLException
	 **/
	public String authUser()
		throws NamingException, SQLException {
		final Users users = new Users();
		final Long userId = users.auth(getLogin(), getPassword());
		final UserBean userBean = users.getUserById(userId);
		
		if (userId == null || userBean == null) {
			showAuthError();
			logAuthError();
			return null;
		
		} else {
			user.setUid(userBean.getUid());
			user.setLogin(userBean.getLogin());
			user.setName(userBean.getName());
		}
		
		return "index";
	}
	
	/**
	 * Invalidate user's session.
	 **/
	public String logout() {
		
		final HttpSession session =
			(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		
		if (session != null) {
			session.invalidate();
		}
		
		user.setUid(-1L);
		user.setName(null);
		user.setLogin(null);
		
		return null;
	}
	
	/**
	 * Add FacesMessage about auth error.
	 **/
	private void showAuthError() {
		final FacesContext context =
			FacesContext.getCurrentInstance();
		
		final String msg =
			Messages.getTranslation(context, "tv_wrong_login_password");
		
		final FacesMessage facesMessage =
			new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
		
		context.addMessage(null, facesMessage);
	}
	
	/**
	 * Log event about failed authentication.
	 * @throws NamingException
	 * @throws SQLException
	 **/
	private void logAuthError()
		throws NamingException, SQLException {
		
		final FacesContext context =
			FacesContext.getCurrentInstance();
		
		final HttpServletRequest request =
			(HttpServletRequest)context.getExternalContext().getRequest();
		
		final SuspiciousActivities events =
			new SuspiciousActivities();
		
		// TODO: log more info (login/password pair for example) (#59)
		// TODO: sanitize all user's values (#60)
		final String page    = request.getRequestURI();
		final Long uid       = (user.getUid() > 0 ? user.getUid() : null);
		final String ip      = request.getRemoteAddr();
		final String referer = request.getHeader("referer");
		final String agent   = request.getHeader("user-agent");
		
		// log failed authentication attempt
		events.logEvent("AuthenticationFailed", page, uid, ip, referer, agent);
	}
	
}
