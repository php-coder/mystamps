<%@ page isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<c:set var="titleCode"><tiles:getAsString name="title" /></c:set>
<c:set var="description"><tiles:getAsString name="errorDescription" /></c:set>
<spring:url var="faviconUrl" value="/favicon.ico" />
<spring:url var="mainCssUrl" value="/static/styles/main.css" />
<spring:url var="errorCssUrl" value="/static/styles/error.css" />

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title><spring:message code="${titleCode}" /></title>
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" href="${mainCssUrl}" />
		<link rel="stylesheet" href="${errorCssUrl}" />
	</head>
	<body>
		<tiles:insertAttribute name="header" />
		<div id="content">
			<table>
				<tr>
					<td id="error-code"><tiles:getAsString name="errorCode" /></td>
					<td id="error-msg">
						<spring:message code="${description}" arguments="<br />" />
					</td>
				</tr>
			</table>
		</div>
		<tiles:insertAttribute name="footer" />
	</body>
</html>
