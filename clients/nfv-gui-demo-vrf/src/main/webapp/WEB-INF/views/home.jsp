<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<script language="JavaScript" type="text/JavaScript">
//    objectArray = ${json};
//    var jsonHtmlTable = ConvertJsonToFlowTable(objectArray, 'jsonTable', null, 'Go to');
//    document.write(jsonHtmlTable);
</script>

<div id="home_info" class="ui-widget-content ui-corner-all padding">
    <h3>Switch Information:</h3>
    <ul>
        <li id="DPID"><b>DPID:</b></li>
        <li id="IP"><b>Controller IP:</b></li>
        <li id="Port"><b>Controller Port:</b></li>
        <li id="Status"><b>Status:</b></li>
    </ul>
    <h3>Flow table:</h3>
    <table id="jsonFlowTable" class="tablesorter"></table>
</div>
<div id="home_topology" class="topology">Topo image</div>

<%@include file="noTemplate.jsp"%>
<script language="JavaScript" type="text/JavaScript">
    function updateSwInfoTxt(){
    console.log("click");
    var dpid = "00:00:00:00:00:00:00:01";
        $.ajax({
		type: "GET",
		url: "getInfoSw/"+dpid,
		success: function(data) {
			$('#ajaxUpdate').html(data);    
                        console.log(data);
		}
	});
        
        document.getElementById("DPID").innerHTML ='<b>DPID: </b>'+dpid;
        document.getElementById("IP").innerHTML ='<b>Controller IP: </b>dasdsa';
        document.getElementById("Port").innerHTML ='<b>Controller Port: </b>'+dpid;
        document.getElementById("Status").innerHTML ='<b>Status: </b>'+dpid;
    }
</script>
<a href="#" onClick="javascript:updateSwInfoTxt()">change</a>

