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
	<title>List Actions</title>
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
		<spring:message code="queue.listactions.title"/>
	</h1>
	<div>
		<div style='float: right'>
			<a href="?locale=en_gb">en</a> | <a href="?locale=es_es">es</a>
		</div>
		<div style='float: left'>
			<a href="/opennaas">Home</a>
		</div>
	</div>
	<hr>
	
	<c:if test="${not empty infoMsg}" >
		<div class="success">
			<span>
				<spring:message text="${infoMsg}" />
			</span>
		</div>
	</c:if>
	
	<div class="span-12 last">	
		<ul>
			<c:forEach var="action" items="${actions}">
				<li>
					<c:out value="${action}"></c:out>
				</li>
	   		</c:forEach>
		</ul>
		<ul>
			<form:form modelAttribute="queue" action="queue" method="post">
			  	<fieldset>		
					<legend><spring:message code="queue.listactions.legend"/></legend>		
					<input type="submit" value="Execute"/>
				</fieldset>
			</form:form>
		</ul>	
	</div>
</div>
</body>
</html>