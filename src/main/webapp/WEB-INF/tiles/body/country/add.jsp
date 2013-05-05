<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>

<h3>
	<spring:message code="t_add_country_ucfirst" />
</h3>
<elem:legend />
<div class="span6 offset3">
	<form:form method="post" modelAttribute="addCountryForm" cssClass="form-horizontal">
		
		<elem:field path="name" label="t_country" required="true">
			<form:input path="name" required="required" autofocus="autofocus" />
		</elem:field>
		
		<elem:submit label="t_add" />
		
	</form:form>
</div>
