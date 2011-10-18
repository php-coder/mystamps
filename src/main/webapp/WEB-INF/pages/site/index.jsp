<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>MyStamps: <spring:message code="t_index_title" /></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" type="text/css" href="${mainCssUrl}" />
	</head>
	<body>
		<%@ include file="/WEB-INF/segments/header.jspf" %>
		<div id="content">
			<spring:message code="t_you_may" />:
			<ul>
				<li><a href="${restorePasswordUrl}"><spring:message code="t_recover_forget_password" /></a></li>
				<li><a href="${addStampsUrl}"><spring:message code="t_add_series" /></a></li>
				<li><a href="${addCountryUrl}"><spring:message code="t_add_country" /></a></li>
			</ul>
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
