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
	$("#dialog").dialog({
		autoOpen : false,
		modal : true
	});
});

$(document).ready(function() {
	$("#dialog").dialog({
		modal : true,
		bgiframe : true,
		width : 300,
		height : 150,
		autoOpen : false
	});

	$("a.confirm").click(function(e) {
		e.preventDefault();
		var theHREF = $(this).attr("href");
		$("#dialog").dialog('option', 'buttons', {
			"Confirm" : function() {
				window.location.href = theHREF;
			},
			"Cancel" : function() {
				$(this).dialog("close");
			}
		});
		$("#dialog").dialog("open");
	});
});

/**
 * Ajax Sample Method that updates the header of a view
 */
function updateHeader() {
	$.ajax({
		type : "GET",
		url : "/opennaas-vcpe/secure/vcpeNetwork/getAjax",
		success : function(data) {
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
		type : "GET",
		url : "/opennaas-vcpe/secure/vcpeNetwork/isInterfaceFree?vcpeId="
				+ vcpeId
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
								"<div id='tooltip'>The Interface is not available</div>");
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
		type : "GET",
		url : "/opennaas-vcpe/secure/vcpeNetwork/isIPFree?vcpeId="
				+ vcpeId + "&ip=" + ip.value,
		success : function(data) {
			if (data == 'false') {
				// Case not available
				// First delete the tooltip
				$("#tooltip").remove();
				// Add the new tooltip, error classes and disable inputs
				$(ip)
						.after(
								"<div id='tooltip'>The IP Address is not available</div>");
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
		type : "GET",
		url : "/opennaas-vcpe/secure/vcpeNetwork/isVLANFree?vcpeId=" + vcpeId
				+ "&vlan=" + vlan.value + "&ifaceName=" + ifaceName.value,
		success : function(data) {
			if (data == 'false') {
				// Case not available
				// First delete the tooltip
				$("#tooltip").remove();
				// Add the new tooltip, error classes and disable inputs
				$(vlan).after(
						"<div id='tooltip'>The VLAN is not available</div>");
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
	/*
	 * beforeActivate : function() { clearJSPlumbStuff(); }, activate :
	 * function() { setJSPlumbStuff(topologyVisible); }
	 */
	});

	$("#vrrp_box").accordion({
		collapsible : true,
		icons : false,
		heightStyle : "content",
		active : true
	/*
	 * beforeActivate : function() { clearJSPlumbStuff(); }, activate :
	 * function() { setJSPlumbStuff(topologyVisible); }
	 */
	});

	// only apply accordion styles when createVCPENetwork.jsp is loaded
	if ($("#home_body").length) {
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

	// only apply accordion styles when createVCPENetwork.jsp is loaded
	if ($("#createVCPENetwork").length) {
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
				clearJSPlumbStuff();
			},
			activate : function() {
				setJSPlumbStuff(topologyVisible);
			}
		});

		/* Client block */
		$("#client").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			active : false,
			beforeActivate : function() {
				clearJSPlumbStuff();
			},
			activate : function() {
				setJSPlumbStuff(topologyVisible);
			}
		});

		/* Protocols */
		$("#bgp").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			active : false,
			beforeActivate : function() {
				clearJSPlumbStuff();
			},
			activate : function() {
				setJSPlumbStuff(topologyVisible);
			}
		});

		$("#vrrp").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			active : false,
			beforeActivate : function() {
				clearJSPlumbStuff();
			},
			activate : function() {
				setJSPlumbStuff(topologyVisible);
			}
		});

		$("#vcpe_topology")
				.accordion(
						{
							collapsible : true,
							icons : false,
							heightStyle : "content",
							active : false,
							beforeActivate : function() {
								clearJSPlumbStuff();
							},
							activate : function(event, ui) {
								var active = $("#vcpe_topology").accordion(
										"option", "active");
								topologyVisible = !(typeof active == 'boolean' && active == false);
								setJSPlumbStuff(topologyVisible);
							}
						});

		$("#wan_logical").accordion({
			collapsible : true,
			icons : false,
			heightStyle : "content",
			active : false
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

	/* Buttons */
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
		open: window.open("?locale=en_gb", "_self");
	});
	// Spanish
	$("#spanish").click(function(event) {
		open: window.open("?locale=es_es", "_self");
	});
	// Check language to activate button
	if ($.getUrlVar('locale') == "es_es") {
		$("#spanish").attr("checked", "checked").button("refresh");
	} else {
		$("#english").attr("checked", "checked").button("refresh");
	}

	$("#radioset").buttonset();
	$("#radioset2").buttonset();
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
function setJSPlumbHome() {

	// WAN -- WAN master & backup
	addConnection("wan", "wan_master", "home_body", 0.302, 1, 0.5, 0, false);
	addConnection("wan", "wan_backup", "home_body", 0.7, 1, 0.5, 0, false);

	// WAN master & backup -- router master & backup
	addConnection("wan_master", "cpe_master", "home_body", 0.5, 1, 0.6, 0,
			false);
	addConnection("wan_backup", "cpe_backup", "home_body", 0.5, 1, 0.4, 0,
			false);

	// router master -- client master & inter master
	addConnection("cpe_master", "cpe_client_master", "home_body", 0.22, 1, 0.5,
			0, false);
	addConnection("cpe_master", "cpe_inter_master", "home_body", 0.83, 1, 0.5,
			0, false);

	// router master -- client master & inter master
	addConnection("cpe_backup", "cpe_inter_backup", "home_body", 0.18, 1, 0.5,
			0, false);
	addConnection("cpe_backup", "cpe_client_backup", "home_body", 0.78, 1, 0.5,
			0, false);

	// client master & backup -- client
	addConnection("cpe_client_master", "cpe_client", "home_body", 0.5, 1,
			0.165, 0, false);
	addConnection("cpe_client_backup", "cpe_client", "home_body", 0.5, 1,
			0.845, 0, false);

	// inter master & backup -- bod
	addConnection("cpe_inter_master", "cpe_bod", "home_body", 0.5, 1, 0.39, 0,
			false);
	addConnection("cpe_inter_backup", "cpe_bod", "home_body", 0.5, 1, 0.615, 0,
			false);
}

