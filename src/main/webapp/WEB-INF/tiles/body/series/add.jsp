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
			
			<elem:field path="country" label="t_country">
				<form:select path="country">
					<form:option value="" />
					<form:options items="${countries}" itemLabel="name" itemValue="id" />
				</form:select>
			</elem:field>
			
			<elem:field path="year" label="t_issue_year">
				<form:select path="year">
					<form:option value="" />
					<form:options items="${years}" />
				</form:select>
			</elem:field>
			
			<elem:field path="quantity" label="t_quantity" required="true">
				<form:input
					path="quantity"
					type="number"
					required="required"
					size="2"
					min="<%= ValidationRules.MIN_STAMPS_IN_SERIES %>"
					max="<%= ValidationRules.MAX_STAMPS_IN_SERIES %>" />
			</elem:field>
			
			<elem:field path="perforated" label="t_perforated">
				<form:checkbox path="perforated" />
			</elem:field>
			
			<tr>
				<td>
					<form:label path="michelNumbers">
						<spring:message code="t_michel" />
					</form:label>
				</td>
				<td></td>
				<td>
					<form:input path="michelNumbers" />
					<form:input path="michelPrice" size="5" />
					<form:select path="michelCurrency">
						<form:options />
					</form:select>
				</td>
				<td>
					<form:errors path="michelNumbers" cssClass="error" />
					<form:errors path="michelPrice" cssClass="error" />
					<form:errors path="michelCurrency" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>
					<form:label path="scottNumbers">
						<spring:message code="t_scott" />
					</form:label>
				</td>
				<td></td>
				<td>
					<form:input path="scottNumbers" />
					<form:input path="scottPrice" size="5" />
					<form:select path="scottCurrency">
						<form:options />
					</form:select>
				</td>
				<td>
					<form:errors path="scottNumbers" cssClass="error" />
					<form:errors path="scottPrice" cssClass="error" />
					<form:errors path="scottCurrency" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>
					<form:label path="yvertNumbers">
						<spring:message code="t_yvert" />
					</form:label>
				</td>
				<td></td>
				<td>
					<form:input path="yvertNumbers" />
					<form:input path="yvertPrice" size="5" />
					<form:select path="yvertCurrency">
						<form:options />
					</form:select>
				</td>
				<td>
					<form:errors path="yvertNumbers" cssClass="error" />
					<form:errors path="yvertPrice" cssClass="error" />
					<form:errors path="yvertCurrency" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>
					<form:label path="gibbonsNumbers">
						<spring:message code="t_sg" />
					</form:label>
				</td>
				<td></td>
				<td>
					<form:input path="gibbonsNumbers" />
					<form:input path="gibbonsPrice" size="5" />
					<form:select path="gibbonsCurrency">
						<form:options />
					</form:select>
				</td>
				<td>
					<form:errors path="gibbonsNumbers" cssClass="error" />
					<form:errors path="gibbonsPrice" cssClass="error" />
					<form:errors path="gibbonsCurrency" cssClass="error" />
				</td>
			</tr>
			
			<elem:field path="comment" label="t_comment">
				<form:textarea path="comment" cols="22" rows="3" />
			</elem:field>
			
			<elem:field path="image" label="t_image" required="true">
				<form:input path="image" type="file" required="required" accept="image/png,image/jpeg" />
			</elem:field>
			
			<elem:submit label="t_add" />
			
		</table>
	</form:form>
</div>
