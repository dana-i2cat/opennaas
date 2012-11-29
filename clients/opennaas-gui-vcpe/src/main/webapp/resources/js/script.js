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
 * Ajax call to check if the interface is free in the environment
 * 
 * @param vcpeId
 * @param iface
 * @param port
 */
function isInterfaceFree(vcpeId, iface, port) {
	$.ajax({
		type: "GET",
		url: "/opennaas-vcpe/secure/vcpeNetwork/isInterfaceFree?vcpeId=" + vcpeId + "&iface=" + iface.value +"&port=" + port.value,
		success: function(data) {
			if (data == 'false') {
				// Case not available
				// First delete the tooltip
		    	$("#tooltip").remove();
		    	// Add the new tooltip, error classes and disable inputs
				$(iface).after("<div id='tooltip'>The Interface is not available</div>");
				iface.className = 'error';
				port.className = 'error';	
		    	// Show the tooltip
				$("#tooltip").show("fade", {}, 400);
			    $("#tooltip").click(function() {
			    	// On click hide tooltip 
			    	$(this).hide("fade", {}, 400); 
			    	$(this).remove();
			    });
			} else {
				// Case available, revert changes
		    	iface.className = ''; 
		    	port.className = ''; 
				$("#tooltip").hide("fade", {}, 400);
		    	$("#tooltip").remove();
			}
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
		url: "/opennaas-vcpe/secure/vcpeNetwork/isIPFree?vcpeId=" + vcpeId + "&ip=" + ip.value,
		success: function(data) {
			if (data == 'false') {
				// Case not available
				// First delete the tooltip
		    	$("#tooltip").remove();
		    	// Add the new tooltip, error classes and disable inputs
				$(ip).after("<div id='tooltip'>The IP Address is not available</div>");
				ip.className = 'error';	
				// Show the tooltip
				$("#tooltip").show("fade", {}, 400);
			    $("#tooltip").click(function() {
			    	// On click hide tooltip 
			    	$(this).hide("fade", {}, 400); 
			    	$(this).remove();
			    });
			} else {
				// Case available, revert changes
				ip.className = '';
				$("#tooltip").hide("fade", {}, 400);
		    	$("#tooltip").remove();

			}
		}
	});
}

/**
 * Ajax call to check if the VLAN is free in the environment
 * 
 * @param ifaceName
 * @param vcpeId
 * @param vlan
 */
function isVLANFree(vcpeId, vlan, ifaceName) {
	$.ajax({
		type: "GET",
		url: "/opennaas-vcpe/secure/vcpeNetwork/isVLANFree?vcpeId=" + vcpeId + "&vlan=" +  vlan.value + "&ifaceName=" + ifaceName.value,
		success: function(data) {
			if (data == 'false') {
				// Case not available
				// First delete the tooltip
		    	$("#tooltip").remove();
		    	// Add the new tooltip, error classes and disable inputs
				$(vlan).after("<div id='tooltip'>The VLAN is not available</div>");
				vlan.className = 'error';	
				// Show the tooltip
				$("#tooltip").show("fade", {}, 400);
			    $("#tooltip").click(function() {
			    	// On click hide tooltip 
			    	$(this).hide("fade", {}, 400); 
			    	$(this).remove();
			    });
			} else {
				// Case available, revert changes
				vlan.className = '';
				$("#tooltip").hide("fade", {}, 400);
		    	$("#tooltip").remove();
			}
		}
	});
}

/**
 * GUI View
 * 
 */
$(function() {
	
	// only apply accordion styles when createVCPENetwork.jsp is loaded
	if($("#home_body").length) {
		jsPlumbNecessary = true;
		/**
		 * Home view
		 */
		/* WAN */
		$("#wan").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			active : false,
			beforeActivate : function() {
				clearJSPlumbHome();
			},
			activate : function() {
				setJSPlumbHome();
			}
		});
		
		/* Physical routers */
		$("#cpe_master").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {
				activeHeader: "ui-icon-check"
			}
		});
		$("#cpe_backup").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {				
				activeHeader: "ui-icon-check"
			}
		});
		$("#cpe_core").accordion({
			collapsible : false,
			icons : false,
			heightStyle : "content"
		});		
		
		/* BoD block */
		$("#cpe_bod").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			active : false
		});
	
		/* Client block */
		$("#cpe_client").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			active : false
		});
		
	}
	// only apply accordion styles when createVCPENetwork.jsp is loaded
	if($("#createVCPENetwork").length) {
		jsPlumbNecessary = true;
		/* vCPE client block */
		$("#vcpe").accordion({
			collapsible : true,
			icons : false,
			autoHeight : true,
			heightStyle : "content",
			beforeActivate : function() {
				clearJSPlumbStuff();
			},
			activate : function(event, ui) {
				var active = $("#vcpe").accordion("option", "active");
				vCPEvisible = !(typeof active == 'boolean' && active == false);
				setJSPlumbStuff(vCPEvisible);
			}
		});
		
		/* BoD block */
		$("#bod").accordion({
			collapsible : true,
			icons : false,
			autoHeight : true,
			heightStyle : "content",
			active: false,
			beforeActivate : function() {
				clearJSPlumbStuff();
			},
			activate : function() {
				setJSPlumbStuff(vCPEvisible);
			}
		});
		
		
		/* Client block */
		$("#client").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			beforeActivate : function() {
				clearJSPlumbStuff();
			},
			activate : function() {
				setJSPlumbStuff(vCPEvisible);
			}
		});
		
		
		/* Protocols */
		$( "#bgp" ).accordion({
			collapsible: true,
			icons: false,
			heightStyle : "content",
			active: false,
			beforeActivate : function() {
				clearJSPlumbStuff();
			},
			activate : function() {
				setJSPlumbStuff(vCPEvisible);
			}
			});
		$( "#vrrp" ).accordion({
			collapsible: true,
			icons: false,
			heightStyle : "content",
			active: false,
			beforeActivate : function() {
				clearJSPlumbStuff();
			},
			activate : function() {
				setJSPlumbStuff(vCPEvisible);
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
	}
	
	/* Buttons */
	$("#button").button();
	$("#button2").button();
	$("#button3").button();
	$("#button4").button();
	$("#button5").button();
	$("#button6").button();
	$("#button7").button();
	$("#button8").button();
	$("#button9").button();
	$("#button10").button();
	$("#button11").button();
	$("#button12").button();
	$("#submitButton").button();
	$("#waitingButton").button({
		icons : {
			primary : 'ui-icon-newwin'
		}
	});
	$("#homeButton").button({		
		icons : {
			primary : 'ui-icon-home'
		}
	});
	$("#logoutButton").button({		
		icons : {
			secondary : 'ui-icon-power'
		}
	});
	$("#submitLogin").button({		
		icons : {
			primary : 'ui-icon-triangle-1-e'
		}
	});

	/**
	 * Waiting dialog
	 */
	// Link to open the dialog
	$("#submitButton").click(function(event) {
		$("#pleaseWait").dialog("open");
	});

	$("#pleaseWait").dialog({
		modal : true,
		autoOpen : false,
		width : 400,
		resizable : false,
		draggable : false,
		closeOnEscape : false,
		open : function() {
			$('.ui-widget-overlay').hide().fadeIn();
			$(".ui-dialog-titlebar-close", $(this).parent()).hide();
		},
		show : "fade",
		hide : "fade"
	});
	
	/**
	 * Language selection
	 */    
    $( "#languages" ).buttonset( {
    	 icons: {
             primary: "ui-icon-flag"
         }
    });        
	// English
	$("#english").click(function(event) {
		open: 
			window.open("?locale=en_gb", "_self");
	});	
	// Spanish
	$("#spanish").click(function(event) {
		open: 
			window.open("?locale=es_es", "_self");			
	});	
	// Check language to activate button
	if ($.getUrlVar('locale') == "es_es")
	{
		$("#spanish").attr("checked", "checked").button("refresh");
	}
	else
	{
		$("#english").attr("checked", "checked").button("refresh");
	}
	
});

