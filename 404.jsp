<%@ include file="/WEB-INF/segments/std.jspf" %>
<%@ page isErrorPage="true" %>
<jsp:useBean id="suspEvent" class="ru.mystamps.site.beans.SuspiciousEventBean">
<jsp:setProperty name="suspEvent" property="type" value="PageNotFound" />
<jsp:setProperty name="suspEvent" property="page" value="${requestScope[\"javax.servlet.error.request_uri\"]}" />
<jsp:setProperty name="suspEvent" property="ip" value="${pageContext.request.remoteAddr}" />
<jsp:setProperty name="suspEvent" property="refererPage" value="${header[\"referer\"]}" />
<jsp:setProperty name="suspEvent" property="userAgent" value="${header[\"user-agent\"]}" />
</jsp:useBean>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><fmt:message key="t_404_title" /></title>
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
			<td id="error-msg">
				<fmt:message key="t_404_description">
					<fmt:param value ="<br />" />
					<fmt:param value ="<br />" />
				</fmt:message>
			</td>
		</tr>
	</table>
</div>
<%@ include file="/WEB-INF/segments/footer.jspf" %>
</body>
</html>