/**
 * jsPlumb create and view
 */
// set jsPlumb stuff
function setJSPlumbStuff(setExtra) {
	// WAN -- UP master & backup
	addConnection("wan_logical", "up_master", "createVCPENetwork", 0.268, 1,
			0.5, 0, false);
	addConnection("wan_logical", "up_backup", "createVCPENetwork", 0.738, 1,
			0.5, 0, false);

	if (setExtra) {
		// UP master & backup -- LR master & backup
		addConnection("up_master", "lr_master", "createVCPENetwork", 0.5, 1,
				0.425, 0, true);
		addConnection("up_backup", "lr_backup", "createVCPENetwork", 0.5, 1,
				0.6, 0, true);

		// CLIENT DOWN master & backup -- CLIENT master & backup
		addConnection("client_master", "client_down_master",
				"createVCPENetwork", 0.5, 1, 0.5, 0, true);
		addConnection("client_backup", "client_down_backup",
				"createVCPENetwork", 0.5, 1, 0.5, 0, true);
	}

	// LR master & backup -- CLIENT DOWN master & backup
	addConnection("lr_master", "client_master", "vcpe_routers", 0.2, 1, 0.5, 0,
			false);
	addConnection("lr_backup", "client_backup", "vcpe_routers", 0.79, 1, 0.5,
			0, false);

	// CLIENT DOWN master & backup -- CLIENT
	addConnection("client_down_master", "client", "createVCPENetwork", 0.5, 1,
			0.187, 0, false);
	addConnection("client_down_backup", "client", "createVCPENetwork", 0.5, 1,
			0.812, 0, false);
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
	if (extra) {
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

	if (extra) {
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

// clear all jsPlumb stuff
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
// jsPlumb intra accordion endpoints
var intra_endpoints = new Array();
// jsPlumb intra accordion connections
var intra_connections = new Array();

// jsPlumb extra accordion endpoints
var extra_endpoints = new Array();
// jsPlumb extra accordion connections
var extra_connections = new Array();

// state of vCPE accordion
var topologyVisible = false;

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
		intra_endpoints = new Array();
		intra_connections = new Array();
		extra_endpoints = new Array();
		extra_connections = new Array();

		// draw jsPlumb stuff when view stuff is ready
		jsPlumb.ready(function() {
			jsPlumb.importDefaults({
				ConnectorZIndex : 5
			});
			if ($("#createVCPENetwork").length) {
				setJSPlumbStuff(topologyVisible);
			} else if ($("#home_body").length) {
				setJSPlumbHome();
			}
		});
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

// IPv4 validator regex based on RFC 1123 http://tools.ietf.org/html/rfc1123
var validIPAddressRegExp = new RegExp("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
var validIPAddressSubnetMaskRegExp = new RegExp("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\/(\d{1}|[0-2]{1}\d{1}|3[0-2])$");

$(document).ready(function() {
	$("#firewallTable").styleTable();
	
	// onload disable create submit button
	$("#submitButton").attr("disabled", true);
	
	// ============ begin custom validators ==================== //
	// regex
	$.validator.addMethod("custom_regex", function(value, element,	regexp) {
		return this.optional(element) || regexp.test(value);
	});
	// ============ end custom validators ==================== //
	
	// ============ begin validation options ==================== //
	$("#VCPENetwork").validate({
		success: function(label) {
			// enable create button when no errors are produced
			console.log("success!!");
		},
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
			
			var errors = this.numberOfInvalids();
			console.log("errors = " + errors);
			if(errors == 0) {
				console.log("no errors, enabling button!");
				$("#submitButton").attr("disabled", false);
			}
			else {
				console.log("errors, disabling button!");
				$("#submitButton").attr("disabled", true);
			}
			
		},
		errorPlacement: function(error,element) {
			return true;
		},
		highlight: function(element, errorClass, validClass) {
			// add error class to label for element
			console.log("marking error on " + element.name);
			$('label[for=\"' + element.name + '\"]').addClass("error");
		},
        unhighlight: function(element, errorClass, validClass) {
        	// remove error class to label for element
			console.log("unmarking error on " + element.name);
			$('label[for=\"' + element.name + '\"]').removeClass("error");
        },

		debug: true,
		onkeyup : function(element) {$(element).valid();},
		onsubmit : false,
		onclick : false,
		onfocusout : function(element) {$(element).valid();}
	});
	// ============ end validation options ==================== //
	
	// ============ begin validation rules ==================== //	
	$('#logicalRouterMaster\\.interfaces2\\.name').rules("add", { required: true });
	$('#logicalRouterMaster\\.interfaces2\\.port').rules("add", { required: true });
	$('#logicalRouterMaster\\.interfaces2\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
	$('#logicalRouterMaster\\.interfaces2\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
	
	$('#logicalRouterBackup\\.interfaces2\\.name').rules("add", { required: true });
	$('#logicalRouterBackup\\.interfaces2\\.port').rules("add", { required: true });
	$('#logicalRouterBackup\\.interfaces2\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
	$('#logicalRouterBackup\\.interfaces2\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
	
	$('#bgp\\.clientASNumber').rules("add", { required: true });
	$('#bgp\\.nocASNumber').rules("add", { required: true });
	$('#bgp\\.clientPrefixes').rules("add", { required: true });

	$('#logicalRouterMaster\\.interfaces1\\.name').rules("add", { required: true });
	$('#logicalRouterMaster\\.interfaces1\\.port').rules("add", { required: true });
	$('#logicalRouterMaster\\.interfaces1\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
	$('#logicalRouterMaster\\.interfaces1\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
	
	$('#logicalRouterMaster\\.interfaces0\\.name').rules("add", { required: true });
	$('#logicalRouterMaster\\.interfaces0\\.port').rules("add", { required: true });
	$('#logicalRouterMaster\\.interfaces0\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
	$('#logicalRouterMaster\\.interfaces0\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
	
	$('#logicalRouterBackup\\.interfaces0\\.name').rules("add", { required: true });
	$('#logicalRouterBackup\\.interfaces0\\.port').rules("add", { required: true });
	$('#logicalRouterBackup\\.interfaces0\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
	$('#logicalRouterBackup\\.interfaces0\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
	
	$('#logicalRouterBackup\\.interfaces1\\.name').rules("add", { required: true });
	$('#logicalRouterBackup\\.interfaces1\\.port').rules("add", { required: true });
	$('#logicalRouterBackup\\.interfaces1\\.ipAddress').rules("add", { custom_regex: validIPAddressSubnetMaskRegExp, required: true });
	$('#logicalRouterBackup\\.interfaces1\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
	
	$('#bod\\.ifaceClient\\.name').rules("add", { required: true, maxlength: 25 });
	$('#bod\\.ifaceClient\\.port').rules("add", { required: true });
	$('#bod\\.ifaceClient\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
	
	$('#bod\\.ifaceClientBackup\\.name').rules("add", { required: true });
	$('#bod\\.ifaceClientBackup\\.port').rules("add", { required: true });
	$('#bod\\.ifaceClientBackup\\.vlan').rules("add", { required: true, min: 0, max: 4094 });
	
	$('#vrrp\\.virtualIPAddress').rules("add", { custom_regex: validIPAddressRegExp, required: true });
	
	$('#name').rules("add", { required: true });
	$('#clientIpRange').rules("add", { required: true });
	// ============ end validation rules ==================== //
});

/**
 * Fill the BGP Client Range. Should be the same as the client range
 */
function fillBgpClientRange (value) {
	document.getElementById("bgp.clientPrefixes").value = value;
}