//Read a page's GET URL variables and return them as an associative array.
$.extend({
	  getUrlVars: function(){
	    var vars = [], hash;
	    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
	    for(var i = 0; i < hashes.length; i++)
	    {
	      hash = hashes[i].split('=');
	      vars.push(hash[0]);
	      vars[hash[0]] = hash[1];
	    }
	    return vars;
	  },
	  getUrlVar: function(name){
	    return $.getUrlVars()[name];
	  }
	});


/**
 * jsPlumb home
 */
// set jsPlumb stuff
function setJSPlumbHome() {
	
	// WAN -- WAN master & backup
	addConnection("wan", "wan_master", "home_body", 0.302, 1, 0.5, 0, false);
	addConnection("wan", "wan_backup", "home_body", 0.7, 1, 0.5, 0, false);
	
	// WAN master & backup -- router master & backup
	addConnection("wan_master", "cpe_master", "home_body", 0.5, 1, 0.6, 0, false);
	addConnection("wan_backup", "cpe_backup", "home_body", 0.5, 1, 0.4, 0, false);
	
	// router master -- client master & inter master
	addConnection("cpe_master", "cpe_client_master", "home_body", 0.22, 1, 0.5, 0, false);
	addConnection("cpe_master", "cpe_inter_master", "home_body", 0.83, 1, 0.5, 0, false);
	
	// router master -- client master & inter master
	addConnection("cpe_backup", "cpe_inter_backup", "home_body", 0.18, 1, 0.5, 0, false);
	addConnection("cpe_backup", "cpe_client_backup", "home_body", 0.78, 1, 0.5, 0, false);
	
	// client master & backup -- client
	addConnection("cpe_client_master", "cpe_client", "home_body", 0.5, 1, 0.165, 0, false);
	addConnection("cpe_client_backup", "cpe_client", "home_body", 0.5, 1, 0.845, 0, false);
	
	// inter master & backup -- bod
	addConnection("cpe_inter_master", "cpe_bod", "home_body", 0.5, 1, 0.39, 0, false);
	addConnection("cpe_inter_backup", "cpe_bod", "home_body", 0.5, 1, 0.615, 0, false);	
}


