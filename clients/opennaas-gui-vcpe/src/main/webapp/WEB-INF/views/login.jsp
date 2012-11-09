<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib  uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
	<title><fmt:message key="login.title"/></title>
	<!-- CSS -->
	<link rel="stylesheet" 	href="<c:url value="/resources/css/screen.css" />" type="text/css">
	<!--[if lt IE 8]>
			<link rel="stylesheet" href="opennaas/resources/css/ie.css" type="text/css" >
		<![endif]-->
	<link rel="stylesheet" href="<c:url value="/resources/css/ui-lightness/jquery-ui-1.9.1.custom.css" />">
	<link rel="stylesheet" href="<c:url value="/resources/css/opennaas.css" />" type="text/css">
	<link rel="stylesheet" href="<c:url value="/resources/css/login.css" />" type="text/css">
	<!-- Javascript -->
	<script src="<c:url value="/resources/js/jquery-1.8.2.js" />"></script>
	<script src="<c:url value="/resources/js/jquery-ui-1.9.1.custom.js" />"></script>
	<script src="<c:url value="/resources/js/script.js" />"></script>
</head>
<body>

	
<div class="container">
	<div id="login_logo">
		<img src="<c:url value="/resources/images/opennaas-orange-400.png" />" height= "264px" width= "400px" alt="OpenNaaS Logo">
	</div>	
	<section id="content">	
		<form action="../j_spring_security_check" method="post">
			<h1><spring:message code="login.welcome"/></h1>
			<div id="login_error">${error}</div>
			<div>
				<input type="text" placeholder=<spring:message code="login.username"/> required="required" id="j_username" name="j_username" />
			</div>
			<div>
				<input type="password" placeholder=<spring:message code="login.password"/> required="required" id="j_password" name="j_password"/>
			</div>
			<div>
				<input type="submit" value="<spring:message code="buttons.login"/>"  id="submitLogin"/>
			</div>
		</form><!-- form -->
		<div id="login-error">${error}</div>
	</section><!-- content -->
</div><!-- container -->

</body>
</html>