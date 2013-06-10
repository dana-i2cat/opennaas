<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ page import="org.opennaas.extensions.vcpe.manager.templates.mp.TemplateConstants;"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authorize access="hasRole('ROLE_USER')">
	<c:set var="subpage" value="/WEB-INF/views/vcpeNetwork/mp/logicalFormUser.jsp" />	
</sec:authorize>
<sec:authorize access="hasRole('ROLE_NOC')">
	<c:set var="subpage" value="/WEB-INF/views/vcpeNetwork/mp/logicalFormNoc.jsp" />	
</sec:authorize>
<sec:authorize access="hasRole('ROLE_ADMIN')">
	<c:set var="subpage" value="/WEB-INF/views/vcpeNetwork/mp/logicalFormAdmin.jsp" />	
</sec:authorize>

<c:import url="${subpage}" />