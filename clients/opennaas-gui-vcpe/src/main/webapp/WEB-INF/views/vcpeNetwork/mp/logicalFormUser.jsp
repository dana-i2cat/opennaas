<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ page import="org.opennaas.extensions.vcpe.manager.templates.mp.TemplateConstants;"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="LR_CLIENT_IFACE_UP1"  value='<%= TemplateConstants.LR_CLIENT_IFACE_UP1 %>' />
<c:set var="LR_CLIENT_IFACE_UP2"  value='<%= TemplateConstants.LR_CLIENT_IFACE_UP2 %>' />

<h2 id="vcpe_title"><spring:message code="logical.mp.title"/></h2>
<form:form modelAttribute="logicalInfrastructure" action="${action}" method="post">
	<form:hidden  path="templateType"/>
	<!-- Graphical view -->
	<div id="mpLogicalForm">
		<div id="iface_up_netclient" class="ui-widget-content">	
			<label><spring:message code="interface"/></label>
			<div class="ui-widget-content config_content">								
				<div>
					<form:hidden path="clientNetwork.networkInterface.templateName" />
					<form:hidden path="clientNetwork.networkInterface.type" />
					<form:label for="clientNetwork.networkInterface.name" path="clientNetwork.networkInterface.name" cssErrorClass="error">
						<form:label for="clientNetwork.networkInterface.port" path="clientNetwork.networkInterface.port" cssErrorClass="error">
							<spring:message code="interface.name" />:
						</form:label>
					</form:label>
					<form:input path="clientNetwork.networkInterface.name" size="8"  />.
					<form:errors path="clientNetwork.networkInterface.name" size="8" />
					<form:input path="clientNetwork.networkInterface.port" size="3"  />
					<form:errors path="clientNetwork.networkInterface.port" size="3" />
					<br>
					<form:label for="clientNetwork.networkInterface.ipAddress" path="clientNetwork.networkInterface.ipAddress" cssErrorClass="error">
						<spring:message code="interface.ipAddress" />
					</form:label>
					<form:input path="clientNetwork.networkInterface.ipAddress" size="18"  />
					<form:errors path="clientNetwork.networkInterface.ipAddress" size="18" />
					<br>
					<form:label for="clientNetwork.networkInterface.vlan" path="clientNetwork.networkInterface.vlan" cssErrorClass="error">
						<spring:message code="interface.vlan" />
					</form:label>
					<form:input path="clientNetwork.networkInterface.vlan" size="3"  />
					<form:errors path="clientNetwork.networkInterface.vlan" size="3" />
				</div>		
			</div>										
		</div>
		
		<div id="network_client_logical">
			<h3>${logicalInfrastructure.clientNetwork.name}</h3>
			<div>		
				<!--  vCPE name -->
				<div id="config" class="ui-widget-content">
					<form:label for="name" path="name" cssErrorClass="error">
						<spring:message code="vcpenetwork.name" />
					</form:label>
					<br />
					<form:input path="name" />
					<br>
					<form:errors path="name" />
				</div>	
				<div id="config" class="ui-widget-content">
					<form:hidden path="clientNetwork.templateName" />
					<form:hidden path="clientNetwork.name" />
					<form:label for="clientNetwork.ASNumber" path="clientNetwork.ASNumber" cssErrorClass="error">
						<spring:message code="network.ASNumber" />
					</form:label>
					<form:input path="clientNetwork.ASNumber" size="8"/>
					<form:errors path="clientNetwork.ASNumber" size="8" /><br>
					
					<form:label for="clientNetwork.iPAddressRange" path="clientNetwork.iPAddressRange" cssErrorClass="error">
						<spring:message code="network.ipAddress" />
					</form:label>
					<form:input path="clientNetwork.iPAddressRange" size="18"/>
					<form:errors path="clientNetwork.iPAddressRange" size="18" />
					
					<form:label for="clientNetwork.owner" path="clientNetwork.owner" cssErrorClass="error">
						<spring:message code="network.owner" />
					</form:label>
					<form:select path="clientNetwork.owner" items="${usersClient}" />
					<form:errors path="clientNetwork.owner" size="18" />
				</div>		
			</div>		
		</div>
	</div>
			
	<!-- End client -->
	<c:if test='${action == "update"}'>
		<input id="submitButton" class="button" type="submit" value="<spring:message code="buttons.update"/>" />
	</c:if>
</form:form>
