<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>

<h3>
	<spring:message code="t_add_country_ucfirst" />
</h3>
<elem:legend />
<div class="generic_form">
	<form:form method="post" modelAttribute="addCountryForm">
		<table>
			<tr>
				<td>
					<form:label path="name">
						<spring:message code="t_country" />
					</form:label>
				</td>
				<td>
					<span id="name.required" class="required_field">*</span>
				</td>
				<td>
					<form:input path="name" required="required" autofocus="autofocus" />
				</td>
				<td>
					<form:errors path="name" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td></td>
				<td></td>
				<td>
					<input type="submit" value="<spring:message code="t_add" />" />
				</td>
				<td></td>
			</tr>
		</table>
	</form:form>
</div>
