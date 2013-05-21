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

<c:set var="username">
	<sec:authentication property="principal.username" /> 
</c:set>

<c:if test="${username != logicalInfrastructure.providerNetwork1.owner}">		
	<c:set var="providerNet1Disabled" value="true" />
</c:if>

<c:if test="${username != logicalInfrastructure.providerNetwork2.owner}">		
	<c:set var="providerNet2Disabled" value="true" />
</c:if>

<sec:authorize access="hasRole('ROLE_NOC')">
	<h2 id="vcpe_title"><spring:message code="logical.mp.title"/></h2>
	<form:form modelAttribute="logicalInfrastructure" action="${action}" method="post">
		<form:hidden path="id" />
		<form:hidden path="name" />
		<form:hidden  path="templateType"/>
		
		<!-- Graphical view -->
		<div id="mpLogicalForm">	
			
			<!-- Wan 1 -->
			<div id="network_wan1_logical">
				<h3>${logicalInfrastructure.providerNetwork1.name}</h3>
				<div>			
					<div id="config" class="ui-widget-content">
						<form:hidden path="providerNetwork1.templateName" />
						<form:hidden path="providerNetwork1.name" />
						<form:label for="providerNetwork1.ASNumber" path="providerNetwork1.ASNumber" cssErrorClass="error">
							<spring:message code="network.ASNumber" />
						</form:label>
						<form:input readonly="${providerNet1Disabled}" path="providerNetwork1.ASNumber" size="8"/>
						<form:errors path="providerNetwork1.ASNumber" size="8" /><br>
						
						<form:label for="providerNetwork1.iPAddressRange" path="providerNetwork1.iPAddressRange" cssErrorClass="error">
							<spring:message code="network.ipAddress" />
						</form:label>
						<form:input readonly="${providerNet1Disabled}"  path="providerNetwork1.iPAddressRange" size="18" />
						<form:errors path="providerNetwork1.iPAddressRange" size="18" />
						
						<form:label for="providerNetwork1.owner" path="providerNetwork1.owner" cssErrorClass="error">
							<spring:message code="network.owner" />
						</form:label>
						<select disabled="disabled">
							<option>
								${logicalInfrastructure.providerNetwork1.owner}
							</option>
						</select>
						<form:hidden path="providerNetwork1.owner" />
						<form:errors path="providerNetwork1.owner" size="18" />
					</div>		
				</div>
			</div>
		
			<!-- Wan 2 -->
			<div id="network_wan2_logical">
				<h3>${logicalInfrastructure.providerNetwork2.name}</h3>
				<div>			
					<div id="config" class="ui-widget-content">
						<form:hidden path="providerNetwork2.templateName" />
						<form:hidden path="providerNetwork2.name" />
						<form:label for="providerNetwork2.ASNumber" path="providerNetwork2.ASNumber" cssErrorClass="error">
							<spring:message code="network.ASNumber" />
						</form:label>
						<form:input readonly="${providerNet2Disabled}" path="providerNetwork2.ASNumber" size="8"/>
						<form:errors path="providerNetwork2.ASNumber" size="8" /><br>
						
						<form:label for="providerNetwork2.iPAddressRange" path="providerNetwork2.iPAddressRange" cssErrorClass="error">
							<spring:message code="network.ipAddress" />
						</form:label>
						<form:input readonly="${providerNet2Disabled}"  path="providerNetwork2.iPAddressRange" size="18"/>
						<form:errors path="providerNetwork2.iPAddressRange" size="18" />
						
						<form:label for="providerNetwork2.owner" path="providerNetwork1.owner" cssErrorClass="error">
							<spring:message code="network.owner" />
						</form:label>
						<select disabled="disabled">
							<option>
								${logicalInfrastructure.providerNetwork2.owner}
							</option>
						</select>
						<form:hidden path="providerNetwork2.owner" />
						<form:errors path="providerNetwork2.owner" size="18" />
					</div>		
				</div>
			</div>
		
			<!-- WAN Interfaces -->
		
			<div id="iface_down_netprovider1" class="ui-widget-content">	
				<label><spring:message code="interface" />:&nbsp;</label>
				<div class="ui-widget-content config_content">								
					<div>
						<form:hidden path="providerNetwork1.networkInterface.templateName" />
						<form:hidden path="providerNetwork1.networkInterface.type" />
						<form:label for="providerNetwork1.networkInterface.name" path="providerNetwork1.networkInterface.name" cssErrorClass="error">
							<form:label for="providerNetwork1.networkInterface.port" path="providerNetwork1.networkInterface.port" cssErrorClass="error">
								<spring:message code="interface.name" />:
							</form:label>
						</form:label>
						<form:input readonly="${providerNet1Disabled}"  path="providerNetwork1.networkInterface.name" size="8"  />.
						<form:errors path="providerNetwork1.networkInterface.name" size="8" />
						<form:input readonly="${providerNet1Disabled}"  path="providerNetwork1.networkInterface.port" size="3"  />
						<form:errors path="providerNetwork1.networkInterface.port" size="3" />
						<br>
						<form:label for="providerNetwork1.networkInterface.ipAddress" path="providerNetwork1.networkInterface.ipAddress" cssErrorClass="error">
							<spring:message code="interface.ipAddress" />
						</form:label>
						<form:input readonly="${providerNet1Disabled}"  path="providerNetwork1.networkInterface.ipAddress" size="18"  />
						<form:errors path="providerNetwork1.networkInterface.ipAddress" size="18" />
						<br>
						<form:label for="providerNetwork1.networkInterface.vlan" path="providerNetwork1.networkInterface.vlan" cssErrorClass="error">
							<spring:message code="interface.vlan" />
						</form:label>
						<form:input readonly="${providerNet1Disabled}" path="providerNetwork1.networkInterface.vlan" size="3"  />
						<form:errors path="providerNetwork1.networkInterface.vlan" size="3" />
						
					</div>		
				</div>										
			</div>
	
			<div id="iface_down_netprovider2" class="ui-widget-content">	
				<label>Interface:&nbsp;</label>
				<div class="ui-widget-content config_content">								
					<div>
						<form:hidden path="providerNetwork2.networkInterface.templateName" />
						<form:hidden path="providerNetwork2.networkInterface.type" />
						<form:label for="providerNetwork2.networkInterface.name" path="providerNetwork2.networkInterface.name" cssErrorClass="error">
							<form:label for="providerNetwork2.networkInterface.port" path="providerNetwork2.networkInterface.port" cssErrorClass="error">
								<spring:message code="interface.name" />:
							</form:label>
						</form:label>
						<form:input readonly="${providerNet2Disabled}"  path="providerNetwork2.networkInterface.name" size="8"  />.
						<form:errors path="providerNetwork2.networkInterface.name" size="8" />
						<form:input readonly="${providerNet2Disabled}"  path="providerNetwork2.networkInterface.port" size="3"  />
						<form:errors path="providerNetwork2.networkInterface.port" size="3" />
						
						<br>
						<form:label for="providerNetwork2.networkInterface.ipAddress" path="providerNetwork2.networkInterface.ipAddress" cssErrorClass="error">
							<spring:message code="interface.ipAddress" />
						</form:label>
						<form:input readonly="${providerNet2Disabled}"  path="providerNetwork2.networkInterface.ipAddress" size="18"  />
						<form:errors path="providerNetwork2.networkInterface.ipAddress" size="18" />
						<br>
						<form:label for="providerNetwork2.networkInterface.vlan" path="providerNetwork2.networkInterface.vlan" cssErrorClass="error">
							<spring:message code="interface.vlan" />
						</form:label>
						<form:input readonly="${providerNet2Disabled}"  path="providerNetwork2.networkInterface.vlan" size="3"  />
						<form:errors path="providerNetwork2.networkInterface.vlan" size="3" />
					</div>		
				</div>										
			</div>
		
		</div>
		<c:if test='${action == "update"}'>
			<input id="submitButton" class="button" type="submit" value="<spring:message code="buttons.update"/>" />
		</c:if>
	</form:form>
</sec:authorize>