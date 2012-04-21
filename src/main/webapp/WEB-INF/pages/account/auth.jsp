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
			
			<c:if test="${not empty sessionScope.user}">
				<spring:message code="t_already_authenticated" />
			</c:if>
			
			<c:if test="${empty sessionScope.user}">
				<div class="hint">
					<span class="hint_item">
						<spring:message code="t_if_you_forget_password"
							arguments="${restorePasswordUrl}" />
					</span>
					<br />
					<span class="hint_item">
						<spring:message code="t_required_fields_legend"
							arguments="<span class=\"required_field\">*</span>" />
					</span>
				</div>
				<div class="generic_form">
					<form:form method="post" modelAttribute="authAccountForm">
						<form:errors id="form.errors" element="div" cssClass="error" />
						<table>
							<tr>
								<td>
									<form:label path="login">
										<spring:message code="t_login" />
									</form:label>
								</td>
								<td>
									<span id="login.required" class="required_field">*</span>
								</td>
								<td>
									<form:input path="login" />
								</td>
								<td>
									<form:errors path="login" cssClass="error" />
								</td>
							</tr>
							<tr>
								<td>
									<form:label path="password">
										<spring:message code="t_password" />
									</form:label>
								</td>
								<td>
									<span id="password.required" class="required_field">*</span>
								</td>
								<td>
									<form:password path="password" />
								</td>
								<td>
									<form:errors path="password" cssClass="error" />
								</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td>
									<input type="submit" value="<spring:message code="t_enter" />" />
								</td>
								<td></td>
							</tr>
						</table>
					</form:form>
				</div>
			</c:if>
			
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
