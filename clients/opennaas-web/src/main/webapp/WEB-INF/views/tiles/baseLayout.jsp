<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib  uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><tiles:insertAttribute name="title" ignore="true" /></title>
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/opennaas.css" />" type="text/css" media="screen, print">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="opennaas//resources/blueprint/ie.css" type="text/css" media="screen, projection">
	<![endif]-->
	
	<!-- JQuery -->
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/jquery/themes/base/jquery.ui.all.css" />">
	<script src="<c:url value="/resources/blueprint/jquery/jquery-1.8.2.js" />"></script>
	<script src="<c:url value="/resources/blueprint/jquery/ui/jquery.ui.core.js" />"></script>
	<script src="<c:url value="/resources/blueprint/jquery/ui/jquery.ui.widget.js" />"></script>
	<script src="<c:url value="/resources/blueprint/jquery/ui/jquery.ui.tabs.js" />"></script>	
	<script>
		$(function() {
			$( "#tabs" ).tabs();
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
