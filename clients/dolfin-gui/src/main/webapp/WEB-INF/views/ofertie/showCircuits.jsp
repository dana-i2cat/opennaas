<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script>
    var routeRowColor = "${settings.colorDynamicRoutes}";
    var type = getURLParameter("type");
//console.log(${json});// obsolete

</script>
<div id="config_routeTable" class="ui-widget-content ui-corner-all routTable padding" style="height: 503px">
    <h3>Allocated circuits</h3> 
    <div id="listRoutes"></div>
    <div class="outer">
	<div id="innerTable" class="innera">
            <table id="jsonTable" class="tablesorter"></table>
        </div><br/>
        <div id="innerTable2" class="innera">
            <table id="jsonQoS" class="tablesorter"></table>
        </div> 
   </div>
</div>
<div id="config_topology" class="topology ui-widget-content ui-corner-all">
        <p id="chart" ></p>
        <input style='margin-right: 11.5px' class='addRouteButton' onClick='deallocateFlows()' type='button' value='Delete flows' name='deleteFlows'/>
        <input style='margin-right: 11.5px' class='addRouteButton' onClick='allocateFlows("iperfReq")' type='button' value='Start congestion' name='insertDemo'/>
        <input style='margin-right: 11.5px' class='addRouteButton' onClick='allocateFlows("demoPath")' type='button' value='Insert Paths' name='insertDemo'/>
</div>
<script src="<c:url value="/resources/js/topology/base.js" />"></script>
<script src="<c:url value="/resources/js/topology/showCircuits.js" />"></script>

<script language="JavaScript" type="text/JavaScript">
//    console.log(${xml});
    var test = getAllCircuits();

//    document.getElementById("config_routeTable").innerHTML += "<input style='margin-right: 11.5px' class='addRouteButton' onClick='showAllCircuits(jsonObject)' type='button' value='Show all circuits' name='button1'/>";
//var xmlText = new XMLSerializer().serializeToString(xml);
//jsonDemo = convertXml2JSon(xmlText);

//    var jsonDemo = '{"circuits":{"circuit":[{"circuitId":"2","route":{"id":"R1","networkConnections":[{"id":"NC1","name":"NC1","source":{"state":{"congested":"false"},"_id":"3"},"destination":{"state":{"congested":"false"},"_id":"2"}},{"id":"NC2","name":"NC2","source":{"state":{"congested":"false"},"_id":"1"},"destination":{"state":{"congested":"false"},"_id":"3"}}]},"qos":{"minLatency":"345","maxLatency":"456","minJitter":"123","maxJitter":"234","minThroughput":"567","maxThroughput":"678","minPacketLoss":"789","maxPacketLoss":"890"},"trafficFilter":{"ingressPort":"3","tosBits":"4"}},{"circuitId":"3","route":{"id":"R1","networkConnections":[{"id":"NC1","name":"NC1","source":{"state":{"congested":"false"},"_id":"1"},"destination":{"state":{"congested":"false"},"_id":"2"}},{"id":"NC2","name":"NC2","source":{"state":{"congested":"false"},"_id":"3"},"destination":{"state":{"congested":"false"},"_id":"4"}}]},"qos":{"minLatency":"345","maxLatency":"456","minJitter":"123","maxJitter":"234","minThroughput":"567","maxThroughput":"678","minPacketLoss":"789","maxPacketLoss":"890"},"trafficFilter":{"ingressPort":"1","tosBits":"4"}}],"_xmlns:ns2":"opennaas.api","__prefix":"ns2"}}';
//    var jsonObject = eval("("+jsonDemo+")");

    var jsonObject = test;
    console.log(jsonObject);
    if(jsonObject.circuits.circuit.length == 0 || jsonObject.circuits == null){
            document.getElementById("innerTable").innerHTML = '<table id="jsonTable" class="tablesorter"></table>';
            document.getElementById("innerTable2").innerHTML = '<table id="jsonQoS" class="tablesorter"></table>';
        }
for ( var i = 0; i < jsonObject.circuits.circuit.length; i++){
        document.getElementById("listRoutes").innerHTML += '<a style="text-decoration:none" href="javascript:void(0)" onclick="getSpecificCircuit(\''+jsonObject.circuits.circuit[i].circuitId+'\', 0)"><span id="innerTextRoute">Circuit: '+jsonObject.circuits.circuit[i].circuitId+'.</span></a><br/>';
    }
    
    
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
        var jsonObject = getAllCircuits();
        console.log(jsonObject);
        document.getElementById("listRoutes").innerHTML = "";
        for ( var i = 0; i < jsonObject.circuits.circuit.length; i++){
            document.getElementById("listRoutes").innerHTML += '<a style="text-decoration:none" href="javascript:void(0)" onclick="getSpecificCircuit(\''+jsonObject.circuits.circuit[i].circuitId+'\', 0)"><span id="innerTextRoute">Circuit: '+jsonObject.circuits.circuit[i].circuitId+'.</span></a><br/>';
        }
        if(jsonObject.circuits.circuit.length == 0 || jsonObject.circuits == null){
            document.getElementById("innerTable").innerHTML = '<table id="jsonTable" class="tablesorter"></table>';
            document.getElementById("innerTable2").innerHTML = '<table id="jsonQoS" class="tablesorter"></table>';
        }
/*        $.ajax({
            type: 'GET',
            url : "getLog/",
            async: false,
            success : function (data) {
                if(data !== ""){
                    var image = '<img src="<c:url value="/resources/images/arrow_icon.svg.png" />" width="18px"/>';
                    document.getElementById('log').innerHTML = image+data;
                }else{
                    document.getElementById('log').innerHTML = data;
                }
                result = data;                 
            }
        });*/
        
    }, ${settings.updateTime}*1000);//5000
    
    function allocateFlows(type){
        var url;
        var response;
        if(type === "demoPath"){
            url = "/ofertie/secure/ofertie/insertDemoPath";
        }else{
            url = "/ofertie/secure/ofertie/insertIperfReq";
        }
        $.ajax({
            type: 'GET',
            url : url,
            async: false,
            success : function (data) {
                response = data;                 
            }
        });
        if(document.getElementById("insertRoute") === null){
        if(response === "500"){
            $("<div id='insertRoute' class='error'>Connection problem with OpenNaaS.</div>" ).insertAfter( "#header_menu").before("<br>");
        }else{
            $("<div id='insertRoute' class='success'>Inserted correctly.</div>" ).insertAfter( "#header_menu").before("<br>");
        }
        $('#insertRoute').next('br').remove();
        setTimeout(function() {
            //$('.success').remove();
            $('#insertRoute').slideUp("slow", function() { $('#insertRoute').remove();});
            //$('.success').fadeOut(300, function(){ $(this).remove();});
        }, 4000);
    }
    return response;
    }
    function deallocateFlows(){
        $.ajax({
            type: 'GET',
            url : "/ofertie/secure/ofertie/delete",
            async: false,
            success : function (data) {
                result = data;                 
            }
        });
        if(document.getElementById("insertRoute") === null){
        if(response === "500"){
            $("<div id='insertRoute' class='error'>Connection problem with OpenNaaS.</div>" ).insertAfter( "#header_menu").before("<br>");
        }else{
            $("<div id='insertRoute' class='success'>Inserted correctly.</div>" ).insertAfter( "#header_menu").before("<br>");
        }
        $('#insertRoute').next('br').remove();
        setTimeout(function() {
            //$('.success').remove();
            $('#insertRoute').slideUp("slow", function() { $('#insertRoute').remove();});
            //$('.success').fadeOut(300, function(){ $(this).remove();});
        }, 4000);
    }
    return response;
    }
</script>
