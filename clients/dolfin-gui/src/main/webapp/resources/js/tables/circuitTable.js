function ConvertJsonToCircuitTable(parsedJson, tableId) {
    waiting(true);
    //Patterns for links and NULL value

    //Pattern for table                          
    var idMarkup = tableId ? ' id="' + tableId + '"' : '';
    var tbl = '<table border="1" cellpadding="1" cellspacing="1"' + idMarkup + '>{0}{1}</table>';

    //Patterns for table content
    var th = '<thead>{0}</thead>';
    var tb = '<tbody>{0}</tbody>';
    var tr = '<tr>{0}</tr>';
    var thRow = '<th>{0}</th>';
    var tdRow = '<td>{0}</td>';
    var tdRowHide = '<td style="display: none; ">{0}</td>';
    var thCon = '';
    var tbCon = '';
    var trCon = '';

    if (parsedJson) {
        var isStringArray = typeof (parsedJson[0]) === 'string';
        var headers;

        headers = array_keys(parsedJson[0]);
//        headers[0] = "Id";
        headers[0] = "Name";
        headers[1] = "Source Port";
        headers[2] = "Target Port";
        headers[3] = "ToS";

        for (i = 0; i < headers.length; i++)
            thCon += thRow.format(headers[i]);

        headers = array_keys(parsedJson.route.networkConnections[0]);
        th = th.format(tr.format(thCon));

        // Create table rows from Json data
        if (isStringArray) {
            for (i = 0; i < parsedJson.length; i++) {
                tbCon += tdRow.format(parsedJson[i]);
                trCon += tr.format(tbCon);
                tbCon = '';
            }
        } else {
            if (headers) {
                var routeId = parsedJson.route['id'];
//                 tbCon += tdRow.format(routeId);
                for (i = 0; i < parsedJson.route.networkConnections.length; i++) {
                    var ncId = parsedJson.route.networkConnections[i]['id'];
//                    tbCon += tdRow.format(ncId);
                    tbCon += tdRow.format(parsedJson.route.networkConnections[i]['name']);
                    tbCon += tdRow.format(parsedJson.route.networkConnections[i]['source']._id);
                    tbCon += tdRow.format(parsedJson.route.networkConnections[i]['destination']._id);
                    tbCon += tdRow.format(parsedJson.trafficFilter.tosBits);
                    trCon += tr.format(tbCon);
                    tbCon = '';
                }
            }
        }
        tb = tb.format(trCon);
        tbl = tbl.format(th, tb);
        waiting(false);
        return tbl;
    }
    waiting(false);
    return null;
}

/**
 * Show QoS parameters
 * @param {type} parsedJson
 * @param {type} tableId
 * @returns {String|ConvertJsonQoSToQoSTable.tbl}
 */
function ConvertJsonQoSToQoSTable(parsedJson, tableId) {
    waiting(true);
    //Patterns for links and NULL value

    //Pattern for table                          
    var idMarkup = tableId ? ' id="' + tableId + '"' : '';
    var tbl = '<table border="1" cellpadding="1" cellspacing="1"' + idMarkup + '>\n\
        <span style="font-weight: bold">QoS parameters:</span>\n\
        {0}{1}</table>';

    //Patterns for table content
    var th = '<thead>{0}</thead>';
    var tb = '<tbody>{0}</tbody>';
    var tr = '<tr>{0}</tr>';
    var thRow = '<th>{0}</th>';
    var tdRow = '<td style="background:#c3d9ff; text-align:center">{0}</td>';
    var tdRow2 = '<td style="text-align:center">{0}</td>';
    var thCon = '';
    var tbCon = '';
    var trCon = '';

    if (parsedJson) {
        var isStringArray = typeof (parsedJson[0]) === 'string';
        var headers;

        headers = array_keys(parsedJson.route.networkConnections[0]);
        th = th.format(tr.format(thCon));

        // Create table rows from Json data
        if (isStringArray) {
            for (i = 0; i < parsedJson.length; i++) {
                tbCon += tdRow.format(parsedJson[i]);
                trCon += tr.format(tbCon);
                tbCon = '';
            }
        } else {
            if (headers) {
                tbCon += tdRow.format("<span >ToS</span>");
                tbCon += tdRow2.format(parsedJson.trafficFilter.tosBits);
                trCon += tr.format(tbCon);
                tbCon = '';
                tbCon += tdRow.format("<span >minLatency</span>");
                tbCon += tdRow2.format(parsedJson.qos.minLatency);
                trCon += tr.format(tbCon);
                tbCon = '';
                tbCon += tdRow.format("maxLatency");
                tbCon += tdRow2.format(parsedJson.qos.maxLatency);
                trCon += tr.format(tbCon);
                tbCon = '';
                tbCon += tdRow.format("minJitter");
                tbCon += tdRow2.format(parsedJson.qos.minJitter);
                trCon += tr.format(tbCon);
                tbCon = '';
                tbCon += tdRow.format("maxJitter");
                tbCon += tdRow2.format(parsedJson.qos.maxJitter);
                trCon += tr.format(tbCon);
                tbCon = '';
                tbCon += tdRow.format("minThroughput");
                tbCon += tdRow2.format(parsedJson.qos.minThroughput);
                trCon += tr.format(tbCon);
                tbCon = '';
                tbCon += tdRow.format("maxThroughput");
                tbCon += tdRow2.format(parsedJson.qos.maxThroughput);
                trCon += tr.format(tbCon);
                tbCon = '';
                tbCon += tdRow.format("minPacketLoss");
                tbCon += tdRow2.format(parsedJson.qos.minPacketLoss);
                trCon += tr.format(tbCon);
                tbCon = '';
                tbCon += tdRow.format("maxPacketLoss");
                tbCon += tdRow2.format(parsedJson.qos.maxPacketLoss);
                trCon += tr.format(tbCon);
                tbCon = '';
            }
        }
        tb = tb.format(trCon);
        tbl = tbl.format(th, tb);
        waiting(false);
        return tbl;
    }
    waiting(false);
    return null;
}

