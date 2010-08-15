<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>
<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<f:view>
		<head>
			<title>MyStamps: <h:outputText value="#{m.t_auth_title}" /></title>
			<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
			<link rel="stylesheet" type="text/css" href="styles/main.css" />
		</head>
		<body>
			<%@ include file="/WEB-INF/segments/header.jspf" %>
			<div id="content">
				<h3>
					<h:outputText value="#{m.t_authentication_on_site}" />
				</h3>
				<h:outputText value="#{m.t_already_authenticated}" rendered="#{user.logged}" />
				<h:panelGroup layout="block" styleClass="hint" rendered="#{not user.logged}">
					<h:outputFormat value="#{m.t_if_you_forget_password}" escape="false">
						<f:param value="<a href=\"/restore_password.jsf\">" />
						<f:param value="</a>" />
					</h:outputFormat>
					<br />
					<h:outputFormat value="#{m.t_required_fields_legend}" escape="false">
						<f:param value="<span class=\"required_field\">*</span>" />
					</h:outputFormat>
				</h:panelGroup>
				<h:panelGroup layout="block" styleClass="generic_form" rendered="#{not user.logged}">
					<h:form id="auth_form" prependId="false">
						<h:messages globalOnly="true" errorClass="error" layout="table" />
						<h:panelGrid columns="4">
							<h:outputLabel for="login" value="#{m.t_login}" />
							<h:outputText id="login_required" value="*" styleClass="required_field" />
							<h:inputText id="login" value="#{auth.login}" required="true">
								<f:validateLength minimum="2" maximum="15" />
								<t:validateRegExpr
									pattern="[-_a-zA-Z0-9]+"
									message="#{e.tv_invalid_login}" />
							</h:inputText>
							<h:message id="login_error" for="login" styleClass="error" />
							
							<h:outputLabel for="pass" value="#{m.t_password}" />
							<h:outputText id="pass_required" value="*" styleClass="required_field" />
							<h:inputSecret id="pass" value="#{auth.password}" required="true">
								<f:validateLength minimum="4" />
								<t:validateRegExpr
									pattern="[-_a-zA-Z0-9]+"
									message="#{e.tv_invalid_password}" />
							</h:inputSecret>
							<h:message id="pass_error" for="pass" styleClass="error" />
							
							<h:panelGroup />
							<h:panelGroup />
							<h:commandButton id="submit"
								type="submit"
								value="#{m.t_enter}"
								action="#{auth.authUser}" />
							<h:panelGroup />
						</h:panelGrid>
					</h:form>
				</h:panelGroup>
			</div>
			<%@ include file="/WEB-INF/segments/footer.jspf" %>
		</body>
	</f:view>
</html>
