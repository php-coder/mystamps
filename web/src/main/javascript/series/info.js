//
// IMPORTANT:
// You must update Url.RESOURCES_VERSION each time whenever you're modified this file!
//

function populateTransactionDateWithTodayDate() {
	var today = DateUtils.formatDateToDdMmYyyy(new Date());
	$('#date').val(today);
}
