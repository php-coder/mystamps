//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//

function populateTransactionDateWithTodayDate() {
	var today = DateUtils.formatDateToDdMmYyyy(new Date());
	$('#date').val(today);
}

function getCurrencyByCatalogName(catalog) {
	switch (catalog) {
		case 'MICHEL':
		case 'YVERT':
			return [ '\u20AC', 'EUR' ];
		case 'SCOTT':
			return [ '$', 'USD' ];
		case 'GIBBONS':
			return [ '\u00A3', 'GBP' ];
		case 'SOLOVYOV':
		case 'ZAGORSKI':
			return [ '\u20BD', 'RUB' ];
	}
}

function initPriceCatalog() {
	var catalogNameElem = document.getElementById('price-catalog-name');
	if (catalogNameElem == null) {
		console.error("Couldn't initialize catalog name selector: element not found");
		return;
	}
	catalogNameElem.addEventListener('change', function changeCatalogCurrency(elem) {
		var name = this.value;
		var info = getCurrencyByCatalogName(this.value);
		var symbolElem = document.getElementById('js-catalog-price-symbol');
		if (symbolElem == null) {
			console.error("Couldn't change currency symbol: element not found");
		}
		var titleElem = document.getElementById('catalog-price');
		if (titleElem == null) {
			console.error("Couldn't change currency title: element not found");
		}
		symbolElem.innerText = info[0];
		titleElem.title = info[1];
	});
}
