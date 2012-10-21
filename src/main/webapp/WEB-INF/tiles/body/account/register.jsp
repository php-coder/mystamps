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
				<tr>
					<td>
						<form:label path="email">
							<spring:message code="t_email" />
						</form:label>
					</td>
					<td>
						<span id="email.required" class="required_field">*</span>
					</td>
					<td>
						<form:input path="email" type="email" required="required" autofocus="autofocus" />
					</td>
					<td>
						<form:errors path="email" cssClass="error" />
					</td>
				</tr>
				<tr>
					<td></td>
					<td></td>
					<td>
						<input type="submit" value="<spring:message code="t_register" />" />
					</td>
					<td></td>
				</tr>
			</table>
		</form:form>
	</div>
</sec:authorize>
