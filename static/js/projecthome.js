$(document).ready(function() {
	jQuery.extend( jQuery.fn.dataTableExt.oSort, {
		"enum-pre": function ( a ) {
			if (typeof(a) != "string") { 
				return 0; 
			}

			switch( $(a).text().toLowerCase() ) {
				case "highest":   return 6;
				case "high":      return 5;
				case "normal":    return 4;
				case "low":       return 3;
				case "lowest":    return 2;
				default:          return 1;
			}
		},

		"enum-asc": function ( a, b ) {
			return ((a < b) ? -1 : ((a > b) ? 1 : 0));
		},

		"enum-desc": function ( a, b ) {
			return ((a < b) ? 1 : ((a > b) ? -1 : 0));
		}
	});

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
			},
		"aoColumns": [
			null,
			null,
			{ "sType": "enum" },
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null
		]
	});
	
	$('#projects').change(function() {
		document.location = "/projects/" + $(this).val() + "/";
	});
	
});