/**
 * jsPlumb create and view
 */
// set jsPlumb stuff
function setJSPlumbStuff(setExtra) {
	// WAN master & backup -- lola & myre
	addConnection("up_master", "lr_master", "acc_body", 0.5, 1, 0.5, 0, false);
	addConnection("up_backup", "lr_backup", "acc_body", 0.5, 1, 0.5, 0, false);
	
	// lola & myre -- down & inter, master & backup
	addConnection("lr_master", "client_master", "acc_body", 0.25, 1, 0.7, 0, false);
	addConnection("lr_master", "inter_master", "acc_body", 0.75, 1, 0.5, 0, false);
	addConnection("lr_backup", "inter_backup", "acc_body", 0.275, 1, 0.5, 0, false);
	addConnection("lr_backup", "client_backup", "acc_body", 0.71, 1, 0.2, 0, false);
	
	// inter master -- inter backup
	addConnection("inter_master", "inter_backup", "acc_body", 1, 0.5, 0, 0.5, false);
	
	// client master & client backup -- client down master & client down backup
	if(setExtra) {
		addConnection("client_master", "client_down_master", "body", 0.5, 1, 0.5, 0, true);
		addConnection("client_backup", "client_down_backup", "body", 0.5, 1, 0.5, 0, true);
	}
	
	// client down master & client down backup -- client
	addConnection("client_down_master", "client", "body", 0.5, 1, 0.16, 0, false);
	addConnection("client_down_backup", "client", "body", 0.5, 1, 0.845, 0, false);	
}

