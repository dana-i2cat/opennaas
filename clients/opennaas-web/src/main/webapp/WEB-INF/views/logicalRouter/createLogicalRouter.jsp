<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib  uri="http://www.springframework.org/tags" prefix="spring" %>
	
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