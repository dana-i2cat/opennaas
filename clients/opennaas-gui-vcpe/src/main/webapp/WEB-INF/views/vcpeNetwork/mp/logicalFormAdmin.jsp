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

<sec:authorize access="hasRole('ROLE_ADMIN')">
	<h2 id="vcpe_title"><spring:message code="logical.mp.title"/></h2>
	<form:form modelAttribute="logicalInfrastructure" action="${action}" method="post">
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
						<form:input path="providerNetwork1.ASNumber" size="8"/>
						<form:errors path="providerNetwork1.ASNumber" size="8" /><br>
						
						<form:label for="providerNetwork1.iPAddressRange" path="providerNetwork1.iPAddressRange" cssErrorClass="error">
							<spring:message code="network.ipAddress" />
						</form:label>
						<form:input path="providerNetwork1.iPAddressRange" size="18" />
						<form:errors path="providerNetwork1.iPAddressRange" size="18" />
						
						<form:label for="providerNetwork1.owner" path="providerNetwork1.owner" cssErrorClass="error">
							<spring:message code="network.owner" />
						</form:label>
						<form:select path="providerNetwork1.owner" items="${usersNOC}" />
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
						<form:input path="providerNetwork2.ASNumber" size="8"/>
						<form:errors path="providerNetwork2.ASNumber" size="8" /><br>
						
						<form:label for="providerNetwork2.iPAddressRange" path="providerNetwork2.iPAddressRange" cssErrorClass="error">
							<spring:message code="network.ipAddress" />
						</form:label>
						<form:input path="providerNetwork2.iPAddressRange" size="18"/>
						<form:errors path="providerNetwork2.iPAddressRange" size="18" />
						
						<form:label for="providerNetwork2.owner" path="providerNetwork1.owner" cssErrorClass="error">
							<spring:message code="network.owner" />
						</form:label>
						<form:select path="providerNetwork2.owner" items="${usersNOC}" />
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
						<form:input path="providerNetwork1.networkInterface.name" size="8"  />.
						<form:errors path="providerNetwork1.networkInterface.name" size="8" />
						<form:input path="providerNetwork1.networkInterface.port" size="3"  />
						<form:errors path="providerNetwork1.networkInterface.port" size="3" />
						<br>
						<form:label for="providerNetwork1.networkInterface.ipAddress" path="providerNetwork1.networkInterface.ipAddress" cssErrorClass="error">
							<spring:message code="interface.ipAddress" />
						</form:label>
						<form:input path="providerNetwork1.networkInterface.ipAddress" size="18"  />
						<form:errors path="providerNetwork1.networkInterface.ipAddress" size="18" />
						<br>
						<form:label for="providerNetwork1.networkInterface.vlan" path="providerNetwork1.networkInterface.vlan" cssErrorClass="error">
							<spring:message code="interface.vlan" />
						</form:label>
						<form:input path="providerNetwork1.networkInterface.vlan" size="3"  />
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
						<form:input path="providerNetwork2.networkInterface.name" size="8"  />.
						<form:errors path="providerNetwork2.networkInterface.name" size="8" />
						<form:input path="providerNetwork2.networkInterface.port" size="3"  />
						<form:errors path="providerNetwork2.networkInterface.port" size="3" />
						
						<br>
						<form:label for="providerNetwork2.networkInterface.ipAddress" path="providerNetwork2.networkInterface.ipAddress" cssErrorClass="error">
							<spring:message code="interface.ipAddress" />
						</form:label>
						<form:input path="providerNetwork2.networkInterface.ipAddress" size="18"  />
						<form:errors path="providerNetwork2.networkInterface.ipAddress" size="18" />
						<br>
						<form:label for="providerNetwork2.networkInterface.vlan" path="providerNetwork2.networkInterface.vlan" cssErrorClass="error">
							<spring:message code="interface.vlan" />
						</form:label>
						<form:input path="providerNetwork2.networkInterface.vlan" size="3"  />
						<form:errors path="providerNetwork2.networkInterface.vlan" size="3" />
					</div>		
				</div>										
			</div>
			
			<!-- Init VCPE -->
			
			<div id="vcpe_mp_logical">
				<h3>VCPE</h3>
				<div>
					<div id="iface_up_lrprovider1" class="ui-widget-content">	
						<label>Interface:&nbsp;</label>
						<c:forEach items="${logicalInfrastructure.providerLR1.interfaces}" varStatus="vs" var="item">				
							<c:choose>
								<c:when test="${item.type == 'Up'}">
									<div class="ui-widget-content config_content">
										<form:hidden path="providerLR1.interfaces[${vs.index}].templateName" />
										<form:hidden path="providerLR1.interfaces[${vs.index}].type" />
										<form:label for="providerLR1.interfaces[${vs.index}].name" path="providerLR1.interfaces[${vs.index}].name" cssErrorClass="error">
											<form:label for="providerLR1.interfaces[${vs.index}].port" path="providerLR1.interfaces[${vs.index}].port" cssErrorClass="error">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="providerLR1.interfaces[${vs.index}].name" size="8" />.
										<form:errors path="providerLR1.interfaces[${vs.index}].name" size="8" />
										<form:input path="providerLR1.interfaces[${vs.index}].port" size="3"    />
										<form:errors path="providerLR1.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="providerLR1.interfaces[${vs.index}].ipAddress" path="providerLR1.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="providerLR1.interfaces[${vs.index}].ipAddress" size="18"  />
										<form:errors path="providerLR1.interfaces[${vs.index}].ipAddress" size="18" />
										<br>
										<form:label for="providerLR1.interfaces[${vs.index}].vlan" path="providerLR1.interfaces[${vs.index}].vlan" cssErrorClass="error">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="providerLR1.interfaces[${vs.index}].vlan" size="3"  />
										<form:errors path="providerLR1.interfaces[${vs.index}].vlan" size="3" />
									</div>
								</c:when>
							</c:choose>				
						</c:forEach>									
					</div>
					<div id="iface_up_lrprovider2" class="ui-widget-content">	
						<label>Interface:&nbsp;</label>
						<c:forEach items="${logicalInfrastructure.providerLR2.interfaces}" varStatus="vs" var="item">				
							<c:choose>
								<c:when test="${item.type == 'Up'}">
									<div class="ui-widget-content config_content">
										<form:hidden path="providerLR2.interfaces[${vs.index}].templateName" />
										<form:hidden path="providerLR2.interfaces[${vs.index}].type" />
										<form:label for="providerLR2.interfaces[${vs.index}].name" path="providerLR2.interfaces[${vs.index}].name" cssErrorClass="error">
											<form:label for="providerLR2.interfaces[${vs.index}].port" path="providerLR2.interfaces[${vs.index}].port" cssErrorClass="error">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="providerLR2.interfaces[${vs.index}].name" size="8" />.
										<form:errors path="providerLR2.interfaces[${vs.index}].name" size="8" />
										<form:input path="providerLR2.interfaces[${vs.index}].port" size="3"    />
										<form:errors path="providerLR2.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="providerLR2.interfaces[${vs.index}].ipAddress" path="providerLR2.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="providerLR2.interfaces[${vs.index}].ipAddress" size="18"  />
										<form:errors path="providerLR2.interfaces[${vs.index}].ipAddress" size="18" />
										<br>
										<form:label for="providerLR2.interfaces[${vs.index}].vlan" path="providerLR2.interfaces[${vs.index}].vlan" cssErrorClass="error">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="providerLR2.interfaces[${vs.index}].vlan" size="3"  />
										<form:errors path="providerLR2.interfaces[${vs.index}].vlan" size="3" />
									</div>
								</c:when>
							</c:choose>				
						</c:forEach>									
					</div>
					
					<!--Logical Router Provider1-->
					<form:hidden path="providerLR1.name"/>
					<form:hidden path="providerLR1.templateName"/>
					<div id="lrprovider1">
						<h3><spring:message code="logicalrouter"/>: ${logicalInfrastructure.providerLR1.name}</h3>
						<div id="iface_lo_lrprovider1" class="ui-widget-content">	
							<label>Interface:&nbsp;</label>
							<c:forEach items="${logicalInfrastructure.providerLR1.interfaces}" varStatus="vs" var="item">				
								<c:choose>
									<c:when test="${item.type == 'Loopback'}">
										<div class="ui-widget-content config_content">
											<form:hidden path="providerLR1.interfaces[${vs.index}].templateName" />
											<form:hidden path="providerLR1.interfaces[${vs.index}].type" />
											<form:label for="providerLR1.interfaces[${vs.index}].name" path="providerLR1.interfaces[${vs.index}].name" cssErrorClass="error">
												<form:label for="providerLR1.interfaces[${vs.index}].port" path="providerLR1.interfaces[${vs.index}].port" cssErrorClass="error">
													<spring:message code="interface.name" />
												</form:label>
											</form:label>
											<form:input path="providerLR1.interfaces[${vs.index}].name" size="8" />.
											<form:errors path="providerLR1.interfaces[${vs.index}].name" size="8" />
											<form:input path="providerLR1.interfaces[${vs.index}].port" size="3"    />
											<form:errors path="providerLR1.interfaces[${vs.index}].port" size="3" />
											<br>
											<form:label for="providerLR1.interfaces[${vs.index}].ipAddress" path="providerLR1.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
												<spring:message code="interface.ipAddress" />
											</form:label>
											<form:input path="providerLR1.interfaces[${vs.index}].ipAddress" size="18"  />
											<form:errors path="providerLR1.interfaces[${vs.index}].ipAddress" size="18" />
										</div>
									</c:when>
								</c:choose>				
							</c:forEach>									
						</div>
					</div>
					
					<!--Logical Router Provider2-->
					<form:hidden path="providerLR2.name"/>
					<form:hidden path="providerLR2.templateName"/>
					<div id="lrprovider2">
						<h3><spring:message code="logicalrouter"/>: ${logicalInfrastructure.providerLR2.name}</h3>			
						<div id="iface_lrprovider2_lo" class="ui-widget-content">	
							<label>Interface:&nbsp;</label>
							<c:forEach items="${logicalInfrastructure.providerLR2.interfaces}" varStatus="vs" var="item">				
								<c:choose>
									<c:when test="${item.type == 'Loopback'}">
										<div class="ui-widget-content config_content">
											<form:hidden path="providerLR2.interfaces[${vs.index}].templateName" />
											<form:hidden path="providerLR2.interfaces[${vs.index}].type" />
											<form:label for="providerLR2.interfaces[${vs.index}].name" path="providerLR2.interfaces[${vs.index}].name" cssErrorClass="error">
												<form:label for="providerLR2.interfaces[${vs.index}].port" path="providerLR2.interfaces[${vs.index}].port" cssErrorClass="error">
													<spring:message code="interface.name" />
												</form:label>
											</form:label>
											<form:input path="providerLR2.interfaces[${vs.index}].name" size="8" />.
											<form:errors path="providerLR2.interfaces[${vs.index}].name" size="8" />
											<form:input path="providerLR2.interfaces[${vs.index}].port" size="3"    />
											<form:errors path="providerLR2.interfaces[${vs.index}].port" size="3" />
											<br>
											<form:label for="providerLR2.interfaces[${vs.index}].ipAddress" path="providerLR2.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
												<spring:message code="interface.ipAddress" />
											</form:label>
											<form:input path="providerLR2.interfaces[${vs.index}].ipAddress" size="18"  />
											<form:errors path="providerLR2.interfaces[${vs.index}].ipAddress" size="18" />
										</div>
									</c:when>
								</c:choose>				
							</c:forEach>									
						</div>
					</div>

					<div id="iface_lrprovider1_down" class="ui-widget-content">	
						<label>Interface:&nbsp;</label>
						<c:forEach items="${logicalInfrastructure.providerLR1.interfaces}" varStatus="vs" var="item">				
							<c:choose>
								<c:when test="${item.type == 'Down'}">
									<div class="ui-widget-content config_content">
										<form:hidden path="providerLR1.interfaces[${vs.index}].templateName" />
										<form:hidden path="providerLR1.interfaces[${vs.index}].type" />
										<form:label for="providerLR1.interfaces[${vs.index}].name" path="providerLR1.interfaces[${vs.index}].name" cssErrorClass="error">
											<form:label for="providerLR1.interfaces[${vs.index}].port" path="providerLR1.interfaces[${vs.index}].port" cssErrorClass="error">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="providerLR1.interfaces[${vs.index}].name" size="8" />.
										<form:errors path="providerLR1.interfaces[${vs.index}].name" size="8" />
										<form:input path="providerLR1.interfaces[${vs.index}].port" size="3"    />
										<form:errors path="providerLR1.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="providerLR1.interfaces[${vs.index}].ipAddress" path="providerLR1.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="providerLR1.interfaces[${vs.index}].ipAddress" size="18"  />
										<form:errors path="providerLR1.interfaces[${vs.index}].ipAddress" size="18" />
									</div>
								</c:when>
							</c:choose>				
						</c:forEach>									
					</div>

					<div id="iface_lrprovider2_down" class="ui-widget-content">	
						<label>Interface:&nbsp;</label>
						<c:forEach items="${logicalInfrastructure.providerLR1.interfaces}" varStatus="vs" var="item">				
							<c:choose>
								<c:when test="${item.type == 'Down'}">
									<div class="ui-widget-content config_content">
										<form:hidden path="providerLR2.interfaces[${vs.index}].templateName" />
										<form:hidden path="providerLR2.interfaces[${vs.index}].type" />
										<form:label for="providerLR2.interfaces[${vs.index}].name" path="providerLR2.interfaces[${vs.index}].name" cssErrorClass="error">
											<form:label for="providerLR2.interfaces[${vs.index}].port" path="providerLR2.interfaces[${vs.index}].port" cssErrorClass="error">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="providerLR2.interfaces[${vs.index}].name" size="8" />.
										<form:errors path="providerLR2.interfaces[${vs.index}].name" size="8" />
										<form:input path="providerLR2.interfaces[${vs.index}].port" size="3"    />
										<form:errors path="providerLR2.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="providerLR2.interfaces[${vs.index}].ipAddress" path="providerLR2.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="providerLR2.interfaces[${vs.index}].ipAddress" size="18"  />
										<form:errors path="providerLR2.interfaces[${vs.index}].ipAddress" size="18" />
									</div>
								</c:when>
							</c:choose>				
						</c:forEach>									
					</div>
	
					<div id="iface_client_up1" class="ui-widget-content">	
						<label>Interface:&nbsp;</label>
						<c:forEach items="${logicalInfrastructure.clientLR.interfaces}" varStatus="vs" var="item">				
							<c:choose>
								<c:when test="${item.templateName == LR_CLIENT_IFACE_UP1}">
									<div class="ui-widget-content config_content">
										<form:hidden path="clientLR.interfaces[${vs.index}].templateName" />
										<form:hidden path="clientLR.interfaces[${vs.index}].type" />
										<form:label for="clientLR.interfaces[${vs.index}].name" path="clientLR.interfaces[${vs.index}].name" cssErrorClass="error">
											<form:label for="clientLR.interfaces[${vs.index}].port" path="clientLR.interfaces[${vs.index}].port" cssErrorClass="error">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="clientLR.interfaces[${vs.index}].name" size="8" />.
										<form:errors path="clientLR.interfaces[${vs.index}].name" size="8" />
										<form:input path="clientLR.interfaces[${vs.index}].port" size="3"    />
										<form:errors path="clientLR.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="clientLR.interfaces[${vs.index}].ipAddress" path="clientLR.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="clientLR.interfaces[${vs.index}].ipAddress" size="18"  />
										<form:errors path="clientLR.interfaces[${vs.index}].ipAddress" size="18" />
									</div>
								</c:when>
							</c:choose>				
						</c:forEach>									
					</div>

					<div id="iface_client_up2" class="ui-widget-content">	
						<label>Interface:&nbsp;</label>
						<c:forEach items="${logicalInfrastructure.clientLR.interfaces}" varStatus="vs" var="item">				
							<c:choose>
								<c:when test="${item.templateName == LR_CLIENT_IFACE_UP2}">
									<div class="ui-widget-content config_content">
										<form:hidden path="clientLR.interfaces[${vs.index}].templateName" />
										<form:hidden path="clientLR.interfaces[${vs.index}].type" />
										<form:label for="clientLR.interfaces[${vs.index}].name" path="clientLR.interfaces[${vs.index}].name" cssErrorClass="error">
											<form:label for="clientLR.interfaces[${vs.index}].port" path="clientLR.interfaces[${vs.index}].port" cssErrorClass="error">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="clientLR.interfaces[${vs.index}].name" size="8" />.
										<form:errors path="clientLR.interfaces[${vs.index}].name" size="8" />
										<form:input path="clientLR.interfaces[${vs.index}].port" size="3"    />
										<form:errors path="clientLR.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="clientLR.interfaces[${vs.index}].ipAddress" path="clientLR.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="clientLR.interfaces[${vs.index}].ipAddress" size="18"  />
										<form:errors path="clientLR.interfaces[${vs.index}].ipAddress" size="18" />
									</div>
								</c:when>
							</c:choose>				
						</c:forEach>									
					</div>
					
					<!--Logical Router Client-->
					<form:hidden path="clientLR.name"/>
					<form:hidden path="clientLR.templateName"/>
					<div id="lrclient">
						<h3><spring:message code="logicalrouter"/>: ${logicalInfrastructure.clientLR.name}</h3>		
						<div id="iface_lrprovider2_lo" class="ui-widget-content">	
							<label>Interface:&nbsp;</label>
							<c:forEach items="${logicalInfrastructure.clientLR.interfaces}" varStatus="vs" var="item">				
								<c:choose>
									<c:when test="${item.type == 'Loopback'}">
										<div class="ui-widget-content config_content">
											<form:hidden path="clientLR.interfaces[${vs.index}].templateName" />
											<form:hidden path="clientLR.interfaces[${vs.index}].type" />
											<form:label for="clientLR.interfaces[${vs.index}].name" path="clientLR.interfaces[${vs.index}].name" cssErrorClass="error">
												<form:label for="clientLR.interfaces[${vs.index}].port" path="clientLR.interfaces[${vs.index}].port" cssErrorClass="error">
													<spring:message code="interface.name" />
												</form:label>
											</form:label>
											<form:input path="clientLR.interfaces[${vs.index}].name" size="8" />.
											<form:errors path="clientLR.interfaces[${vs.index}].name" size="8" />
											<form:input path="clientLR.interfaces[${vs.index}].port" size="3"    />
											<form:errors path="clientLR.interfaces[${vs.index}].port" size="3" />
											<br>
											<form:label for="clientLR.interfaces[${vs.index}].ipAddress" path="clientLR.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
												<spring:message code="interface.ipAddress" />
											</form:label>
											<form:input path="clientLR.interfaces[${vs.index}].ipAddress" size="18"  />
											<form:errors path="clientLR.interfaces[${vs.index}].ipAddress" size="18" />
										</div>
									</c:when>
								</c:choose>				
							</c:forEach>									
						</div>
					</div>
							
					<div id="iface_client_down" class="ui-widget-content">	
						<label>Interface:&nbsp;</label>
						<c:forEach items="${logicalInfrastructure.clientLR.interfaces}" varStatus="vs" var="item">				
							<c:choose>
								<c:when test="${item.type == 'Down'}">
									<div class="ui-widget-content config_content">
										<form:hidden path="clientLR.interfaces[${vs.index}].templateName" />
										<form:hidden path="clientLR.interfaces[${vs.index}].type" />
										<form:label for="clientLR.interfaces[${vs.index}].name" path="clientLR.interfaces[${vs.index}].name" cssErrorClass="error">
											<form:label for="clientLR.interfaces[${vs.index}].port" path="clientLR.interfaces[${vs.index}].port" cssErrorClass="error">
												<spring:message code="interface.name" />
											</form:label>
										</form:label>
										<form:input path="clientLR.interfaces[${vs.index}].name" size="8" />.
										<form:errors path="clientLR.interfaces[${vs.index}].name" size="8" />
										<form:input path="clientLR.interfaces[${vs.index}].port" size="3"    />
										<form:errors path="clientLR.interfaces[${vs.index}].port" size="3" />
										<br>
										<form:label for="clientLR.interfaces[${vs.index}].ipAddress" path="clientLR.interfaces[${vs.index}].ipAddress" cssErrorClass="error">
											<spring:message code="interface.ipAddress" />
										</form:label>
										<form:input path="clientLR.interfaces[${vs.index}].ipAddress" size="18"  />
										<form:errors path="clientLR.interfaces[${vs.index}].ipAddress" size="18" />
										<br>
										<form:label for="clientLR.interfaces[${vs.index}].vlan" path="clientLR.interfaces[${vs.index}].vlan" cssErrorClass="error">
											<spring:message code="interface.vlan" />
										</form:label>
										<form:input path="clientLR.interfaces[${vs.index}].vlan" size="3"  />
										<form:errors path="clientLR.interfaces[${vs.index}].vlan" size="3" />
									</div>
								</c:when>
							</c:choose>				
						</c:forEach>
						</div>								
				</div>	
			</div>
			<!-- End VCPE -->
			
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
		<c:if test='${action == "create"}'>
			<input id="submitButton" class="button" type="submit" value="<spring:message code="buttons.create"/>" />
		</c:if>
		<c:if test='${action == "update"}'>
			<input id="submitButton" class="button" type="submit" value="<spring:message code="buttons.update"/>" />
		</c:if>
	</form:form>
</sec:authorize>