<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div id="updateIPs">
<form:form modelAttribute="VCPENetwork" action="updateIps" method="post">
	<form:hidden path="id" />
	<form:hidden path="name" />
	<form:hidden path="templateType" />
	<form:hidden path="clientIpRange"/> 
	<form:hidden path="logicalRouterMaster.name"/>
	<form:hidden path="logicalRouterMaster.templateName"/>
	<form:hidden path="logicalRouterBackup.name"/>
	<form:hidden path="logicalRouterBackup.templateName"/>

<div>				
	<c:forEach items="${VCPENetwork.logicalRouterMaster.interfaces}"	varStatus="vs" var="item">	
		<c:choose>
			<c:when test="${item.labelName == 'Up'}">																						
				<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].templateName" />
				<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].name" />
				<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].labelName" />
				<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].port" />
				<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].vlan" />																					
				<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].ipAddress" />																
			</c:when>
		</c:choose>		
	</c:forEach>
</div>

<div>				
	<c:forEach items="${VCPENetwork.logicalRouterBackup.interfaces}"	varStatus="vs" var="item">	
		<c:choose>
			<c:when test="${item.labelName == 'Up'}">																						
				<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].templateName" />
				<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].name" />
				<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].labelName" />
				<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].port" />
				<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].vlan" />																					
				<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].ipAddress" />																
			</c:when>
		</c:choose>		
	</c:forEach>
</div>

	<div>											
		<form:hidden path="bgp.clientASNumber"/>												
		<form:hidden path="bgp.nocASNumber" />							
		<form:hidden path="bgp.clientPrefixes"/>					
	</div>
				
	
<div id="client_master_box" class="ui-widget-content">				
	<c:forEach items="${VCPENetwork.logicalRouterMaster.interfaces}"	varStatus="vs" var="item">	
		<c:choose>
			<c:when test="${item.labelName == 'Down'}">
				<div class="input">
					<label>${item.labelName} Master</label>
					<br>																						
					<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].templateName" />
					<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].name" />
					<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].labelName" />
					<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].port" />
					<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].vlan" />																					
					<form:label for="logicalRouterMaster.interfaces[${vs.index}].ipAddress" path="logicalRouterMaster.interfaces[${vs.index}].ipAddress" cssErrorClass="error_field">
						<spring:message code="interface.ipAddress" />
					</form:label>
					<form:input path="logicalRouterMaster.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
					<form:errors path="logicalRouterMaster.interfaces[${vs.index}].ipAddress" size="13" />														
				</div>
			</c:when>
		</c:choose>		
	</c:forEach>
</div>	

<div id="inter_master_box" class="ui-widget-content">				
	<c:forEach items="${VCPENetwork.logicalRouterMaster.interfaces}"	varStatus="vs" var="item">	
		<c:choose>
			<c:when test="${item.labelName == 'Inter'}">
				<div class="input">
					<label>${item.labelName} Master</label>
					<br>																						
					<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].templateName" />
					<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].name" />
					<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].labelName" />
					<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].port" />
					<form:hidden path="logicalRouterMaster.interfaces[${vs.index}].vlan" />																					
					<form:label for="logicalRouterMaster.interfaces[${vs.index}].ipAddress" path="logicalRouterMaster.interfaces[${vs.index}].ipAddress" cssErrorClass="error_field">
						<spring:message code="interface.ipAddress" />
					</form:label>
					<form:input path="logicalRouterMaster.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
					<form:errors path="logicalRouterMaster.interfaces[${vs.index}].ipAddress" size="13" />														
				</div>
			</c:when>
		</c:choose>		
	</c:forEach>
</div>	
	
<div id="inter_backup_box" class="ui-widget-content">				
	<c:forEach items="${VCPENetwork.logicalRouterBackup.interfaces}"	varStatus="vs" var="item">	
		<c:choose>
			<c:when test="${item.labelName == 'Inter'}">
				<div class="input">
					<label>${item.labelName} Backup</label>
					<br>																						
					<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].templateName" />
					<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].name" />
					<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].labelName" />
					<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].port" />
					<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].vlan" />																					
					<form:label for="logicalRouterBackup.interfaces[${vs.index}].ipAddress" path="logicalRouterBackup.interfaces[${vs.index}].ipAddress" cssErrorClass="error_field">
						<spring:message code="interface.ipAddress" />
					</form:label>
					<form:input path="logicalRouterBackup.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
					<form:errors path="logicalRouterBackup.interfaces[${vs.index}].ipAddress" size="13" />														
				</div>
			</c:when>
		</c:choose>		
	</c:forEach>
</div>

