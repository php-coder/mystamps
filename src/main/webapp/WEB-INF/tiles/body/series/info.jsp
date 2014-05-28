<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib tagdir="/WEB-INF/tags/elem" prefix="elem" %>

<div class="row-fluid">
	
	<div class="span4">
		<spring:url var="imgUrl" value="${series.imageUrl}" />
		<img id="image" src="${imgUrl}" class="img-polaroid" />
	</div>
	
	<div class="span6">
		<dl class="dl-horizontal">
			<dt>
				<spring:message code="t_category" />
			</dt>
			<dd id="category_name">
				<c:out value="${series.category.name}" />
			</dd>
			<c:if test="${not empty series.country}">
				<dt>
					<spring:message code="t_country" />
				</dt>
				<dd id="country_name">
					<c:out value="${series.country.name}" />
				</dd>
			</c:if>
			<c:if test="${not empty series.releaseYear}">
				<dt>
					<spring:message code="t_issue_date" />
				</dt>
				<dd id="issue_date">
					<c:if test="${not empty series.releaseDay}">
						<fmt:formatNumber type="number" minIntegerDigits="2" value="${series.releaseDay}" />.
					</c:if>
					<c:if test="${not empty series.releaseMonth}">
						<fmt:formatNumber type="number" minIntegerDigits="2" value="${series.releaseMonth}" />.
					</c:if>
					<c:if test="${not empty series.releaseYear}">
						<c:out value="${series.releaseYear}" />
					</c:if>
				</dd>
			</c:if>
				<dt>
					<spring:message code="t_quantity" />
				</dt>
				<dd id="quantity">
					<c:out value="${series.quantity}" />
				</dd>
				
				<dt>
					<spring:message code="t_perforated" />
				</dt>
				<dd id="perforated">
					<c:choose>
						<c:when test="${series.perforated}">
							<spring:message code="t_yes" /> <i class="icon-ok"></i>
						</c:when>
						<c:otherwise>
							<spring:message code="t_no" /> <i class="icon-remove"></i>
						</c:otherwise>
					</c:choose>
				</dd>
			<c:if test="${not empty michelNumbers or not empty series.michelPrice}">
				<dt>
					<spring:message code="t_michel" />
				</dt>
				<dd id="michel_catalog_info">
					<elem:catalog-numbers-with-price
						catalogNumbers="${michelNumbers}"
						catalogPrice="${series.michelPrice}"
					/>
				</dd>
			</c:if>
			<c:if test="${not empty scottNumbers or not empty series.scottPrice}">
				<dt>
					<spring:message code="t_scott" />
				</dt>
				<dd id="scott_catalog_info">
					<elem:catalog-numbers-with-price
						catalogNumbers="${scottNumbers}"
						catalogPrice="${series.scottPrice}"
					/>
				</dd>
			</c:if>
			<c:if test="${not empty yvertNumbers or not empty series.yvertPrice}">
				<dt>
					<spring:message code="t_yvert" />
				</dt>
				<dd id="yvert_catalog_info">
					<elem:catalog-numbers-with-price
						catalogNumbers="${yvertNumbers}"
						catalogPrice="${series.yvertPrice}"
					/>
				</dd>
			</c:if>
			<c:if test="${not empty gibbonsNumbers or not empty series.gibbonsPrice}">
				<dt>
					<spring:message code="t_sg" />
				</dt>
				<dd id="gibbons_catalog_info">
					<elem:catalog-numbers-with-price
						catalogNumbers="${gibbonsNumbers}"
						catalogPrice="${series.gibbonsPrice}"
					/>
				</dd>
			</c:if>
			<sec:authorize var="userCanAddComments" access="hasAuthority('ADD_COMMENTS_TO_SERIES')" />
			<c:if test="${userCanAddComments and not empty series.comment}">
				<dt>
					<spring:message code="t_comment" />
				</dt>
				<dd id="comment">
					<c:out value="${series.comment}" />
				</dd>
			</c:if>
		</dl>
	</div>
	
</div>
