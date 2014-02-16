<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>

<h3>
	<c:out value="${category.name}" />
</h3>

<ul>
	<c:forEach var="series" items="${seriesOfCategory}">
		<li>
			<elem:series-info series="${series}" prependCategory="false" />
		</li>
	</c:forEach>
</ul>
