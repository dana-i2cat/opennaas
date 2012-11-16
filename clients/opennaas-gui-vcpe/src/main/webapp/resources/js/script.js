/*
 * OpenNaaS script
 */

/**
 * Ajax Sample Method that updates the header of a view
 */
function updateHeader() {
	$.ajax({
		type: "GET",
		url: "/opennaas-vcpe/secure/vcpeNetwork/getAjax",
		success: function(data) {
		    $('#ajaxUpdate').html(data);			    
		}
	});
}

/**
 * Ajax call to check if the VLAN is free in the environment
 * 
 * @param vcpeId
 * @param vlan
 */
function isVLANFree(vcpeId, vlan) {
	$.ajax({
		type: "GET",
		url: "/opennaas-vcpe/secure/vcpeNetwork/isVLANFree?vcpeId=" + vcpeId + "&vlan=" +  vlan,
		success: function(data) {
		    $('#ajaxUpdate').html(data);			    
		}
	});
}

/**
 * Ajax call to check if the IP is free in the environment
 * 
 * @param vcpeId
 * @param ip
 */
function isIPFree(vcpeId, ip) {
	$.ajax({
		type: "GET",
		url: "/opennaas-vcpe/secure/vcpeNetwork/isIPFree?vcpeId=" + vcpeId + "&ip=" + ip,
		success: function(data) {
		    $('#ajaxUpdate').html(data);			    
		}
	});
}

/**
 * Ajax call to check if the interface is free in the environment
 * 
 * @param vcpeId
 * @param iface
 * @param port
 */
function isInterfaceFree(vcpeId, iface, port) {
	$.ajax({
		type: "GET",
		url: "/opennaas-vcpe/secure/vcpeNetwork/isInterfaceFree?vcpeId=" + vcpeId + "&iface=" + iface +"&port=" + port,
		success: function(data) {
		    $('#ajaxUpdate').html(data);			    
		}
	});
}