function initPage() {
	$('#country').selectize();
	
	$('.js-catalog-numbers').on('blur', function() {
		$(this).val(function(idx, val) {
			return CatalogUtils.expandNumbers(val);
		});
	});
	
	$('.collapse').on('show.bs.collapse hide.bs.collapse', function toggleChevron() {
		$(this)
			.prev('.js-collapse-toggle-header')
			.find('.glyphicon')
			.toggleClass('glyphicon-chevron-down glyphicon-chevron-right');
	});
	
	$('.js-with-tooltip').tooltip({
		'placement': 'right'
	});
}
