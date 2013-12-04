<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<ol id="selectable">
	No routing resource started!
</ol>
<br>
<div id="home_info">
    Switch Information:
    <ul>
        <li>DPID: </li>
        <li>Controller IP: </li>
        <li>Controller Port: </li>
    </ul>
    Flow table:
    <table id="jsonFlowTable" class="tablesorter"></table>
</div>
<div id="home_topology" class="topology"></div>

<%@include file="noTemplate.jsp"%>

