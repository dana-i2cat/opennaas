<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib  uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<ul>
	<li><a href="<c:url value="/logicalRouter" />"><spring:message code="index.menu.logicalrouter"/></a></li>
</ul>
<ul>
	<li><a href="<c:url value="/queue" />"><spring:message code="index.menu.queue"/></a></li>
</ul>
