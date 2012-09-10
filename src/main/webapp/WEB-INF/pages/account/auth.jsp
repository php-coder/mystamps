<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>MyStamps: <spring:message code="t_auth_title" /></title>
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" type="text/css" href="${mainCssUrl}" />
	</head>
	<body>
		<%@ include file="/WEB-INF/segments/header.jspf" %>
		<div id="content">
			<h3>
				<spring:message code="t_authentication_on_site" />
			</h3>
			
			<sec:authorize access="isAuthenticated()">
				<spring:message code="t_already_authenticated" />
			</sec:authorize>
			
			<sec:authorize access="isAnonymous()">
				<elem:legend />
				<c:if test="${pageContext.request.queryString eq 'failed' and SPRING_SECURITY_LAST_EXCEPTION ne null}">
					<c:set var="errorMessage" value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
					<c:set var="lastLogin" value="${SPRING_SECURITY_LAST_EXCEPTION.authentication.principal}" />
				</c:if>
				<div class="generic_form">
					<form id="authAccountForm" action="${loginUrl}" method="post">
						<c:if test="${not empty pageScope.errorMessage}">
							<div id="form.errors" class="error">
								<c:out value="${pageScope.errorMessage}" />
							</div>
						</c:if>
						<table>
							<tr>
								<td>
									<label for="login">
										<spring:message code="t_login" />
									</label>
								</td>
								<td>
									<span id="login.required" class="required_field">*</span>
								</td>
								<td>
									<input type="text" id="login" name="login" required="required" value="<c:out value='${pageScope.lastLogin}' />" />
								</td>
							</tr>
							<tr>
								<td>
									<label for="password">
										<spring:message code="t_password" />
									</label>
								</td>
								<td>
									<span id="password.required" class="required_field">*</span>
								</td>
								<td>
									<input type="password" id="password" name="password" required="required" value="" />
								</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td>
									<input type="submit" value="<spring:message code="t_enter" />" />
								</td>
							</tr>
						</table>
					</form>
				</div>
			</sec:authorize>
			
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
