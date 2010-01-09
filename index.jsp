<%@ include file="/WEB-INF/segments/std.jspf" %>

<c:url value="/register_user.jsp" var="registerUserUrl" />
<c:url value="/auth_user.jsp" var="authUserUrl" />
<c:url value="/restore_password.jsp" var="restorePasswordUrl" />
<c:url value="/add_stamps.jsp" var="addStampsUrl" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>MyStamps: <fmt:message key="t_index_title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
<link rel="stylesheet" type="text/css" href="styles/main.css" />
</head>
<body>
<%@ include file="/WEB-INF/segments/header.jspf" %>
<div id="content">
<fmt:message key="t_you_may" />:
<ul>
	<li><a href="<c:out value="${registerUserUrl}" />"><fmt:message key="t_register_on_site" /></a></li>
	<li><a href="<c:out value="${authUserUrl}" />"><fmt:message key="t_auth_on_site" /></a></li>
	<li><a href="<c:out value="${restorePasswordUrl}" />"><fmt:message key="t_recover_forget_password" /></a></li>
	<li><a href="<c:out value="${addStampsUrl}" />"><fmt:message key="t_add_series" /></a></li>
</ul>
</div>
<%@ include file="/WEB-INF/segments/footer.jspf" %>
</body>
</html>
