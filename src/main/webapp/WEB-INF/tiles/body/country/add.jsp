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
			
			<elem:field path="name" label="t_country" required="true">
				<form:input path="name" required="required" autofocus="autofocus" />
			</elem:field>
			
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
