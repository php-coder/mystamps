//
// IMPORTANT:
// You must update Url.RESOURCES_VERSION each time whenever you're modified this file!
//

var CatalogUtils = {
	
	/** @public */
	expandNumbers: function(input) {
		if (input === '' || input === null || typeof(input) === 'undefined') {
			return input;
		}
		
		if (input.indexOf('-') < 0 && input.indexOf('/') < 0) {
			return input;
		}
		
		if (! /^\s*[0-9]+[-\/][0-9]+(,[ ]*[0-9]+([-\/][0-9]+)?)*\s*$/.test(input)) {
			return input;
		}
		
		var ranges = input.replace(' ', '').split(','),
			result = [];
		// FIXME: use jQuery.each() + jQuery.map()
		for (var i = 0; i < ranges.length; i++) {
			var range = ranges[i];
			result.push(this.expandRange(range));
		}
		
		return result.join(',');
	},
	
	/** @private */
	expandRange: function(range) {
		var parts = range.split(/[-\/]/);
		if (parts.length != 2) {
			return range;
		}
		
		var from = parts[0],
			to = parts[1],
			start = parseInt(from, 10);
		if (isNaN(start)) {
			return range;
		}
		
		var end = parseInt(to, 10);
		if (isNaN(end)) {
			return range;
		}
		
		if (end < start) {
			var lengthOfSharedPart = from.length - to.length,
				sharedPart = from.substring(0, lengthOfSharedPart),
				correctedEnd = sharedPart + to,
				newEnd = parseInt(correctedEnd, 10);
			if (isNaN(newEnd)) {
				return range;
			}
			
			// when first number has leading zero(s)
			if (newEnd < start) {
				return range;
			}
			
			end = newEnd;
		}
		
		var result = [];
		for (var i = start; i <= end; i++) {
			result.push(i);
		}
		
		return result.join(',');
	}
	
}
