<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<%@ attribute name="path" required="true" rtexprvalue="true" %>
<%@ attribute name="label" required="true" rtexprvalue="true" %>
<%@ attribute name="required" required="false" rtexprvalue="true" type="java.lang.Boolean" %>

<spring:bind path="${path}">
	<c:set var="hasError" value="${status.error}" />
	
	<div class="control-group ${hasError ? 'error' : ''}">
		<form:label path="${path}" cssClass="control-label">
			<span class="field-label">
				<spring:message code="${label}" />
			</span>
			<c:if test="${required}">
				<span id="${path}.required" class="required_field">*</span>
			</c:if>
		</form:label>
		<div class="controls">
			<jsp:doBody />
			<form:errors path="${path}" cssClass="help-inline" />
		</div>
	</div>
</spring:bind>
