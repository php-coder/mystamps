<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>MyStamps: <spring:message code="t_index_title" /></title>
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" type="text/css" href="${mainCssUrl}" />
	</head>
	<body>
		<%@ include file="/WEB-INF/segments/header.jspf" %>
		<div id="content">
			<sec:authorize access="hasRole('ROLE_USER')">
				<spring:message code="t_you_may" />:
				<ul>
					<li><a href="${addSeriesUrl}"><spring:message code="t_add_series" /></a></li>
					<li><a href="${addCountryUrl}"><spring:message code="t_add_country" /></a></li>
				</ul>
			</sec:authorize>
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
