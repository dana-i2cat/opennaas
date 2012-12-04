<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><tiles:insertAttribute name="title" ignore="true" /></title>
	<!-- CSS -->
	<link rel="stylesheet" href="<c:url value="/resources/css/screen.css" />" type="text/css">
	<!--[if lt IE 8]>
			<link rel="stylesheet" href="opennaas/resources/css/ie.css" type="text/css" >
		<![endif]-->
	<link rel="stylesheet" href="<c:url value="/resources/css/ui-lightness/jquery-ui-1.9.1.custom.css" />">
	<link rel="stylesheet" href="<c:url value="/resources/css/opennaas.css" />" type="text/css">
	<!-- Javascript -->
	<script src="<c:url value="/resources/js/jquery-1.8.2.js" />"></script>
	<script src="<c:url value="/resources/js/jquery-ui-1.9.1.custom.js" />"></script>
	<script src="<c:url value="/resources/js/script.js" />"></script>
	<script
		src="<c:url value="/resources/js/jquery.jsPlumb-1.3.16-all.js" />"></script>
	<script>
		// Javascript for menu
		$(function() {
			$("#_menu").menu();
		});
		// Javascript for tabs	
		$(function() {
			$("#tabs").tabs();
		});
		// Javascript for link confirm	
		$(document).ready(function() {
		    $("#dialog").dialog({
		      autoOpen: false,
		      modal: true
			});
		});

		$(document).ready(function(){
		    $("#dialog").dialog({
		      modal: true,
		            bgiframe: true,
		            width: 300,
		            height: 150,
		      autoOpen: false
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
	</script>
</head>
<body>
	<div class="container">
		<div id="header">
			<tiles:insertAttribute name="header" />
		</div>
		<div id="menu">
			<tiles:insertAttribute name="menu" />
		</div>
		<div id="body">
			<tiles:insertAttribute name="body" />
		</div>
		<div id="footer">
			<tiles:insertAttribute name="footer" />
		</div>
	</div>
</body>
</html>
