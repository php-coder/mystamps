<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ attribute name="catalogNumbers" required="true" %>
<%@ attribute name="catalogPrice" type="ru.mystamps.web.entity.Price" required="true" %>

<c:set var="showBrackets" value="${not empty catalogNumbers and not empty catalogPrice}" />

<c:if test="${not empty catalogNumbers}">
	#<c:out value="${catalogNumbers}" />
</c:if>
<c:if test="${not empty catalogPrice}">
	<fmt:formatNumber var="price" value="${catalogPrice.value}" pattern="###.##" />
	<c:out value="${showBrackets ? '(' : ''}${price} ${catalogPrice.currency}${showBrackets ? ')' : ''}" />
</c:if>
