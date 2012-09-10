<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib  uri="http://www.springframework.org/tags" prefix="spring" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><fmt:message key="index.title"/></title>
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css" media="print">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/custom.css" />" type="text/css" media="print">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css" media="screen, projection">
	<![endif]-->
</head>
<body>
<div class="container">  
	<h1>
		<fmt:message key="index.title"/>
	</h1>
	<div>
		<div style='float: right'>
			<a href="?locale=en_gb">en</a> | <a href="?locale=es_es">es</a>
		</div>
	</div>
	<hr>
	<ul>
		<li><a href="logicalRouter"><spring:message code="index.menu.logicalrouter"/></a></li>
	</ul>
	<ul>
		<li><a href="queue"><spring:message code="index.menu.queue"/></a></li>
	</ul>
</div>
</body>
</html>