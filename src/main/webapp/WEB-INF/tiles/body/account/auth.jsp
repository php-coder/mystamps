<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>
<%@ page import="ru.mystamps.web.Url" %>
<spring:url var="loginUrl" value="<%= Url.LOGIN_PAGE %>" />

<c:if test="${justActivatedUser}">
	<div class="row-fluid span12">
		<div class="alert alert-success text-center span8 offset2">
			<spring:message code="t_activation_successful" />
		</div>
	</div>
</c:if>

<h3>
	<spring:message code="t_authentication_on_site" />
</h3>

<sec:authorize access="isAuthenticated()">
	<div class="row-fluid span12">
		<div class="alert alert-info text-center span8 offset2">
			<spring:message code="t_already_authenticated" />
		</div>
	</div>
</sec:authorize>

<sec:authorize access="isAnonymous()">
	<elem:legend />
	<c:if test="${pageContext.request.queryString eq 'failed' and SPRING_SECURITY_LAST_EXCEPTION ne null}">
		<c:set var="errorMessage" value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
		<c:set var="lastLogin" value="${SPRING_SECURITY_LAST_EXCEPTION.authentication.principal}" />
	</c:if>
	<div class="row-fluid span12">
		<c:if test="${not empty pageScope.errorMessage}">
			<div id="form.errors" class="alert alert-error text-center span6 offset3">
				<c:out value="${pageScope.errorMessage}" />
			</div>
		</c:if>
	</div>
	<div class="row-fluid span12">
		<div class="span6 offset3">
			<form id="authAccountForm" action="${loginUrl}" method="post" class="form-horizontal">
					<div class="control-group">
						<label for="login" class="control-label">
							<span class="field-label"><spring:message code="t_login" /></span>
							<span id="login.required" class="required_field">*</span>
						</label>
						<div class="controls">
							<input type="text" id="login" name="login" required="required" value="<c:out value='${pageScope.lastLogin}' />" />
						</div>
					</div>
					<div class="control-group">
						<label for="password" class="control-label">
							<span class="field-label"><spring:message code="t_password" /></span>
							<span id="password.required" class="required_field">*</span>
						</label>
						<div class="controls">
							<input type="password" id="password" name="password" required="required" value="" />
						</div>
					</div>
					
					<elem:submit label="t_enter" />
			</form>
		</div>
	</div>
</sec:authorize>
