<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ attribute name="label" required="true" rtexprvalue="true" %>

<div class="form-actions">
	<input type="submit" class="btn btn-primary" value="<spring:message code="${label}" />" />
</div>
