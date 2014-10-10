<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

    
        <div id="home_info" class="ui-widget-content ui-corner-all padding">
    <h3>Switch Information:</h3>
    <ul>
        <li id="DPID"><b>DPID:</b></li>
        <li id="IP"><b>Controller IP:</b></li>
        <li id="Port"><b>Controller Port:</b></li>
    </ul>
    <h3>Allocated table:</h3>
    <table id="jsonFlowTable" class="tablesorter"></table>
    <div id="preloader" style="display:none;"><img src="<c:url value="/resources/images/ajax-loader.gif" />" /></div>
</div>
       <div id="home_topology" class="topology ui-widget-content ui-corner-all">
        <p id="chart" ></p>
         </div>

<script src="<c:url value="/resources/js/topology/base.js" />"></script>
<script src="<c:url value="/resources/js/topology/showFlows.js" />"></script>

<script language="JavaScript" type="text/JavaScript">
    
    var switchContent = '<h3>Switch Information:</h3><ul>\
        <li id="SwitchName"><b>Switch Name</b></li>\
        <li id="IP"><b>Controller IP:</b></li>\
        <li id="Port"><b>Controller Port:</b></li>\
<!--        <li id="Status"><b>Status:</b></li>-->\
    </ul><h3>Allocated table:</h3>\
    <table id="jsonFlowTable" class="tablesorter"></table>\
    <div id="preloader" style="display:none;"><img src="<c:url value="/resources/images/ajax-loader.gif" />" /></div>';
    
    var ctrlContent = "<h3>Connected switchs:</h3>";
    
    function switchSelected(dpid, controller){
        document.getElementById("home_info").innerHTML = switchContent;
        updateSwInfoTxt(dpid, controller);
    }
    
    function updateSwInfoTxt(dpid, controller){
        console.log("Update");
        document.getElementById("IP").innerHTML ='<b>Controller IP: </b>';
        document.getElementById("Port").innerHTML ='<b>Controller Port: </b>';
//        document.getElementById("Status").innerHTML ='<b>Status: </b>';

        removeFlowAll();
        $.ajax({
            type: "GET",
            url: "getAllocatedFlows/"+dpid,
            success: function(data) {
                $('#ajaxUpdate').html(data);    
                var json = convertXml2JSon(data);
                data = "";                        
                json = eval("("+json+")");
                var jsonHtmlTable = ConvertJsonToFlowTable(json, 'jsonFlowTable', null, 'Go to');
//                        var status = getStatusCtrl(dpid);
console.log(jsonHtmlTable);
                document.getElementById("jsonFlowTable").innerHTML = jsonHtmlTable;
            }
	});
        document.getElementById("SwitchName").innerHTML ='<b>Switch Name </b>'+dpid;
        document.getElementById("IP").innerHTML ='<b>Controller IP: </b>'+controller.split(":")[0];
        document.getElementById("Port").innerHTML ='<b>Controller Port: </b>'+controller.split(":")[1];
//        document.getElementById("Status").innerHTML ='<b>Status: </b>'+status;
    }
    function updateCtrlInfo(listSw, controllers){
        document.getElementById("home_info").innerHTML = ctrlContent;
        listSw.forEach(function(entry){
            var controllerURL = controllers.filter(function(d){return d.id == entry.controller})[0].controller;
            document.getElementById("home_info").innerHTML += '<a href="javascript:void(0)" onclick="updateSwInfoTxt(\''+entry.dpid+'\',\''+controllerURL+'\')">Switch id: '+entry.id+'</a><br/>';
        });
        document.getElementById("home_info").innerHTML += "<br/>"+switchContent;
    }
</script>
<!--showHidePreloader(false); remove this two lines-->
<div class="modal"></div>