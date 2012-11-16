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
		url: "/opennaas-vcpe/secure/vcpeNetwork/isVLANFree?vcpeId=" + vcpeId + "vlan=" +  vlan,
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

/**
 * GUI View
 * 
 */
$(function() {
	/* vCPE customer block */
	$("#vcpe").accordion({
		collapsible : true,
		icons : false,
		autoHeight : true,
		heightStyle : "content"
	});
	
	/* BoD block */
	$("#bod").accordion({
		collapsible : true,
		icons : false,
		autoHeight : true,
		heightStyle : "content",
		active: false
	});
	
	
	/* Customer block */
	$("#customer").accordion({
		collapsible : true,
		icons : false,
		heightStyle : "content"
	});
	
	
	/* Protocols */
	$( "#bgp" ).accordion({
		collapsible: true,
		icons: false,
		heightStyle : "content",
		active: false
		});
	$( "#vrrp" ).accordion({
		collapsible: true,
		icons: false,
		heightStyle : "content",
		active: false
		});
	
	/* Routers */
	$( "#lr_master" ).accordion({
		collapsible: false,
		icons: false,
		heightStyle: "content"
		});
	$( "#lr_backup" ).accordion({
		collapsible: false,
		icons: false,
		heightStyle: "content"
		});
	
	/* Buttons */
	$( "#button" ).button();
	$( "#button2" ).button();
	$( "#button3" ).button();
	$( "#button4" ).button();
	$( "#button5" ).button();
	$( "#button6" ).button();
	$( "#button7" ).button();
	$( "#button8" ).button();
	$( "#button9" ).button();
	$( "#button10" ).button();
	$( "#button11" ).button();
	$( "#submitButton" ).button();

});
