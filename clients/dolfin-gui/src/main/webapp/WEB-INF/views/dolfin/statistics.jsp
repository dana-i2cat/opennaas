<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div id="resources_list">
    Show list of switches with a accordion menu, when click a switch the list of ports is expanded. Using JQuery UI
    <div id="accordion">
        <c:forEach items="${listSwitches}" var="item">
            <h3>${item.dpid}</h3>
            <div>
            <c:forEach items="${item.ports}" var="port">
                <p>${port}</p>
            </c:forEach>
            </div>
        </c:forEach>
    </div>
</div>
<div id="statistics">
    Show table of statistics. Is updated using AJAX each X seconds and change stadistics when a port of switch is clicked.
    <table id="jsonStatisticTable" class="tablesorter"></table>
</div>

<script language="JavaScript" type="text/JavaScript">
//    console.log(${xml});
/*    var test = getAllCircuits();
    var jsonObject = test;
    console.log(jsonObject);
    if(jsonObject.circuits.circuit.length == 0 || jsonObject.circuits == null){
            document.getElementById("innerTable").innerHTML = '<table id="jsonTable" class="tablesorter"></table>';
            document.getElementById("innerTable2").innerHTML = '<table id="jsonQoS" class="tablesorter"></table>';
        }
    for ( var i = 0; i < jsonObject.circuits.circuit.length; i++){
        document.getElementById("listRoutes").innerHTML += '<a style="text-decoration:none" href="javascript:void(0)" onclick="getSpecificCircuit(\''+jsonObject.circuits.circuit[i].circuitId+'\', 0)"><span id="innerTextRoute">Circuit: '+jsonObject.circuits.circuit[i].circuitId+'.</span></a><br/>';
    }
*/    
</script>

<div class="modal"></div>
<script>
    if ( $(window).height() > 450 ){
        $(".innera").height(($(window).height() - 400)/2);
    }
    
    setInterval(function(){
        if(selectedCircuit !== null){
            console.log("Int");
        }
//        var jsonObject = getAllCircuits();
        console.log(jsonObject);
        document.getElementById("listRoutes").innerHTML = "";
        for ( var i = 0; i < jsonObject.circuits.circuit.length; i++){
            document.getElementById("listRoutes").innerHTML += '<a style="text-decoration:none" href="javascript:void(0)" onclick="getSpecificCircuit(\''+jsonObject.circuits.circuit[i].circuitId+'\', 0)"><span id="innerTextRoute">Circuit: '+jsonObject.circuits.circuit[i].circuitId+'.</span></a><br/>';
        }
        if(jsonObject.circuits.circuit.length == 0 || jsonObject.circuits == null){
            document.getElementById("innerTable").innerHTML = '<table id="jsonTable" class="tablesorter"></table>';
            document.getElementById("innerTable2").innerHTML = '<table id="jsonQoS" class="tablesorter"></table>';
        }
        
    }, ${settings.updateTime}*10000);//5000
    
</script>
