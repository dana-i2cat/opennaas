<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib  uri="http://www.springframework.org/tags" prefix="spring" %>

<div class="span-12 last">	
	<ul>
		<c:forEach var="action" items="${actions}">
			<li>
				<c:out value="${action}"></c:out>
			</li>
   		</c:forEach>
	</ul>
	<ul>
		<form:form modelAttribute="queue" action="queue/execute" method="get">
		  	<fieldset>		
				<legend><spring:message code="queue.listactions.legend"/></legend>		
				<input type="submit" value="Execute"/>
			</fieldset>
		</form:form>
	</ul>	
</div>