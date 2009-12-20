<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
	<li><a href="register.jsp">зарегистрироваться на сайте</a></li>
	<li><a href="auth.jsp">авторизоваться на сайте</a></li>
	<li><a href="add_stamps.jsp">добавить серию марок в базу данных</a></li>
</ul>
</div>
<%@ include file="/WEB-INF/segments/footer.jspf" %>
</body>
</html>
