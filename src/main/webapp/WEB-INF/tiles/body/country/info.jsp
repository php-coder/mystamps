<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>

<h3>
	<c:out value="${country.getLocalizedName(pageContext.request.locale)}" />
</h3>

<ul>
	<c:forEach var="series" items="${seriesOfCountry}">
		<li>
			<elem:series-info series="${series}" prependCountry="false" />
		</li>
	</c:forEach>
</ul>
