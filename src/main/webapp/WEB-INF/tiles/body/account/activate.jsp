<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>
<%@ page import="ru.mystamps.web.Url" %>
<spring:url var="activateUrl" value="<%= Url.ACTIVATE_ACCOUNT_PAGE %>" />

<c:if test="${justRegisteredUser}">
	<div class="row-fluid span12">
		<div class="alert alert-success text-center span8 offset2">
			<spring:message code="t_activation_sent_message" />
		</div>
	</div>
</c:if>

<h3>
	<spring:message code="t_activation_on_site" />
</h3>

<sec:authorize access="isAuthenticated()">
	<div class="row-fluid span12">
		<div class="alert alert-info text-center span8 offset2">
			<spring:message code="t_already_activated" />
		</div>
	</div>
</sec:authorize>

<sec:authorize access="isAnonymous()">
	<elem:legend />
	<div class="span6 offset3">
		<form:form method="post" action="${activateUrl}" modelAttribute="activateAccountForm" cssClass="form-horizontal">
			
			<elem:field path="login" label="t_login" required="true">
				<form:input path="login" required="required" />
			</elem:field>
			
			<elem:field path="name" label="t_name">
				<form:input path="name" />
			</elem:field>
			
			<elem:field path="password" label="t_password" required="true">
				<form:password path="password" required="required" />
			</elem:field>
			
			<elem:field path="passwordConfirmation" label="t_password_again" required="true">
				<form:password path="passwordConfirmation" required="required" />
			</elem:field>
			
			<elem:field path="activationKey" label="t_activation_key" required="true">
				<form:input path="activationKey" required="required" />
			</elem:field>
			
			<elem:submit label="t_activate" />
			
		</form:form>
	</div>
</sec:authorize>