/**
 * Request the list of links of specific route
 */
var showRoutes = false;
var toggle;
function getSpecificCircuit(circuitId, type) {
    selectedCircuit = circuitId;
    var newToggle = circuitId;
    if (showRoutes) {
        console.log("Hide routes");
        document.getElementById("innerTable").innerHTML = '<table id="jsonTable" class="tablesorter"></table>';
        document.getElementById("innerTable2").innerHTML = '<table id="jsonQoS" class="tablesorter"></table>';
        $("#config_routeTable").height(503);
        cleanDisplayedLink();
        selectedCircuit = null;
        showRoutes = false;
        if (toggle !== null) {
            if (toggle === newToggle) {
                return;
            }
        }
    }
        
     toggle = circuitId;
     console.log("Get specific circuit "+circuitId);
     var json = getCircuit(circuitId);
/*    if(circuitId == 3){
        result = '{"circuitId":"3","route":{"id":"R1","networkConnections":[{"id":"NC1","name":"NC1","source":{"state":{"congested":"false"},"_id":"4"},"destination":{"state":{"congested":"false"},"_id":"1"}},{"id":"NC2","name":"NC2","source":{"state":{"congested":"false"},"_id":"1"},"destination":{"state":{"congested":"false"},"_id":"2"}},{"id":"NC3","name":"NC3","source":{"state":{"congested":"false"},"_id":"2"},"destination":{"state":{"congested":"false"},"_id":"3"}},{"id":"NC4","name":"NC4","source":{"state":{"congested":"false"},"_id":"2"},"destination":{"state":{"congested":"false"},"_id":"4"}}]},"qos":{"minLatency":"345","maxLatency":"456","minJitter":"123","maxJitter":"234","minThroughput":"567","maxThroughput":"678","minPacketLoss":"789","maxPacketLoss":"890"},"trafficFilter":{"ingressPort":"4","tosBits":"4"}}';
    } else if(circuitId == 2){
        result = '{"circuitId":"2","route":{"id":"R1","networkConnections":[{"id":"NC1","name":"NC1","source":{"state":{"congested":"false"},"_id":"4"},"destination":{"state":{"congested":"false"},"_id":"3"}},{"id":"NC2","name":"NC2","source":{"state":{"congested":"false"},"_id":"1"},"destination":{"state":{"congested":"false"},"_id":"4"}}]},"qos":{"minLatency":"345","maxLatency":"456","minJitter":"123","maxJitter":"234","minThroughput":"567","maxThroughput":"678","minPacketLoss":"789","maxPacketLoss":"890"},"trafficFilter":{"ingressPort":"4","tosBits":"4"}}';
//        result = '{"circuitId":"3","route":{"id":"R1","networkConnections":[{"id":"NC1","name":"NC1","source":{"state":{"congested":"false"},"_id":"3"},"destination":{"state":{"congested":"false"},"_id":"2"}},{"id":"NC2","name":"NC2","source":{"state":{"congested":"false"},"_id":"1"},"destination":{"state":{"congested":"false"},"_id":"4"}},{"id":"NC3","name":"NC3","source":{"state":{"congested":"false"},"_id":"1"},"destination":{"state":{"congested":"false"},"_id":"4"}},{"id":"NC4","name":"NC4","source":{"state":{"congested":"false"},"_id":"1"},"destination":{"state":{"congested":"false"},"_id":"4"}},{"id":"NC5","name":"NC5","source":{"state":{"congested":"false"},"_id":"1"},"destination":{"state":{"congested":"false"},"_id":"4"}},{"id":"NC6","name":"NC6","source":{"state":{"congested":"false"},"_id":"1"},"destination":{"state":{"congested":"false"},"_id":"4"}},{"id":"NC7","name":"NC7","source":{"state":{"congested":"false"},"_id":"1"},"destination":{"state":{"congested":"false"},"_id":"4"}},{"id":"NC8","name":"NC8","source":{"state":{"congested":"false"},"_id":"1"},"destination":{"state":{"congested":"false"},"_id":"4"}},{"id":"NC9","name":"NC9","source":{"state":{"congested":"false"},"_id":"1"},"destination":{"state":{"congested":"false"},"_id":"4"}}]},"qos":{"minLatency":"345","maxLatency":"456","minJitter":"123","maxJitter":"234","minThroughput":"567","maxThroughput":"678","minPacketLoss":"789","maxPacketLoss":"890"},"trafficFilter":{"ingressPort":"3","tosBits":"4"}}';
    }
*/    
//    var json = eval("(" + result + ")");
    var circuit = json.circuit;
    //console.log(result);
    if(type === 0){
        var jsonHtmlTable = ConvertJsonToCircuitTable(circuit, 'jsonTable');
        document.getElementById("innerTable").innerHTML = '<table id="jsonTable" class="tablesorter"></table>';
        document.getElementById("jsonTable").innerHTML = jsonHtmlTable;
        document.getElementById("innerTable").innerHTML += "</table>";

        var jsonQoSHtmlTable = ConvertJsonQoSToQoSTable(circuit, 'jsonQoS');
        document.getElementById("innerTable2").innerHTML = '<table id="jsonQoS" class="tablesorter"></table>';
        document.getElementById("jsonQoS").innerHTML = jsonQoSHtmlTable;
        document.getElementById("innerTable2").innerHTML += "</table>";

        showRoutes = true;
        console.log($(window).height());
        if ( $(window).height() > 543 ){
            $("#innerTable").height($("#jsonTable").height()+10);
            $("#innerTable2").height($("#jsonQoS").height()+11);
            $("#config_routeTable").height($("#innerTable").height()+$("#innerTable2").height() +170);
        }else{
            $("#innerTable").height($(window).height()/3);
            $("#innerTable2").height($(window).height()/3);
        }
    }
    showGraphicRoute(circuitId, circuit, type);
}

