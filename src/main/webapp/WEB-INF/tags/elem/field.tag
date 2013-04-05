<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<%@ attribute name="path" required="true" rtexprvalue="true" %>
<%@ attribute name="label" required="true" rtexprvalue="true" %>
<%@ attribute name="required" required="false" rtexprvalue="true" type="java.lang.Boolean" %>

<tr>
	<td>
		<form:label path="${path}">
			<spring:message code="${label}" />
		</form:label>
	</td>
	<td>
		<c:if test="${required}">
			<span id="${path}.required" class="required_field">*</span>
		</c:if>
	</td>
	<td>
		<jsp:doBody />
	</td>
	<td>
		<form:errors path="${path}" cssClass="error" />
	</td>
</tr>
