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
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/opennaas.css" />" type="text/css">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css">
	<![endif]-->
</head>
<body>
	<div id="login">
		<fieldset>
			<h4>Login</h4>
			<div id="login-error">${error}</div>
		
			<form action="../j_spring_security_check" method="post">
				<p>
					<label for="j_username">Username</label> <input id="j_username" name="j_username" type="text" />
				</p>
				<p>
					<label for="j_password">Password</label> <input id="j_password" name="j_password" type="password" />
				</p>
				<input type="submit" value="Login" />
			</form>
		</fieldset>
	</div>
</body>
</html>