/**
 * Request to OpenNaaS in order to obtain all the circuits (CircuitCollection)
 * @returns {unresolved}
 */
function getAllCircuits() {
    var xml = "";
    $.ajax({
        type: 'GET',
        url: "ajax/getCircuits",
//        url: "http://84.88.40.109:8083/dolfin/secure/dolfin/ajax/getCircuits",
        async: false,
        success: function(data) {
            //                $("#dynamicContent").html(data);
            xml = data;
        }
    });
    var xmlText = new XMLSerializer().serializeToString(xml);
    var json = convertXml2JSon(xmlText);
    return eval("(" + json + ")");
}

/**
 * Request to OpenNaaS in order to obtain the list of routes given the IP version in the following format (Ipv4/IPv6)
 * @param {type} circuitId
 * @returns {unresolved}
 */
function getCircuit(circuitId) {
    var xml = "";
    $.ajax({
        type: 'GET',
        url: "ajax/getCircuit/" + circuitId,
//        url: "http://84.88.40.109:8083/dolfin/secure/dolfin/ajax/getCircuit/"+circuitId,
        async: false,
        success: function(data) {
            //                $("#dynamicContent").html(data);
            xml = data;
        }
    });
    var xmlText = new XMLSerializer().serializeToString(xml);
    var json = convertXml2JSon(xmlText);
    return eval("(" + json + ")");
}

function waiting(status) {
    $body = $("body");
    if (status)
        $body.addClass("loading");
    else
        $body.removeClass("loading");
}

function portIdsMap(){
    var xml = "";
    $.ajax({
        type: 'GET',
        url: "ajax/portIdsMap",
        async: false,
        success: function(data) {
            //                $("#dynamicContent").html(data);
            xml = data;
        }
    });
//    var xmlText = new XMLSerializer().serializeToString(xml);
//    var json = convertXml2JSon(xmlText);
//    return eval("(" + json + ")");
    return xml;
}

function findPort(port){
    for(key in portsIdMap){
        console.log(portsIdMap[key]);
        if(port == key){
            return portsIdMap[key].devicePortId;
        }
    }
}