<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>MyStamps: <spring:message code="t_add_series" /></title>
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" type="text/css" href="${mainCssUrl}" />
	</head>
	<body>
		<%@ include file="/WEB-INF/segments/header.jspf" %>
		<div id="content">
			<h3>
				<spring:message code="t_add_series_ucfirst" />
			</h3>
			<div class="generic_form">
				<form:form method="post" enctype="multipart/form-data" modelAttribute="addSeriesForm">
					<table>
						<tr>
							<td>
								<form:label path="country">
									<spring:message code="t_country" />
								</form:label>
							</td>
							<td>
								<form:select path="country">
									<form:option value="" />
									<form:options items="${countries}" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td>
								<form:label path="year">
									<spring:message code="t_issue_year" />
								</form:label>
							</td>
							<td>
								<form:select path="year">
									<form:option value="" />
									<form:options items="${years}" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td>
								<form:label path="quantity">
									<spring:message code="t_quantity" />
								</form:label>
							</td>
							<td>
								<form:input path="quantity" maxlength="2" />
							</td>
						</tr>
						<tr>
							<td>
								<form:label path="perforated">
									<spring:message code="t_perforated" />
								</form:label>
							</td>
							<td>
								<form:checkbox path="perforated" />
							</td>
						</tr>
						<tr>
							<td>
								<form:label path="michelNumbers">
									<spring:message code="t_michel_no" />
								</form:label>
							</td>
							<td>
								<form:input path="michelNumbers" />
							</td>
						</tr>
						<tr>
							<td>
								<form:label path="scottNumbers">
									<spring:message code="t_scott_no" />
								</form:label>
							</td>
							<td>
								<form:input path="scottNumbers" />
							</td>
						</tr>
						<tr>
							<td>
								<form:label path="yvertNumbers">
									<spring:message code="t_yvert_no" />
								</form:label>
							</td>
							<td>
								<form:input path="yvertNumbers" />
							</td>
						</tr>
						<tr>
							<td>
								<form:label path="gibbonsNumbers">
									<spring:message code="t_sg_no" />
								</form:label>
							</td>
							<td>
								<form:input path="gibbonsNumbers" />
							</td>
						</tr>
						<tr>
							<td>
								<form:label path="comment">
									<spring:message code="t_comment" />
								</form:label>
							</td>
							<td>
								<form:textarea path="comment" cols="22" rows="3" />
							</td>
						</tr>
						<tr>
							<td>
								<form:label path="image">
									<spring:message code="t_image" />
								</form:label>
							</td>
							<td>
								<form:input path="image" type="file" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td>
								<input type="submit" value="<spring:message code="t_add" />" />
							</td>
						</tr>
					</table>
				</form:form>
			</div>
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
