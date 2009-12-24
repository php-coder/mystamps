<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>MyStamps: регистрация</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
<link rel="stylesheet" type="text/css" href="styles/main.css" />
</head>
<body>
<%@ include file="/WEB-INF/segments/header.jspf" %>
<div id="content">
	<h3>Регистрация на сайте</h3>
	<div class="hint">
		Если вы уже зарегистрированы, то необходимо <a href="auth_user.jsp">авторизоваться</a>.<br />
		Если вы забыли пароль, то мы поможем его <a href="restore_password.jsp">восстановить</a>.
	</div>
		<div class="generic_form">
			<form action="" method="post">
				<table>
					<tr>
						<th>Логин</th>
						<td><input type="text" name="login" /></td>
					</tr>
					<tr>
						<th>Пароль</th>
						<td><input type="password" name="pass1" /></td>
					</tr>
					<tr>
						<th>Пароль (ещё раз)</th>
						<td><input type="password" name="pass2" /></td>
					</tr>
					<tr>
						<th>E-mail</th>
						<td><input type="text" name="email" /></td>
					</tr>
					<tr>
						<th>Имя</th>
						<td><input type="text" name="name" /></td>
					</tr>
					<tr>
						<th></th>
						<td><input type="submit" value="Зарегистрироваться" /></td>
					</tr>
				</table>
			</form>
		</div>
</div>
<%@ include file="/WEB-INF/segments/footer.jspf" %>
</body>
</html>
