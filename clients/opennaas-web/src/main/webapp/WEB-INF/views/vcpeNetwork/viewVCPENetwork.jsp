<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div>
	<fieldset class="box">
		<legend>
			<spring:message code="vcpenetwork" />:&nbsp;&nbsp;<spring:eval expression="vcpenetwork.name" />
		</legend>
		<b><spring:message code="vcpenetwork.id" />:</b>&nbsp;<spring:eval expression="vcpenetwork.id" /><br/>
		<b><spring:message code="vcpenetwork.name" />:</b>&nbsp;<spring:eval expression="vcpenetwork.name" /><br/>
		<b><spring:message code="vcpenetwork.clientIpRange" />:</b>&nbsp;<spring:eval expression="vcpenetwork.clientIpRange" /><br/>
		<b><spring:message code="vcpenetwork.template" />:</b>&nbsp;<spring:eval expression="vcpenetwork.template" /><br/>
		
	</fieldset>
</div>

<div>
	<fieldset class="box">
		<legend>
			<spring:message code="vcpenetwork.logicalrouter1" />:&nbsp;<spring:eval expression="vcpenetwork.logicalRouter1.name" />
		</legend>
		<c:forEach items="${vcpenetwork.logicalRouter1.interfaces}" varStatus="vs" var="item">
			<div class="field">			
				<h6>${item.labelName} Interface</h6>
				<hr>
				<div style="margin-left: 20px;" class="input">
					<b><spring:message code="interface.name" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter1.interfaces[${vs.index}].name" /><br/>
					<b><spring:message code="interface.port" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter1.interfaces[${vs.index}].port" /><br/>
					<b><spring:message code="interface.ipAddress" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter1.interfaces[${vs.index}].ipAddress" /><br/>
					<b><spring:message code="interface.vlan" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter1.interfaces[${vs.index}].vlan" /><br/>
				</div>
				<br/>
			</div>
		</c:forEach>
	</fieldset>
</div>

<div>
	<fieldset class="box">
		<legend>
			<spring:message code="vcpenetwork.logicalrouter2" />:&nbsp;<spring:eval expression="vcpenetwork.logicalRouter2.name" />
		</legend>
		<c:forEach items="${vcpenetwork.logicalRouter2.interfaces}" varStatus="vs" var="item">
			<div class="field">	
				<h6>${item.labelName} Interface</h6>
				<hr>
				<div style="margin-left: 20px;" class="input">
					<b><spring:message code="interface.name" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter2.interfaces[${vs.index}].name" /><br/>
					<b><spring:message code="interface.port" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter2.interfaces[${vs.index}].port" /><br/>
					<b><spring:message code="interface.ipAddress" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter2.interfaces[${vs.index}].ipAddress" /><br/>
					<b><spring:message code="interface.vlan" />:</b>&nbsp;<spring:eval expression="vcpenetwork.logicalRouter2.interfaces[${vs.index}].vlan" /><br/>
				</div>
				<br/>
			</div>
		</c:forEach>
	</fieldset>
</div>



