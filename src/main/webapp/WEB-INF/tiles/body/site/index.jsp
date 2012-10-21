<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ page import="ru.mystamps.web.Url" %>
<spring:url var="addSeriesUrl" value="<%= Url.ADD_SERIES_PAGE %>" />
<spring:url var="addCountryUrl" value="<%= Url.ADD_COUNTRY_PAGE %>" />

<sec:authorize access="hasRole('ROLE_USER')">
	<spring:message code="t_you_may" />:
	<nav>
		<ul>
			<li><a href="${addSeriesUrl}"><spring:message code="t_add_series" /></a></li>
			<li><a href="${addCountryUrl}"><spring:message code="t_add_country" /></a></li>
		</ul>
	</nav>
</sec:authorize>
