<%@ include file="/WEB-INF/segments/std.jspf" %>

<c:set var="hasErrors" value="${requestScope.context.countFailedElements > 0}" />
<c:set var="elements" value="${requestScope.context.elements}" />
<c:set var="failedElements" value="${requestScope.context.failedElements}" />

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
			<form action="" method="post" id="auth_form">
				<table>
					<tr>
						<th><fmt:message key="t_login" /></th>
						<td><input type="text" name="login" value="${elements.login}" /></td>
						<c:if test="${hasErrors}">
							<td class="error">
								<c:if test="${! empty failedElements.login}">
									<fmt:message key="${failedElements.login}" />
								</c:if>
							</td>
						</c:if>
					</tr>
					<tr>
						<th><fmt:message key="t_password" /></th>
						<td><input type="password" name="pass" value="${elements.pass}" /></td>
						<c:if test="${hasErrors}">
							<td class="error">
								<c:if test="${! empty failedElements.pass}">
									<fmt:message key="${failedElements.pass}" />
								</c:if>
							</td>
						</c:if>
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
