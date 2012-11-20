<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<form:form modelAttribute="VCPENetwork" action="${action}" method="post">
	<form:hidden path="id" />
	<form:hidden path="logicalRouter1.name" />
	<form:hidden path="logicalRouter1.templateName" />
	<form:hidden path="logicalRouter2.name" />
	<form:hidden path="logicalRouter2.templateName" />
	<!-- Graphical view -->
	<!-- Start vCPE -->
	<div id="vcpe">
		<h3><spring:message code="vcpenetwork.create.vcpe"/> ${VCPENetwork.name}</h3>
		<div id="acc_body">
			<!-- BGP -->
			<div id="bgp">
				<h2><spring:message code="vcpenetwork.bgp"/></h2>
				<div>
					<div id="config" class="ui-widget-content">
						<p>Global configuration parameters</p>
					</div>
					<button id="button" class="button"><spring:message code="buttons.deactivate"/></button>
					<br>
					<button id="button2" class="button"><spring:message code="buttons.reapply"/></button>
				</div>
			</div>
			<!-- VRRP -->
			<div id="vrrp">
				<h2><spring:message code="vcpenetwork.vrrp"/></h2>
				<div>
					<div id="config" class="ui-widget-content">
						<p>Global configuration parameters</p>
					</div>
					<button id="button3" class="button"><spring:message code="buttons.deactivate"/></button>
					<br>
					<button id="button4" class="button"><spring:message code="buttons.reapply"/></button>
				</div>
			</div>
			<!-- Up Interfaces -->
			<div id="up_interfaces">
				<div id="up_master" class="ui-widget-content">
					<c:forEach items="${VCPENetwork.logicalRouter1.interfaces}" varStatus="vs" var="item">
						<div class="input">
							<c:choose>
								<c:when test="${item.labelName == 'Up'}">
									<label>${item.labelName} Master</label>
									<br><spring:message code="interface.name" />
									<form:hidden path="logicalRouter1.interfaces[${vs.index}].templateName" />
									<form:input path="logicalRouter1.interfaces[${vs.index}].name" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter1.interfaces${vs.index}.port').value);" />.
									<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8" />
									<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter1.interfaces${vs.index}.name').value, this.value);" />
									<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="3" />
									<br><spring:message code="interface.ipAddress" />
									<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
									<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" />
									<br><spring:message code="interface.vlan" />
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
									<label>${item.labelName} Backup</label>
									<br><spring:message code="interface.name" />
									<form:hidden path="logicalRouter2.interfaces[${vs.index}].templateName" />
									<form:input path="logicalRouter2.interfaces[${vs.index}].name" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter2.interfaces${vs.index}.port').value);" />.
									<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8" />
									<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter2.interfaces${vs.index}.name').value, this.value);" />
									<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="3" />
									<br><spring:message code="interface.ipAddress" />
									<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
									<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" />
									<br><spring:message code="interface.vlan" />
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
				<h2><spring:message code="vcpenetwork.lrmaster"/></h2>
					<div>
						<div id="config" class="ui-widget-content">
							<p>Global configuration parameters</p>
						</div>
					<button id="button5" class="button"><spring:message code="buttons.activate"/></button>
					</div>
				</div>
				<div id="lr_backup">
				<h2><spring:message code="vcpenetwork.lrbackup"/></h2>
					<div>
						<div id="config" class="ui-widget-content">
							<p>Global configuration parameters</p>
						</div>
					<button id="button6" class="button"><spring:message code="buttons.backup"/></button>
				</div>
			</div>
			<!-- VCPE Interfaces -->
			<div id="vcpe_interfaces">
				<div id="customer_master" class="ui-widget-content">				
					<c:forEach items="${VCPENetwork.logicalRouter1.interfaces}"
						varStatus="vs" var="item">
						<div class="input">
							<c:choose>
								<c:when test="${item.labelName == 'Down'}">
									<label>${item.labelName} Master</label>
									<br><spring:message code="interface.name" />
								<form:hidden path="logicalRouter1.interfaces[${vs.index}].templateName" />
								<form:input path="logicalRouter1.interfaces[${vs.index}].name" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter1.interfaces${vs.index}.port').value);" />.
								<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8" />
								<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter1.interfaces${vs.index}.name').value, this.value);" />
								<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="3" />
									<br><spring:message code="interface.ipAddress" />
								<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
								<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" />
									<br><spring:message code="interface.vlan" />
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
									<label>${item.labelName} Master</label>
									<br><spring:message code="interface.name" />
								<form:hidden path="logicalRouter1.interfaces[${vs.index}].templateName" />
								<form:input path="logicalRouter1.interfaces[${vs.index}].name" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter1.interfaces${vs.index}.port').value);" />.
								<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8" />
								<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter1.interfaces${vs.index}.name').value, this.value);" />
								<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="3" />
									<br><spring:message code="interface.ipAddress" />
								<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
								<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" />
									<br><spring:message code="interface.vlan" />
								<form:input path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this.value);" />
								<form:errors path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" />
								</c:when>
							</c:choose>
						</div>
					</c:forEach>
				</div>
				<div id="inter_backup" class="ui-widget-content">
					<c:forEach items="${VCPENetwork.logicalRouter2.interfaces}"
						varStatus="vs" var="item">
						<div class="input">
							<c:choose>
								<c:when test="${item.labelName == 'Inter'}">
									<label>${item.labelName} Backup</label>
									<br><spring:message code="interface.name" />
								<form:hidden path="logicalRouter2.interfaces[${vs.index}].templateName" />
								<form:input path="logicalRouter2.interfaces[${vs.index}].name" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter2.interfaces${vs.index}.port').value);" />.
								<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8" />
								<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter2.interfaces${vs.index}.name').value, this.value);" />
								<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="3" />
									<br><spring:message code="interface.ipAddress" />
								<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
								<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" />
									<br><spring:message code="interface.vlan" />
								<form:input path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this.value);" />
								<form:errors path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" />
								</c:when>
							</c:choose>
						</div>
					</c:forEach>
				</div>
				<div id="customer_backup" class="ui-widget-content">
					<c:forEach items="${VCPENetwork.logicalRouter2.interfaces}"
						varStatus="vs" var="item">
						<div class="input">
							<c:choose>
								<c:when test="${item.labelName == 'Down'}">
									<label>${item.labelName} Backup</label>
									<br><spring:message code="interface.name" />
								<form:hidden path="logicalRouter2.interfaces[${vs.index}].templateName" />
								<form:input path="logicalRouter2.interfaces[${vs.index}].name" size="8" onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter2.interfaces${vs.index}.port').value);" />.
								<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8" />
								<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="3" onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter2.interfaces${vs.index}.name').value, this.value);" />
								<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="3" />
									<br><spring:message code="interface.ipAddress" />
								<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
								<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" />
									<br><spring:message code="interface.vlan" />
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
		<h3><spring:message code="vcpenetwork.bod"/></h3>
		<div>
			<div id="bod_master">
				<h2><spring:message code="vcpenetwork.bod.master"/></h2>
				<div>
					<div id="config" class="ui-widget-content">
						<p>Global configuration parameters</p>
					</div>
					<button id="button7" class="button"><spring:message code="buttons.cancel"/></button>
					<button id="button8" class="button"><spring:message code="buttons.renew"/></button>
				</div>
			</div>
			<div id="bod_inter">
				<h2>INTER</h2>
				<div>
					<div id="config" class="ui-widget-content">
						<p>Global configuration parameters</p>
					</div>
					<button id="button9" class="button">Cancel</button>
					<button id="button10" class="button">Renew</button>
				</div>
			</div>
			<div id="bod_backup">
				<h2>BACKUP</h2>
				<div>
					<div id="config" class="ui-widget-content">
						<p>Global configuration parameters</p>
					</div>
					<button id="button11" class="button">Cancel</button>
					<button id="button12" class="button">Renew</button>
				</div>
			</div>
		</div>
	</div>
	<!-- End BoD  -->
	<!-- Start Customer interfaces -->
	<div id="customer_interfaces">
		<div id="customer_down_master" class="ui-widget-content">
			<p>Customer master</p>
		</div>
		<div id="customer_down_backup" class="ui-widget-content">
			<p>Customer backup</p>
		</div>
	</div>
	<!-- End Customer interfaces -->
	<!-- Start customer -->
	<div id="customer">
		<h3>Customer</h3>
		<div id="customer_data" class="ui-widget-content">
			<div id="customer_network">
				<form:label for="name" path="name" cssErrorClass="error">
					<spring:message code="vcpenetwork.name" />
				</form:label>
				<br />
				<form:input path="name" />
				<form:errors path="name" />
			</div>

			<div id="customer_ip">
				<form:label for="clientIpRange" path="clientIpRange" cssErrorClass="error">
					<spring:message code="vcpenetwork.clientIpRange" />
				</form:label>
				<br />
				<form:input path="clientIpRange" size="12" />
				<form:errors path="clientIpRange" />
			</div>

			<div id="customer_template">
				<form:label for="template" path="template" cssErrorClass="error">
					<spring:message code="vcpenetwork.template" />
				</form:label>
				<br />
				<form:select path="template">
					<form:option value="basic.template">Basic Template </form:option>
				</form:select>
				<form:errors path="template" />
			</div>
			<input id="submitButton" class="button" type="submit" value="<spring:message code="buttons.create"/>" />
		</div>
	</div>
	<!-- End customer -->
	
</form:form>