<%@ include file="/WEB-INF/segments/std.jspf" %>

<c:url value="/auth_user.jsp" var="authUserUrl" />
<c:url value="/restore_password.jsp" var="restorePasswordUrl" />

<c:set var="hasErrors" value="${requestScope.context.countFailedElements > 0}" />
<c:set var="elements" value="${requestScope.context.elements}" />
<c:set var="failedElements" value="${requestScope.context.failedElements}" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>MyStamps: <fmt:message key="t_registration_title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
<link rel="stylesheet" type="text/css" href="styles/main.css" />
</head>
<body>
<%@ include file="/WEB-INF/segments/header.jspf" %>
<div id="content">
	<h3><fmt:message key="t_registration_on_site" /></h3>
	<div class="hint">
		<fmt:message key="t_if_you_already_registered" >
			<fmt:param value="<a href=\"${authUserUrl}\">" />
			<fmt:param value="</a>" />
		</fmt:message>
		.<br />
		<fmt:message key="t_if_you_forget_password" >
			<fmt:param value="<a href=\"${restorePasswordUrl}\">" />
			<fmt:param value="</a>" />
		</fmt:message>
		.
	</div>
		<div class="generic_form">
			<form action="" method="post">
				<table>
					<tr>
						<th><fmt:message key="t_login" /></th>
						<td><input type="text" name="login" value="<c:out value='${elements.login}' />" /></td>
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
						<td><input type="password" name="pass1" value="<c:out value='${elements.pass1}' />" /></td>
						<c:if test="${hasErrors}">
							<td class="error">
								<c:if test="${! empty failedElements.pass1}">
									<fmt:message key="${failedElements.pass1}" />
								</c:if>
							</td>
						</c:if>
					</tr>
					<tr>
						<th><fmt:message key="t_password_again" /></th>
						<td><input type="password" name="pass2" value="<c:out value='${elements.pass2}' />" /></td>
						<c:if test="${hasErrors}">
							<td class="error">
								<c:if test="${! empty failedElements.pass2}">
									<fmt:message key="${failedElements.pass2}" />
								</c:if>
							</td>
						</c:if>
					</tr>
					<tr>
						<th><fmt:message key="t_email" /></th>
						<td><input type="text" name="email" value="<c:out value='${elements.email}' />" /></td>
						<c:if test="${hasErrors}">
							<td class="error">
								<c:if test="${! empty failedElements.email}">
									<fmt:message key="${failedElements.email}" />
								</c:if>
							</td>
						</c:if>
					</tr>
					<tr>
						<th><fmt:message key="t_name" /></th>
						<td><input type="text" name="name" value="<c:out value='${elements.name}' />" /></td>
						<c:if test="${hasErrors}">
							<td class="error">
								<c:if test="${! empty failedElements.name}">
									<fmt:message key="${failedElements.name}" />
								</c:if>
							</td>
						</c:if>
					</tr>
					<tr>
						<th></th>
						<td colspan="2"><input type="submit" value="<fmt:message key="t_register" />" /></td>
					</tr>
				</table>
			</form>
		</div>
</div>
<%@ include file="/WEB-INF/segments/footer.jspf" %>
</body>
</html>
