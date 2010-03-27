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
					<br />
					<h:outputFormat value="#{m.t_if_you_forget_password}" escape="false">
						<f:param value="<a href=\"#{facesContext.externalContext.requestContextPath}/restore_password.jsf\">" />
						<f:param value="</a>" />
					</h:outputFormat>
					<br />
					<h:outputFormat value="#{m.t_required_fields_legend}" escape="false">
						<f:param value="<span class=\"required_field\">*</span>" />
					</h:outputFormat>
				</div>
				<div class="generic_form">
					<h:form id="register_form" prependId="false">
						<h:panelGrid columns="4">
							<h:outputLabel for="login" value="#{m.t_login}" />
							<h:outputText value="*" styleClass="required_field" />
							<h:inputText id="login" required="true"
								binding="#{register.loginInput}">
								<f:validateLength
									minimum="#{initParam[\"LOGIN_MIN_LENGTH\"]}"
									maximum="#{initParam[\"LOGIN_MAX_LENGTH\"]}" />
							</h:inputText>
							<h:message for="login" styleClass="error" />
							
							<h:outputLabel for="pass1" value="#{m.t_password}" />
							<h:outputText value="*" styleClass="required_field" />
							<h:inputSecret id="pass1"
								required="true"
								binding="#{register.passwordInput}"
								validator="#{register.validatePasswordLoginMismatch}"
								redisplay="true">
								<f:validateLength
									minimum="#{initParam[\"PASSWORD_MIN_LENGTH\"]}" />
							</h:inputSecret>
							<h:message for="pass1" styleClass="error" />
							
							<h:outputLabel for="pass2" value="#{m.t_password_again}" />
							<h:outputText value="*" styleClass="required_field" />
							<h:inputSecret id="pass2"
								required="true"
								validator="#{register.validatePasswordConfirm}"
								redisplay="true" />
							<h:message for="pass2" styleClass="error" />
							
							<h:outputLabel for="email" value="#{m.t_email}" />
							<h:outputText value="*" styleClass="required_field" />
							<h:inputText id="email" required="true" />
							<h:message for="email" styleClass="error" />
							
							<h:outputLabel for="name" value="#{m.t_name}" />
							<h:outputText value="*" styleClass="required_field" />
							<h:inputText id="name" required="true" />
							<h:message for="name" styleClass="error" />
							
							<h:panelGroup />
							<h:panelGroup />
							<h:commandButton id="submit" type="submit" value="#{m.t_register}" />
							<h:panelGroup />
						</h:panelGrid>
					</h:form>
				</div>
			</div>
			<%@ include file="/WEB-INF/segments/footer.jspf" %>
		</body>
	</f:view>
</html>
