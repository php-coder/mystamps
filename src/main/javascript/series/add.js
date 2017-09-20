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

	$('.js-collapse-toggle-header').show();
	
	$('.collapse').on('show.bs.collapse hide.bs.collapse', function toggleChevron() {
		$(this)
			.prev('.js-collapse-toggle-header')
			.find('.glyphicon')
			.toggleClass('glyphicon-chevron-down glyphicon-chevron-right');
	});

	// emulates collapse('hide') but hides elements faster
	$('.collapse').not('.has-error, .js-has-data').height(0).removeClass('in').trigger('hide.bs.collapse');
	
	$('.js-with-tooltip').tooltip({
		'placement': 'right'
	});

	if (suggestCountryUrl != null) {
		$.get(suggestCountryUrl, function handleSuggestedCountry(slug) {
			if (slug == '') {
				return;
			}

			var guessCountryLink = $('#js-guess-country-link');
			guessCountryLink.click(function chooseSuggestedCountry() {
				guessCountryLink.addClass('hidden');
				chooseCountryBySlug(slug);
			});
			guessCountryLink.removeClass('hidden');
		});
	}
}

function chooseCountryBySlug(slug) {
	var countrySelectBox = $('#country').selectize();
	var selectize = countrySelectBox[0].selectize;
	selectize.setValue(slug);
}
