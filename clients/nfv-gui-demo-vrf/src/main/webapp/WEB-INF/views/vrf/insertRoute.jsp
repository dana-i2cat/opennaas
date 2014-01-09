<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div id="insert_topology" class="topology">
    <p onmousedown="cleanDrag()" id="chart" ></p>
</div>
<script src="<c:url value="/resources/js/topology/baseTopology.js" />"></script>
<script src="<c:url value="/resources/js/topology/insertRouteTopology.js" />"></script>
<input id="changeMode" type="submit" class="button" value="Automatic" onClick="javascript:change(this);"/>
<div id="insert_info" class="ui-widget-content ui-corner-all padding">
    <h3>Route information:</h3>

</div>
<div id="insert_routeTable" class="routeTable">
    <h3>Route table Insert</h3>
</div>

<table id="jsonTable" class="tablesorter">

    <form:form modelAttribute="insertRoutes" name="frm" method="post" onSubmit="return Validate();"> 
        <div class="config4">
            <table id="Routes" class="TableSorter"><thead><tr>
                        <th><form:label path="">Source Address</form:label></th>
                        <th><form:label path="">Destination Address</form:label></th>
                        <th><form:label path="">Switch Mac</form:label></th>
                        <th><form:label path="">Input Port</form:label></th>
                        <th><form:label path="">Output Port</form:label></th>
                            <th></th>
                        </tr></thead>
                    <tr>
                        <td><form:input class="ipInput" path="listRoutes[0].sourceAddress" type="text" value="192.168.1.1"/></td>                    
                    <td><form:input class="ipInput" path="listRoutes[0].destinationAddress" type="text" value="192.168.2.1"/></td>
                    <td><form:input class="macInput" path="listRoutes[0].switchInfo.macAddress" type="text" value="00:00:00:00:00:00:00:10"/></td>
                    <td><form:input class="portInput" path="listRoutes[0].switchInfo.inputPort" type="text" value="1"/></td>
                    <td><form:input class="portInput" path="listRoutes[0].switchInfo.outputPort" type="text" value="2"/></td>
                    <td class="td-last"><input style="float:right" class="deleteButton" type="button" value="Delete"/>
                    </td>
                </tr>

            </table>
            <input style="margin-right: 11.5px" class="addRouteButton" onClick="addRout()" type="button" value="Add" name="addRoute"/>
            <input id="submitUpdateConfig" class="button" type="submit" value="Update" />
        </div>
    </form:form>
    <br/><br/>
    <input style="margin-right: 2.5px" class="addRouteButton" onClick="fill2()" type="button" value="Default Values 2" name="addDefaultValues"/>
    <!-- <input style="margin-right: 2.5px" class="addRouteButton" onClick="fill()" type="button" value="Default Values" name="addDefaultValues"/>
    --><input style="margin-right: 11.5px" class="addRouteButton" onClick="fillDemo()" type="button" value="Default Values" name="addDefaultValues"/>
