<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div id="config_routeTable" class="ui-widget-content ui-corner-all routTable padding">
    <h3>Configured routes</h3>
    <div class="outer">
	<div class="innera">
            <table id="jsonTable" class="tablesorter"></table>
        </div>
    </div>
    <script>
        var text = "<input style='margin-right: 11.5px' class='addRouteButton' onClick='removeAll()' type='button' value='Clean table' name='Clean table'/>";
        var jsonRoutes = ${json};
        if( JSON.stringify(jsonRoutes) !== 'OpenNaaS is not started' && JSON.stringify(jsonRoutes.routeTable) !==  '[]'){
            $('.outer').after(text);
        }
    </script>
</div>
<div id="config_topology" class="topology">
    <p onmousedown="cleanDrag()" id="chart" ></p>
</div>
<script src="<c:url value="/resources/js/topology/base.js" />"></script>
<script src="<c:url value="/resources/js/topology/configTopology.js" />"></script>

<script language="JavaScript" type="text/JavaScript">
    var type = getURLParameter("type");
    var jsonHtmlTable = ConvertJsonToRouteTable(${json}, 'jsonTable', null, 'Download');
    document.getElementById("jsonTable").innerHTML = jsonHtmlTable;
</script>

<div class="modal"></div>