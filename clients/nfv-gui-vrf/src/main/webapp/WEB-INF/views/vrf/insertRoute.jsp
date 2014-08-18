<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div id="insert_topology" class="topology ui-widget-content ui-corner-all">
    <c:if test="${!empty topologyName}">
         <p onmousedown="cleanDrag()" id="chart" ></p>
    </c:if>
    <c:if test="${empty topologyName}">
        <br/>
        <center>
            <h3 style="color:red"><spring:message code="topology.notdefined"/></h3>
        </center>
        <br/>
    </c:if>
</div>
<script src="<c:url value="/resources/js/topology/base.js" />"></script>
<script src="<c:url value="/resources/js/topology/insertRouteTopology.js" />"></script>
<div style="margin-left: 3px; float: left; width: 31%;">
    <span id="mode" style="float: right; margin-right: 0px;">
        <input style="position:absolute;" type="radio" id="mode0" name="repeat" checked="checked" onClick="javascript:change('Automatic');"><label for="mode0">Automatic</label>
        <input style="position:absolute;" type="radio" id="mode1" name="repeat"><label for="mode1" onClick="javascript:change('Manual');">Manual</label>
    </span>
    <br/><br/>
    <input id="manualType" type="submit" class="button" style="display:none" value="Point-to-point" onClick="javascript:toggleManualType(this);"/>
    <br/><br/><br/>
    <div id="insertRouteInfo" class="ui-widget-content ui-corner-all" style="display:none">
        <h3>Route information:</h3>
        <form>
            <fieldset>
                <label for="name" class="ipLabel">Source IP:</label>
                <input type="text" name="ipSrc" id="ipSrc" class="ipInput text ui-widget-content ui-corner-all" value=""/>
                <br/><br/>
                <label for="name" class="ipLabel">Destination IP:</label>
                <input type="text" name="ipDest" id="ipDest" class="ipInput text ui-widget-content ui-corner-all" value=""/>
            </fieldset>
            <input style="margin-right: 11.5px" class="addRouteButton" onClick="insertPath()" type="button" value="Insert Routes" name="addDefaultValues"/>
        </form>
    </div>
</div>
<div id="insert_routeTable" class="routeTable ui-widget-content ui-corner-all">
    <h3>Insert routes into table manually:</h3>

    <form:form modelAttribute="insertRoutes" name="frm" method="post" onSubmit="return Validate();"> 
        <div class="config4">
            <table id="Routes" class="TableSorter">
                <thead>
                    <tr>
                        <th><form:label path="">Source Address</form:label></th>
                        <th><form:label path="">Destination Address</form:label></th>
                        <th><form:label path="">Switch Mac</form:label></th>
                        <th><form:label path="">Input Port</form:label></th>
                        <th><form:label path="">Output Port</form:label></th>
                        <th></th>
                    </tr>
                </thead>
                <tr>
                    <td><form:input class="ipInput" path="listRoutes[0].sourceAddress" type="text" value="192.168.1.1"/></td>                    
                    <td><form:input class="ipInput" path="listRoutes[0].destinationAddress" type="text" value="192.168.2.1"/></td>
                    <td><form:input class="macInput" path="listRoutes[0].switchInfo.macAddress" type="text" value="00:00:00:00:00:00:00:10"/></td>
                    <td><form:input class="portInput" path="listRoutes[0].switchInfo.inputPort" type="text" value="1"/></td>
                    <td><form:input class="portInput" path="listRoutes[0].switchInfo.outputPort" type="text" value="2"/></td>
                    <td class="td-last"><input style="float:right" class="deleteButton" type="button" value="Delete"/></td>
                </tr>
            </table>
            <input style="margin-right: 11.5px" class="addRouteButton" onClick="addRout()" type="button" value="Add" name="addRoute"/>
            <input id="submitUpdateConfig" class="button" type="submit" value="Update" />
        </div>
    </form:form>
    <br/><br/>
    <input style="margin-right: 11.5px" class="addRouteButton" onClick="fillDemo()" type="button" value="Default Values" name="addDefaultValues"/>
