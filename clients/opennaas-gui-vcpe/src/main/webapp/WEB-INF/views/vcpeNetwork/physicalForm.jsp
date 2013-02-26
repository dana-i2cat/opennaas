<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:choose>
	<c:when test="${physicalInfrastructure.templateType == 'sp_vcpe'}">
		<c:set var="subpage" value="/WEB-INF/views/vcpeNetwork/sp/physicalForm.jsp" />	
	</c:when>
	<c:otherwise>
		<c:set var="subpage" value="/WEB-INF/views/vcpeNetwork/mp/physicalForm.jsp" />				
	</c:otherwise>
</c:choose>

<c:import url="${subpage}" />