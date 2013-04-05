<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>

<h3>
	<spring:message code="t_registration_on_site" />
</h3>

<sec:authorize access="isAuthenticated()">
	<spring:message code="t_already_registered" />
</sec:authorize>

<sec:authorize access="isAnonymous()">
	<elem:legend showLinkToAuthPage="true" />
	<div class="generic_form">
		<form:form method="post" modelAttribute="registerAccountForm">
			<table>
				
				<elem:field path="email" label="t_email" required="true">
					<form:input path="email" type="email" required="required" autofocus="autofocus" />
				</elem:field>
				
				<elem:submit label="t_register" />
				
			</table>
		</form:form>
	</div>
</sec:authorize>
