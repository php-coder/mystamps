<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>MyStamps: <spring:message code="t_activation_title" /></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
		<link rel="stylesheet" type="text/css" href="/styles/main.css" />
	</head>
	<body>
		<%@ include file="/WEB-INF/segments/header.jspf" %>
		<div id="content">
			<h3>
				<spring:message code="t_activation_on_site" />
			</h3>
			<spring:message code="t_activation_successful" arguments="${authUrl}" />
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
