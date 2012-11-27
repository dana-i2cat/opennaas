<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div id="createVCPENetwork" >
	<form:form modelAttribute="VCPENetwork" action="${action}" method="post">
		<form:hidden path="id" />
		<form:hidden path="logicalRouter1.name" />
		<form:hidden path="logicalRouter1.templateName" />
		<form:hidden path="logicalRouter2.name" />
		<form:hidden path="logicalRouter2.templateName" />
		<!-- Graphical view -->
		<!-- Start vCPE -->
		<div id="vcpe">
			<h2><spring:message code="vcpenetwork.create.vcpe"/> ${VCPENetwork.name}</h2>
			<div id="acc_body">
				<!-- BGP -->
				<div id="bgp">
					<h2><spring:message code="bgp"/></h2>
					<div id="bgp_data" class="fields">
						<div class="field">
							<form:label for="bgp.clientASNumber" path="bgp.clientASNumber" cssErrorClass="error_field">
								<spring:message code="bgp.clientASNumber" />
							</form:label>
							<br />				
							<form:input path="bgp.clientASNumber" size="10" />
							<br />		
							<form:errors path="bgp.clientASNumber" size="8" />
						</div>
						<div class="field"> 
							<form:label for="bgp.nocASNumber" path="bgp.nocASNumber" cssErrorClass="error_field">
								<spring:message code="bgp.nocASNumber" />
							</form:label>
							<br />
							<form:input path="bgp.nocASNumber" size="10" />
							<br />		
							<form:errors path="bgp.nocASNumber" size="8" />
						</div>
						<div class="field">
							<form:label for="bgp.clientPrefixes" path="bgp.clientPrefixes" cssErrorClass="error_field">
								<spring:message code="bgp.clientPrefixes" />
							</form:label>
							<br />
							<form:input path="bgp.clientPrefixes" size="16" />
							<br />		
							<form:errors path="bgp.clientPrefixes" size="8" />
						</div>
						<br>
						<div>
							<button id="button" class="button" disabled="disabled"><spring:message code="buttons.deactivate"/></button>
							<button id="button2" class="button" disabled="disabled"><spring:message code="buttons.reapply"/></button>
						</div>
					</div>
				</div>
				<!-- VRRP -->
				<div id="vrrp">
					<h2><spring:message code="vrrp"/></h2>
					<div  id="vrrp_data" class="fields">
						<form:hidden path="vrrp.priorityMaster" />	
						<form:hidden path="vrrp.priorityBackup" />	
						<form:label for="vrrp.virtualIPAddress" path="vrrp.virtualIPAddress" cssErrorClass="error_field">
							<spring:message code="vrrp.virtualIPAddress" />
						</form:label>
						<br />
						<form:input path="vrrp.virtualIPAddress" size="8" />
						<br>
						<form:errors path="vrrp.virtualIPAddress" size="8" />
						<br>
						<button id="button3" class="button" disabled="disabled"><spring:message code="buttons.deactivate"/></button>
						<button id="button4" class="button" disabled="disabled"><spring:message code="buttons.reapply"/></button>
					</div>
				</div>	

				<!-- Up Interfaces -->
				<div id="up_interfaces">
					<div id="up_master" class="ui-widget-content">
						<c:forEach items="${VCPENetwork.logicalRouter1.interfaces}" varStatus="vs" var="item">
							<div class="input">
								<c:choose>
									<c:when test="${item.labelName == 'Up'}">
										<label>${item.labelName} Master</label><br>
										<form:hidden path="logicalRouter1.interfaces[${vs.index}].templateName" />
										<form:label for="logicalRouter1.interfaces[${vs.index}].name" path="logicalRouter1.interfaces[${vs.index}].name" cssErrorClass="error_field">
											<form:label for="logicalRouter1.interfaces[${vs.index}].name" path="logicalRouter1.interfaces[${vs.index}].name" cssErrorClass="error_field">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].name" size="8" readonly="true" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter1.interfaces${vs.index}.port').value);" />.
										<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8" />
										<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter1.interfaces${vs.index}.name').value, this.value);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="logicalRouter1.interfaces[${vs.index}].ipAddress" path="logicalRouter1.interfaces[${vs.index}].ipAddress" cssErrorClass="error_field">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" />
										<br>
										<form:label for="logicalRouter1.interfaces[${vs.index}].vlan" path="logicalRouter1.interfaces[${vs.index}].vlan" cssErrorClass="error_field">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this.value);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" />
									</c:when>
								</c:choose>
							</div>
						</c:forEach>
					</div>
					<div id="up_backup" class="ui-widget-content">				
						<c:forEach items="${VCPENetwork.logicalRouter2.interfaces}" varStatus="vs" var="item">
							<div class="input">
								<c:choose>
									<c:when test="${item.labelName == 'Up'}">
										<label>${item.labelName} Backup</label><br>
										<form:hidden path="logicalRouter2.interfaces[${vs.index}].templateName" />
										<form:label for="logicalRouter2.interfaces[${vs.index}].name" path="logicalRouter2.interfaces[${vs.index}].name" cssErrorClass="error_field">
											<form:label for="logicalRouter2.interfaces[${vs.index}].port" path="logicalRouter2.interfaces[${vs.index}].port" cssErrorClass="error_field">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].name" size="8" readonly="true" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter2.interfaces${vs.index}.port').value);" />.
										<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8" />
										<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter2.interfaces${vs.index}.name').value, this.value);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="logicalRouter2.interfaces[${vs.index}].ipAddress" path="logicalRouter2.interfaces[${vs.index}].ipAddress" cssErrorClass="error_field">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" />
										<br>
										<form:label for="logicalRouter2.interfaces[${vs.index}].vlan" path="logicalRouter2.interfaces[${vs.index}].vlan" cssErrorClass="error_field">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this.value);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" />
									</c:when>
								</c:choose>
							</div>
						</c:forEach>
					</div>
				</div>
				<!-- Routers -->
				<div id="vcpe_routers">
					<div id="lr_master">
					<h3><spring:message code="vcpenetwork.lrmaster"/></h3>
						<div>
							<div id="config" class="ui-widget-content">
								<p>Global configuration parameters</p>
							</div>
						<button id="button5" class="button"><spring:message code="buttons.activate"/></button>
						</div>
					</div>
					<div id="lr_backup">
					<h3><spring:message code="vcpenetwork.lrbackup"/></h3>
						<div>
							<div id="config" class="ui-widget-content">
								<p>Global configuration parameters</p>
							</div>
						<button id="button6" class="button"><spring:message code="buttons.backup"/></button>
					</div>
				</div>
				<!-- VCPE Interfaces -->
				<div id="vcpe_interfaces">
					<div id="client_master" class="ui-widget-content">				
						<c:forEach items="${VCPENetwork.logicalRouter1.interfaces}"
							varStatus="vs" var="item">
							<div class="input">
								<c:choose>
									<c:when test="${item.labelName == 'Down'}">
										<label>${item.labelName} Master</label><br>
										<form:hidden path="logicalRouter1.interfaces[${vs.index}].templateName" />
										<form:label for="logicalRouter1.interfaces[${vs.index}].name" path="logicalRouter1.interfaces[${vs.index}].name" cssErrorClass="error_field">
											<form:label for="logicalRouter1.interfaces[${vs.index}].port" path="logicalRouter1.interfaces[${vs.index}].port" cssErrorClass="error_field">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].name" size="8" readonly="true" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter1.interfaces${vs.index}.port').value);" />.
										<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8" />
										<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter1.interfaces${vs.index}.name').value, this.value);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="logicalRouter1.interfaces[${vs.index}].ipAddress" path="logicalRouter1.interfaces[${vs.index}].ipAddress" cssErrorClass="error_field">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" />
										<br>
										<form:label for="logicalRouter1.interfaces[${vs.index}].vlan" path="logicalRouter1.interfaces[${vs.index}].vlan" cssErrorClass="error_field">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this.value);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" />
									</c:when>
								</c:choose>
							</div>
						</c:forEach>
					</div>
					<div id="inter_master" class="ui-widget-content">
						<c:forEach items="${VCPENetwork.logicalRouter1.interfaces}"
							varStatus="vs" var="item">
							<div class="input">
								<c:choose>
									<c:when test="${item.labelName == 'Inter'}">
										<label>${item.labelName} Master</label><br>
										<form:hidden path="logicalRouter1.interfaces[${vs.index}].templateName" />
										<form:label for="logicalRouter1.interfaces[${vs.index}].name" path="logicalRouter1.interfaces[${vs.index}].name" cssErrorClass="error_field">
											<form:label for="logicalRouter1.interfaces[${vs.index}].port" path="logicalRouter1.interfaces[${vs.index}].port" cssErrorClass="error_field">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].name" readonly="true" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter1.interfaces${vs.index}.port').value);" />.
										<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8" />
										<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter1.interfaces${vs.index}.name').value, this.value);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="logicalRouter1.interfaces[${vs.index}].ipAddress" path="logicalRouter1.interfaces[${vs.index}].ipAddress" cssErrorClass="error_field">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" />
										<br>
										<form:label for="logicalRouter1.interfaces[${vs.index}].vlan" path="logicalRouter1.interfaces[${vs.index}].vlan" cssErrorClass="error_field">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this.value);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" />
									</c:when>
								</c:choose>
							</div>
						</c:forEach>
					</div>
					<div id="inter_backup" class="ui-widget-content">
						<c:forEach items="${VCPENetwork.logicalRouter2.interfaces}" varStatus="vs" var="item">
							<div class="input">
								<c:choose>
									<c:when test="${item.labelName == 'Inter'}">
										<label>${item.labelName} Backup</label><br>
										<form:hidden path="logicalRouter2.interfaces[${vs.index}].templateName" />
										<form:label for="logicalRouter2.interfaces[${vs.index}].name" path="logicalRouter2.interfaces[${vs.index}].name" cssErrorClass="error_field">
											<form:label for="logicalRouter2.interfaces[${vs.index}].port" path="logicalRouter2.interfaces[${vs.index}].port" cssErrorClass="error_field">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].name" readonly="true" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter2.interfaces${vs.index}.port').value);" />.
										<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8" />
										<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter2.interfaces${vs.index}.name').value, this.value);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="logicalRouter2.interfaces[${vs.index}].ipAddress" path="logicalRouter2.interfaces[${vs.index}].ipAddress" cssErrorClass="error_field">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" />
										<br>
										<form:label for="logicalRouter2.interfaces[${vs.index}].vlan" path="logicalRouter2.interfaces[${vs.index}].vlan" cssErrorClass="error_field">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this.value);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" />
									</c:when>
								</c:choose>
							</div>
						</c:forEach>
					</div>
					<div id="client_backup" class="ui-widget-content">
						<c:forEach items="${VCPENetwork.logicalRouter2.interfaces}" varStatus="vs" var="item">
							<div class="input">
								<c:choose>
									<c:when test="${item.labelName == 'Down'}">
										<label>${item.labelName} Backup</label><br>
										<form:hidden path="logicalRouter2.interfaces[${vs.index}].templateName" />
										<form:label for="logicalRouter2.interfaces[${vs.index}].name" path="logicalRouter2.interfaces[${vs.index}].name" cssErrorClass="error_field">
											<form:label for="logicalRouter2.interfaces[${vs.index}].port" path="logicalRouter2.interfaces[${vs.index}].port" cssErrorClass="error_field">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].name" readonly="true" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter2.interfaces${vs.index}.port').value);" />.
										<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8" />
										<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter2.interfaces${vs.index}.name').value, this.value);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="logicalRouter2.interfaces[${vs.index}].ipAddress" path="logicalRouter2.interfaces[${vs.index}].ipAddress" cssErrorClass="error_field">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" />
										<br>
										<form:label for="logicalRouter2.interfaces[${vs.index}].vlan" path="logicalRouter2.interfaces[${vs.index}].vlan" cssErrorClass="error_field">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this.value);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" />
									</c:when>
								</c:choose>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
		</div>
		<!-- End vCPE -->
		<!-- Start BoD -->
		<div id="bod">
			<h2><spring:message code="bod"/></h2>
			<div>
				<div id="bod_master" class="ui-widget">
					<h3><spring:message code="vcpenetwork.bod.master"/></h3>
					<div>
						<div id="config" class="ui-widget-content">
							<p>Global configuration parameters</p>
						</div>
						<button id="button7" class="button"><spring:message code="buttons.cancel"/></button>
						<button id="button8" class="button"><spring:message code="buttons.renew"/></button>
					</div>
				</div>
				<div id="bod_inter">
					<h3>Inter</h3>
					<div class="ui-widget">
						<div id="config" class="ui-widget-content">
							<p>Global configuration parameters</p>
						</div>
						<button id="button9" class="button"><spring:message code="buttons.cancel"/></button>
						<button id="button10" class="button"><spring:message code="buttons.renew"/></button>
					</div>
				</div>
				<div id="bod_backup">
					<h3>Backup</h3>
					<div class="ui-widget">
						<div id="config" class="ui-widget-content">
							<p>Global configuration parameters</p>
						</div>
						<button id="button11" class="button"><spring:message code="buttons.cancel"/></button>
						<button id="button23" class="button"><spring:message code="buttons.renew"/></button>
					</div>
				</div>
			</div>
		</div>
		<!-- End BoD  -->
		<!-- Start Client interfaces -->
		<div id="client_interfaces">
			<div id="client_down_master" class="ui-widget-content">
				<div class="input">
					<label><spring:message code="bod.ifaceClient"/></label><br>
					<form:hidden path="bod.ifaceClient.templateName" />
					<form:label for="bod.ifaceClient.name" path="bod.ifaceClient.name" cssErrorClass="error_field">
						<form:label for="bod.ifaceClient.port" path="bod.ifaceClient.port" cssErrorClass="error_field">
							<spring:message code="interface.name" />
						</form:label>
					</form:label>
					<form:input path="bod.ifaceClient.name" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('bod.ifaceClient.port').value);" />.				
					<form:errors path="bod.ifaceClient.name" size="8" />
					<form:input path="bod.ifaceClient.port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('bod.ifaceClient.name').value, this.value);" />	
					<form:errors path="bod.ifaceClient.port" size="3" />
					<br>
					<form:label for="bod.ifaceClient.vlan" path="bod.ifaceClient.vlan" cssErrorClass="error_field">
						<spring:message code="interface.vlan" />
					</form:label>
					<form:input path="bod.ifaceClient.vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this.value);" />
					<br>
					<form:errors path="bod.ifaceClient.vlan" size="3" />
				</div>
			</div>
			<div id="client_down_backup" class="ui-widget-content">
				<div class="input">
					<label><spring:message code="bod.ifaceClientBackup"/></label><br>
					<form:hidden path="bod.ifaceClientBackup.templateName" />
					<form:label for="bod.ifaceClientBackup.name" path="bod.ifaceClientBackup.name" cssErrorClass="error_field">
						<form:label for="bod.ifaceClientBackup.port" path="bod.ifaceClientBackup.port" cssErrorClass="error_field">
							<spring:message code="interface.name" />
						</form:label>
					</form:label>
					<form:input path="bod.ifaceClientBackup.name" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('bod.ifaceClientBackup.port').value);" />.
					<form:errors path="bod.ifaceClientBackup.name" size="8" />
					<form:input path="bod.ifaceClientBackup.port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('bod.ifaceClientBackup.name').value, this.value);" />
					<br>
					<form:errors path="bod.ifaceClientBackup.port" size="3" />
					<br>
					<form:label for="bod.ifaceClientBackup.vlan" path="bod.ifaceClientBackup.vlan" cssErrorClass="error_field">
						<spring:message code="interface.vlan" />
					</form:label>
					<form:input path="bod.ifaceClientBackup.vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this.value); " />
					<br>
					<form:errors path="bod.ifaceClientBackup.vlan" size="3" />
				</div>
			</div>
		</div>
		<!-- End Client interfaces -->
		<!-- Start client -->
		<div id="client">
			<h2>Client</h2>
			<div id="client_data" class="fields">
				<div class="field">
					<form:label for="name" path="name" cssErrorClass="error_field">
						<spring:message code="vcpenetwork.name" />
					</form:label>
					<br />
					<form:input path="name" />
					<br>
					<form:errors path="name" />
				</div>
	
				<div class="field">
					<form:label for="clientIpRange" path="clientIpRange" cssErrorClass="error_field">
						<spring:message code="vcpenetwork.clientIpRange" />
					</form:label>
					<br />
					<form:input path="clientIpRange" size="12" />
					<br>
					<form:errors path="clientIpRange" />
				</div>
				<div class="field">
					<form:label for="template" path="template" cssErrorClass="error_field">
						<spring:message code="vcpenetwork.template" />
					</form:label>
					<form:select path="template">
						<form:option value="basic.template">Basic Template </form:option>
					</form:select>
					<form:errors path="template" />
				</div>
				<br>
			</div>						
		</div>
		<!-- End client -->
		<input id="submitButton" class="button" type="submit" value="<spring:message code="buttons.create"/>" />
	</form:form>
</div>