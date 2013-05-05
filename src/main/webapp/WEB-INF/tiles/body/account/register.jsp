<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>

<h3>
	<spring:message code="t_registration_on_site" />
</h3>

<sec:authorize access="isAuthenticated()">
	<div class="row-fluid span12">
		<div class="alert alert-info text-center span8 offset2">
			<spring:message code="t_already_registered" />
		</div>
	</div>
</sec:authorize>

<sec:authorize access="isAnonymous()">
	<elem:legend showLinkToAuthPage="true" />
	<div class="span6 offset3">
		<form:form method="post" modelAttribute="registerAccountForm" cssClass="form-horizontal">
			
			<elem:field path="email" label="t_email" required="true">
				<form:input path="email" type="email" required="required" autofocus="autofocus" />
			</elem:field>
			
			<elem:submit label="t_register" />
			
		</form:form>
	</div>
</sec:authorize>
