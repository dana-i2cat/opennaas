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
	<title><spring:message code="logicalrouter.create.title"/></title>
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
		<spring:message code="logicalrouter.create.title"/>
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
	
	<c:if test="${not empty errorMsg}" >
		<div class="error">
			<span>
				<spring:message text="${errorMsg}" />
			</span>
		</div>
	</c:if>
	
	<div class="span-12 last">	
		<form:form modelAttribute="logicalRouter" action="logicalRouter" method="post">
		  	<fieldset>		
				<legend><spring:message code="logicalrouter.create.legend"/></legend>
				<p>
					<form:label for="name" path="name" cssErrorClass="error"><spring:message code="logicalrouter.name"/></form:label><br/>
					<form:input path="name" /> <form:errors path="name" />			
				</p>
				<p>	
					<form:label for="router" path="router" cssErrorClass="error"><spring:message code="logicalrouter.router"/></form:label><br/>
					<form:input path="router" /> <form:errors path="router" />
				</p>
				<p>	
					<form:label for="iface" path="iface" cssErrorClass="error"><spring:message code="logicalrouter.interface"/></form:label><br/>
					<form:input path="iface" /> <form:errors path="iface" />
				</p>
				<p>	
					<input type="submit" value="<spring:message code="buttons.enter"/>"/>
				</p>
			</fieldset>
		</form:form>
	</div>
</div>
</body>
</html>