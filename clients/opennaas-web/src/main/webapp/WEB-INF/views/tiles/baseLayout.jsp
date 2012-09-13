<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib  uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><tiles:insertAttribute name="title" ignore="true" /></title>
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css" media="print">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="opennaas//resources/blueprint/ie.css" type="text/css" media="screen, projection">
	<![endif]-->
</head>

<body>
<div class="container">
	<div id="header">
		<tiles:insertAttribute name="header" />
	</div>
	<div id="menu" style="float:left; width:250px; margin-top:10px;">
	    <tiles:insertAttribute name="menu" />
	</div>
	<div id="body" style="float:left; width:500px; min-height: 300px">
		<tiles:insertAttribute name="body" />
	</div>
	<div id="footer"style="margin:0px auto 0px auto; width:500px" >
    	<tiles:insertAttribute name="footer" />
	</div>
</div>
</body>
</html>
