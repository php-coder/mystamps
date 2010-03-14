<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<f:view>
		<head>
			<title>MyStamps: <h:outputText value="#{m.t_index_title}" /></title>
			<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
			<link rel="stylesheet" type="text/css" href="styles/main.css" />
		</head>
		<body>
			<%@ include file="/WEB-INF/segments/header.jspf" %>
			<div id="content">
				<h:outputText value="#{m.t_you_may}" />:
				<ul>
					<li>
						<h:outputLink value="#{facesContext.externalContext.requestContextPath}/register_user.jsf">
							<h:outputText value="#{m.t_register_on_site}" />
						</h:outputLink>
					</li>

					<li>
						<h:outputLink value="#{facesContext.externalContext.requestContextPath}/auth_user.jsf">
							<h:outputText value="#{m.t_auth_on_site}" />
						</h:outputLink>
					</li>
					<li>
						<h:outputLink value="#{facesContext.externalContext.requestContextPath}/restore_password.jsf">
							<h:outputText value="#{m.t_recover_forget_password}" />
						</h:outputLink>
					</li>
					<li>
						<h:outputLink value="#{facesContext.externalContext.requestContextPath}/add_stamps.jsf">
							<h:outputText value="#{m.t_add_series}" />
						</h:outputLink>
					</li>
				</ul>
			</div>
			<%@ include file="/WEB-INF/segments/footer.jspf" %>
		</body>
	</f:view>
</html>
