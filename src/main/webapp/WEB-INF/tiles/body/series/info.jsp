<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>

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
	<c:if test="${not empty michelNumbers or not empty series.michelPrice}">
		<tr>
			<td>
				<spring:message code="t_michel" />
			</td>
			<td id="michel_catalog_info">
				<elem:catalog-numbers-with-price
					catalogNumbers="${michelNumbers}"
					catalogPrice="${series.michelPrice}"
				/>
			</td>
		</tr>
	</c:if>
	<c:if test="${not empty scottNumbers or not empty series.scottPrice}">
		<tr>
			<td>
				<spring:message code="t_scott" />
			</td>
			<td id="scott_catalog_info">
				<elem:catalog-numbers-with-price
					catalogNumbers="${scottNumbers}"
					catalogPrice="${series.scottPrice}"
				/>
			</td>
		</tr>
	</c:if>
	<c:if test="${not empty yvertNumbers or not empty series.yvertPrice}">
		<tr>
			<td>
				<spring:message code="t_yvert" />
			</td>
			<td id="yvert_catalog_info">
				<elem:catalog-numbers-with-price
					catalogNumbers="${yvertNumbers}"
					catalogPrice="${series.yvertPrice}"
				/>
			</td>
		</tr>
	</c:if>
	<c:if test="${not empty gibbonsNumbers or not empty series.gibbonsPrice}">
		<tr>
			<td>
				<spring:message code="t_sg" />
			</td>
			<td id="gibbons_catalog_info">
				<elem:catalog-numbers-with-price
					catalogNumbers="${gibbonsNumbers}"
					catalogPrice="${series.gibbonsPrice}"
				/>
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
