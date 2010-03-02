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
				<h3><h:outputText value="#{m.t_add_series_ucfirst}" /></h3>
					<div class="generic_form">
						<h:form id="add_stamps_form" prependId="false">
							<table>
								<tr>
									<th><h:outputLabel for="country" value="#{m.t_country}" /></th>
									<td><h:inputText id="country" /></td>
								</tr>
								<tr>
									<th><h:outputLabel for="year" value="#{m.t_year}" /></th>
									<td><h:inputText id="year" maxlength="4" /></td>
								</tr>
								<tr>
									<th><h:outputLabel for="count" value="#{m.t_count}" /></th>
									<td><h:inputText id="count" maxlength="2" /></td>
								</tr>
								<tr>
									<th><h:outputLabel for="woperf" value="#{m.t_without_perforation}" /></th>
									<td><h:selectBooleanCheckbox id="woperf" /></td>
								</tr>
								<tr>
									<th><h:outputLabel for="michelno" value="#{m.t_michel_no}" /></th>
									<td><h:inputText id="michelno" /></td>
								</tr>
								<tr>
									<th><h:outputLabel for="scottno" value="#{m.t_scott_no}" /></th>
									<td><h:inputText id="scottno" /></td>
								</tr>
								<tr>
									<th><h:outputLabel for="yvertno" value="#{m.t_yvert_no}" /></th>
									<td><h:inputText id="yvertno" /></td>
								</tr>
								<tr>
									<th><h:outputLabel for="gibbonsno" value="#{m.t_sg_no}" /></th>
									<td><h:inputText id="gibbonsno" /></td>
								</tr>
								<tr>
									<th><h:outputLabel for="comment" value="#{m.t_comment}" /></th>
									<td><h:inputTextarea id="comment" cols="22" rows="3" /></td>
								</tr>
								<tr>
									<th><h:outputText value="<label for=\"image\">#{m.t_image}</label>" escape="false" /></th>
									<td><input type="file" id="image" name="image" /></td>
								</tr>
								<tr>
									<th></th>
									<td><h:commandButton id="submit" type="submit" value="#{m.t_add}" /></td>
								</tr>
							</table>
						</h:form>
					</div>
			</div>
			<%@ include file="/WEB-INF/segments/footer.jspf" %>
		</body>
	</f:view>
</html>
