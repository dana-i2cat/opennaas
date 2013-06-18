/*
 * OpenNaaS script
 */

/**
 * Javascript for menu
 */
$(function() {
	$("#_menu").menu();
});

/**
 * Javascript for tabs
 */
$(function() {
	$("#tabs").tabs();
});


/**
 * Javascript for link confirm
 */
$(document).ready(function() {
        
        $("#jsonTable").tablesorter(); 

        $(".deleteButton").live("click", function () {
            $(this).parent().parent().remove();
        });

        $('th').click(function() {
            $("#jsonTable th").css('backgroundColor', '#c3d9ff');
            $("#jsonTable th").css('color', 'black');
            $(this).css('backgroundColor', 'rgb(5, 110, 207)');
            $(this).css('color', 'rgb(255, 255, 255)');
        });

	$("#dialog").dialog({
		autoOpen : false,
		modal : true,
		bgiframe : true,
		width : 300,
		height : 150,
		resizable : false,
		draggable : false
	});

	$(".link_confirm").click(function(e) {
		e.preventDefault();
		var theHREF = $(this).attr("href");
		$("#dialog").dialog('option', 'buttons', {
			"Confirm" : function() {
				$("#pleaseWait").dialog("open");
				window.location.href = theHREF;
			},
			"Cancel" : function() {
				$(this).dialog("close");
			}
		});
		$("#dialog").dialog("open");
	});

	$(".button_confirm").click(function(e) {
		e.preventDefault();
		var $form = $(this).closest('form');
		var action = $(this).attr("formaction");
		$form.attr("action", action);
		$("#dialog").dialog('option', 'buttons', {
			"Confirm" : function() {
				$("#pleaseWait").dialog("open");
				$form.submit();
			},
			"Cancel" : function() {
				$(this).dialog("close");
			}
		});
		$("#dialog").dialog("open");
	});
});

/**
 * Ajax call to check if the interface is free in the environment
 * 
 * @param vcpeId
 * @param iface
 * @param port
 */