</table> 

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

    function fill(){
    deleteAll('Routes');
    //        $('Routes  tr:last').append('<tbody><tr></tr></tbody>');
    count=0;
    if(count==2)
    return;
    addRout();
    $("#listRoutes0\\.sourceAddress").val("10.0.1.0");
    $("#listRoutes0\\.destinationAddress").val("10.0.2.0");
    $("#listRoutes0\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:01");
    $("#listRoutes0\\.switchInfo\\.inputPort").val("5");
    $("#listRoutes0\\.switchInfo\\.outputPort").val("1");
    addRout();
    $("#listRoutes1\\.sourceAddress").val("10.0.2.0");
    $("#listRoutes1\\.destinationAddress").val("10.0.1.1");
    $("#listRoutes1\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:01");
    $("#listRoutes1\\.switchInfo\\.inputPort").val("1");
    $("#listRoutes1\\.switchInfo\\.outputPort").val("5");
    addRout();
    $("#listRoutes2\\.sourceAddress").val("10.0.2.0");
    $("#listRoutes2\\.destinationAddress").val("10.0.1.0");
    $("#listRoutes2\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:02");
    $("#listRoutes2\\.switchInfo\\.inputPort").val("5");
    $("#listRoutes2\\.switchInfo\\.outputPort").val("1");
    addRout();
    $("#listRoutes3\\.sourceAddress").val("10.0.1.0");
    $("#listRoutes3\\.destinationAddress").val("10.0.2.51");
    $("#listRoutes3\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:02");
    $("#listRoutes3\\.switchInfo\\.inputPort").val("1");
    $("#listRoutes3\\.switchInfo\\.outputPort").val("5");
    }

    function fill2(){
    deleteAll("Routes");
    count=0;
    if(count==2)
    return;
    addRout();
    $("#listRoutes0\\.sourceAddress").val("10.1.10.0");
    $("#listRoutes0\\.destinationAddress").val("10.1.11.51");
    $("#listRoutes0\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:0b");
    $("#listRoutes0\\.switchInfo\\.inputPort").val("1");
    $("#listRoutes0\\.switchInfo\\.outputPort").val("2");
    addRout();
    $("#listRoutes1\\.sourceAddress").val("10.1.11.0");
    $("#listRoutes1\\.destinationAddress").val("10.1.10.1");
    $("#listRoutes1\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:0a");
    $("#listRoutes1\\.switchInfo\\.inputPort").val("1");
    $("#listRoutes1\\.switchInfo\\.outputPort").val("2");
    addRout();
    $("#listRoutes2\\.sourceAddress").val("10.1.11.0");
    $("#listRoutes2\\.destinationAddress").val("10.1.10.0");
    $("#listRoutes2\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:0b");
    $("#listRoutes2\\.switchInfo\\.inputPort").val("2");
    $("#listRoutes2\\.switchInfo\\.outputPort").val("1");
    addRout();
    $("#listRoutes3\\.sourceAddress").val("10.1.10.10");
    $("#listRoutes3\\.destinationAddress").val("10.1.11.0");
    $("#listRoutes3\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:0a");
    $("#listRoutes3\\.switchInfo\\.inputPort").val("2");
    $("#listRoutes3\\.switchInfo\\.outputPort").val("1");
    }

    function fillDemo(){
    deleteAll("Routes");
    count=0;
    if(count==2)
    return;
    addRout();
    $("#listRoutes0\\.sourceAddress").val("10.0.0.0");
    $("#listRoutes0\\.destinationAddress").val("10.0.2.2");
    $("#listRoutes0\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:01");
    $("#listRoutes0\\.switchInfo\\.inputPort").val("1");
    $("#listRoutes0\\.switchInfo\\.outputPort").val("2");
    addRout();
    $("#listRoutes1\\.sourceAddress").val("10.0.0.0");
    $("#listRoutes1\\.destinationAddress").val("10.0.2.2");
    $("#listRoutes1\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:02");
    $("#listRoutes1\\.switchInfo\\.inputPort").val("2");
    $("#listRoutes1\\.switchInfo\\.outputPort").val("1");
    addRout();
    $("#listRoutes2\\.sourceAddress").val("10.0.2.0");
    $("#listRoutes2\\.destinationAddress").val("10.0.0.1");
    $("#listRoutes2\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:02");
    $("#listRoutes2\\.switchInfo\\.inputPort").val("1");
    $("#listRoutes2\\.switchInfo\\.outputPort").val("2");
    addRout();        
    $("#listRoutes3\\.sourceAddress").val("10.0.2.0");
    $("#listRoutes3\\.destinationAddress").val("10.0.0.1");
    $("#listRoutes3\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:01");
    $("#listRoutes3\\.switchInfo\\.inputPort").val("2");
    $("#listRoutes3\\.switchInfo\\.outputPort").val("1");
    }        
