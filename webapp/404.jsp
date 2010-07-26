<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" isErrorPage="true" %>
<jsp:useBean id="suspEvent" class="ru.mystamps.site.beans.SuspiciousEventBean">
<jsp:setProperty name="suspEvent" property="type" value="PageNotFound" />
<jsp:setProperty name="suspEvent" property="page" value="${requestScope[\"javax.servlet.error.request_uri\"]}" />
<jsp:setProperty name="suspEvent" property="ip" value="${pageContext.request.remoteAddr}" />
<jsp:setProperty name="suspEvent" property="refererPage" value="${fn:escapeXml(header[\"referer\"])}" />
<jsp:setProperty name="suspEvent" property="userAgent" value="${fn:escapeXml(header[\"user-agent\"])}" />
</jsp:useBean>
<jsp:getProperty name="suspEvent" property="logResult" />
<fmt:setBundle basename="ru.mystamps.i18n.Messages" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title><fmt:message key="t_404_title" /></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
		<link rel="stylesheet" type="text/css" href="/styles/main.css" />
		<link rel="stylesheet" type="text/css" href="/styles/error.css" />
	</head>
	<body>
		<table id="header">
			<tr>
				<td>
					<div id="logo">
						<fmt:message key="t_my_stamps" />
					</div>
				</td>
				<td id="user_bar"></td>
			</tr>
		</table>
		</table>
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
		<div id="footer">
			&copy;
			<a href="mailto:slava.semushin@gmail.com" title="<fmt:message key="t_write_email" />">
				<fmt:message key="t_slava_semushin" />
			</a>, 2009-2010
		</div>
	</body>
</html>
