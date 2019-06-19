//
// IMPORTANT:
// You must update ResourceUrl.RESOURCES_VERSION each time whenever you're modified this file!
//

function initPage() {
	$('#url').on('change', function tryToSetParticipantGroup() {
		var currentGroup = $('#group option:selected').val();
		if (currentGroup != '') {
			// don't change what has been selected already to prevent
			// overwriting user's choice
			return;
		}
		
		try {
			var url = new URL($(this).val());
			var newGroupName = url.hostname.replace(/^www\./, '');
			$('#group option')
				.filter(function findGroupByName() {
					return this.text.replace(/^www\./, '') == newGroupName;
				})
				.prop('selected', true);
			
		} catch (ignoredError) {
			// if we aren't able to parse URL, it's a non-fatal error: maybe
			// user provided an invalid URL? Anyway, user always can select
			// group manually.
		}
	});
}
