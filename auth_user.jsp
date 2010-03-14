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
							<table>
								<tr>
									<th><h:outputText value="#{m.t_login}" /></th>
									<td><h:inputText id="login" required="true" /></td>
									<td class="error"><h:message for="login" /></td>
								</tr>
								<tr>
									<th><h:outputText value="#{m.t_password}" /></th>
									<td><h:inputSecret id="pass" redisplay="true" required="true" />
									<td class="error"><h:message for="pass" /></td>
								</tr>
								<tr>
									<th></th>
									<td colspan="2">
										<h:commandButton id="submit" type="submit" value="#{m.t_enter}" />
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
