<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<spring:url var="authUrl" value="<%= ru.mystamps.web.Url.AUTHENTICATION_PAGE %>" />

<%@ attribute name="showLinkToAuthPage" required="false" rtexprvalue="true" %>
<div class="hint">
	<c:if test="${showLinkToAuthPage}">
		<span class="hint_item">
			<spring:message
				code="t_if_you_already_registered"
				arguments="${authUrl}"
			/>
		</span>
		<br />
	</c:if>
	<span class="hint_item">
		<spring:message
			code="t_required_fields_legend"
			arguments="<span class=\"required_field\">*</span>"
		/>
	</span>
</div>
