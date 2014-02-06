<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<spring:url var="catalogUtilsJsUrl" value="/public/js/CatalogUtils.js" />
<spring:url var="jqueryJsUrl" value="/public/jquery/jquery.min.js" />

<script src="${catalogUtilsJsUrl}"></script>
<script src="${jqueryJsUrl}"></script>

<script>
	$(function() {
		$('.js-catalog-numbers').on('blur', function() {
			$(this).val(function(idx, val) {
				return CatalogUtils.expandNumbers(val);
			});
		});
	});
</script>

