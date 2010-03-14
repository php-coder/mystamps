<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ page pageEncoding="UTF-8" %>
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
				<h3><h:outputText value="#{m.t_authorization_on_site}" /></h3>
					<div class="generic_form">
						<h:form id="auth_form" prependId="false">
							<h:panelGrid columns="3">
								<h:outputLabel for="login" value="#{m.t_login}" />
								<h:inputText id="login" required="true" />
								<h:message for="login" styleClass="error" />
								
								<h:outputLabel for="pass" value="#{m.t_password}" />
								<h:inputSecret id="pass" redisplay="true" required="true" />
								<h:message for="pass" styleClass="error" />
								
								<h:outputText value="" />
								<h:commandButton id="submit" type="submit" value="#{m.t_enter}" />
								<h:outputText value="" />
							</h:panelGrid>
						</h:form>
					</div>
			</div>
			<%@ include file="/WEB-INF/segments/footer.jspf" %>
		</body>
	</f:view>
</html>
