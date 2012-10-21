<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>
<%@ page import="ru.mystamps.web.Url" %>
<spring:url var="loginUrl" value="<%= Url.LOGIN_PAGE %>" />

<h3>
	<spring:message code="t_authentication_on_site" />
</h3>

<sec:authorize access="isAuthenticated()">
	<spring:message code="t_already_authenticated" />
</sec:authorize>

<sec:authorize access="isAnonymous()">
	<elem:legend />
	<c:if test="${pageContext.request.queryString eq 'failed' and SPRING_SECURITY_LAST_EXCEPTION ne null}">
		<c:set var="errorMessage" value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
		<c:set var="lastLogin" value="${SPRING_SECURITY_LAST_EXCEPTION.authentication.principal}" />
	</c:if>
	<div class="generic_form">
		<form id="authAccountForm" action="${loginUrl}" method="post">
			<c:if test="${not empty pageScope.errorMessage}">
				<div id="form.errors" class="error">
					<c:out value="${pageScope.errorMessage}" />
				</div>
			</c:if>
			<table>
				<tr>
					<td>
						<label for="login">
							<spring:message code="t_login" />
						</label>
					</td>
					<td>
						<span id="login.required" class="required_field">*</span>
					</td>
					<td>
						<input type="text" id="login" name="login" required="required" value="<c:out value='${pageScope.lastLogin}' />" />
					</td>
				</tr>
				<tr>
					<td>
						<label for="password">
							<spring:message code="t_password" />
						</label>
					</td>
					<td>
						<span id="password.required" class="required_field">*</span>
					</td>
					<td>
						<input type="password" id="password" name="password" required="required" value="" />
					</td>
				</tr>
				<tr>
					<td></td>
					<td></td>
					<td>
						<input type="submit" value="<spring:message code="t_enter" />" />
					</td>
				</tr>
			</table>
		</form>
	</div>
</sec:authorize>
