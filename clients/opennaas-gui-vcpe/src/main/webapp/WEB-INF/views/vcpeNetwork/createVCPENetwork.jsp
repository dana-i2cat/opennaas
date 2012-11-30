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

		<!-- WAN -->
		<div id="wan_logical">
			<h2><spring:message code="home.wan"/></h2>			
		</div>	
		<!-- Up Interfaces -->
		<div id="up_interfaces">
			<div id="up_master" class="ui-widget-content">
				<c:forEach items="${VCPENetwork.logicalRouter1.interfaces}" varStatus="vs" var="item">					
					<c:choose>
						<c:when test="${item.labelName == 'Up'}">							
							<label>${item.labelName} Master</label><br>
							<div class="ui-widget-content config_content">
								<form:hidden path="logicalRouter1.interfaces[${vs.index}].templateName" />
								<form:hidden path="logicalRouter1.interfaces[${vs.index}].labelName" />
								<form:label for="logicalRouter1.interfaces[${vs.index}].name" path="logicalRouter1.interfaces[${vs.index}].name" cssErrorClass="error">
									<form:label for="logicalRouter1.interfaces[${vs.index}].name" path="logicalRouter1.interfaces[${vs.index}].name" cssErrorClass="error">
										<spring:message code="interface.name" />
									</form:label>
								</form:label>
								<form:input path="logicalRouter1.interfaces[${vs.index}].name" size="8" readonly="true" onchange="isInterfaceFree('${VCPENetwork.id}', this, document.getElementById('logicalRouter1.interfaces${vs.index}.port'));" />.
								<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8" />
								<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter1.interfaces${vs.index}.name'), this);" />
								<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="3" />
								<br>
								<form:label for="logicalRouter1.interfaces[${vs.index}].ipAddress" path="logicalRouter1.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
									<spring:message code="interface.ipAddress" />
								</form:label>
								<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this);" />
								<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" />
								<br>
								<form:label for="logicalRouter1.interfaces[${vs.index}].vlan" path="logicalRouter1.interfaces[${vs.index}].vlan" cssErrorClass="error">
									<spring:message code="interface.vlan" />
								</form:label>
								<form:input path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this, document.getElementById('logicalRouter1.interfaces${vs.index}.name'));" />
								<form:errors path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" />
							</div>
						</c:when>
					</c:choose>				
				</c:forEach>
			</div>
			<div id="up_backup" class="ui-widget-content">				
				<c:forEach items="${VCPENetwork.logicalRouter2.interfaces}" varStatus="vs" var="item">
					
						<c:choose>
							<c:when test="${item.labelName == 'Up'}">
								<label>${item.labelName} Backup</label><br>
								<div class="ui-widget-content config_content">
								<form:hidden path="logicalRouter2.interfaces[${vs.index}].templateName" />
								 <form:hidden path="logicalRouter2.interfaces[${vs.index}].labelName" />
								<form:label for="logicalRouter2.interfaces[${vs.index}].name" path="logicalRouter2.interfaces[${vs.index}].name" cssErrorClass="error">
									<form:label for="logicalRouter2.interfaces[${vs.index}].port" path="logicalRouter2.interfaces[${vs.index}].port" cssErrorClass="error">
										<spring:message code="interface.name" />
									</form:label>
								</form:label>
								<form:input path="logicalRouter2.interfaces[${vs.index}].name" size="8" readonly="true" onchange="isInterfaceFree('${VCPENetwork.id}', this, document.getElementById('logicalRouter2.interfaces${vs.index}.port'));" />.
								<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8" />
								<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter2.interfaces${vs.index}.name'), this);" />
								<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="3" />
								<br>
								<form:label for="logicalRouter2.interfaces[${vs.index}].ipAddress" path="logicalRouter2.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
									<spring:message code="interface.ipAddress" />
								</form:label>
								<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this);" />
								<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" />
								<br>
								<form:label for="logicalRouter2.interfaces[${vs.index}].vlan" path="logicalRouter2.interfaces[${vs.index}].vlan" cssErrorClass="error">
									<spring:message code="interface.vlan" />
								</form:label>
								<form:input path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this, document.getElementById('logicalRouter2.interfaces${vs.index}.name'));" />
								<form:errors path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" />
								</div>
							</c:when>
						</c:choose>
					
				</c:forEach>
			</div>
		</div>
		<!-- Start vCPE -->
		<div id="vcpe">
			<h2><spring:message code="vcpenetwork.create.vcpe"/> ${VCPENetwork.name}</h2>
			<div id="acc_body">
				<!-- BGP -->
				<div id="bgp">
					<h3><spring:message code="bgp"/></h3>
					<div id="bgp_data" class="fields">
						<div class="field">
							<form:label for="bgp.clientASNumber" path="bgp.clientASNumber" cssErrorClass="error">
								<spring:message code="bgp.clientASNumber" />
							</form:label>
							<br />				
							<form:input path="bgp.clientASNumber" size="10" />
							<br />		
							<form:errors path="bgp.clientASNumber" size="8" />
						</div>
						<div class="field"> 
							<form:label for="bgp.nocASNumber" path="bgp.nocASNumber" cssErrorClass="error">
								<spring:message code="bgp.nocASNumber" />
							</form:label>
							<br />
							<form:input path="bgp.nocASNumber" size="10" />
							<br />		
							<form:errors path="bgp.nocASNumber" size="8" />
						</div>
						<div class="field">
							<form:label for="bgp.clientPrefixes" path="bgp.clientPrefixes" cssErrorClass="error">
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

			
				<!-- Routers -->
				<div id="vcpe_topology">
				<h3><spring:message code="vcpenetwork.topology"/></h3>
					<div id="vcpe_routers">
						<div id="lr_master">
						<h3><spring:message code="vcpenetwork.lrmaster"/></h3>
							<div>
								<div id="config" class="ui-widget-content">
									<p>Global configuration parameters</p>
								</div>
							<button id="button5" class="button" disabled="disabled"><spring:message code="buttons.activate"/></button>
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
					<div id="client_master" class="ui-widget-content">				
						<c:forEach items="${VCPENetwork.logicalRouter1.interfaces}"
							varStatus="vs" var="item">
							
								<c:choose>
									<c:when test="${item.labelName == 'Down'}">
										<label>${item.labelName} Master</label><br>
										<div class="ui-widget-content config_content">
										<form:hidden path="logicalRouter1.interfaces[${vs.index}].templateName" />
										 <form:hidden path="logicalRouter1.interfaces[${vs.index}].labelName" />
										<form:label for="logicalRouter1.interfaces[${vs.index}].name" path="logicalRouter1.interfaces[${vs.index}].name" cssErrorClass="error">
											<form:label for="logicalRouter1.interfaces[${vs.index}].port" path="logicalRouter1.interfaces[${vs.index}].port" cssErrorClass="error">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].name" size="8" readonly="true" onchange="isInterfaceFree('${VCPENetwork.id}', this, document.getElementById('logicalRouter1.interfaces${vs.index}.port'));" />.
										<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8" />
										<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter1.interfaces${vs.index}.name'), this);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="logicalRouter1.interfaces[${vs.index}].ipAddress" path="logicalRouter1.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" />
										<br>
										<form:label for="logicalRouter1.interfaces[${vs.index}].vlan" path="logicalRouter1.interfaces[${vs.index}].vlan" cssErrorClass="error">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this, document.getElementById('logicalRouter1.interfaces${vs.index}.name'));" />				
										<form:errors path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" />
										</div>
									</c:when>
								</c:choose>
							
						</c:forEach>
					</div>
					<div id="inter_master" class="ui-widget-content">
						<c:forEach items="${VCPENetwork.logicalRouter1.interfaces}"
							varStatus="vs" var="item">
							
								<c:choose>
									<c:when test="${item.labelName == 'Inter'}">
										<label>${item.labelName} Master</label><br>
										<div class="ui-widget-content config_content">
										<form:hidden path="logicalRouter1.interfaces[${vs.index}].templateName" />
										 <form:hidden path="logicalRouter1.interfaces[${vs.index}].labelName" />
										<form:label for="logicalRouter1.interfaces[${vs.index}].name" path="logicalRouter1.interfaces[${vs.index}].name" cssErrorClass="error">
											<form:label for="logicalRouter1.interfaces[${vs.index}].port" path="logicalRouter1.interfaces[${vs.index}].port" cssErrorClass="error">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].name" readonly="true" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this, document.getElementById('logicalRouter1.interfaces${vs.index}.port'));" />.
										<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8" />
										<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter1.interfaces${vs.index}.name'), this);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="logicalRouter1.interfaces[${vs.index}].ipAddress" path="logicalRouter1.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this);" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" />
										<br>
										<form:label for="logicalRouter1.interfaces[${vs.index}].vlan" path="logicalRouter1.interfaces[${vs.index}].vlan" cssErrorClass="error">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this, document.getElementById('logicalRouter1.interfaces${vs.index}.name'));" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" />
										</div>
									</c:when>
								</c:choose>
							
						</c:forEach>
					</div>
					<div id="inter_backup" class="ui-widget-content">
						<c:forEach items="${VCPENetwork.logicalRouter2.interfaces}" varStatus="vs" var="item">
							
								<c:choose>
									<c:when test="${item.labelName == 'Inter'}">
										<label>${item.labelName} Backup</label><br>
										<div class="ui-widget-content config_content">
										<form:hidden path="logicalRouter2.interfaces[${vs.index}].templateName" />
										 <form:hidden path="logicalRouter2.interfaces[${vs.index}].labelName" />
										<form:label for="logicalRouter2.interfaces[${vs.index}].name" path="logicalRouter2.interfaces[${vs.index}].name" cssErrorClass="error">
											<form:label for="logicalRouter2.interfaces[${vs.index}].port" path="logicalRouter2.interfaces[${vs.index}].port" cssErrorClass="error">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].name" readonly="true" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this, document.getElementById('logicalRouter2.interfaces${vs.index}.port'));" />.
										<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8" />
										<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter2.interfaces${vs.index}.name'), this);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="logicalRouter2.interfaces[${vs.index}].ipAddress" path="logicalRouter2.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" />
										<br>
										<form:label for="logicalRouter2.interfaces[${vs.index}].vlan" path="logicalRouter2.interfaces[${vs.index}].vlan" cssErrorClass="error">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this, document.getElementById('logicalRouter2.interfaces${vs.index}.name'));" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" />
										</div>
									</c:when>
								</c:choose>
							
						</c:forEach>
					</div>
					<div id="client_backup" class="ui-widget-content">
						<c:forEach items="${VCPENetwork.logicalRouter2.interfaces}" varStatus="vs" var="item">
							
								<c:choose>
									<c:when test="${item.labelName == 'Down'}">
										<label>${item.labelName} Backup</label><br>
										<div class="ui-widget-content config_content">
										<form:hidden path="logicalRouter2.interfaces[${vs.index}].templateName" />
										<form:hidden path="logicalRouter2.interfaces[${vs.index}].labelName" />
										<form:label for="logicalRouter2.interfaces[${vs.index}].name" path="logicalRouter2.interfaces[${vs.index}].name" cssErrorClass="error">
											<form:label for="logicalRouter2.interfaces[${vs.index}].port" path="logicalRouter2.interfaces[${vs.index}].port" cssErrorClass="error">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].name" readonly="true" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this, document.getElementById('logicalRouter2.interfaces${vs.index}.port'));" />.
										<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8" />
										<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter2.interfaces${vs.index}.name'), this);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="logicalRouter2.interfaces[${vs.index}].ipAddress" path="logicalRouter2.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this);" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" />
										<br>
										<form:label for="logicalRouter2.interfaces[${vs.index}].vlan" path="logicalRouter2.interfaces[${vs.index}].vlan" cssErrorClass="error">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this, document.getElementById('logicalRouter2.interfaces${vs.index}.name'));" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" />
										</div>
									</c:when>
								</c:choose>
							
						</c:forEach>
					</div> <!-- End All interfaces  -->
				</div>				
			</div> <!-- End Topology -->
		</div>
	</div>
		<!-- End vCPE -->
		<!-- Start BoD -->
		<div id="bod">
			<h2><spring:message code="bod"/></h2>
			<div>
				<div id="bod_master" class="ui-widget-content"> 
					<label><spring:message code="bod.linkMaster" /></label>
					<div id="bod_inputs">
						<form:label path="bod.linkMaster.source.name">
							<form:label path="bod.linkMaster.source.port">
								<spring:message code="interface.name" />:&nbsp;
							</form:label>
						</form:label>
						${VCPENetwork.bod.linkMaster.source.name}.${VCPENetwork.bod.linkMaster.source.port}
						<br>
						<form:label path="bod.linkMaster.source.vlan">
							<spring:message code="interface.vlan" />:&nbsp;
						</form:label>
						${VCPENetwork.bod.linkMaster.source.vlan}
						<br>
					</div>
					<div id="bod_buttons">
						<button id="button7" class="button" disabled="disabled"><spring:message code="buttons.cancel"/></button>
						<button id="button8" class="button" disabled="disabled"><spring:message code="buttons.renew"/></button>					
					</div>
				</div>

				<div id="bod_inter" class="ui-widget-content"> 
					<label><spring:message code="bod.linkInter" /></label>
					<div id="bod_inputs">
						<form:label path="bod.linkInter.source.name">
							<form:label path="bod.linkInter.source.port">
								<spring:message code="interface.name" />:&nbsp;
							</form:label>
						</form:label>
						${VCPENetwork.bod.linkInter.source.name}.${VCPENetwork.bod.linkInter.source.port}
						<br>
						<form:label path="bod.linkInter.source.vlan">
							<spring:message code="interface.vlan" />:&nbsp;
						</form:label>
						${VCPENetwork.bod.linkInter.source.vlan}
						<br>
					</div>
					<div id="bod_buttons">
						<button id="button9" class="button" disabled="disabled"><spring:message code="buttons.cancel"/></button>
						<button id="button10" class="button" disabled="disabled"><spring:message code="buttons.renew"/></button>
					</div>
				</div>
				
				<div id="bod_backup" class="ui-widget-content"> 
					<label><spring:message code="bod.linkBackup" /></label>
					<div id="bod_inputs">
						<form:label path="bod.linkBackup.source.name">
							<form:label path="bod.linkBackup.source.port">
								<spring:message code="interface.name" />:&nbsp;
							</form:label>
						</form:label>
						${VCPENetwork.bod.linkBackup.source.name}.${VCPENetwork.bod.linkBackup.source.port}
						<br>
						<form:label path="bod.linkBackup.source.vlan">
							<spring:message code="interface.vlan" />:&nbsp;
						</form:label>
						${VCPENetwork.bod.linkBackup.source.vlan}
						<br>
					</div>
					<div id="bod_buttons">
						<button id="button11" class="button" disabled="disabled"><spring:message code="buttons.cancel"/></button>
						<button id="button12" class="button" disabled="disabled"><spring:message code="buttons.renew"/></button>
					</div>
				</div>
			</div>
		</div>
		<!-- End BoD  -->
		<!-- Start Client interfaces -->
		<div id="client_interfaces">
			<div id="client_down_master" class="ui-widget-content">
				
					<label><spring:message code="bod.ifaceClient"/></label><br>
					<div class="ui-widget-content config_content">
					<form:hidden path="bod.ifaceClient.templateName" />
					<form:label for="bod.ifaceClient.name" path="bod.ifaceClient.name" cssErrorClass="error">
						<form:label for="bod.ifaceClient.port" path="bod.ifaceClient.port" cssErrorClass="error">
							<spring:message code="interface.name" />
						</form:label>
					</form:label>
					<form:input path="bod.ifaceClient.name" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this, document.getElementById('bod.ifaceClient.port'));" />.				
					<form:errors path="bod.ifaceClient.name" size="8" />
					<form:input path="bod.ifaceClient.port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('bod.ifaceClient.name'), this);" />	
					<form:errors path="bod.ifaceClient.port" size="3" />
					<br>
					<form:label for="bod.ifaceClient.vlan" path="bod.ifaceClient.vlan" cssErrorClass="error">
						<spring:message code="interface.vlan" />
					</form:label>
					<form:input path="bod.ifaceClient.vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this, document.getElementById('bod.ifaceClient.name'));" />
					<br>
					<form:errors path="bod.ifaceClient.vlan" size="3" />
				</div>
			</div>
			<div id="client_down_backup" class="ui-widget-content">
				
					<label><spring:message code="bod.ifaceClientBackup"/></label><br>
					<div class="ui-widget-content config_content">
					<form:hidden path="bod.ifaceClientBackup.templateName" />
					<form:label for="bod.ifaceClientBackup.name" path="bod.ifaceClientBackup.name" cssErrorClass="error">
						<form:label for="bod.ifaceClientBackup.port" path="bod.ifaceClientBackup.port" cssErrorClass="error">
							<spring:message code="interface.name" />
						</form:label>
					</form:label>
					<form:input path="bod.ifaceClientBackup.name" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this, document.getElementById('bod.ifaceClientBackup.port'));" />.
					<form:errors path="bod.ifaceClientBackup.name" size="8" />
					<form:input path="bod.ifaceClientBackup.port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('bod.ifaceClientBackup.name'), this);" />
					<form:errors path="bod.ifaceClientBackup.port" size="3" />
					<br>
					<form:label for="bod.ifaceClientBackup.vlan" path="bod.ifaceClientBackup.vlan" cssErrorClass="error">
						<spring:message code="interface.vlan" />
					</form:label>
					<form:input path="bod.ifaceClientBackup.vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this, document.getElementById('bod.ifaceClientBackup.name')); " />
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
			
			<div id="vrrp">
					<h3><spring:message code="vrrp"/></h3>
					<div  id="vrrp_data" class="fields">
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
						<button id="button3" class="button" disabled="disabled"><spring:message code="buttons.deactivate"/></button>
						<button id="button4" class="button" disabled="disabled"><spring:message code="buttons.reapply"/></button>
					</div>
				</div>			

			
				<!--  vCPE name and template -->
				<div id="client_config" class="ui-widget-content">
					<div class="field">
						<form:label for="name" path="name" cssErrorClass="error">
							<spring:message code="vcpenetwork.name" />
						</form:label>
						<br />
						<form:input path="name" />
						<br>
						<form:errors path="name" />
					</div>					
					<div class="field">
						<form:label for="template" path="template" cssErrorClass="error">
							<spring:message code="vcpenetwork.template" />
						</form:label>
						<form:select path="template">
							<form:option value="basic.template">Basic Template </form:option>
						</form:select>
						<form:errors path="template" />
					</div>
				</div>
				<!--  IP range -->
				<div id="client_config" class="ui-widget-content">			
					<div class="field">
						<form:label for="clientIpRange" path="clientIpRange" cssErrorClass="error">
							<spring:message code="vcpenetwork.clientIpRange" />
						</form:label>
						<br />
						<form:input path="clientIpRange" size="20" />
						<br>
						<form:errors path="clientIpRange" />
					</div>				
				</div>	
				<!--  IGP selector -->
				<div id="client_config" class="ui-widget-content">
					<div class="field">
						<label>
							<spring:message code="vcpenetwork.igpSelector" />
						</label>
						<br />							
						<div id="ospf_info">
							<span>OSPF</span>								
							<span id="radioset">
								<input type="radio" id="radio1" name="radio" checked="checked"><label style="width: 40px !important;" for="radio1">ON</label>
								<input type="radio" id="radio2" name="radio"><label for="radio2" style="width: 40px !important;">OFF</label>
							</span>							
						</div>
						<br>
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
				</div>																					
				<br>
			</div>						
		</div>
		<!-- End client -->
		<input id="submitButton" class="button" type="submit" value="<spring:message code="buttons.create"/>" />
	</form:form>
</div>