<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>
<%@ page import="ru.mystamps.web.validation.ValidationRules" %>

<h3>
	<spring:message code="t_add_series_ucfirst" />
</h3>
<elem:legend />
<div class="generic_form">
	<form:form method="post" enctype="multipart/form-data" modelAttribute="addSeriesForm">
		<table>
			<tr>
				<td>
					<form:label path="country">
						<spring:message code="t_country" />
					</form:label>
				</td>
				<td></td>
				<td>
					<form:select path="country">
						<form:option value="" />
						<form:options items="${countries}" />
					</form:select>
				</td>
				<td>
					<form:errors path="country" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>
					<form:label path="year">
						<spring:message code="t_issue_year" />
					</form:label>
				</td>
				<td></td>
				<td>
					<form:select path="year">
						<form:option value="" />
						<form:options items="${years}" />
					</form:select>
				</td>
				<td>
					<form:errors path="year" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>
					<form:label path="quantity">
						<spring:message code="t_quantity" />
					</form:label>
				</td>
				<td>
					<span id="quantity.required" class="required_field">*</span>
				</td>
				<td>
					<form:input
						path="quantity"
						type="number"
						size="2"
						min="<%= ValidationRules.MIN_STAMPS_IN_SERIES %>"
						max="<%= ValidationRules.MAX_STAMPS_IN_SERIES %>" />
				</td>
				<td>
					<form:errors path="quantity" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>
					<form:label path="perforated">
						<spring:message code="t_perforated" />
					</form:label>
				</td>
				<td></td>
				<td>
					<form:checkbox path="perforated" />
				</td>
				<td>
					<form:errors path="perforated" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>
					<form:label path="michelNumbers">
						<spring:message code="t_michel_no" />
					</form:label>
				</td>
				<td></td>
				<td>
					<form:input path="michelNumbers" />
				</td>
				<td>
					<form:errors path="michelNumbers" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>
					<form:label path="scottNumbers">
						<spring:message code="t_scott_no" />
					</form:label>
				</td>
				<td></td>
				<td>
					<form:input path="scottNumbers" />
				</td>
				<td>
					<form:errors path="scottNumbers" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>
					<form:label path="yvertNumbers">
						<spring:message code="t_yvert_no" />
					</form:label>
				</td>
				<td></td>
				<td>
					<form:input path="yvertNumbers" />
				</td>
				<td>
					<form:errors path="yvertNumbers" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>
					<form:label path="gibbonsNumbers">
						<spring:message code="t_sg_no" />
					</form:label>
				</td>
				<td></td>
				<td>
					<form:input path="gibbonsNumbers" />
				</td>
				<td>
					<form:errors path="gibbonsNumbers" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>
					<form:label path="comment">
						<spring:message code="t_comment" />
					</form:label>
				</td>
				<td></td>
				<td>
					<form:textarea path="comment" cols="22" rows="3" />
				</td>
				<td>
					<form:errors path="comment" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>
					<form:label path="image">
						<spring:message code="t_image" />
					</form:label>
				</td>
				<td>
					<span id="image.required" class="required_field">*</span>
				</td>
				<td>
					<form:input path="image" type="file" accept="image/png,image/jpeg" />
				</td>
				<td>
					<form:errors path="image" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td></td>
				<td></td>
				<td>
					<input type="submit" value="<spring:message code="t_add" />" />
				</td>
				<td></td>
			</tr>
		</table>
	</form:form>
</div>
