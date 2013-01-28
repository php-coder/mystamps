<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<c:set var="titleCode"><tiles:getAsString name="title" /></c:set>
<spring:url var="faviconUrl" value="/favicon.ico" />
<spring:url var="mainCssUrl" value="/static/styles/main.css" />

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>MyStamps: <spring:message code="${titleCode}" /></title>
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" href="${mainCssUrl}" />
	</head>
	<body>
		<tiles:insertAttribute name="header" />
		<div id="content">
			<tiles:insertAttribute name="body" />
		</div>
		<tiles:insertAttribute name="footer" />
	</body>
</html>
