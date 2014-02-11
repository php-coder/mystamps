<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>

<h3>
	<spring:message code="t_create_category_ucfirst" />
</h3>
<elem:legend />
<div class="span6 offset3">
	<form:form method="post" modelAttribute="addCategoryForm" cssClass="form-horizontal">
		
		<elem:field path="name" label="t_category_on_english" required="true">
			<form:input path="name" required="required" />
		</elem:field>
		
		<elem:field path="nameRu" label="t_category_on_russian" required="true">
			<form:input path="nameRu" required="required" />
		</elem:field>
		
		<elem:submit label="t_add" />
		
	</form:form>
</div>
