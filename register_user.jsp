<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<f:view>
		<head>
			<title>MyStamps: <h:outputText value="#{m.t_registration_title}" /></title>
			<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
			<link rel="stylesheet" type="text/css" href="styles/main.css" />
		</head>
		<body>
			<%@ include file="/WEB-INF/segments/header.jspf" %>
			<div id="content">
				<h3>
					<h:outputText value="#{m.t_registration_on_site}" />
				</h3>
				<div class="hint">
					<h:outputFormat value="#{m.t_if_you_already_registered}" escape="false">
						<f:param value="<a href=\"#{facesContext.externalContext.requestContextPath}/auth_user.jsf\">" />
						<f:param value="</a>" />
					</h:outputFormat>
					.<br />
					<h:outputFormat value="#{m.t_if_you_forget_password}" escape="false">
						<f:param value="<a href=\"#{facesContext.externalContext.requestContextPath}/restore_password.jsf\">" />
						<f:param value="</a>" />
					</h:outputFormat>
					.
				</div>
				<div class="generic_form">
					<h:form id="register_form" prependId="false">
						<table>
							<tr>
								<th>
									<h:outputText value="#{m.t_login}" />
								</th>
								<td>
									<h:inputText id="login" required="true" binding="#{register.loginInput}" />
								</td>
								<td class="error">
									<h:message for="login" />
								</td>
							</tr>
							<tr>
								<th>
									<h:outputText value="#{m.t_password}" />
								</th>
								<td>
									<h:inputSecret id="pass1"
										required="true"
										binding="#{register.passwordInput}"
										validator="#{register.validatePasswordLoginMismatch}"
										redisplay="true" />
								</td>
								<td class="error">
									<h:message for="pass1" />
								</td>
							</tr>
							<tr>
								<th>
									<h:outputText value="#{m.t_password_again}" />
								</th>
								<td>
									<h:inputSecret id="pass2"
										required="true"
										validator="#{register.validatePasswordConfirm}"
										redisplay="true" />
								</td>
								<td class="error">
									<h:message for="pass2" />
								</td>
							</tr>
							<tr>
								<th>
									<h:outputText value="#{m.t_email}" />
								</th>
								<td>
									<h:inputText id="email" required="true" />
								</td>
								<td class="error">
									<h:message for="email" />
								</td>
							</tr>
							<tr>
								<th>
									<h:outputText value="#{m.t_name}" />
								</th>
								<td>
									<h:inputText id="name" required="true" />
								</td>
								<td class="error">
									<h:message for="name" />
								</td>
							</tr>
							<tr>
								<th></th>
								<td colspan="2">
									<h:commandButton id="submit" type="submit" value="#{m.t_register}" />
								</td>
							</tr>
						</table>
					</h:form>
				</div>
			</div>
			<%@ include file="/WEB-INF/segments/footer.jspf" %>
		</body>
	</f:view>
</html>
