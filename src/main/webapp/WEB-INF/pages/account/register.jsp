<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>MyStamps: <spring:message code="t_registration_title" /></title>
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" type="text/css" href="${mainCssUrl}" />
	</head>
	<body>
		<%@ include file="/WEB-INF/segments/header.jspf" %>
		<div id="content">
			<h3>
				<spring:message code="t_registration_on_site" />
			</h3>
			
			<sec:authorize access="isAuthenticated()">
				<spring:message code="t_already_registered" />
			</sec:authorize>
			
			<sec:authorize access="isAnonymous()">
				<elem:legend showLinkToAuthPage="true" />
				<div class="generic_form">
					<form:form method="post" modelAttribute="registerAccountForm">
						<table>
							<tr>
								<td>
									<form:label path="email">
										<spring:message code="t_email" />
									</form:label>
								</td>
								<td>
									<span id="email.required" class="required_field">*</span>
								</td>
								<td>
									<form:input path="email" type="email" required="required" autofocus="autofocus" />
								</td>
								<td>
									<form:errors path="email" cssClass="error" />
								</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td>
									<input type="submit" value="<spring:message code="t_register" />" />
								</td>
								<td></td>
							</tr>
						</table>
					</form:form>
				</div>
			</sec:authorize>
			
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
