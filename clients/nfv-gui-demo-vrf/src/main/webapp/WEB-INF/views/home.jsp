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
<!--        <li id="Status"><b>Status:</b></li>-->
    </ul>
    <h3>Flow table:</h3>
    <table id="jsonFlowTable" class="tablesorter"></table>
    <div id="preloader" style="display:none;"><img src="<c:url value="/resources/images/ajax-loader.gif" />" /></div>
</div>
<div id="home_topology" class="topology">
    <p id="chart" ></p>
</div>
<script src="<c:url value="/resources/js/topology/base.js" />"></script>
<script src="<c:url value="/resources/js/topology/homeTopology.js" />"></script>

<script language="JavaScript" type="text/JavaScript">
    function updateSwInfoTxt(dpid, controller){
        document.getElementById("IP").innerHTML ='<b>Controller IP: </b>';
        document.getElementById("Port").innerHTML ='<b>Controller Port: </b>';
//        document.getElementById("Status").innerHTML ='<b>Status: </b>';
        $.ajax({
		type: "GET",
		url: "getInfoSw/"+dpid,
		success: function(data) {
			$('#ajaxUpdate').html(data);    
                        var json = convertXml2JSon(data);
                        console.log(data);
console.log(json);                        
                        var jsonHtmlTable = ConvertJsonToFlowTable(json, 'jsonFlowTable', null, 'Go to');
//                        var status = getStatusCtrl(dpid);
console.log(jsonHtmlTable);
                        document.getElementById("jsonFlowTable").innerHTML = jsonHtmlTable;
		}
	});
        document.getElementById("DPID").innerHTML ='<b>DPID: </b>'+dpid;
        document.getElementById("IP").innerHTML ='<b>Controller IP: </b>'+controller.split(":")[0];
        document.getElementById("Port").innerHTML ='<b>Controller Port: </b>'+controller.split(":")[1];
//        document.getElementById("Status").innerHTML ='<b>Status: </b>'+status;
    }
</script>
<!--showHidePreloader(false); remove this two lines-->
<div class="modal"></div>