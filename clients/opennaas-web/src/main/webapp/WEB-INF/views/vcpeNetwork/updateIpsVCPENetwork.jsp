<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="span-15 last">
	<form:form modelAttribute="VCPENetwork" action="updateIps" method="post">
		<fieldset>
			<legend>
				<spring:message code="vcpenetwork.updateIps.legend" />
			</legend>

			<form:hidden path="id" />
			<form:hidden path="name" />
			<form:hidden path="template" />
			
			<div class="demo">
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1"><spring:message code="vcpenetwork.logicalrouter1" /></a></li>
						<li><a href="#tabs-2"><spring:message code="vcpenetwork.logicalrouter2" /></a></li>
					</ul>

					<div id="tabs-1">
						<div>
							<c:forEach items="${VCPENetwork.logicalRouter1.interfaces}" varStatus="vs" var="item">
								<div class="field">
									<label>${item.labelName} Interface</label>
									<div class="input">
										<spring:message code="logicalrouter.physicalinterface" />
										<spring:message code="logicalrouter.ipAddress" />
										<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="13"/>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
					<div id="tabs-2">
						<div>
							<c:forEach items="${VCPENetwork.logicalRouter2.interfaces}" varStatus="vs" var="item">
								<div class="field">
									<label>${item.labelName} Interface</label>
									<div class="input">
										<spring:message code="logicalrouter.physicalinterface" />
										<spring:message code="logicalrouter.ipAddress" />
										<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="13"/>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>		
			</div>
			<div style="margin-top: 15px">
				<hr/>
				<input type="submit" value="<spring:message code="buttons.update"/>" />
			</div>
		</fieldset>
	</form:form>
</div>