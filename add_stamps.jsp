<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>MyStamps: добавить серию марок</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
<link rel="stylesheet" type="text/css" href="styles/main.css" />
</head>
<body>
<%@ include file="/WEB-INF/segments/header.jspf" %>
<div id="content">
	<h3>Добавить серию марок</h3>
		<div id="add_series_form">
			<form action="" method="post" enctype="multipart/form-data">
				<table>
					<tr>
						<th>Страна</th>
						<td><input type="text" name="country" /></td>
					</tr>
					<tr>
						<th>Год</th>
						<td><input type="text" name="year" maxlength="4" /></td>
					</tr>
					<tr>
						<th>Количество</th>
						<td><input type="text" name="count" maxlength="2" /></td>
					</tr>
					<tr>
						<th>Michel #</th>
						<td><input type="text" name="michelno" /></td>
					</tr>
					<tr>
						<th>Scott #</th>
						<td><input type="text" name="scottno" /></td>
					</tr>
					<tr>
						<th>Yvert #</th>
						<td><input type="text" name="yvertno" /></td>
					</tr>
					<tr>
						<th>Gibbons#</th>
						<td><input type="text" name="gibbonsno" /></td>
					</tr>
					<tr>
						<th>Изображение</th>
						<td><input type="file" name="image" /></td>
					</tr>
					<tr>
						<th></th>
						<td><input type="submit" value="Добавить" /></td>
					</tr>
				</table>
			</form>
		</div>
</div>
<%@ include file="/WEB-INF/segments/footer.jspf" %>
</body>
</html>
