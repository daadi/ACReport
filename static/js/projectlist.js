$(document).ready(function() {
	$('#projects').dataTable( {
			"bPaginate": false,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bStateSave": false,
			"bAutoWidth": true,
			"sDom": 'W<"clear">lrit'			
	});
});
