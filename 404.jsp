<%@ page isErrorPage="true" contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="suspEvent" class="ru.mystamps.site.events.SuspiciousEventBean">
<jsp:setProperty name="suspEvent" property="type" value="PageNotFound" />
<jsp:setProperty name="suspEvent" property="page" value="${pageContext.request.servletPath}" />
<jsp:setProperty name="suspEvent" property="ip" value="${pageContext.request.remoteAddr}" />
<jsp:setProperty name="suspEvent" property="refererPage" value="${headerValues[\"referer\"][0]}" />
<jsp:setProperty name="suspEvent" property="userAgent" value="${headerValues[\"user-agent\"][0]}" />
</jsp:useBean>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>404: страница не найдена</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
<link rel="stylesheet" type="text/css" href="styles/main.css" />
<link rel="stylesheet" type="text/css" href="styles/error.css" />
</head>
<body>
<%@ include file="/WEB-INF/segments/header.jspf" %>
<div id="content">
	<table>
		<tr>
			<td id="error-code">404</td>
			<td id="error-msg">Запрашиваемая<br />вами страница<br />не найдена</td>
		</tr>
	</table>
</div>
<%@ include file="/WEB-INF/segments/footer.jspf" %>
</body>
</html>
