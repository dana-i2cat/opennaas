<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div>
	<h3><spring:message code="vcpenetwork" />:&nbsp;&nbsp;<spring:eval expression="vcpenetwork.name" /> </h3>
	<fieldset>
		<b><spring:message code="vcpenetwork.id" />:</b>&nbsp;<spring:eval expression="vcpenetwork.id" /><br/>
		<b><spring:message code="vcpenetwork.name" />:</b>&nbsp;<spring:eval expression="vcpenetwork.name" /><br/>
		<b><spring:message code="vcpenetwork.clientIpRange" />:</b>&nbsp;<spring:eval expression="vcpenetwork.clientIpRange" /><br/>
		<b><spring:message code="vcpenetwork.template" />:</b>&nbsp;<spring:eval expression="vcpenetwork.template" /><br/>
		
	</fieldset>
</div>

<div>
	<fieldset>
		<legend>
			<spring:message code="vcpenetwork.logicalrouter1" />	
		</legend>
		<b><spring:message code="logicalrouter.name" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter1.name" /><br/>
		<b><spring:message code="logicalrouter.interfaces" />:</b>
		<c:forEach items="${vcpenetwork.logicalRouter1.interfaces}" varStatus="vs" var="item">
			<div class="field">
				<label>${item.labelName} Interface</label>
				<div class="input">
					<b><spring:message code="interface.name" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter1.interfaces[${vs.index}].name" /><br/>
					<b><spring:message code="interface.port" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter1.interfaces[${vs.index}].port" /><br/>
					<b><spring:message code="interface.ipAddress" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter1.interfaces[${vs.index}].ipAddress" /><br/>
					<b><spring:message code="interface.vlan" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter1.interfaces[${vs.index}].vlan" /><br/>
				</div>
			</div>
		</c:forEach>
	</fieldset>
</div>

<div>
	<fieldset>
		<legend>
			<spring:message code="vcpenetwork.logicalrouter2" />	
		</legend>
		<b><spring:message code="logicalrouter.name" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter2.name" /><br/>
		<b><spring:message code="logicalrouter.interfaces" />:</b>
		<c:forEach items="${vcpenetwork.logicalRouter2.interfaces}" varStatus="vs" var="item">
			<div class="field">
				<label>${item.labelName} Interface</label>
				<div class="input">
					<b><spring:message code="interface.name" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter2.interfaces[${vs.index}].name" /><br/>
					<b><spring:message code="interface.port" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter2.interfaces[${vs.index}].port" /><br/>
					<b><spring:message code="interface.ipAddress" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter2.interfaces[${vs.index}].ipAddress" /><br/>
					<b><spring:message code="interface.vlan" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter2.interfaces[${vs.index}].vlan" /><br/>
				</div>
			</div>
		</c:forEach>
	</fieldset>
</div>



