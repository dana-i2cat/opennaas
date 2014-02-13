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
    <div id="listRoutes"></div>
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
<div id="config_topology" class="topology ui-widget-content ui-corner-all">
    <c:if test="${!empty topologyName}">
        <p onmousedown="cleanDrag()" id="chart" ></p>
    </c:if>
    <c:if test="${empty topologyName}">
        <br/><h3>No topology file loaded</h3><br/>
    </c:if>
</div>
<script src="<c:url value="/resources/js/topology/base.js" />"></script>
<script src="<c:url value="/resources/js/topology/configTopology.js" />"></script>

<script language="JavaScript" type="text/JavaScript">
    
    var type = getURLParameter("type");
console.log(${json});
    var listRoutes = getRouteList(${json});
    //getRoute
    var initial = listRoutes[0];
console.log(listRoutes);
console.log(listRoutes[0]);
    for ( var i = 0; i < listRoutes.length; i++){
        initial = listRoutes[i];
        for ( var j = i; j < listRoutes.length; j++){
            if( listRoutes[i] != listRoutes[j] ){
                document.getElementById("listRoutes").innerHTML += '<a href="javascript:void(0)" onclick="getSpecificRoute(\''+listRoutes[i].node+'\',\''+listRoutes[j].node+'\')">Route: '+listRoutes[i].id+'</a><br/>';
                initial = listRoutes[i+1];
            }
        }
    }
//    var jsonHtmlTable = ConvertJsonToRouteTable(${json}, 'jsonTable');
//    document.getElementById("jsonTable").innerHTML = jsonHtmlTable;
</script>

<div class="modal"></div>

<script>
    if ( $(window).height() > 450 ){
        $(".innera").height($(window).height() - 350);
    }
</script>