<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>MyStamps: <spring:message code="t_series_info" /></title>
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" type="text/css" href="${mainCssUrl}" />
	</head>
	<body>
		<%@ include file="/WEB-INF/segments/header.jspf" %>
		<div id="content">
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
							<spring:message code="t_michel_no" />
						</td>
						<td id="michel_numbers">
							<c:out value="${michelNumbers}" />
						</td>
					</tr>
				</c:if>
				<c:if test="${not empty scottNumbers}">
					<tr>
						<td>
							<spring:message code="t_scott_no" />
						</td>
						<td id="scott_numbers">
							<c:out value="${scottNumbers}" />
						</td>
					</tr>
				</c:if>
				<c:if test="${not empty yvertNumbers}">
					<tr>
						<td>
							<spring:message code="t_yvert_no" />
						</td>
						<td id="yvert_numbers">
							<c:out value="${yvertNumbers}" />
						</td>
					</tr>
				</c:if>
				<c:if test="${not empty gibbonsNumbers}">
					<tr>
						<td>
							<spring:message code="t_sg_no" />
						</td>
						<td id="gibbons_numbers">
							<c:out value="${gibbonsNumbers}" />
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
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