<div id="client_backup_box" class="ui-widget-content">				
	<c:forEach items="${VCPENetwork.logicalRouterBackup.interfaces}"	varStatus="vs" var="item">	
		<c:choose>
			<c:when test="${item.labelName == 'Down'}">
				<div class="input">
					<label>${item.labelName} Backup</label>
					<br>																						
					<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].templateName" />
					<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].name" />
					<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].labelName" />
					<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].port" />
					<form:hidden path="logicalRouterBackup.interfaces[${vs.index}].vlan" />																					
					<form:label for="logicalRouterBackup.interfaces[${vs.index}].ipAddress" path="logicalRouterBackup.interfaces[${vs.index}].ipAddress" cssErrorClass="error_field">
						<spring:message code="interface.ipAddress" />
					</form:label>
					<form:input path="logicalRouterBackup.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
					<form:errors path="logicalRouterBackup.interfaces[${vs.index}].ipAddress" size="13" />														
				</div>
			</c:when>
		</c:choose>		
	</c:forEach>
</div>	
	
<!-- Start client -->
<div id="client_box">
	<h2>Client</h2>
	<div id="client_data_box" class="fields">
	<!-- VRRP -->
	<div id="vrrp_box">
		<h3><spring:message code="vrrp"/></h3>
		<div  id="vrrp_data_box" class="fields">
			<form:hidden path="vrrp.priorityMaster" />	
			<form:hidden path="vrrp.priorityBackup" />	
			<form:label for="vrrp.virtualIPAddress" path="vrrp.virtualIPAddress" cssErrorClass="error_field">
				<spring:message code="vrrp.virtualIPAddress" />
			</form:label>
			<br />
			<form:input path="vrrp.virtualIPAddress" size="20" />
			<br>
			<form:errors path="vrrp.virtualIPAddress" size="20" />
			<br>
			<button id="button1" class="button" disabled="disabled"><spring:message code="buttons.change"/></button>
		</div>
	</div>	
				
		<!--  IP range -->
		<div id="client_config_box" class="ui-widget-content">			
			<div class="field">
				<form:label for="clientIpRange" path="clientIpRange" cssErrorClass="error_field">
					<spring:message code="vcpenetwork.clientIpRange" />
				</form:label>			
				<br>	
				<form:input path="clientIpRange" size="12" onchange="fillBgpClientRange(this.value)"/>
				<form:errors path="clientIpRange" />			
			</div>				
		</div>	
		<!--  IGP selector -->
		<div id="client_config_box" class="ui-widget-content">
			<div class="field">
				<label>
					<spring:message code="vcpenetwork.igpSelector" />
				</label>
				<br />							
				<div id="ospf_info">
					<span>OSPF</span>								
					<span id="radioset2">
						<input type="radio" id="radio1" name="radio" checked="checked"><label for="radio1" style="width: 40px !important;">ON</label>
						<input type="radio" id="radio2" name="radio"><label for="radio2" style="width: 40px !important;">OFF</label>
					</span>							
				</div>
			</div>					
		</div>	
		<!--  Firewall rules -->
		<div id="client_config_firewall" class="ui-widget-content">
			<div class="field">
				<label>
					<spring:message code="vcpenetwork.firewall" />
				</label>
				<br />
				<div>
					<table id="firewallTable" class="full">
					    <tr>
					        <th>Name</th>
					        <th>Action</th>
					        <th>Protocol</th>
					        <th>Source</th>
					        <th>Destination</th>
					        <th>Port</th>
					        <th>&nbsp;</th>
					    </tr>
					    <tr>
					        <td>WWW</td>
					        <td>Allow</td>
					        <td>TCP</td>
					        <td>A*</td>
					        <td>any</td>
					        <td>80</td>
					        <td class="fwButton"><button id="fwRemove1">Remove</button></td>
					    </tr>
					    <tr>
					        <td>IRC</td>
					        <td>Block</td>
					        <td>TCP</td>
					        <td>A*</td>
					        <td>any</td>
					        <td>6667</td>
					        <td class="fwButton"><button id="fwRemove2">Remove</button></td>
					    </tr>
					     <tr>
					        <td>WS</td>
					        <td>Block</td>
					        <td>TCP</td>
					        <td>any</td>
					        <td>A*</td>
					        <td>8080</td>
					        <td class="fwButton"><button id="fwRemove3">Remove</button></td>
					    </tr>
					    <tr>
					        <td><input size="10"></input></td>
					        <td>
					        	<form>
						        	<select>
										<option value="">Allow</option>
										<option value="">Block</option>
									</select>
								</form>
							</td>
					        <td>
					        	<form>
						        	<select>
										<option value="">TCP</option>
										<option value="">UDP</option>
									</select>
								</form>
					        </td>
					        <td><input size="10"></input></td>
					        <td><input size="10"></input></td>
					        <td><input size="10"></input></td>
					        <td class="fwButton"><button id="fwAdd">Add</button></td>
					    </tr>
					</table>
				</div>
				<br>
			</div>					
		</div> <!-- End Firewall  -->																					
		<br>
	</div>						
</div>
<!-- End client -->

<input id="updateIpButton" class="button" type="submit" value="<spring:message code="buttons.update"/>" />
</form:form>
</div>