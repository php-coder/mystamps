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
