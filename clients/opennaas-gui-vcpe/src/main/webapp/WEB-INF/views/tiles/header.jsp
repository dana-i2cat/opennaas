<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib  uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div id="header_logo">
	<img src="<c:url value="/resources/images/opennaas-orange-150.png" />" height= "99px" width= "150px" alt="OpenNaaS Logo">
</div>
<div id="header_title">
	<h1><spring:message code="header.title"/></h1>
</div>	
<div id="header_user" class="ui-state-default">
	<span id="user_icon" class="ui-icon ui-icon-person"></span>
	<span id="username">
		<spring:message code="header.user"/>: <sec:authentication property="principal.username" />
	</span>
	<div><a href="<c:url value="/auth/logout" />"><button id="logoutButton" class="button"><spring:message code="header.logout"/></button></a></div>
</div>	
<div id="header_menu" class="ui-widget-content ui-corner-all">
	<div id="home">
		<a href="/opennaas-vcpe/secure/vcpeNetwork/home"><button id="homeButton" class="button" style="margin: 0px"><spring:message code="header.home"/></button></a>
	</div>
	<div id="languages">
		<input type="radio" id="english" name="radio"><label for="english"><span class="languageText"><spring:message code="header.language.english"/></span></label>
		<input type="radio" id="spanish" name="radio"><label for="spanish"><span class="languageText"><spring:message code="header.language.spanish"/></span></label>
	</div>
</div>

<br>

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

<c:if test="${not empty noticeMsg}" >
	<div class="notice">
		<span>
			<spring:message text="${noticeMsg}" />
		</span>
	</div>
</c:if>

