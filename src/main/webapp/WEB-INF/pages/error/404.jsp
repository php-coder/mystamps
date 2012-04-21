<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" isErrorPage="true" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><spring:message code="t_404_title" /></title>
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" type="text/css" href="${mainCssUrl}" />
		<link rel="stylesheet" type="text/css" href="${errorCssUrl}" />
	</head>
	<body>
		<%@ include file="/WEB-INF/segments/header.jspf" %>
		<div id="content">
			<table>
				<tr>
					<td id="error-code">404</td>
					<td id="error-msg">
						<spring:message code="t_404_description" arguments="<br />" />
					</td>
				</tr>
			</table>
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
