<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url value="/register_user.jsp" var="registerUserUrl" />
<c:url value="/auth_user.jsp" var="authUserUrl" />
<c:url value="/restore_password.jsp" var="restorePasswordUrl" />
<c:url value="/add_stamps.jsp" var="addStampsUrl" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>MyStamps: создай свою виртуальную коллекцию!</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
<link rel="stylesheet" type="text/css" href="styles/main.css" />
</head>
<body>
<%@ include file="/WEB-INF/segments/header.jspf" %>
<div id="content">
Вы можете:
<ul>
	<li><a href="<c:out value="${registerUserUrl}" />">зарегистрироваться на сайте</a></li>
	<li><a href="<c:out value="${authUserUrl}" />">авторизоваться на сайте</a></li>
	<li><a href="<c:out value="${restorePasswordUrl}" />">восстановить забытый пароль</a></li>
	<li><a href="<c:out value="${addStampsUrl}" />">добавить серию марок в базу данных</a></li>
</ul>
</div>
<%@ include file="/WEB-INF/segments/footer.jspf" %>
</body>
</html>
