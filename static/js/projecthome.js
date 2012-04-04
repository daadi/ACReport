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
				aiExclude: [ 0, 1, 5, 7 ],
				sSeparator: ',  ',
				bGroupTerms: false
			}
	});
	
	$('#projects').change(function() {
		document.location = "/projects/" + $(this).val() + "/";
	});
});