</script>
<script>
    var ipSrcDialog = "";
    var ipDestDialog = "";
    function updateTips( t ) {
        tips.text( t ).addClass( "ui-state-highlight" );
        setTimeout(function() {
         tips.removeClass( "ui-state-highlight", 1500 );
        }, 500 );
    }
    /* Move to a centralized file? */
    function checkLength( o, n, min, max ) {
        var newO;
        if(typeof o === 'string' || o instanceof String ){
            newO = o;
        }else{
            newO = o.val();
        }
        if ( newO.length > max || newO.length < min ) {
            o.addClass( "ui-state-error" );
            updateTips( "Length of " + n + " must be between " + min + " and " + max + "." );
            return false;
        } else {
            return true;
        }
    }
        //192.168.2.1
    function checkRegexp( o, regexp, n ) {
        var newO;
        if(typeof o === 'string' || o instanceof String ){
            newO = o;
        }else{
            newO = o.val();
        }
        if ( !( regexp.test( newO ) ) ) {
            o.addClass( "ui-state-error" );
            updateTips( n );
            return false;
        } else {
            return true;
        }
    }
    
    //
    function checkIp( ip ){
        var validIp = true;
        validIp = validIp && checkLength( ip, "ip", 8, 16 );
        validIp = validIp && checkRegexp( ip, /^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$/, "Insert an IP address with the following format: www.xxx.yyy.zzz" );
        return validIp;
    }

    function insertIpDialog(newLink, originLink){
        var defer = $.Deferred();//allows to return values using dialogs. --> synchronous
        var ipSrc = $( "#ipSrc" );
        var ipDest = $( "#ipDest" );
        var srcValid = true;
        var dstValid = true;
        allFields = $( [] ).add( name );
        tips = $( ".validateTips" );
//Obtain the source Ip of the graph. Only in the case that one of the selected nodes is a host. If not (is a switch), doesn't save anything...
        if(newLink.source.ip){
            srcValid = checkIp(newLink.source.ip);
            if ( srcValid ) {
                ipSrc.value =  newLink.source.ip;
            }
        } else if(newLink.target.ip){
            dstValid = checkIp(newLink.target.ip);
            if ( dstValid ) {
                document.getElementById('ipSrc').value = newLink.target.ip;
                ipSrc.value =  newLink.target.ip;
            }
        }
console.log(ipSrc.val());
        document.getElementById('ipSrc').value = ipSrc.val();//recover the IP value, obtained from graph, or from html tag (input) (saved before)
        document.getElementById('ipDest').value = ipSrc.val();
        
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
                        srcValid = true;
                        dstValid = true;
//                      allFields.removeClass( "ui-state-error" );
                        srcValid = checkIp(ipSrc);
                        dstValid = checkIp(ipDest);

                        if ( srcValid && dstValid ) {//the source IP or destination IP is defined by the drawed node
                            ipSrcDialog = ipSrc.val();
                            ipDestDialog = ipDest.val();
                            document.getElementById('ipSrc').value = ipSrcDialog;
                            document.getElementById('ipDest').value = ipDestDialog;
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

        $( "#ipDest" ).val(ipDestDialog);
        return defer.promise();
    }

    /**
     * Insert manual link given the ip src/dst and port src/dst
     *
     * @param {type} newLink
     * @param {type} originLink
     * @param {type} ipSrc
     * @param {type} ipDest
     * @returns {undefined}
     */
    function insertManualLink(newLink, originLink, ipSrc, ipDest){
console.log(originLink);
            insertRoute(ipSrc, ipDest, newLink.source.dpid, originLink.srcPort, originLink.dstPort);
    }
    
</script>
<div id="insertRouteIp" title="Required information for this route" style="display:none">
    <p class="validateTips">All form fields are required.</p>
    <form>
        <fieldset>
            <label for="name">Source IP:</label>
            <input type="text" name="ipSrc" id="ipSrc" class="text ui-widget-content ui-corner-all" value=""/>
            <br/>
            <label for="name">Destination IP:</label>
            <input type="text" name="ipDest" id="ipDest" class="text ui-widget-content ui-corner-all" value=""/>
        </fieldset>
    </form>
</div>
