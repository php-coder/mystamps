//
// IMPORTANT:
// You must update Url.RESOURCES_VERSION each time whenever you're modified this file!
//

var DateUtils = {
	
	/** @public */
	formatDateToDdMmYyyy: function(date) {
		return date
			.toISOString()  // "2018-06-12T16:59:54.451Z"
			.split(/[-:T]/) // [ "2018", "06", "12", "17", "00", "26.244Z" ]
			.slice(0, 3)    // [ "2018", "06", "12" ]
			.reverse()      // [ "12", "06", "2018" ]
			.join('.');     // "12.06.2018"
	}
	
}
