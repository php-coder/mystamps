<%@ include file="/WEB-INF/segments/std.jspf" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>MyStamps: <fmt:message key="t_add_series" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
<link rel="stylesheet" type="text/css" href="styles/main.css" />
</head>
<body>
<%@ include file="/WEB-INF/segments/header.jspf" %>
<div id="content">
	<h3><fmt:message key="t_add_series_ucfirst" /></h3>
		<div class="generic_form">
			<form action="" method="post" enctype="multipart/form-data" id="add_stamps_form">
				<table>
					<tr>
						<th><fmt:message key="t_country" /></th>
						<td><input type="text" name="country" /></td>
					</tr>
					<tr>
						<th><fmt:message key="t_year" /></th>
						<td><input type="text" name="year" maxlength="4" /></td>
					</tr>
					<tr>
						<th><fmt:message key="t_count" /></th>
						<td><input type="text" name="count" maxlength="2" /></td>
					</tr>
					<tr>
						<th><fmt:message key="t_without_perforation" /></th>
						<td><input type="checkbox" name="woperf" /></td>
					</tr>
					<tr>
						<th><fmt:message key="t_michel_no" /></th>
						<td><input type="text" name="michelno" /></td>
					</tr>
					<tr>
						<th><fmt:message key="t_scott_no" /></th>
						<td><input type="text" name="scottno" /></td>
					</tr>
					<tr>
						<th><fmt:message key="t_yvert_no" /></th>
						<td><input type="text" name="yvertno" /></td>
					</tr>
					<tr>
						<th><fmt:message key="t_sg_no" /></th>
						<td><input type="text" name="gibbonsno" /></td>
					</tr>
					<tr>
						<th><fmt:message key="t_comment" /></th>
						<td><textarea name="comment" cols="22" rows="3"></textarea></td>
					</tr>
					<tr>
						<th><fmt:message key="t_image" /></th>
						<td><input type="file" name="image" /></td>
					</tr>
					<tr>
						<th></th>
						<td><input type="submit" value="<fmt:message key="t_add" />" /></td>
					</tr>
				</table>
			</form>
		</div>
</div>
<%@ include file="/WEB-INF/segments/footer.jspf" %>
</body>
</html>
