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
	<title>Error</title>
	<link rel="stylesheet" href="<c:url value="/resources/css/screen.css" />" type="text/css">
	<link rel="stylesheet" href="<c:url value="/resources/css/print.css" />" type="text/css">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/resources/css/ie.css" />" type="text/css">
	<![endif]-->
</head>
<body>
<div class="container">  
	<h1>
		Error page
	</h1>

	<h2>Your application has generated an error</h2>
    <h3>Please check for the error given below</h3>
	<b>Exception message:</b><br> 	    
	<hr>
	<c:if test="${not empty exception}" >
		<div class="error">
			<span>
				<spring:message text="${exception}" />
			</span>
		</div>
	</c:if>
</div>
</body>
</html>