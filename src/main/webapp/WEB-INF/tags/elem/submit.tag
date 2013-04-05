<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ attribute name="label" required="true" rtexprvalue="true" %>

<tr>
	<td></td>
	<td></td>
	<td>
		<input type="submit" value="<spring:message code="${label}" />" />
	</td>
	<td></td>
</tr>
