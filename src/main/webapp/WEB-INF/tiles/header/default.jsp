<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ page import="ru.mystamps.web.Url" %>
<spring:url var="authUrl" value="<%= Url.AUTHENTICATION_PAGE %>" />
<spring:url var="logoutUrl" value="<%= Url.LOGOUT_PAGE %>" />
<spring:url var="registerUrl" value="<%= Url.REGISTRATION_PAGE %>" />

<table id="header">
	<tr>
		<td>
			<div id="logo">
				<spring:message code="t_my_stamps" />
			</div>
		</td>
		<td id="user_bar">
			<ul>
				<li>
					
					<sec:authorize access="isAuthenticated()">
						<sec:authentication property="principal.user.name" />
					</sec:authorize>
					
					<sec:authorize access="isAnonymous()">
						<a href="${authUrl}">
							<spring:message code="t_enter" />
						</a>
					</sec:authorize>
					
				</li>
				<li>
					
					<sec:authorize access="isAuthenticated()">
						<a href="${logoutUrl}">
							<spring:message code="t_logout" />
						</a>
					</sec:authorize>
					
					<sec:authorize access="isAnonymous()">
						<a href="${registerUrl}">
							<spring:message code="t_register" />
						</a>
					</sec:authorize>
					
				</li>
			</ul>
		</td>
	</tr>
</table>
