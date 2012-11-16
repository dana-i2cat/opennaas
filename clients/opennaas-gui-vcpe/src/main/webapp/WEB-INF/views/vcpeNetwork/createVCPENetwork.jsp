<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript">
<!--
	function setLRNames () {
		var networName = document.getElementById('name').value;
		var lr1 = document.getElementById('logicalRouter1.name');
		var lr2 = document.getElementById('logicalRouter2.name');
		lr1.value = lr1.value + '-' + networName;
		lr2.value = lr2.value + '-' + networName;
	}
//-->
</script>

<div class="span-15 last">
		
	<form:form modelAttribute="VCPENetwork" action="${action}" method="post">
		<fieldset>
			<legend>
				<spring:message code="vcpenetwork.create.legend" />
			</legend>

			<form:hidden path="id" />

			<div class="demo">
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1"><spring:message code="vcpenetwork.basicfields" /></a></li>
						<li><a href="#tabs-2"><spring:message code="vcpenetwork.logicalrouter1" /></a></li>
						<li><a href="#tabs-3"><spring:message code="vcpenetwork.logicalrouter2" /></a></li>
					</ul>
					<div id="tabs-1">
						<div style="margin-bottom: 50px">
							<div style="float: left;">
								<form:label for="name" path="name" cssErrorClass="error">
									<spring:message code="vcpenetwork.name" />
								</form:label>
								<br />
								<form:input path="name" onchange="setLRNames();"/>
								<form:errors path="name" />
							</div>
							
							<div style="float: left; margin-left: 10px;">
								<form:label for="clientIpRange" path="clientIpRange" cssErrorClass="error">
									<spring:message code="vcpenetwork.clientIpRange" />
								</form:label>
								<br />
								<form:input path="clientIpRange" size="12" />
								<form:errors path="clientIpRange" />
							</div>

							<div style="float: left; margin-left: 10px;" >
								<form:label for="template" path="template" cssErrorClass="error">
									<spring:message code="vcpenetwork.template" />
								</form:label>
								<br />
								<form:select path="template">
										<form:option value="basic.template">Basic Template </form:option>
								</form:select>
								<form:errors path="template" />
							</div>
						</div>
					</div>
					<div id="tabs-2">
						<div>
							<form:hidden path="logicalRouter1.name" />
							<form:hidden path="logicalRouter1.templateName"/>
							<c:forEach items="${VCPENetwork.logicalRouter1.interfaces}" varStatus="vs" var="item">
								<div class="field">
									<label>${item.labelName} Interface</label>
									<div class="input">
										<c:choose>		
											<c:when test="${item.labelName != 'Up'}">
												<spring:message code="interface.name" />
												<form:hidden path="logicalRouter1.interfaces[${vs.index}].templateName"/>
												<form:input path="logicalRouter1.interfaces[${vs.index}].name" size="8" 
													onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter1.interfaces${vs.index}.port').value);" />
												<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8"/>.
												<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="3" 
													onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter1.interfaces${vs.index}.name').value, this.value);" />
												<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="3" />
												<spring:message code="interface.ipAddress" />
												<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
												<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13"/>
												<spring:message code="interface.vlan" />
												<form:input path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this.value);" />
												<form:errors path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" />
											</c:when>
											<c:otherwise>
												<spring:message code="interface.name" />
												<form:hidden path="logicalRouter1.interfaces[${vs.index}].templateName"/>
												<form:input path="logicalRouter1.interfaces[${vs.index}].name" size="8" readonly="true" />
												<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8"/>.
												<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="3" readonly="true" />
												<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="3" />
												<spring:message code="interface.ipAddress" />
												<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" readonly="true" />
												<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13"/>
												<spring:message code="interface.vlan" />
												<form:input path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" readonly="true" />
												<form:errors path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" />
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
					<div id="tabs-3">
						<div>
							<form:hidden path="logicalRouter2.name"/>
							<form:hidden path="logicalRouter2.templateName"/>
							<c:forEach items="${VCPENetwork.logicalRouter2.interfaces}" varStatus="vs" var="item">
								<div class="field">
									<label>${item.labelName} Interface</label>
									<div class="input">
										<c:choose>		
											<c:when test="${item.labelName != 'Up'}">
												<spring:message code="interface.name" />
												<form:hidden path="logicalRouter2.interfaces[${vs.index}].templateName"/>
												<form:input path="logicalRouter2.interfaces[${vs.index}].name" size="8" 
													onchange="isInterfaceFree('${VCPENetwork.id}', this.value, document.getElementById('logicalRouter2.interfaces${vs.index}.port').value);" />
												<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8"/>.
												<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="3" 
													onchange="isInterfaceFree('${VCPENetwork.id}', document.getElementById('logicalRouter2.interfaces${vs.index}.name').value, this.value);" />
												<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="3" />
												<spring:message code="interface.ipAddress" />
												<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" onchange="isIPFree('${VCPENetwork.id}', this.value);" />
												<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13"/>
												<spring:message code="interface.vlan" />
												<form:input path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" onchange="isVLANFree('${VCPENetwork.id}', this.value);" />
												<form:errors path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" />
											</c:when>
											<c:otherwise>
												<spring:message code="interface.name" />
												<form:hidden path="logicalRouter2.interfaces[${vs.index}].templateName"/>
												<form:input path="logicalRouter2.interfaces[${vs.index}].name" size="8" readonly="true" />
												<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8" />.
												<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="3" readonly="true" />
												<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="3" />
												<spring:message code="interface.ipAddress" />
												<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" readonly="true" />
												<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13"/>
												<spring:message code="interface.vlan" />
												<form:input path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" readonly="true" />
												<form:errors path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" />
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>		
			</div>
			<div style="margin-top: 15px">
				<hr/>
				<input type="submit" value="<spring:message code="buttons.create"/>" />
			</div>
		</fieldset>
	</form:form>
</div>