// add a connection and its endpoints
function addConnection(origin, destination, parent, originAnchorX,
		originAnchorY, destinationAnchorX, destinationAnchorY, extra) {
	var originEndpoint = jsP.addEndpoint(origin, {
		anchor : [ originAnchorX, originAnchorY ],
		container : parent
	});
	var destinationEndpoint = jsP.addEndpoint(destination, {
		anchor : [ destinationAnchorX, destinationAnchorY ],
		container : parent
	});
	if(extra) {
		extra_endpoints.push(originEndpoint);
		extra_endpoints.push(destinationEndpoint);
	} else {
		intra_endpoints.push(originEndpoint);
		intra_endpoints.push(destinationEndpoint);
	}

	var connection = jsP.connect({
		source : originEndpoint,
		target : destinationEndpoint,
		container : $("#" + parent)
	});

	if(extra) {
		extra_connections.push(connection);
	} else {
		intra_connections.push(connection);

	}
}

// clear all jsPlumb stuff
function clearJSPlumbStuff() {
	jsPlumb.deleteEveryEndpoint();
	jsPlumb.detachAllConnections();

	// remove all overlays of each connection
	if (intra_connections != null) {
		var connection = null;
		while ((connection = intra_connections.pop()) != null) {
			connection.removeAllOverlays();
		}
	}
	
	if (extra_connections != null) {
		var connection = null;
		while ((connection = extra_connections.pop()) != null) {
			connection.removeAllOverlays();
		}
	}

	// detach all endpoints
	if (intra_endpoints != null) {
		var endpoint = null;
		while ((endpoint = intra_endpoints.pop()) != null) {
			endpoint.detachAll();
		}
	}
	
	if (extra_endpoints != null) {
		var endpoint = null;
		while ((endpoint = extra_endpoints.pop()) != null) {
			endpoint.detachAll();
		}
	}
}

//clear all jsPlumb stuff
function clearJSPlumbHome() {
	jsPlumb.deleteEveryEndpoint();
	jsPlumb.detachAllConnections();

	// remove all overlays of each connection
	if (intra_connections != null) {
		var connection = null;
		while ((connection = intra_connections.pop()) != null) {
			connection.removeAllOverlays();
		}
	}
	
	if (extra_connections != null) {
		var connection = null;
		while ((connection = extra_connections.pop()) != null) {
			connection.removeAllOverlays();
		}
	}

	// detach all endpoints
	if (intra_endpoints != null) {
		var endpoint = null;
		while ((endpoint = intra_endpoints.pop()) != null) {
			endpoint.detachAll();
		}
	}
}

// jsPlumb instance necessary
var jsPlumbNecessary = false;

// jsPlumb instance
var jsP;
//jsPlumb intra accordion endpoints
var intra_endpoints = new Array();
// jsPlumb intra accordion connections
var intra_connections = new Array();

//jsPlumb extra accordion endpoints
var extra_endpoints = new Array();
// jsPlumb extra accordion connections
var extra_connections = new Array();

// state of vCPE accordion
var vCPEvisible = true;

$(function() {
	if(jsPlumbNecessary){
		// initialize jsPlumb instance
		jsP = jsPlumb.getInstance({
			PaintStyle : {
				lineWidth : 1,
				strokeStyle : "#567567",
				outlineColor : "#6E6E6E",
				outlineWidth : 1
			},
			Connector : "Straight",
			Endpoint : "Blank"
		});
	
		// initialize endpoints and connections arrays
		intra_endpoints = new Array();
		intra_connections = new Array();
		extra_endpoints = new Array();
		extra_connections = new Array();
	
		// draw jsPlumb stuff when view stuff is ready
		jsPlumb.ready(function() {
			jsPlumb.importDefaults({
				ConnectorZIndex : 5
			});
			if($("#createVCPENetwork").length){
				setJSPlumbStuff(vCPEvisible);
			} else if($("#home_body").length){
				setJSPlumbHome();
			}
		});
	}
});
