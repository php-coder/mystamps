<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ tag import="ru.mystamps.web.Url" %>

<%@ attribute name="series" required="true" rtexprvalue="true" type="ru.mystamps.web.service.dto.SeriesInfoDto" %>
<%@ attribute name="prependCategory" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="prependCountry" required="false" rtexprvalue="true" type="java.lang.Boolean" %>

<c:if test="${prependCategory eq null or prependCategory}">
	<spring:url var="categoryUrl" value="<%= Url.INFO_CATEGORY_PAGE %>">
		<spring:param name="id" value="${series.category.id}" />
	</spring:url>
	
	<a href="${categoryUrl}">
		<c:out value="${series.category.name}" />
	</a>&raquo;
</c:if>

<c:if test="${(prependCountry eq null or prependCountry) and series.country.id ne null}">
	<spring:url var="countryUrl" value="<%= Url.INFO_COUNTRY_PAGE %>">
		<spring:param name="id" value="${series.country.id}" />
	</spring:url>
	
	<a href="${countryUrl}">
		<c:out value="${series.country.name}" />
	</a>&raquo;
</c:if>

<spring:url var="seriesInfoUrl" value="<%= Url.INFO_SERIES_PAGE %>">
	<spring:param name="id" value="${series.id}" />
</spring:url>

<a href="${seriesInfoUrl}">
	<c:if test="${not empty series.releasedAt}">
		<fmt:formatDate value="${series.releasedAt}" pattern="yyyy, " />
	</c:if>
	
	<c:out value="${series.quantity}" />&nbsp;<spring:message code="t_items" />
	
	<c:if test="${not series.perforated}">
		(<spring:message code="t_wo_perforation_short" />)
	</c:if>
</a>
