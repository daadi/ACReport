$(document).ready(function() {
	$('#tasks').dataTable( {
			"bPaginate": false,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bStateSave": false,
			"bAutoWidth": true,
			"sDom": 'W<"clear">lfrit',
			"oColumnFilterWidgets": {
				aiExclude: [ 0, 1, 6, 8 ],
				sSeparator: ',  ',
				bGroupTerms: false
			}
	});
	
	$('#projects').change(function() {
		document.location = "/projects/" + $(this).val() + "/";
	});
});
