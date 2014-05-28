<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>
<%@ page import="ru.mystamps.web.validation.ValidationRules" %>

<h3>
	<spring:message code="t_add_series_ucfirst" />
</h3>
<elem:legend />
<div class="span6 offset3">
	<form:form method="post" enctype="multipart/form-data" modelAttribute="addSeriesForm" cssClass="form-horizontal">
		
		<elem:field path="category" label="t_category" required="true">
			<form:select path="category" required="required">
				<form:option value="" />
				<form:options items="${categories}" itemLabel="name" itemValue="id" />
			</form:select>
		</elem:field>
		
		<elem:field path="country" label="t_country">
			<form:select path="country">
				<form:option value="" />
				<form:options items="${countries}" itemLabel="name" itemValue="id" />
			</form:select>
		</elem:field>
		
		<spring:hasBindErrors name="addSeriesForm">
			<c:if test="${errors.hasFieldErrors('day') or errors.hasFieldErrors('month') or errors.hasFieldErrors('year')}">
				<c:set var="releaseDateHasError" value="true" />
			</c:if>
		</spring:hasBindErrors>
		
		<div class="control-group ${releaseDateHasError ? 'error' : ''}">
			<form:label path="year" cssClass="control-label">
				<span class="field-label"><spring:message code="t_issue_date" /></span>
			</form:label>
			
			<div class="controls controls-row">
				<form:select path="day" cssClass="span2">
					<form:option value="" />
					<form:options items="${days}" />
				</form:select>
				
				<form:select path="month" cssClass="span2">
					<form:option value="" />
					<form:options items="${months}" />
				</form:select>
				
				<form:select path="year" cssClass="span2">
					<form:option value="" />
					<form:options items="${years}" />
				</form:select>
				
			</div>
			<div class="controls">
				<form:errors path="day" cssClass="help-block" />
				<form:errors path="month" cssClass="help-block" />
				<form:errors path="year" cssClass="help-block" />
			</div>
		</div>
		
		<elem:field path="quantity" label="t_quantity" required="true">
			<form:input
				path="quantity"
				cssClass="span2"
				type="number"
				required="required"
				min="<%= ValidationRules.MIN_STAMPS_IN_SERIES %>"
				max="<%= ValidationRules.MAX_STAMPS_IN_SERIES %>" />
		</elem:field>
		
		<elem:field path="perforated" label="t_perforated">
			<form:checkbox path="perforated" id="perforated" />
		</elem:field>
		
		<spring:hasBindErrors name="addSeriesForm">
			<c:if test="${errors.hasFieldErrors('michelNumbers') or errors.hasFieldErrors('michelPrice') or errors.hasFieldErrors('michelCurrency')}">
				<c:set var="michelHasError" value="true" />
			</c:if>
		</spring:hasBindErrors>
		
		<div class="control-group ${michelHasError ? 'error' : ''}">
			<form:label path="michelNumbers" cssClass="control-label">
				<span class="field-label"><spring:message code="t_michel" /></span>
			</form:label>
			<div class="controls controls-row">
				<form:input path="michelNumbers" cssClass="span4 js-catalog-numbers" />
				<form:input path="michelPrice" cssClass="span2" size="5" />
				<form:select path="michelCurrency" cssClass="span2">
					<form:options />
				</form:select>
				<form:errors path="michelNumbers" cssClass="help-inline" />
				<form:errors path="michelPrice" cssClass="help-inline" />
				<form:errors path="michelCurrency" cssClass="help-inline" />
			</div>
		</div>
		
		<spring:hasBindErrors name="addSeriesForm">
			<c:if test="${errors.hasFieldErrors('scottNumbers') or errors.hasFieldErrors('scottPrice') or errors.hasFieldErrors('scottCurrency')}">
				<c:set var="scottHasError" value="true" />
			</c:if>
		</spring:hasBindErrors>
		
		<div class="control-group ${scottHasError ? 'error' : ''}">
			<form:label path="scottNumbers" cssClass="control-label">
				<span class="field-label"><spring:message code="t_scott" /></span>
			</form:label>
			<div class="controls controls-row">
				<form:input path="scottNumbers" cssClass="span4 js-catalog-numbers" />
				<form:input path="scottPrice" cssClass="span2" size="5" />
				<form:select path="scottCurrency" cssClass="span2">
					<form:options />
				</form:select>
				<form:errors path="scottNumbers" cssClass="help-inline" />
				<form:errors path="scottPrice" cssClass="help-inline" />
				<form:errors path="scottCurrency" cssClass="help-inline" />
			</div>
		</div>
		
		<spring:hasBindErrors name="addSeriesForm">
			<c:if test="${errors.hasFieldErrors('yvertNumbers') or errors.hasFieldErrors('yvertPrice') or errors.hasFieldErrors('yvertCurrency')}">
				<c:set var="yvertHasError" value="true" />
			</c:if>
		</spring:hasBindErrors>
		
		<div class="control-group ${yvertHasError ? 'error' : ''}">
			<form:label path="yvertNumbers" cssClass="control-label">
				<span class="field-label"><spring:message code="t_yvert" /></span>
			</form:label>
			<div class="controls controls-row">
				<form:input path="yvertNumbers" cssClass="span4 js-catalog-numbers" />
				<form:input path="yvertPrice" cssClass="span2" size="5" />
				<form:select path="yvertCurrency" cssClass="span2">
					<form:options />
				</form:select>
				<form:errors path="yvertNumbers" cssClass="help-inline" />
				<form:errors path="yvertPrice" cssClass="help-inline" />
				<form:errors path="yvertCurrency" cssClass="help-inline" />
			</div>
		</div>
		
		<spring:hasBindErrors name="addSeriesForm">
			<c:if test="${errors.hasFieldErrors('gibbonsNumbers') or errors.hasFieldErrors('gibbonsPrice') or errors.hasFieldErrors('gibbonsCurrency')}">
				<c:set var="gibbonsHasError" value="true" />
			</c:if>
		</spring:hasBindErrors>
		
		<div class="control-group ${gibbonsHasError ? 'error' : ''}">
			<form:label path="gibbonsNumbers" cssClass="control-label">
				<span class="field-label"><spring:message code="t_sg" /></span>
			</form:label>
			<div class="controls controls-row">
				<form:input path="gibbonsNumbers" cssClass="span4 js-catalog-numbers" />
				<form:input path="gibbonsPrice" cssClass="span2" size="5" />
				<form:select path="gibbonsCurrency" cssClass="span2">
					<form:options />
				</form:select>
				<form:errors path="gibbonsNumbers" cssClass="help-inline" />
				<form:errors path="gibbonsPrice" cssClass="help-inline" />
				<form:errors path="gibbonsCurrency" cssClass="help-inline" />
			</div>
		</div>
		
		<sec:authorize var="userCanAddComments" access="hasAuthority('ADD_COMMENTS_TO_SERIES')" />
		<elem:field path="comment" label="t_comment" render="${userCanAddComments}">
			<form:textarea path="comment" cssClass="span6" cols="22" rows="3" />
		</elem:field>
		
		<elem:field path="image" label="t_image" required="true">
			<form:input path="image" type="file" required="required" accept="image/png,image/jpeg" />
		</elem:field>
		
		<elem:submit label="t_add" />
		
	</form:form>
</div>
