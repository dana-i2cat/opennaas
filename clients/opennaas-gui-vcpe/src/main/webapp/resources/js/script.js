/**
 * OpenNaaS script
 */

/* Ajax Sample Method that updates the header of a view*/
function updateHeader() {
	$.ajax({
			type: "GET",
			url: "/opennaas-vcpe/secure/vcpeNetwork/getAjax",
			success: function(data) {
			    $('#ajaxUpdate').html(data);			    
			  }
		});

}