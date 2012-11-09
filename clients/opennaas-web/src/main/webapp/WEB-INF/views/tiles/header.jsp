<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib  uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div style='float: right'>
	User: <sec:authentication property="principal.username" /> | <a href="<c:url value="/auth/logout" />">Logout</a>
</div>

<h1>
	<spring:message code="index.title"/>
</h1>

<div>
	<div style='float: right'>
		<a href="?locale=en_gb">en</a> | <a href="?locale=es_es">es</a>
	</div>
	<div style='float: left'>
		<a href="<c:url value="/secure/home" />">Home</a>
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

<c:if test="${not empty noticeMsg}" >
	<div class="notice">
		<span>
			<spring:message text="${noticeMsg}" />
		</span>
	</div>
</c:if>

