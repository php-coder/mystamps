<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<c:set var="titleCode"><tiles:getAsString name="title" /></c:set>

<spring:url var="faviconUrl" value="/favicon.ico" />
<spring:url var="mainCssUrl" value="/static/styles/main.css" />
<spring:url var="bootstrapCssUrl" value="/public/bootstrap/css/bootstrap.min.css" />
<spring:url var="bootstrapResponsiveCssUrl" value="/public/bootstrap/css/bootstrap-responsive.min.css" />
<spring:url var="bootstrapJsUrl" value="/public/bootstrap/js/bootstrap.min.js" />

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<title>MyStamps: <spring:message code="${titleCode}" /></title>
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" href="${bootstrapCssUrl}" />
		<link rel="stylesheet" href="${bootstrapResponsiveCssUrl}" />
		<link rel="stylesheet" href="${mainCssUrl}" />
	</head>
	<body>
		<div class="row-fluid">
			<tiles:insertAttribute name="header" />
		</div>
		<div class="row-fluid">
			<div id="content" class="span12">
				<tiles:insertAttribute name="body" />
			</div>
		</div>
		<div class="row-fluid">
			<tiles:insertAttribute name="footer" />
		</div>
		
		<!-- Placed at the end of the document so the pages load faster -->
		<script src="${bootstrapJsUrl}"></script>
	</body>
</html>
