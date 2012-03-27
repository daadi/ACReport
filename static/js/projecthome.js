$(document).ready(function() {
	$('#tickets').dataTable( {
			"bPaginate": false,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bStateSave": false,
			"bAutoWidth": true,
			"sDom": 'W<"clear">lfrtip',
			"oColumnFilterWidgets": {
				aiExclude: [ 0, 1 ],
				sSeparator: ',  ',
				bGroupTerms: true
			},
			"fnDrawCallback": function() {
				//alert( 'DataTables has redrawn the table' );
			}
	} )
	
//	.rowGrouping({
//			iGroupingColumnIndex: 3,
//			sGroupingColumnSortDirection: "asc"
//	});
;

	

});

function fnShowHide( iCol )
{
	/* Get the DataTables object again - this is not a recreation, just a get of the object */
	var oTable = $('#tickets').dataTable();

	var bVis = oTable.fnSettings().aoColumns[iCol].bVisible;
	oTable.fnSetColumnVis( iCol, bVis ? false : true );
}

