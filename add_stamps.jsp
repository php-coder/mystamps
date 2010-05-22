<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<f:view>
		<head>
			<title>MyStamps: <h:outputText value="#{m.t_add_series}" /></title>
			<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
			<link rel="stylesheet" type="text/css" href="styles/main.css" />
		</head>
		<body>
			<%@ include file="/WEB-INF/segments/header.jspf" %>
			<div id="content">
				<h3>
					<h:outputText value="#{m.t_add_series_ucfirst}" />
				</h3>
				<div class="generic_form">
					<h:form id="add_stamps_form" prependId="false">
						<h:panelGrid columns="2">
								<h:outputLabel for="country" value="#{m.t_country}" />
								<h:inputText id="country" />
								
								<h:outputLabel for="year" value="#{m.t_year}" />
								<h:inputText id="year" maxlength="4" />
								
								<h:outputLabel for="count" value="#{m.t_count}" />
								<h:inputText id="count" maxlength="2" />
								
								<h:outputLabel for="woperf" value="#{m.t_without_perforation}" />
								<h:selectBooleanCheckbox id="woperf" />
								
								<h:outputLabel for="michelno" value="#{m.t_michel_no}" />
								<h:inputText id="michelno" />
								
								<h:outputLabel for="scottno" value="#{m.t_scott_no}" />
								<h:inputText id="scottno" />
								
								<h:outputLabel for="yvertno" value="#{m.t_yvert_no}" />
								<h:inputText id="yvertno" />
								
								<h:outputLabel for="gibbonsno" value="#{m.t_sg_no}" />
								<h:inputText id="gibbonsno" />
								
								<h:outputLabel for="comment" value="#{m.t_comment}" />
								<h:inputTextarea id="comment" cols="22" rows="3" />
								
								<h:outputText value="<label for=\"image\">#{m.t_image}</label>"
									escape="false" />
								<h:outputText value="<input type=\"file\" id=\"image\" name=\"image\" />"
									escape="false" />
								
								<h:panelGroup />
								<h:commandButton id="submit" type="submit" value="#{m.t_add}" />
						</h:panelGrid>
					</h:form>
				</div>
			</div>
			<%@ include file="/WEB-INF/segments/footer.jspf" %>
		</body>
	</f:view>
</html>
