<%@page import="org.apache.commons.collections.EnumerationUtils"%>
<%@page import="java.util.Enumeration"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib  uri="http://www.springframework.org/tags" prefix="spring" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><spring:message code="exception.title" /></title>
	<link rel="stylesheet" href="<c:url value="/resources/css/screen.css" />" type="text/css">
	<link rel="stylesheet" href="<c:url value="/resources/css/print.css" />" type="text/css">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/resources/css/ie.css" />" type="text/css">
	<![endif]-->
</head>
<body>
<div class="container">  
	<h1>
		<spring:message code="exception.title2" />
	</h1>
	
	<c:set var="errorException" value="${requestScope['javax.servlet.error.exception']}"/>	
	<c:set var="errorExceptionType" value="${requestScope['javax.servlet.error.exception_type']}"/>	
	<c:set var="errorServletName" value="${requestScope['javax.servlet.error.servlet_name']}"/>
	<c:set var="errorMessage" value="${requestScope[' javax.servlet.error.message']}"/>
	<c:set var="errorRequestUri" value="${requestScope['javax.servlet.error.request_uri']}"/>
	<c:set var="errorExceptionType" value="${requestScope['javax.servlet.error.exception_type']}"/>	

	<h2><spring:message code="exception.message1" /></h2>
    <h3><spring:message code="exception.message2" /></h3>
	<b><spring:message code="exception.message3" /></b><br> 	    
	<hr>
	
	<div class="error">
	
		<c:if test="${not empty errorException}" >
			<c:if test="${not empty errorException.message}" >
				<span>
					<b>Exception Message:</b><br> 	  
					<c:out value="${errorException.message}"/><br/>		
				</span>
			</c:if>
		</c:if>
	
		<c:if test="${not empty errorExceptionType}" >
			<span>
				<b>Error exception type:</b><br> 	  
				<c:out value="${errorExceptionType}"/><br/>	
			</span>
		</c:if>

		<c:if test="${not empty errorServletName}" >
			<span>
				<b>Error servlet name:</b><br> 	  
				<c:out value="${errorServletName}"/><br/>	
			</span>
		</c:if>

		<c:if test="${not empty errorRequestUri}" >
			<span>
				<b>Error request URI:</b><br> 	  
				<c:out value="${errorRequestUri}"/><br/>	
			</span>
		</c:if>
		
		<c:if test="${not empty errorException}" >
			<span>
				<b>Exception Stack Trace:</b><br> 	
	  			<% 
					Exception e = (Exception)request.getAttribute("javax.servlet.error.exception");
					e.printStackTrace(new java.io.PrintWriter(out)); 
				%> 
			</span>
		</c:if>
	</div>	
</div>
</body>
</html>