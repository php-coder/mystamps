<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page import="ru.mystamps.web.Url" %>
<spring:url var="authUrl" value="<%= Url.AUTHENTICATION_PAGE %>" />

<h3>
	<spring:message code="t_activation_on_site" />
</h3>
<spring:message code="t_activation_successful" arguments="${authUrl}" />