function isInterfaceFree(vcpeId, router, iface, port, message) {
	$.ajax({
		type : "GET",
		url : "/opennaas-vcpe/secure/vcpeNetwork/isInterfaceFree?" 
				+ "vcpeId="
				+ vcpeId
				+ "&router="
				+ router				
				+ "&iface="
				+ iface.value
				+ "&port="
				+ port.value,
		success : function(data) {
			if (data == 'false') {
				// Case not available
				// First delete the tooltip
				$("#tooltip").remove();
				// Add the new tooltip, error classes and disable inputs
				$(iface)
						.after(
								"<div id='tooltip'>" + message + "</div>");
				iface.className = 'error';
				port.className = 'error';
				// Show the tooltip
				$("#tooltip").show("fade", {}, 200);
				$("#tooltip").click(function() {
					// On click hide tooltip
					$(this).hide("fade", {}, 200);
					$(this).remove();
				});
			} else {
				// Case available, revert changes
				iface.className = '';
				port.className = '';
				$("#tooltip").hide("fade", {}, 200);
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
function isIPFree(vcpeId, router, ip, message) {
	$.ajax({
		type : "GET",
		url : "/opennaas-vcpe/secure/vcpeNetwork/isIPFree?" 
				+"vcpeId="
				+ vcpeId 
				+ "&router="
				+ router	
				+ "&ip=" 
				+ ip.value,
		success : function(data) {
			if (data == 'false') {
				// Case not available
				// First delete the tooltip
				$("#tooltip").remove();
				// Add the new tooltip, error classes and disable inputs
				$(ip)
						.after(
								"<div id='tooltip'>" + message + "</div>");
				ip.className = 'error';
				// Show the tooltip
				$("#tooltip").show("fade", {}, 200);
				$("#tooltip").click(function() {
					// On click hide tooltip
					$(this).hide("fade", {}, 200);
					$(this).remove();
				});
			} else {
				// Case available, revert changes
				ip.className = '';
				$("#tooltip").hide("fade", {}, 200);
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
function isVLANFree(vcpeId, router, vlan, ifaceName, message) {
	$.ajax({
		type : "GET",
		url : "/opennaas-vcpe/secure/vcpeNetwork/isVLANFree?" 
				+ "vcpeId=" 
				+ vcpeId
				+ "&router="
				+ router	
				+ "&vlan=" 
				+ vlan.value 
				+ "&ifaceName=" 
				+ ifaceName.value,
		success : function(data) {
			if (data == 'false') {
				// Case not available
				// First delete the tooltip
				$("#tooltip").remove();
				// Add the new tooltip, error classes and disable inputs
				$(vlan).after(
						"<div id='tooltip'>" + message + "</div>");
				vlan.className = 'error';
				// Show the tooltip
				$("#tooltip").show("fade", {}, 200);
				$("#tooltip").click(function() {
					// On click hide tooltip
					$(this).hide("fade", {}, 200);
					$(this).remove();
				});
			} else {
				// Case available, revert changes
				vlan.className = '';
				$("#tooltip").hide("fade", {}, 200);
				$("#tooltip").remove();
			}
		}
	});
}

/**
 * GUI create and update view
 */
$(function() {

	// client update IPs

	/* Client block */
	$("#client_box").accordion({
		collapsible : true,
		icons : false,
		heightStyle : "content",
		active : true
	});

	$("#vrrp_box").accordion({
		collapsible : true,
		icons : false,
		heightStyle : "content",
		active : true
	});

	// accordion styles when physical sp is loaded
	if ($("#spPhysicalForm").length) {
		jsPlumbNecessary = true;
		/**
		 * Home view
		 */
		/* WAN */
		$("#wan").accordion({
			collapsible : false,
			icons : false,
			heightStyle : "content",
			active : false,
			beforeActivate : function() {
				clearJSPlumbSPPhysical();
			},
			activate : function() {
				setJSPlumbSPPhysical();
			}
		});

		/* Physical routers */
		$("#cpe_master").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {
				activeHeader : "ui-icon-check"
			}
		});
		$("#cpe_backup").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {
				activeHeader : "ui-icon-check"
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

	// accordion styles when physical sp is loaded
	if ($("#mpPhysicalForm").length) {
		jsPlumbNecessary = true;
		
		$("#vcpe_mp_physical").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {
				activeHeader : "ui-icon-check"
			},
			beforeActivate : function() {
				clearJSPlumbMPPhysical();
			},
			activate : function() {
				setJSPlumbMPPhysical();
			}
		});
		
		/* MP Physical view */
		$("#network_wan1").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {
				activeHeader : "ui-icon-check"
			}
		});

		/* MP Physical view */
		$("#network_wan2").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {
				activeHeader : "ui-icon-check"
			}
		});
		
		$("#network_client").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {
				activeHeader : "ui-icon-check"
			}
		});
		
		$("#phy_router").accordion({
			collapsible : false,
			beforeActivate : function() {
				clearJSPlumbMPPhysical();
			},
			activate : function() {
				setJSPlumbMPPhysical();
			},
			icons : {
				activeHeader : "ui-icon-check"
			}
		});
	}
	
	// only apply accordion styles when spLogicalForm.jsp is loaded
	if ($("#spLogicalForm").length) {
		jsPlumbNecessary = true;
		/* vCPE client block */
		$("#vcpe").accordion({
			collapsible : false,
			icons : false,
			autoHeight : true,
			heightStyle : "content",

		});

		/* BoD block */
		$("#bod").accordion({
			collapsible : true,
			icons : false,
			autoHeight : true,
			heightStyle : "content",
			active : false,
			beforeActivate : function() {
				clearJSPlumbSPLogical();
			},
			activate : function(event, ui) {
				var active = $("#bod").accordion("option", "active");
				bodVisible = !(typeof active == 'boolean' && active == false);
				if (bodVisible) {
					$("#bod").css('zIndex', '');
				} else {
					$("#bod").css('zIndex', 100);
				}
				setJSPlumbSPLogical(topologyVisible, bodVisible);
			}
		});

		/* Client block */
		$("#client").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			active : false,
			beforeActivate : function() {
				clearJSPlumbSPLogical();
			},
			activate : function() {
				setJSPlumbSPLogical(topologyVisible, bodVisible);
			}
		});

		/* Protocols */
		$("#bgp").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			active : false,
			beforeActivate : function() {
				clearJSPlumbSPLogical();
			},
			activate : function() {
				setJSPlumbSPLogical(topologyVisible, bodVisible);
			}
		});

		$("#vrrp").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			active : false,
			beforeActivate : function() {
				clearJSPlumbSPLogical();
			},
			activate : function() {
				setJSPlumbSPLogical(topologyVisible, bodVisible);
			}
		});

		$("#vcpe_topology").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			active : false,
			beforeActivate : function() {
				clearJSPlumbSPLogical();
			},
			activate : function(event, ui) {
				var active = $("#vcpe_topology").accordion("option", "active");
				topologyVisible = !(typeof active == 'boolean' && active == false);
				setJSPlumbSPLogical(topologyVisible, bodVisible);
			}
		});

		$("#wan_logical").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			active : true,
			beforeActivate : function() {
				clearJSPlumbSPLogical();
			},
			activate : function(event, ui) {
				var active = $("#vcpe_topology").accordion("option", "active");
				topologyVisible = !(typeof active == 'boolean' && active == false);
				setJSPlumbSPLogical(topologyVisible, bodVisible);
			}
		});

		/* Routers */
		$("#lr_master").accordion({
			collapsible : false,
			icons : false,
			heightStyle : "content"
		});

		$("#lr_backup").accordion({
			collapsible : false,
			icons : false,
			heightStyle : "content"
		});
	}

	// accordion styles when physical sp is loaded
	if ($("#mpLogicalForm").length) {
		jsPlumbNecessary = true;
	
		/* MP Physical view */
		
		$("#vcpe_mp_logical").accordion({
			collapsible : true,
			active: false,
			heightStyle : "content",
			icons : {
				activeHeader : "ui-icon-check"
			},
			beforeActivate : function() {
				clearJSPlumbMPLogical();
			},
			activate : function() {
				setJSPlumbMPLogical(topologyVisible, bodVisible);
			}
		});
		
		$("#network_client_logical").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {
				activeHeader : "ui-icon-check"
			}
		});
		
		/* MP Physical view */
		$("#network_wan1_logical").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {
				activeHeader : "ui-icon-check"
			}
		});

		/* MP Physical view */
		$("#network_wan2_logical").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {
				activeHeader : "ui-icon-check"
			}
		});
		
		$("#iface_router_up2").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {
				activeHeader : "ui-icon-check"
			}
		});
		
		$("#iface_router_down").accordion({
			collapsible : false,
			heightStyle : "content",
			icons : {
				activeHeader : "ui-icon-check"
			}
		});
		
		$("#lrprovider1").accordion({
			collapsible : false,
			icons : {
				activeHeader : "ui-icon-check"
			}
		});

		$("#lrprovider2").accordion({
			collapsible : false,
			icons : {
				activeHeader : "ui-icon-check"
			}
		});

		$("#lrclient").accordion({
			collapsible : false,
			icons : {
				activeHeader : "ui-icon-check"
			}
		});
		
	}
	
	/* Buttons */

        $("#submitUpdateConfig").button();
        $("#defaultValues").button();
        $(".addRouteButton").button();
        $(".addCtrlInfoButton").button();
        $(".deleteButton").button();

	$("#button1").button();
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

	$("#selectTemplateButton").button();
	$("#updateIpButton").button();
	$("#waitingButton").button({
		icons : {
			primary : 'ui-icon-newwin'
		}
	});
	$("#button6").click(function(event) {
		event.preventDefault();
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
	 * Firewall table
	 */
	$("#fwRemove1").button();
	$("#fwRemove1").click(function(event) {
		event.preventDefault();
	});

	$("#fwRemove2").button();
	$("#fwRemove2").click(function(event) {
		event.preventDefault();
	});

	$("#fwRemove3").button();
	$("#fwRemove3").click(function(event) {
		event.preventDefault();
	});

	$("#fwAdd").button();
	$("#fwAdd").click(function(event) {
		event.preventDefault();
	});

	/**
	 * Waiting dialog
	 */
	// Link to open the dialog
	$("#submitButton").click(function(event) {
		$("#pleaseWait").dialog("open");
	});

	$("#updateIpButton").click(function(event) {
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
	$("#languages").buttonset({
		icons : {
			primary : "ui-icon-flag"
		}
	});
	// English
	$("#english").click(function(event) {
		open: window.open("/opennaas-vcpe/secure/vcpeNetwork/home?locale=en_gb", "_self");
	});
	// Spanish
	$("#spanish").click(function(event) {
		open: window.open("/opennaas-vcpe/secure/vcpeNetwork/home?locale=es_es", "_self");
	});
	// Check language to activate button
	if ($.getUrlVar('locale') == "es_es") {
		$("#spanish").attr("checked", "checked").button("refresh");
	} else {
		$("#english").attr("checked", "checked").button("refresh");
	}

	$("#radioset").buttonset();
	$("#radioset2").buttonset();
	
	
	/**
	 * Template selection
	 */	
	$("#selectable").selectable();
	$("#selectTemplateButton").click(function(event) {	
		if ($('#selectable .ui-selected').attr('id') != undefined) {
			if ($('#selectable .ui-selected').attr('id') == 'sp_vcpe') {
				url = "/opennaas-vcpe/secure/noc/vcpeNetwork/singleProvider/physical?templateType=" + $('#selectable .ui-selected').attr('id');
			} else {
				url = "/opennaas-vcpe/secure/noc/vcpeNetwork/multipleProvider/physical?templateType=" + $('#selectable .ui-selected').attr('id');				
			}
			open: window.open(url, "_self");
		}
		else {
			$("#noTemplate").dialog('option', 'buttons', {			
				"Go back" : function() {
					$(this).dialog("close");
				}
			});
			$(".ui-dialog-titlebar-close").hide();
			$("#noTemplate").dialog("open");
		}
	});	
	
	$("#noTemplate").dialog({
		autoOpen : false,
		modal : true,
		bgiframe : true,
		width : 300,
		height : 200,
		resizable : false,
		draggable : false
	});
	
});

// Read a page's GET URL variables and return them as an associative array.
$.extend({
	getUrlVars : function() {
		var vars = [], hash;
		var hashes = window.location.href.slice(
				window.location.href.indexOf('?') + 1).split('&');
		for ( var i = 0; i < hashes.length; i++) {
			hash = hashes[i].split('=');
			vars.push(hash[0]);
			vars[hash[0]] = hash[1];
		}
		return vars;
	},
	getUrlVar : function(name) {
		return $.getUrlVars()[name];
	}
});

/**
 * jsPlumb home
 */
// set jsPlumb stuff
function setJSPlumbSPPhysical() {

	// WAN -- WAN master & backup
	addConnection("wan", "wan_master", "spPhysicalForm", 0.302, 1, 0.5, 0, false);
	addConnection("wan", "wan_backup", "spPhysicalForm", 0.7, 1, 0.5, 0, false);

	// WAN master & backup -- router master & backup
	addConnection("wan_master", "cpe_master", "spPhysicalForm", 0.5, 1, 0.6, 0,
			false);
	addConnection("wan_backup", "cpe_backup", "spPhysicalForm", 0.5, 1, 0.4, 0,
			false);

	// router master -- client master & inter master
	addConnection("cpe_master", "cpe_client_master", "spPhysicalForm", 0.22, 1, 0.5,
			0, false);
	addConnection("cpe_master", "cpe_inter_master", "spPhysicalForm", 0.83, 1, 0.5,
			0, false);

	// router master -- client master & inter master
	addConnection("cpe_backup", "cpe_inter_backup", "spPhysicalForm", 0.18, 1, 0.5,
			0, false);
	addConnection("cpe_backup", "cpe_client_backup", "spPhysicalForm", 0.78, 1, 0.5,
			0, false);

	// client master & backup -- client
	addConnection("cpe_client_master", "cpe_client", "spPhysicalForm", 0.5, 1,
			0.165, 0, false);
	addConnection("cpe_client_backup", "cpe_client", "spPhysicalForm", 0.5, 1,
			0.845, 0, false);

	// inter master & backup -- bod
	addConnection("cpe_inter_master", "cpe_bod", "spPhysicalForm", 0.5, 1, 0.39, 0,
			false);
	addConnection("cpe_inter_backup", "cpe_bod", "spPhysicalForm", 0.5, 1, 0.615, 0,
			false);
}

/**
 * jsPlumb create and view
 */
// set jsPlumb stuff
function setJSPlumbSPLogical(topologyVisible, bodVisible) {
	// WAN -- UP master & backup
	addConnection("wan_logical", "up_master", "spLogicalForm", 0.268, 1,
			0.5, 0, false);
	addConnection("wan_logical", "up_backup", "spLogicalForm", 0.738, 1,
			0.5, 0, false);

	if (topologyVisible) {
		// UP master & backup -- LR master & backup
		addConnection("up_master", "lr_master", "spLogicalForm", 0.5, 1,
				0.425, 0, true);
		addConnection("up_backup", "lr_backup", "spLogicalForm", 0.5, 1,
				0.59, 0, true);

		// CLIENT DOWN master & backup -- CLIENT master & backup
		addConnection("client_master", "client_down_master",
				"spLogicalForm", 0.5, 1, 0.5, 0, true);
		addConnection("client_backup", "client_down_backup",
				"spLogicalForm", 0.5, 1, 0.5, 0, true);
	} else {
		// UP master & backup -- vCPE
		addConnection("up_master", "vcpe_topology", "spLogicalForm", 0.5,
				1, 0.248, 0, true);
		addConnection("up_backup", "vcpe_topology", "spLogicalForm", 0.5,
				1, 0.758, 0, true);

		// vCPE -- CLIENT master & backup
		addConnection("vcpe_topology", "client_down_master",
				"spLogicalForm", 0.16, 1, 0.5, 0, true);
		addConnection("vcpe_topology", "client_down_backup",
				"spLogicalForm", 0.84, 1, 0.5, 0, true);
	}

	// LR master & backup -- CLIENT DOWN master & backup, down & inter
	addConnection("lr_master", "client_master", "vcpe_routers", 0.2, 1, 0.5, 0,
			false);
	addConnection("lr_backup", "client_backup", "vcpe_routers", 0.78, 1, 0.5,
			0, false);
	addConnection("lr_master", "inter_master", "vcpe_routers", 0.76, 1, 0.5, 0,
			false);
	addConnection("lr_backup", "inter_backup", "vcpe_routers", 0.23, 1, 0.5, 0,
			false);

	if (bodVisible && topologyVisible) {
		// inter master & backup -- BoD inter
		addConnection("inter_master", "bod_inter", "spLogicalForm", 0.5,
				1, 0.13, 0, true);
		addConnection("inter_backup", "bod_inter", "spLogicalForm", 0.5,
				1, 0.84, 0, true);
	} else if (bodVisible && !topologyVisible) {
		// inter master & backup -- CLIENT master & backup
		addConnection("vcpe_topology", "bod_inter",
				"spLogicalForm", 0.385, 1, 0.12, 0, true);
		addConnection("vcpe_topology", "bod_inter",
				"spLogicalForm", 0.615, 1, 0.85, 0, true);
	} else if (!bodVisible && topologyVisible) {
		// inter master & backup -- BoD
		addConnection("inter_master", "bod", "spLogicalForm", 0.5,
				1, 0.395, 0, true);
		addConnection("inter_backup", "bod", "spLogicalForm", 0.5,
				1, 0.605, 0, true);
	} else {
		// vCPE -- bod
		addConnection("vcpe_topology", "bod",
				"spLogicalForm", 0.385, 1, 0.395, 0, true);
		addConnection("vcpe_topology", "bod",
				"spLogicalForm", 0.615, 1, 0.605, 0, true);
	}

	// CLIENT DOWN master & backup -- CLIENT
	addConnection("client_down_master", "client", "spLogicalForm", 0.5, 1,
			0.187, 0, false);
	addConnection("client_down_backup", "client", "spLogicalForm", 0.5, 1,
			0.812, 0, false);
}

// add a connection and its endpoints
function addConnection(origin, destination, parent, originAnchorX,
		originAnchorY, destinationAnchorX, destinationAnchorY, extra_topology) {
	var originEndpoint = jsP.addEndpoint(origin, {
		anchor : [ originAnchorX, originAnchorY ],
		container : parent
	});
	var destinationEndpoint = jsP.addEndpoint(destination, {
		anchor : [ destinationAnchorX, destinationAnchorY ],
		container : parent
	});
	if (extra_topology) {
		extra_topology_endpoints.push(originEndpoint);
		extra_topology_endpoints.push(destinationEndpoint);
	} else {
		intra_topology_endpoints.push(originEndpoint);
		intra_topology_endpoints.push(destinationEndpoint);
	}

	var connection = jsP.connect({
		source : originEndpoint,
		target : destinationEndpoint,
		container : $("#" + parent)
	});

	if (extra_topology) {
		extra_topology_connections.push(connection);
	} else {
		intra_topology_connections.push(connection);

	}
}

// clear all jsPlumb stuff
function clearJSPlumbSPLogical() {
	jsPlumb.deleteEveryEndpoint();
	jsPlumb.detachAllConnections();

	// remove all overlays of each connection
	if (intra_topology_connections != null) {
		var connection = null;
		while ((connection = intra_topology_connections.pop()) != null) {
			connection.removeAllOverlays();
		}
	}

	if (extra_topology_connections != null) {
		var connection = null;
		while ((connection = extra_topology_connections.pop()) != null) {
			connection.removeAllOverlays();
		}
	}

	// detach all endpoints
	if (intra_topology_endpoints != null) {
		var endpoint = null;
		while ((endpoint = intra_topology_endpoints.pop()) != null) {
			endpoint.detachAll();
		}
	}

	if (extra_topology_endpoints != null) {
		var endpoint = null;
		while ((endpoint = extra_topology_endpoints.pop()) != null) {
			endpoint.detachAll();
		}
	}
}

// clear all jsPlumb stuff
function clearJSPlumbSPPhysical() {
	jsPlumb.deleteEveryEndpoint();
	jsPlumb.detachAllConnections();

	// remove all overlays of each connection
	if (intra_topology_connections != null) {
		var connection = null;
		while ((connection = intra_topology_connections.pop()) != null) {
			connection.removeAllOverlays();
		}
	}

	if (extra_topology_connections != null) {
		var connection = null;
		while ((connection = extra_topology_connections.pop()) != null) {
			connection.removeAllOverlays();
		}
	}

	// detach all endpoints
	if (intra_topology_endpoints != null) {
		var endpoint = null;
		while ((endpoint = intra_topology_endpoints.pop()) != null) {
			endpoint.detachAll();
		}
	}
}

//set jsPlumb physical view
function setJSPlumbMPPhysical() {
	if($('#vcpe_mp_physical').children().hasClass('ui-state-active')) {
		addConnection("network_wan1", "iface_router_up1", "mpPhysicalForm", 0.511, 1, 0.5, 0, false);
		addConnection("network_wan2", "iface_router_up2", "mpPhysicalForm", 0.5, 1, 0.5, 0, false);
		addConnection("iface_router_up1", "phy_router", "mpPhysicalForm", 0.5, 1, 0.167, 0.035, false);
		addConnection("iface_router_up2", "phy_router", "mpPhysicalForm", 0.5, 1, 0.821, 0.035, false);
		addConnection("phy_router", "iface_router_down", "mpPhysicalForm", 0.5, 0.975, 0.5, 0, false);
		addConnection("iface_router_down", "network_client", "mpPhysicalForm", 0.5, 0.988, 0.5, 0.08, false);
 	}	else {
		addConnection("network_wan1", "vcpe_mp_physical", "mpPhysicalForm", 0.5, 1, 0.28, 0, false);
		addConnection("network_wan2", "vcpe_mp_physical", "mpPhysicalForm", 0.5, 1, 0.71, 0, false);
		addConnection("vcpe_mp_physical", "network_client", "mpPhysicalForm", 0.5, 1, 0.5, 0.1, false);
		
	}
}

//clear all jsPlumb physical
function clearJSPlumbMPPhysical() {
		jsPlumb.deleteEveryEndpoint();
		jsPlumb.detachAllConnections();
	
		// remove all overlays of each connection
		if (intra_topology_connections != null) {
			var connection = null;
			while ((connection = intra_topology_connections.pop()) != null) {
				connection.removeAllOverlays();
			}
		}
	
		if (extra_topology_connections != null) {
			var connection = null;
			while ((connection = extra_topology_connections.pop()) != null) {
				connection.removeAllOverlays();
			}
		}
	
		// detach all endpoints
		if (intra_topology_endpoints != null) {
			var endpoint = null;
			while ((endpoint = intra_topology_endpoints.pop()) != null) {
				endpoint.detachAll();
			}
		}
}

//set jsPlumb logical view
function setJSPlumbMPLogical() {
	if($('#vcpe_mp_logical').children().hasClass('ui-state-active')) {	
		addConnection("network_wan1_logical", "iface_down_netprovider1", "mpLogicalForm", 0.5, 1, 0.5, 0, false);
		addConnection("network_wan2_logical", "iface_down_netprovider2", "mpLogicalForm", 0.5, 1, 0.5, 0, false);
		
		addConnection("iface_down_netprovider1", "iface_up_lrprovider1", "mpLogicalForm", 0.5, 1, 0.5, 0, false);
		addConnection("iface_down_netprovider2", "iface_up_lrprovider2", "mpLogicalForm", 0.5, 1, 0.5, 0, false);
		
		addConnection("iface_up_lrprovider1", "lrprovider1", "mpLogicalForm", 0.5, 1, 0.485, 0.05, false);
		addConnection("iface_up_lrprovider2", "lrprovider2", "mpLogicalForm", 0.5, 1, 0.49, 0.05, false);
		
		addConnection("lrprovider1", "iface_lrprovider1_down", "mpLogicalForm", 0.49, 0.96, 0.5, 0.01, false);
		addConnection("lrprovider2", "iface_lrprovider2_down", "mpLogicalForm", 0.49, 0.96, 0.5, 0.01, false);
		
		addConnection("iface_lrprovider1_down", "iface_client_up1", "mpLogicalForm", 0.5, 1, 0.5, 0, false);
		addConnection("iface_lrprovider2_down", "iface_client_up2", "mpLogicalForm", 0.5, 1, 0.5, 0, false);
		
		addConnection("iface_client_up1", "lrclient", "mpLogicalForm", 0.80, 1, 0.093, 0.055, false);
		addConnection("iface_client_up2", "lrclient", "mpLogicalForm", 0.2, 1, 0.910, 0.055, false);
		
		addConnection("lrclient", "iface_client_down", "mpLogicalForm", 0.5, 0.965, 0.5, 0.0, false);	
		
		addConnection("iface_client_down", "iface_up_netclient", "mpLogicalForm", 0.52, 1, 0.5, 0, false);
		addConnection("iface_up_netclient", "network_client_logical", "mpLogicalForm", 0.5, 1, 0.487, 0.015, false);
	}
	else {
		addConnection("network_wan1_logical", "iface_down_netprovider1", "mpLogicalForm", 0.5, 1, 0.5, 0, false);
		addConnection("network_wan2_logical", "iface_down_netprovider2", "mpLogicalForm", 0.5, 1, 0.5, 0, false);

		addConnection("iface_down_netprovider1", "vcpe_mp_logical", "mpLogicalForm", 0.5, 1, 0.245, 0.09, false);
		addConnection("iface_down_netprovider2", "vcpe_mp_logical", "mpLogicalForm", 0.5, 1, 0.71, 0.09, false);
		
		addConnection("vcpe_mp_logical", "iface_up_netclient", "mpLogicalForm", 0.483, 1, 0.50, 0, false);
		addConnection("iface_up_netclient", "network_client_logical", "mpLogicalForm", 0.5, 1, 0.487, 0.015, false);
	}
	
}

//clear all jsPlumb logical
function clearJSPlumbMPLogical() {
	jsPlumb.deleteEveryEndpoint();
	jsPlumb.detachAllConnections();

	// remove all overlays of each connection
	if (intra_topology_connections != null) {
		var connection = null;
		while ((connection = intra_topology_connections.pop()) != null) {
			connection.removeAllOverlays();
		}
	}

	if (extra_topology_connections != null) {
		var connection = null;
		while ((connection = extra_topology_connections.pop()) != null) {
			connection.removeAllOverlays();
		}
	}

	// detach all endpoints
	if (intra_topology_endpoints != null) {
		var endpoint = null;
		while ((endpoint = intra_topology_endpoints.pop()) != null) {
			endpoint.detachAll();
		}
	}
}

// jsPlumb instance necessary
var jsPlumbNecessary = false;

// jsPlumb instance
var jsP;
// jsPlumb intra accordion endpoints
var intra_topology_endpoints = new Array();
// jsPlumb intra accordion connections
var intra_topology_connections = new Array();

// jsPlumb extra accordion endpoints
var extra_topology_endpoints = new Array();
// jsPlumb extra accordion connections
var extra_topology_connections = new Array();

// state of vCPE accordion
var topologyVisible = false;
// state of BoD accordion
var bodVisible = false;

$(function() {
	if (jsPlumbNecessary) {
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
		intra_topology_endpoints = new Array();
		intra_topology_connections = new Array();
		extra_topology_endpoints = new Array();
		extra_topology_connections = new Array();

		// draw jsPlumb stuff when view stuff is ready
		jsPlumb.ready(function() {
			jsPlumb.importDefaults({
				ConnectorZIndex : 50
			});
			if ($("#spLogicalForm").length) {
				setJSPlumbSPLogical(topologyVisible, bodVisible);
			} else if ($("#spPhysicalForm").length) {
				setJSPlumbSPPhysical();
			} else if ($("#mpPhysicalForm").length) {
				setJSPlumbMPPhysical();
			} else if ($("#mpLogicalForm").length) {
				setJSPlumbMPLogical();
			} 
		});
		
		$(window).bind("resize", resizeWindow);
	}

	$.fn.styleTable = function(options) {
		var defaults = {
			css : 'styleTable'
		};
		options = $.extend(defaults, options);

		return this.each(function() {

			input = $(this);
			input.addClass(options.css);

			input.find("tr").live('mouseover mouseout', function(event) {
				if (event.type == 'mouseover') {
					$(this).children("td").addClass("ui-state-hover");
				} else {
					$(this).children("td").removeClass("ui-state-hover");
				}
			});

			input.find("th").addClass("ui-state-default");
			input.find("td").addClass("ui-widget-content");

			input.find("tr").each(function() {
				$(this).children("td:not(:first)").addClass("first");
				$(this).children("th:not(:first)").addClass("first");
			});
		});
	};
});

function resizeWindow(e) {
	if ($("#spLogicalForm").length) {
		clearJSPlumbSPLogical();
		setJSPlumbSPLogical(topologyVisible, bodVisible);
	} else if ($("#spPhysicalForm").length) {
		clearJSPlumbSPLogical();
		setJSPlumbSPPhysical();
	} else if ($("#mpPhysicalForm").length) {
		clearJSPlumbMPPhysical();
		setJSPlumbMPPhysical();
	} else if ($("#mpLogicalForm").length) {
		clearJSPlumbMPLogical();
		setJSPlumbMPLogical();
	}
}

// IPv4 validator regex based on RFC 1123 http://tools.ietf.org/html/rfc1123
var validIPAddressRegExp = new RegExp("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
var validIPAddressSubnetMaskRegExp = new RegExp("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\/(\\d{1}|[0-2]{1}\\d{1}|3[0-2])$");

$(document).ready(function() {
	// only apply when create view or update IPs view are loaded
	if($("#spLogicalForm").length || $("#updateIPs").length || $("#mpLogicalForm").length) {
		// apply firewall table style
		$("#firewallTable").styleTable();
		// get submit button
		var button = null;
		if($("#spLogicalForm").length || $("#mpLogicalForm").length) {
			button = $("#submitButton");
		} else if($("#updateIPs").length) {
			button = $("#updateIpButton");
		}

		// predefined style on BoD div
		$("#bod").css('zIndex', 100);

		// ============ begin custom validators ==================== //
		// regex
		$.validator.addMethod("custom_regex", function(value, element,	regexp) {
			return this.optional(element) || regexp.test(value);
		});
		// ============ end custom validators ==================== //

		// ============ begin validation options ==================== //
		$("#logicalInfrastructure").validate({
			showErrors: function(errorMap, errorList) {
				var i, elements;
				for ( i = 0; this.errorList[i]; i++ ) {
					var error = this.errorList[i];
					if ( this.settings.highlight ) {
						this.settings.highlight.call( this, error.element, this.settings.errorClass, this.settings.validClass );
					}
				}
				
				if (this.settings.unhighlight) {
					for ( i = 0, elements = this.validElements(); elements[i]; i++ ) {
						this.settings.unhighlight.call( this, elements[i], this.settings.errorClass, this.settings.validClass );
					}
				}
				
				// enable/disable submit button when errors are produced
				var errors = this.numberOfInvalids();
				if(errors == 0) {
					button.attr("disabled", false);
				}
				else {
					button.attr("disabled", true);
				}
			},
			errorPlacement: function(error,element) {
				return true;
			},
			highlight: function(element, errorClass, validClass) {
				// add error class to label for element
				$('label[for=\"' + element.name + '\"]').addClass("error");
			},
	        unhighlight: function(element, errorClass, validClass) {
	        	// remove error class to label for element
				$('label[for=\"' + element.name + '\"]').removeClass("error");
	        },
	
			onkeyup : false,
			onsubmit : false,
			onclick : false,
			onfocusout : function(element) {$(element).valid();}
		});
		// ============ end validation options ==================== //
	}
	
	// create sp view form validation rules
	if($("#spLogicalForm").length) {
		// ============ begin validation rules ==================== //	
		$('#logicalRouterMaster\\.interfaces2\\.name').rules("add", { required: true });
		$('#logicalRouterMaster\\.interfaces2\\.port').rules("add", { required: true, min: 0 });
		$('#logicalRouterMaster\\.interfaces2\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#logicalRouterMaster\\.interfaces2\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
		
		$('#logicalRouterBackup\\.interfaces2\\.name').rules("add", { required: true });
		$('#logicalRouterBackup\\.interfaces2\\.port').rules("add", { required: true, min: 0 });
		$('#logicalRouterBackup\\.interfaces2\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#logicalRouterBackup\\.interfaces2\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
		
		$('#bgp\\.clientASNumber').rules("add", { required: true });
		$('#bgp\\.nocASNumber').rules("add", { required: true, min: 0, max: 4294967295 });
		$('#bgp\\.clientPrefixes').rules("add", { required: true });
	
		$('#logicalRouterMaster\\.interfaces1\\.name').rules("add", { required: true });
		$('#logicalRouterMaster\\.interfaces1\\.port').rules("add", { required: true, min: 0 });
		$('#logicalRouterMaster\\.interfaces1\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#logicalRouterMaster\\.interfaces1\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
		
		$('#logicalRouterMaster\\.interfaces0\\.name').rules("add", { required: true });
		$('#logicalRouterMaster\\.interfaces0\\.port').rules("add", { required: true, min: 0 });
		$('#logicalRouterMaster\\.interfaces0\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#logicalRouterMaster\\.interfaces0\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
		
		$('#logicalRouterBackup\\.interfaces0\\.name').rules("add", { required: true });
		$('#logicalRouterBackup\\.interfaces0\\.port').rules("add", { required: true, min: 0 });
		$('#logicalRouterBackup\\.interfaces0\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#logicalRouterBackup\\.interfaces0\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
		
		$('#logicalRouterBackup\\.interfaces1\\.name').rules("add", { required: true });
		$('#logicalRouterBackup\\.interfaces1\\.port').rules("add", { required: true, min: 0 });
		$('#logicalRouterBackup\\.interfaces1\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#logicalRouterBackup\\.interfaces1\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
		
		$('#bod\\.ifaceClient\\.name').rules("add", { required: true, maxlength: 25 });
		$('#bod\\.ifaceClient\\.port').rules("add", { required: true, min: 0, max: 4094 });
		$('#bod\\.ifaceClient\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
		
		$('#bod\\.ifaceClientBackup\\.name').rules("add", { required: true });
		$('#bod\\.ifaceClientBackup\\.port').rules("add", { required: true, min: 0 });
		$('#bod\\.ifaceClientBackup\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
		
		$('#vrrp\\.virtualIPAddress').rules("add", { custom_regex: validIPAddressRegExp, required: true });
		
		$('#name').rules("add", { required: true });
		$('#clientIpRange').rules("add", { required: true });
		$('#nocIpRange').rules("add", { required: true });
		// ============ end validation rules ==================== //
	}
	
	// update IPs view form validation rules
	else if($("#updateIPs").length) {
		// ============ begin validation rules ==================== //
		$('#logicalRouterMaster\\.interfaces1\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#logicalRouterMaster\\.interfaces0\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#logicalRouterBackup\\.interfaces0\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#logicalRouterBackup\\.interfaces1\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		// ============ end validation rules ==================== //
	} else if($("#mpLogicalForm").length) {
		// ============ begin validation rules ==================== //	
		$('#providerNetwork1\\.ASNumber').rules("add", { required: true });
		$('#providerNetwork1\\.iPAddressRange').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });

		$('#providerNetwork2\\.ASNumber').rules("add", { required: true });
		$('#providerNetwork2\\.iPAddressRange').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });

		$('#clientNetwork\\.ASNumber').rules("add", { required: true });
		$('#clientNetwork\\.iPAddressRange').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });

		$('#providerNetwork1\\.networkInterface\\.name').rules("add", { required: true });
		$('#providerNetwork1\\.networkInterface\\.port').rules("add", { required: true, min: 0 });
		$('#providerNetwork1\\.networkInterface\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#providerNetwork1\\.networkInterface\\.vlan').rules("add", { required: true, min: 0, max: 4094 });

		$('#providerNetwork2\\.networkInterface\\.name').rules("add", { required: true });
		$('#providerNetwork2\\.networkInterface\\.port').rules("add", { required: true, min: 0 });
		$('#providerNetwork2\\.networkInterface\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#providerNetwork2\\.networkInterface\\.vlan').rules("add", { required: true, min: 0, max: 4094 });		

		$('#providerLR1\\.interfaces0\\.name').rules("add", { required: true });
		$('#providerLR1\\.interfaces0\\.port').rules("add", { required: true, min: 0 });
		$('#providerLR1\\.interfaces0\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#providerLR1\\.interfaces0\\.vlan').rules("add", { required: true, min: 0, max: 4094 });

		$('#providerLR2\\.interfaces0\\.name').rules("add", { required: true });
		$('#providerLR2\\.interfaces0\\.port').rules("add", { required: true, min: 0 });
		$('#providerLR2\\.interfaces0\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#providerLR2\\.interfaces0\\.vlan').rules("add", { required: true, min: 0, max: 4094 });

		$('#providerLR1\\.interfaces2\\.name').rules("add", { required: true });
		$('#providerLR1\\.interfaces2\\.port').rules("add", { required: true, min: 0 });
		$('#providerLR1\\.interfaces2\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });

		$('#providerLR2\\.interfaces2\\.name').rules("add", { required: true });
		$('#providerLR2\\.interfaces2\\.port').rules("add", { required: true, min: 0 });
		$('#providerLR2\\.interfaces2\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		
		$('#providerLR1\\.interfaces1\\.name').rules("add", { required: true });
		$('#providerLR1\\.interfaces1\\.port').rules("add", { required: true, min: 0 });
		$('#providerLR1\\.interfaces1\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#providerLR1\\.interfaces1\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
		
		$('#providerLR2\\.interfaces1\\.name').rules("add", { required: true });
		$('#providerLR2\\.interfaces1\\.port').rules("add", { required: true, min: 0 });
		$('#providerLR2\\.interfaces1\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#providerLR2\\.interfaces1\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
		
		$('#clientLR\\.interfaces0\\.name').rules("add", { required: true });
		$('#clientLR\\.interfaces0\\.port').rules("add", { required: true, min: 0 });
		$('#clientLR\\.interfaces0\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#clientLR\\.interfaces0\\.vlan').rules("add", { required: true, min: 0, max: 4094 });

		$('#clientLR\\.interfaces1\\.name').rules("add", { required: true });
		$('#clientLR\\.interfaces1\\.port').rules("add", { required: true, min: 0 });
		$('#clientLR\\.interfaces1\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#clientLR\\.interfaces1\\.vlan').rules("add", { required: true, min: 0, max: 4094 });

		$('#clientLR\\.interfaces3\\.name').rules("add", { required: true });
		$('#clientLR\\.interfaces3\\.port').rules("add", { required: true, min: 0 });
		$('#clientLR\\.interfaces3\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });

		$('#clientLR\\.interfaces2\\.name').rules("add", { required: true });
		$('#clientLR\\.interfaces2\\.port').rules("add", { required: true, min: 0 });
		$('#clientLR\\.interfaces2\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#clientLR\\.interfaces2\\.vlan').rules("add", { required: true, min: 0, max: 4094 });

		$('#clientNetwork\\.networkInterface\\.name').rules("add", { required: true });
		$('#clientNetwork\\.networkInterface\\.port').rules("add", { required: true, min: 0 });
		$('#clientNetwork\\.networkInterface\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
		$('#clientNetwork\\.networkInterface\\.vlan').rules("add", { required: true, min: 0, max: 4094 });

		$('#name').rules("add", { required: true });
	}
});