</div>
<script language="JavaScript" type="text/JavaScript">
    function deleteAll(table){
        this.table = document.getElementById(table);
        rows = this.table.getElementsByTagName("tr");
        x=1;
        while ( x<rows.length) {
            document.getElementById(table).deleteRow(x);
        }
    }

    count = 1;
    function addRout(){
        var appendTxt = "<tr>";                
        appendTxt = appendTxt + "<td><input class=\"ipInput\" id=\"listRoutes" + count + ".sourceAddress\" name=\"listRoutes[" + count + "].sourceAddress\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td><input class=\"ipInput\" id=\"listRoutes" + count + ".destinationAddress\" name=\"listRoutes[" + count + "].destinationAddress\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td><input class=\"macInput\" id=\"listRoutes" + count + ".switchInfo.macAddress\" name=\"listRoutes[" + count + "].switchInfo.macAddress\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td><input class=\"portInput\" id=\"listRoutes" + count + ".switchInfo.inputPort\" name=\"listRoutes[" + count + "].switchInfo.inputPort\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td><input class=\"portInput\" id=\"listRoutes" + count + ".switchInfo.outputPort\" name=\"listRoutes[" + count + "].switchInfo.outputPort\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td class=\"td-last\"><input style=\"float:right\" class=\"deleteButton\" type=\"button\" value=\"Delete\"/></td>";
        $("#Routes tr:last").after(appendTxt);
        count = count + 1;
        $(".deleteButton").button();
    }
    function insertNewInsertRowDemo(i, srcSubnet, dstSubnet, dpid, inPort, outPort){
        addRout();
        $("#listRoutes"+i+"\\.sourceAddress").val(srcSubnet);
        $("#listRoutes"+i+"\\.destinationAddress").val(dstSubnet);
        $("#listRoutes"+i+"\\.switchInfo\\.macAddress").val(dpid);
        $("#listRoutes"+i+"\\.switchInfo\\.inputPort").val(inPort);
        $("#listRoutes"+i+"\\.switchInfo\\.outputPort").val(outPort);
        i++;
        return i;
    }
    function fillDemo(){
        var ipSrc = "192.168.1.1";
        var ipDst = "192.168.2.51";
        var srcSubnet = "192.168.1.0/24";
        var dstSubnet = "192.168.2.0/24";
        var path = ["00:00:00:00:00:00:00:01", "00:00:00:00:00:00:00:03", "00:00:00:00:00:00:00:04", "00:00:00:00:00:00:00:06", "00:00:00:00:00:00:00:07", "00:00:00:00:00:00:00:08"];
        var inPorts = [3, 1, 1, 1, 1, 1];
        var outPorts = [2, 3, 3, 3, 2, 2];
        deleteAll("Routes");
        count=0;
        if(count==2)
        return;
        
        var j=0;
        j = insertNewInsertRowDemo(j, ipSrc, dstSubnet, path[0], inPorts[0], outPorts[0]);
        j = insertNewInsertRowDemo(j, dstSubnet, ipSrc, path[0], outPorts[0], inPorts[0]);
        for(i = 1; i < path.length - 1; i++) {
            j = insertNewInsertRowDemo(j, srcSubnet, dstSubnet, path[i], inPorts[i], outPorts[i]);
            j = insertNewInsertRowDemo(j, dstSubnet, srcSubnet, path[i], outPorts[i], inPorts[i]);
        }
        j = insertNewInsertRowDemo(j, srcSubnet, ipDst, path[path.length-1], inPorts[inPorts.length-1], outPorts[outPorts.length-1]);
        j = insertNewInsertRowDemo(j, ipDst, srcSubnet, path[path.length-1], outPorts[outPorts.length-1], inPorts[inPorts.length-1]);
    }  
</script>
<script>
    var ipSrcDialog = "";
    var ipDestDialog = "";
    
    var defer = $.Deferred();//allows to return values using dialogs. --> synchronous
    var ipSrc = $( "#dialogIpSrc" ).val();
    var ipDest = $( "#dialogIpDest" ).val();
