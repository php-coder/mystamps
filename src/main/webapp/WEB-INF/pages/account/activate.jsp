<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>MyStamps: <spring:message code="t_activation_title" /></title>
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" type="text/css" href="${mainCssUrl}" />
	</head>
	<body>
		<%@ include file="/WEB-INF/segments/header.jspf" %>
		<div id="content">
			<h3>
				<spring:message code="t_activation_on_site" />
			</h3>
			
			<sec:authorize access="isAuthenticated()">
				<spring:message code="t_already_activated" />
			</sec:authorize>
			
			<sec:authorize access="isAnonymous()">
					<elem:legend />
					<div class="generic_form">
						<form:form method="post" action="${activateUrl}" modelAttribute="activateAccountForm">
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
										<form:input path="login" required="required" />
									</td>
									<td>
										<form:errors path="login" cssClass="error" />
									</td>
								</tr>
								<tr>
									<td>
										<form:label path="name">
											<spring:message code="t_name" />
										</form:label>
									</td>
									<td></td>
									<td>
										<form:input path="name" />
									</td>
									<td>
										<form:errors path="name" cssClass="error" />
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
										<form:password path="password" required="required" />
									</td>
									<td>
										<form:errors path="password" cssClass="error" />
									</td>
								</tr>
								<tr>
									<td>
										<form:label path="passwordConfirmation">
											<spring:message code="t_password_again" />
										</form:label>
									</td>
									<td>
										<span id="passwordConfirmation.required" class="required_field">*</span>
									</td>
									<td>
										<form:password path="passwordConfirmation" required="required" />
									</td>
									<td>
										<form:errors path="passwordConfirmation" cssClass="error" />
									</td>
								</tr>
								<tr>
									<td>
										<form:label path="activationKey">
											<spring:message code="t_activation_key" />
										</form:label>
									</td>
									<td>
										<span id="activationKey.required" class="required_field">*</span>
									</td>
									<td>
										<form:input path="activationKey" required="required" />
									</td>
									<td>
										<form:errors path="activationKey" cssClass="error" />
									</td>
								</tr>
								<tr>
									<td></td>
									<td></td>
									<td>
										<input type="submit" value="<spring:message code="t_activate" />" />
									</td>
									<td>
									</td>
								</tr>
							</table>
						</form:form>
					</div>
				</sec:authorize>
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
