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
		heightStyle : "content",
		changestart : function() {
			clearJSPlumbStuff();
		},
		change : function() {
			setJSPlumbStuff();
		}
	});
	
	/* BoD block */
	$("#bod").accordion({
		collapsible : true,
		icons : false,
		autoHeight : true,
		heightStyle : "content",
		active: false,
		changestart : function() {
			clearJSPlumbStuff();
		},
		change : function() {
			setJSPlumbStuff();
		}
	});
	
	
	/* Customer block */
	$("#customer").accordion({
		collapsible : true,
		icons : false,
		heightStyle : "content",
		changestart : function() {
			clearJSPlumbStuff();
		},
		change : function() {
			setJSPlumbStuff();
		}
	});
	
	
	/* Protocols */
	$( "#bgp" ).accordion({
		collapsible: true,
		icons: false,
		heightStyle : "content",
		active: false,
		changestart : function() {
			clearJSPlumbStuff();
		},
		change : function() {
			setJSPlumbStuff();
		}
		});
	$( "#vrrp" ).accordion({
		collapsible: true,
		icons: false,
		heightStyle : "content",
		active: false,
		changestart : function() {
			clearJSPlumbStuff();
		},
		change : function() {
			setJSPlumbStuff();
		}
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

/**
 * jsPlumb stuff
 */
// set jsPlumb stuff
function setJSPlumbStuff() {
	// falten els div's de WAN, WAN master i WAN backup a dalt de tot
	// addConnection("bgp", "vrrp", "acc_body", 0.20, 1, 0.20, 0);
	// addConnection("bgp", "vrrp", "acc_body", 0.80, 1, 0.80, 0);
	
	// WAN master & backup -- lola & myre
	addConnection("up_master", "lr_master", "acc_body", 0.5, 1, 0.5, 0);
	addConnection("up_backup", "lr_backup", "acc_body", 0.5, 1, 0.5, 0);
	
	// lola & myre -- down & inter, master & backup
	addConnection("lr_master", "customer_master", "acc_body", 0.25, 1, 0.7, 0);
	addConnection("lr_master", "inter_master", "acc_body", 0.75, 1, 0.5, 0);
	addConnection("lr_backup", "inter_backup", "acc_body", 0.275, 1, 0.5, 0);
	addConnection("lr_backup", "customer_backup", "acc_body", 0.71, 1, 0.2, 0);
}

// add a connection and its endpoints
function addConnection(origin, destination, parent, originAnchorX,
		originAnchorY, destinationAnchorX, destinationAnchorY) {
	var originEndpoint = jsP.addEndpoint(origin, {
		anchor : [ originAnchorX, originAnchorY ],
		container : parent
	});
	var destinationEndpoint = jsP.addEndpoint(destination, {
		anchor : [ destinationAnchorX, destinationAnchorY ],
		container : parent
	});

	endpoints.push(originEndpoint);
	endpoints.push(destinationEndpoint);

	var connection = jsP.connect({
		source : originEndpoint,
		target : destinationEndpoint,
		container : $("#" + parent)
	});

	connections.push(connection);
}

// clear all jsPlumb stuff
function clearJSPlumbStuff() {
	jsPlumb.deleteEveryEndpoint();
	jsPlumb.detachAllConnections();

	// remove all overlays of each connection
	if (connections != null) {
		var connection = null;
		while ((connection = connections.pop()) != null) {
			connection.removeAllOverlays();
		}
	}

	// detach all endpoints
	if (endpoints != null) {
		var endpoint = null;
		while ((endpoint = endpoints.pop()) != null) {
			endpoint.detachAll();
		}
	}
}

// jsPlumb instance
var jsP;
//jsPlumb endpoints
var endpoints = new Array();
// jsPlumb connections
var connections = new Array();

$(function() {
	// initialize jsPlumb instance
	jsP = jsPlumb.getInstance({
		PaintStyle : {
			lineWidth : 1,
			strokeStyle : "#567567",
			outlineColor : "black",
			outlineWidth : 1
		},
		Connector : "Straight",
		Endpoint : "Blank"
	});

	// initialize endpoints and connections arrays
	endpoints = new Array();
	connections = new Array();

	// draw jsPlumb stuff when view stuff is ready
	jsPlumb.ready(function() {
		jsPlumb.importDefaults({
			ConnectorZIndex : 5
		});
		setJSPlumbStuff();
	});
});
