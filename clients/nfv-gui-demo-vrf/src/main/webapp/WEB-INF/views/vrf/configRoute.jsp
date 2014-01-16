<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script src="<c:url value="/resources/js/topology/jquery.tipsy.js" />"></script>
<link rel="stylesheet" href="<c:url value="/resources/css/topology/tipsy.css" />" type="text/css">
	
<div id="config_routeTable" class="ui-widget-content ui-corner-all routTable padding">
    <h3>Configured routes</h3>
    <table id="jsonTable" class="tablesorter"></table>
    <script>
        var text = "<input style='margin-right: 11.5px' class='addRouteButton' onClick='removeAll()' type='button' value='Remove all table' name='Remove all table'/>";
        var jsonRoutes = ${json};
        if( jsonRoutes != 'OpenNaaS is not started' && 
            JSON.stringify(jsonRoutes.routeTable) !=  '[]'){
            
            $('#jsonTable').after(text);
        }
    </script>
</div>
<div id="config_topology" class="topology">
    <p onmousedown="cleanDrag()" id="chart" ></p>
</div>
<script src="<c:url value="/resources/js/topology/base.js" />"></script>
<script src="<c:url value="/resources/js/topology/configTopology.js" />"></script>

<script language="JavaScript" type="text/JavaScript">
    var jsonRoutes = ${json};
    var type = getURLParameter("type");
    var jsonHtmlTable = ConvertJsonToRouteTable(jsonRoutes, 'jsonTable', null, 'Download');
    document.getElementById("jsonTable").innerHTML = jsonHtmlTable;
</script>

<div class="modal"></div>