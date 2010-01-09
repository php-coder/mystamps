<%@ include file="/WEB-INF/segments/std.jspf" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>MyStamps: <fmt:message key="t_maintenance_title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
<link rel="stylesheet" type="text/css" href="styles/main.css" />
<link rel="stylesheet" type="text/css" href="styles/error.css" />
</head>
<body>
<%@ include file="/WEB-INF/segments/header.jspf" %>
<div id="content">
	<table>
		<tr>
			<td id="error-msg">
				<fmt:message key="t_maintenance_on_site">
					<fmt:param value="<br />" />
				</fmt:message>
			</td>
		</tr>
	</table>
</div>
<%@ include file="/WEB-INF/segments/footer.jspf" %>
</body>
</html>
