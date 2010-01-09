<%@ include file="/WEB-INF/segments/std.jspf" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>MyStamps: <fmt:message key="t_auth_title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
<link rel="stylesheet" type="text/css" href="styles/main.css" />
</head>
<body>
<%@ include file="/WEB-INF/segments/header.jspf" %>
<div id="content">
	<h3><fmt:message key="t_authorization_on_site" /></h3>
		<div class="generic_form">
			<form action="" method="post">
				<table>
					<tr>
						<th><fmt:message key="t_login" /></th>
						<td><input type="text" name="login" /></td>
					</tr>
					<tr>
						<th><fmt:message key="t_password" /></th>
						<td><input type="password" name="pass" /></td>
					</tr>
					<tr>
						<th></th>
						<td><input type="submit" value="<fmt:message key="t_enter" />" /></td>
					</tr>
				</table>
			</form>
		</div>
</div>
<%@ include file="/WEB-INF/segments/footer.jspf" %>
</body>
</html>
