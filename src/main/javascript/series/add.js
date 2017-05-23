//
// IMPORTANT:
// You have to update Url.RESOURCES_VERSION each time whenever you're modified this file!
//
function initPage(suggestCountryUrl) {
	$('#country').selectize();
	
	$('.js-catalog-numbers').on('blur', function expandCatalogNumbers() {
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

	if (suggestCountryUrl != null) {
		$.get(suggestCountryUrl, function handleSuggestedCountry(slug) {
			if (slug == "") {
				return;
			}

			var guessCountryLink = $("#js-guess-country-link");
			guessCountryLink.click(function chooseSuggestedCountry() {
				guessCountryLink.hide();
				chooseCountryBySlug(slug);
			});
			guessCountryLink.show();
		});
	}
}

function chooseCountryBySlug(slug) {
	var select_country = $("#country").selectize();
	var selectize = select_country[0].selectize;
	selectize.setValue(slug);
}
