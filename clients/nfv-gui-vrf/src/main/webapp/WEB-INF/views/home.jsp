<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script language="JavaScript">
        function Validate(){
            var owlFile =document.getElementById("owlFile").value;
            if(owlFile != ''){
                var checkimg = owlFile.toLowerCase();
                if (!checkimg.match(/(\.json)$/)){
                    $( "#dialogUploadVI" ).dialog("open");
                    //document.getElementById("owlFile").focus();
                    return false;
                }
            }
            return true;
        }
    </script>	

    <c:if test="${!empty topologyName}">
        
        <div id="home_info" class="ui-widget-content ui-corner-all padding">
    <h3>Switch Information:</h3>
    <ul>
        <li id="DPID"><b>DPID:</b></li>
        <li id="IP"><b>Controller IP:</b></li>
        <li id="Port"><b>Controller Port:</b></li>
    </ul>
    <h3>Flow table:</h3>
    <table id="jsonFlowTable" class="tablesorter"></table>
    <div id="preloader" style="display:none;"><img src="<c:url value="/resources/images/ajax-loader.gif" />" /></div>
</div>
       <div id="home_topology" class="topology ui-widget-content ui-corner-all">
        <p id="chart" ></p>
         </div>
    </c:if>
    <c:if test="${empty topologyName}">
        <div id="home_topology" class="topology ui-widget-content ui-corner-all" style="width: 100%;">
            <center><h3 style="color:red"><spring:message code="topology.notdefined"/></h3></center>
            <hr>
            <ul>
                <li>
                    <div id="otherTop2">
                        <h3>Load topology from OpenNaaS Generic network</h3>
                        <a href="/nfv-gui-vrf/secure/nfvRouting/home/opennaasTopology">Load (in the case that exists)</a>
                    </div>
                </li>
                <br/>
                <li>
                    <div id="create">
                        <h3>Upload a topology file</h3>
                        <form:form modelAttribute="uploadedFile" name="frm" method="post" enctype="multipart/form-data" onSubmit="return Validate();">
                            <fieldset>
                                <legend>Topology form</legend>
                                <p><form:label for="fileName" path="fileName">Topology Name</form:label><br/>
                                <form:input path="fileName" type="text" name="fileName" id="fileName" class="ipInput text ui-widget-content ui-corner-all" value=""/></p>
                                <p><form:label for="file" path="file">Topology File</form:label><br/>
                                <form:input class="submitButton" path="file" id="owlFile" type="file"/></p>
                                <input id="submitVI" class="submitButton" type="submit" value="<spring:message code="buttons.upload"/>" />
                            </fieldset>
                        </form:form>
                    </div>
                </li>
                <li>
                    <div id="otherTop">
                        <h3>Other used topologies</h3>
                        <a href="/nfv-gui-vrf/secure/nfvRouting/home/demoTopo">Demo Topology (demo-topology.json)</a>
                    </div>
                </li>
            </ul>
        </div>
    </c:if>
<script src="<c:url value="/resources/js/topology/base.js" />"></script>
<script src="<c:url value="/resources/js/topology/homeTopology.js" />"></script>

<script language="JavaScript" type="text/JavaScript">
    var switchContent = '<h3>Switch Information:</h3><ul>\
        <li id="DPID"><b>DPID:</b></li>\
        <li id="IP"><b>Controller IP:</b></li>\
        <li id="Port"><b>Controller Port:</b></li>\
<!--        <li id="Status"><b>Status:</b></li>-->\
    </ul><h3>Flow table:</h3>\
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
            url: "switchInfo/"+dpid,
            success: function(data) {
                $('#ajaxUpdate').html(data);    
                var json = convertXml2JSon(data);
                data = "";                        

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