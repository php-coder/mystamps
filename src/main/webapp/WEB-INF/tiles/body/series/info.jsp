<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<table>
	<c:if test="${not empty series.imageUrl}">
		<tr>
			<td colspan="2" align="center">
				<spring:url var="imgUrl" value="${series.imageUrl}" />
				<img id="image" src="${imgUrl}" />
			</td>
		</tr>
	</c:if>
	<c:if test="${not empty series.country}">
		<tr>
			<td>
				<spring:message code="t_country" />
			</td>
			<td id="country_name">
				<c:out value="${series.country.name}" />
			</td>
		</tr>
	</c:if>
	<c:if test="${not empty series.releasedAt}">
		<tr>
			<td>
				<spring:message code="t_issue_year" />
			</td>
			<td id="issue_year">
				<fmt:formatDate value="${series.releasedAt}" pattern="yyyy" />
			</td>
		</tr>
	</c:if>
		<tr>
			<td>
				<spring:message code="t_quantity" />
			</td>
			<td id="quantity">
				<c:out value="${series.quantity}" />
			</td>
		</tr>
		<tr>
			<td>
				<spring:message code="t_perforated" />
			</td>
			<td id="perforated">
				<c:choose>
					<c:when test="${series.perforated}">
						<spring:message code="t_yes" />
					</c:when>
					<c:otherwise>
						<spring:message code="t_no" />
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
	<c:if test="${not empty michelNumbers}">
		<tr>
			<td>
				<spring:message code="t_michel" />
			</td>
			<td id="michel_catalog_info">
				#<c:out value="${michelNumbers}" />
			</td>
		</tr>
	</c:if>
	<c:if test="${not empty scottNumbers}">
		<tr>
			<td>
				<spring:message code="t_scott" />
			</td>
			<td id="scott_catalog_info">
				#<c:out value="${scottNumbers}" />
			</td>
		</tr>
	</c:if>
	<c:if test="${not empty yvertNumbers}">
		<tr>
			<td>
				<spring:message code="t_yvert" />
			</td>
			<td id="yvert_catalog_info">
				#<c:out value="${yvertNumbers}" />
			</td>
		</tr>
	</c:if>
	<c:if test="${not empty gibbonsNumbers}">
		<tr>
			<td>
				<spring:message code="t_sg" />
			</td>
			<td id="gibbons_catalog_info">
				#<c:out value="${gibbonsNumbers}" />
			</td>
		</tr>
	</c:if>
	<c:if test="${not empty series.comment}">
		<tr>
			<td>
				<spring:message code="t_comment" />
			</td>
			<td id="comment">
				<c:out value="${series.comment}" />
			</td>
		</tr>
	</c:if>
</table>
