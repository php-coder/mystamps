<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>
<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
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
						<f:param value="<a href=\"/auth_user.jsf\">" />
						<f:param value="</a>" />
					</h:outputFormat>
					<br />
					<h:outputFormat value="#{m.t_if_you_forget_password}" escape="false">
						<f:param value="<a href=\"/restore_password.jsf\">" />
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
							<h:outputLabel for="email" value="#{m.t_email}" />
							<h:outputText id="email_required" value="*" styleClass="required_field" />
							<h:inputText id="email" required="true" value="#{register.email}">
								<f:validateLength
									maximum="#{initParam['EMAIL_MAX_LENGTH']}" />
								<t:validateEmail />
							</h:inputText>
							<h:message id="email_error" for="email" styleClass="error" />
							
							<h:panelGroup />
							<h:panelGroup />
							<h:commandButton id="submit" type="submit"
								value="#{m.t_register}"
								action="#{register.sendActivationKey}" />
							<h:panelGroup />
						</h:panelGrid>
					</h:form>
				</div>
			</div>
			<%@ include file="/WEB-INF/segments/footer.jspf" %>
		</body>
	</f:view>
</html>
