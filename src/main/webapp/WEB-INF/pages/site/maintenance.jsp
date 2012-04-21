<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>MyStamps: <spring:message code="t_maintenance_title" /></title>
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" type="text/css" href="${mainCssUrl}" />
		<link rel="stylesheet" type="text/css" href="${errorCssUrl}" />
	</head>
	<body>
		<%@ include file="/WEB-INF/segments/header.jspf" %>
		<div id="content">
			<div id="error-msg">
				<spring:message code="t_maintenance_on_site" arguments="<br />" />
			</div>
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
