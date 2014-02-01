<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ page import="ru.mystamps.web.Url" %>
<spring:url var="addSeriesUrl" value="<%= Url.ADD_SERIES_PAGE %>" />
<spring:url var="addCountryUrl" value="<%= Url.ADD_COUNTRY_PAGE %>" />

<div class="span10">
	<sec:authorize access="hasAnyAuthority('CREATE_COUNTRY', 'CREATE_SERIES')">
		<spring:message code="t_you_may" />:
		<nav>
			<ul>
				<sec:authorize access="hasAuthority('CREATE_SERIES')">
					<li><a href="${addSeriesUrl}"><spring:message code="t_add_series" /></a></li>
				</sec:authorize>
				<sec:authorize access="hasAuthority('CREATE_COUNTRY')">
					<li><a href="${addCountryUrl}"><spring:message code="t_add_country" /></a></li>
				</sec:authorize>
			</ul>
		</nav>
	</sec:authorize>
</div>

<div class="span2">
	<p>
		<strong><spring:message code="t_in_db" />:</strong>
	</p>
	<p>
		<spring:message code="t_countries_amount" />: ${countryCounter}
	</p>
	<p>
		<spring:message code="t_series_amount" />: ${seriesCounter}
	</p>
</div>
