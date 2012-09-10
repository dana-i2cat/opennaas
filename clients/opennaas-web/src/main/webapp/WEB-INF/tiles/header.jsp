<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib  uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

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