console.log(ipDest);    
    var srcValid = true;
    var dstValid = true;
    
    function insertIpDialog(newLink, originLink){
var ipDest = $( "#dialogIpDest" ).val();
console.log(ipDest);
var name = "";
        allFields = $( [] ).add( name );
        tips = $( ".validateTips" );
//Obtain the source Ip of the graph. Only in the case that one of the selected nodes is a host. If not (is a switch), doesn't save anything...
        if(newLink.source.ip){
            srcValid = checkIp(newLink.source.ip);
            if ( srcValid ) {
                ipSrc =  newLink.source.ip;
            }
        } else if(newLink.target.ip){
            dstValid = checkIp(newLink.target.ip);
            if ( dstValid ) {
                document.getElementById('dialogIpDest').value = newLink.target.ip;
                ipDst =  newLink.target.ip;
            }
        }
console.log("IpSource "+ipSrc);

        document.getElementById('dialogIpSrc').value = ipSrc;//recover the IP value, obtained from graph, or from html tag (input) (saved before)
        document.getElementById('dialogIpDest').value = ipDest;
        $( "#insertRouteIp" ).dialog({
            autoOpen: true,
            height: 300,
            close: function () { 
                $(this).dialog('destroy');
                allFields.val( "" ).removeClass( "ui-state-error" );
            },
            width: 350,
            modal: true,
            buttons: {
                "Create Route": function() {
                    var ipDest = $( "#dialogIpDest" ).val(); 
console.log("Create Route "+ipSrc+ " to "+ipDest);
                    srcValid = true;
                    dstValid = true;
//                      allFields.removeClass( "ui-state-error" );
                    srcValid = checkIp(ipSrc);
                    dstValid = checkIp(ipDest);

                    if ( srcValid && dstValid ) {//the source IP or destination IP is defined by the drawed node
                        ipSrcDialog = ipSrc;
                        ipDestDialog = ipDest;
                        document.getElementById('dialogIpSrc').value = ipSrcDialog;
                        document.getElementById('dialogIpDest').value = ipDestDialog;
                        defer.resolve(ipDestDialog);//response is not required
                        insertManualLink(newLink, originLink, ipSrcDialog, ipDestDialog);
                        $( this ).dialog( "close" );
                    }
                },
                Cancel: function() {             
                    removeLastLink();//remove last link inserted (push) and remove the dragged line
                    defer.resolve("cancel");//response is not required
                    $( this ).dialog( "close" );
                }
            }
        });
        $( "#dialogIpDest" ).val(ipDestDialog);
        return defer.promise();
    }
    
    function insertIpDiv(newLink){
        var name = "";
        allFields = $( [] ).add( name );
        tips = $( ".validateTips" );
//Obtain the source Ip of the graph. Only in the case that one of the selected nodes is a host. If not (is a switch), doesn't save anything...
        if(newLink.source.ip){
            srcValid = checkIp(newLink.source.ip);
            if ( srcValid ) {
                ipSrc =  newLink.source.ip;
            }
        } else if(newLink.target.ip){
            dstValid = checkIp(newLink.target.ip);
            if ( dstValid ) {
                document.getElementById('ipDest').value = newLink.target.ip;
                ipDest =  newLink.target.ip;
            }
        }
console.log(ipSrc);
console.log(ipDest);
        document.getElementById('ipSrc').value = ipSrc;//recover the IP value, obtained from graph, or from html tag (input) (saved before)
        document.getElementById('ipDest').value = ipDest;
            
        srcValid = true;
        dstValid = true;
        srcValid = checkIp(ipSrc);
        dstValid = checkIp(ipDest);

        if ( srcValid && dstValid ) {//the source IP or destination IP is defined by the drawed node
            document.getElementById('ipSrc').value = ipSrc;
            document.getElementById('ipDest').value = ipDest;
            defer.resolve(ipDestDialog);//response is not required
        }
sourceIp = ipSrc;
destinationIp = ipDest;
        $( "#ipDest" ).val(ipDest);
    }

    /**
     * 
     * @param {type} newLink
     * @param {type} originLink
     * @param {type} ipSrc
     * @param {type} ipDst
     * @returns {undefined}
     */
    function insertManualLink(newLink, originLink, ipSrc, ipDst){
console.log(originLink);
console.log(newLink);
console.log(stackRoute);

        if (stackRoute.length > 0){
            for ( i = stackRoute.length-1; i >= 0; i--){
                if(ipSrc == stackRoute[i].ipSrc && ipDst == stackRoute[i].ipDst && 
                    newLink.source.dpid == stackRoute[i].dpid ){
                    insertRoute(ipSrc, ipDst, stackRoute[i].dpid, stackRoute[i].inPort, originLink.dstPort);
                    insertRoute(ipDst, ipSrc, stackRoute[i].dpid, originLink.dstPort, stackRoute[i].inPort);
                    stackRoute.splice(i, 1);
                }
            }
        }
        if ( originLink.target.ip == ipDst ){
            insertRoute(ipSrc, ipDst, newLink.source.dpid, originLink.srcPort, originLink.dstPort);
            insertRoute(ipDst, ipSrc, newLink.source.dpid, originLink.dstPort, originLink.srcPort);
        }else{
console.log("Saving route");            
            var route = new Object();
            route.ipSrc = ipSrc;
            route.ipDst = ipDst;
            route.dpid = originLink.target.dpid;
            route.inPort = originLink.dstPort;
            route.outPort = "X";
            stackRoute.push(route);
        }
console.log(stackRoute);
    }
</script>
<div id="insertRouteIp" title="Required information for this route" style="display:none">
    <p class="validateTips">All form fields are required.</p>
    <form>
        <fieldset>
            <label for="name" class="ipLabel">Source IP:</label>
            <input type="text" name="ipSrc" id="dialogIpSrc" class="ipInput text ui-widget-content ui-corner-all" value=""/>
            <br/><br/>
            <label for="name" class="ipLabel">Destination IP:</label>
            <input type="text" name="ipDest" id="dialogIpDest" class="ipInput text ui-widget-content ui-corner-all" value=""/>
        </fieldset>
    </form>
</div>
