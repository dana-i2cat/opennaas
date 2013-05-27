<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ page import="org.opennaas.extensions.vcpe.manager.templates.sp.SPTemplateConstants;"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<c:choose>
	<c:when test="${logicalInfrastructure.templateType == 'sp_vcpe'}">
		<c:set var="subpage" value="/WEB-INF/views/vcpeNetwork/sp/logicalForm.jsp" />	
	</c:when>
	<c:otherwise>
		<c:set var="subpage" value="/WEB-INF/views/vcpeNetwork/mp/logicalForm.jsp" />				
	</c:otherwise>
</c:choose>

<c:import url="${subpage}" />