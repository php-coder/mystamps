<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
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
			
			<c:if test="${not empty sessionScope.user}">
				<spring:message code="t_already_registered" />
			</c:if>
			
			<c:if test="${empty sessionScope.user}">
				<div class="hint">
					<span class="hint_item">
						<spring:message code="t_if_you_already_registered"
							arguments="${authUrl}" />
					</span>
					<br />
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
									<form:input path="email" />
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
			</c:if>
			
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
