<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>
<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<f:view>
		<head>
			<title>MyStamps: <h:outputText value="#{m.t_activation_title}" /></title>
			<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
			<link rel="stylesheet" type="text/css" href="styles/main.css" />
		</head>
		<body>
			<%@ include file="/WEB-INF/segments/header.jspf" %>
			<div id="content">
				<h3>
					<h:outputText value="#{m.t_activation_on_site}" />
				</h3>
				<div class="hint">
					<h:outputFormat value="#{m.t_required_fields_legend}" escape="false">
						<f:param value="<span class=\"required_field\">*</span>" />
					</h:outputFormat>
				</div>
				<div class="generic_form">
					<h:form id="activate_form" prependId="false">
						<h:panelGrid columns="4">
							<h:outputLabel for="login" value="#{m.t_login}" />
							<h:outputText id="login_required" value="*" styleClass="required_field" />
							<h:inputText id="login" required="true"
								binding="#{activate.loginInput}">
								<f:validateLength
									minimum="#{initParam[\"LOGIN_MIN_LENGTH\"]}"
									maximum="#{initParam[\"LOGIN_MAX_LENGTH\"]}" />
								<t:validateRegExpr
									pattern="[-_a-zA-Z0-9]+"
									message="#{e.tv_invalid_login}" />
							</h:inputText>
							<h:message id="login_error" for="login" styleClass="error" />
							
							<h:outputLabel for="pass1" value="#{m.t_password}" />
							<h:outputText id="pass1_required" value="*" styleClass="required_field" />
							<h:inputSecret id="pass1"
								required="true"
								validator="#{activate.validatePasswordLoginMismatch}"
								redisplay="true">
								<f:validateLength
									minimum="#{initParam[\"PASSWORD_MIN_LENGTH\"]}" />
								<t:validateRegExpr
									pattern="[-_a-zA-Z0-9]+"
									message="#{e.tv_invalid_password}" />
							</h:inputSecret>
							<h:message id="pass1_error" for="pass1" styleClass="error" />
							
							<h:outputLabel for="pass2" value="#{m.t_password_again}" />
							<h:outputText id="pass2_required" value="*" styleClass="required_field" />
							<h:inputSecret id="pass2" required="true" redisplay="true">
								<t:validateEqual for="pass1" />
							</h:inputSecret>
							<h:message id="pass2_error" for="pass2" styleClass="error" />
							
							<h:outputLabel for="name" value="#{m.t_name}" />
							<h:panelGroup />
							<h:inputText id="name" />
							<h:message id="name_error" for="name" styleClass="error" />
							
							<h:outputLabel for="act_key" value="#{m.t_activation_key}" />
							<h:outputText id="act_key_required" value="*" styleClass="required_field" />
							<h:inputText id="act_key" required="true" value="#{param['key']}" />
							<h:message id="act_key_error" for="act_key" styleClass="error" />
							
							<h:panelGroup />
							<h:panelGroup />
							<h:commandButton id="submit" type="submit" value="#{m.t_activate}" />
							<h:panelGroup />
						</h:panelGrid>
					</h:form>
				</div>
			</div>
			<%@ include file="/WEB-INF/segments/footer.jspf" %>
		</body>
	</f:view>
</html>
