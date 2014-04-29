function ConvertJsonToRouteTable(parsedJson, tableId) {
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
        headers[0] = "Id";
        headers[1] = "Src IP";
        headers[2] = "Dst IP";
        headers[3] = "Switch";
        headers[4] = "Action";

        for (i = 0; i < headers.length; i++)
            thCon += thRow.format(headers[i]);

        headers = array_keys(parsedJson.routeTable[0]);
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
                for (i = 0; i < parsedJson.routeTable.length; i++) {
                    var rowId = parsedJson.routeTable[i]['id'];
                    if(parsedJson.routeTable[i]['type'] == "dynamic")
                        tbCon += tdRow.format("D"+parsedJson.routeTable[i]['id']);
                    else
                        tbCon += tdRow.format(parsedJson.routeTable[i]['id']);
                    tbCon += tdRow.format(parsedJson.routeTable[i]['sourceAddress']);
                    tbCon += tdRow.format(parsedJson.routeTable[i]['destinationAddress']);
                    //                    tbCon += tdRow.format(parsedJson.routeTable[i]['switchInfo'].inputPort);
                    //                    tbCon += tdRow.format(parsedJson.routeTable[i]['switchInfo'].dpid.substr(21));
                    var dpid = parsedJson.routeTable[i]['switchInfo'].dpid;
                    var switchId = nodes.filter(function (d) {
                        return (d.dpid === dpid );
                    })[0].id;
                    tbCon += tdRow.format(switchId);
                    //                    tbCon += tdRow.format(parsedJson.routeTable[i]['switchInfo'].outputPort);
                    tbCon += tdRowHide.format(parsedJson.routeTable[i]['switchInfo'].dpid);
                    tbCon += tdRow.format("<input onclick='del(" + rowId + "); return false;' class='deleteButton  ui-button ui-widget ui-state-default ui-corner-all ui-state-focus' type='button' value='Delete'/>");
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
 * Remove a route. In order to do this, this function remove the links that are represented in a table.
 */
function removeAll(){
    waiting(true);
    var table = document.getElementById("jsonTable");
    for ( var i = 1; row = table.rows[i]; i++ ) {
        row = table.rows[i];       
        col = row.cells[0];
        del(col.firstChild.nodeValue);
    //alert(col.firstChild.nodeValue);
    }
    for(var i = table.rows.length - 1; i > 0; i--){
        table.deleteRow(i);
    }
    setTimeout( 'waiting(false)' ,2000);

    //get the routes from OpenNaaS (updated list)
    var jsonNewRoutes = getAllRoutes(type);
    var listRoutes = getRouteList(jsonNewRoutes);
    
    document.getElementById("listRoutes").innerHTML = "";
    for ( var i = 0; i < listRoutes.length; i++){
        document.getElementById("listRoutes").innerHTML += '<a style="text-decoration:none" href="javascript:void(0)" onclick="getSpecificRoute(\''+listRoutes[i].node.split(":")[0]+'\',\''+listRoutes[i].node.split(":")[1]+'\')"><span id="innerTextRoute">Route: '+listRoutes[i].id+'.</span> Source/target: '+listRoutes[i].node+'</span></a><br/>';
    }
}

/**
 * Remove a link given its id.
 * @param {type} id
 * @returns {String|data}
 */
function del(id){
    waiting(true);
    if(id.charAt(0) == "D"){
	id = id.substr(1, id.length);
    }
    var result = "";
    $.ajax({
        type: 'POST',
        url : "deleteRoute/"+id+"?type="+getURLParameter("type"),
        async: false,
        success : function (data) {
            $("#dynamicContent").html(data);
            result = data;                 
        }
    });
    if(document.getElementById("removedOk") === null){
        $("<div id='removedOk' class='success'>Removed correctly.</div>" ).insertAfter( "#header_menu").before("<br>");
        $('.success').next('br').remove();
        setTimeout(function() {
            //$('.success').remove();
            $('.success').slideUp("slow", function() {
                $('.success').remove();
            });
        //$('.success').fadeOut(300, function(){ $(this).remove();});
        }, 3000);
    }
    setTimeout( 'waiting(false)' ,2000);
    return result;
}

/**
 * Remove all the routes loaded in OpenNaaS. It doesn't matter if the routes are represented in a table.
 */
function removeAllRoutes(){
    waiting(true);
    var result = "";
    $.ajax({
        type: 'POST',
        //        url : "deleteAllRoutes?type="+getURLParameter("type"),
        url : "deleteAllRoutes",
        async: false,
        success : function (data) {
            $("#dynamicContent").html(data);
            result = data;                 
        }
    });
    if(document.getElementById("removedOk") === null){
        $("<div id='removedOk' class='success'>Removed correctly.</div>" ).insertAfter( "#header_menu").before("<br>");
        $('.success').next('br').remove();
        setTimeout(function() {
            //$('.success').remove();
            $('.success').slideUp("slow", function() {
                $('.success').remove();
            });
        //$('.success').fadeOut(300, function(){ $(this).remove();});
        }, 3000);
    }
    setTimeout( 'waiting(false)' ,2000);
    document.getElementById("innerTable").innerHTML = "";
    var jsonNewRoutes = getAllRoutes(type);
    var listRoutes = getRouteList(jsonNewRoutes);
    
    document.getElementById("listRoutes").innerHTML = "";
    for ( var i = 0; i < listRoutes.length; i++){
        document.getElementById("listRoutes").innerHTML += '<a style="text-decoration:none" href="javascript:void(0)" onclick="getSpecificRoute(\''+listRoutes[i].node.split(":")[0]+'\',\''+listRoutes[i].node.split(":")[1]+'\')"><span id="innerTextRoute">Route: '+listRoutes[i].id+'.</span> Source/target: '+listRoutes[i].node+'</span></a><br/>';
    }
    return result;
}
/**
 * Create an array of possible routes given the routes created in OpenNaaS.
 * Returns a list with the following format: [ipSrc1:ipDst1, ipSrc2:ipDst2]
 * @param {type} jsonReceived
 * @returns {getRouteList.possibleRoutes|Array}
 */
function getRouteList(jsonReceived){
    var possibleRoutes = [];
    //console.log(jsonReceived.routeTable);
    var listRoutes = jsonReceived.routeTable;
    var listNodes = new Array();

    listRoutes.forEach(function(entry){
        var found = jQuery.inArray(entry.sourceAddress+":"+entry.destinationAddress, listNodes);
        var found2 = jQuery.inArray(entry.destinationAddress+":"+entry.sourceAddress, listNodes);
        if (found == -1 && found2 == -1 && entry.destinationAddress.indexOf("/") == -1) {
            listNodes.push(entry.sourceAddress+":"+entry.destinationAddress);
        }
    });

    //console.log(listNodes);
    var routeObject = new Object();
    var initial = listNodes[0];
    //console.log(listNodes.length);
    for ( var i = 0; i <= listNodes.length -1; i++){
        //getRoute(initial, listNode[i+1]);
        routeObject = new Object();
        routeObject.id = "id"+i;
        routeObject.node = initial;
        possibleRoutes.push(routeObject);
        initial = listNodes[i+1];
        
    }
    return possibleRoutes;
}

var showRoutes = false;
var toggle;
/**
 * Request the list of links of specific route
 * @param {type} src
 * @param {type} dst
 * @returns {undefined}
 */
function getSpecificRoute(src, dst){
    var newToggle = src+"-"+dst;
    if(showRoutes){
        console.log("Hide routes");
        document.getElementById("innerTable").innerHTML = '<table id="jsonTable" class="tablesorter"></table>';
        showRoutes = false;
        if(toggle != null){
            if(toggle == newToggle){
                return;
            }
        }
    }
    
    toggle = src+"-"+dst;
    console.log("Get specific route "+src+" "+dst);
    var result = "";
    $.ajax({
        type: 'GET',
        url : "route/"+src+"/"+dst,
        async: false,
        success : function (data) {
            //                $("#dynamicContent").html(data);
            result = data;
        }
    });
    var json = eval("(" + result + ")");
    //console.log(result);
    var jsonHtmlTable = ConvertJsonToRouteTable(json, 'jsonTable'); 
    document.getElementById("innerTable").innerHTML = '<table id="jsonTable" class="tablesorter"></table>';
    document.getElementById("jsonTable").innerHTML = jsonHtmlTable;
    document.getElementById("innerTable").innerHTML += "</table><input style='margin-right: 11.5px' class='addRouteButton ui-button ui-widget ui-state-default ui-corner-all' onClick='removeAll()' type='button' value='Remove this route' name='Clean table'/>";
    showRoutes = true;

    highlightDynamicRoutes();
    showGraphicRoute(src, dst, json);
}

/**
 * Request to OpenNaaS in order to obtain the list of routes given the IP version in the following format (Ipv4/IPv6)
 * @param {type} type
 * @returns {unresolved}
 */
function getAllRoutes(type){
    var result = "";
    $.ajax({
        type: 'GET',
        url : "routeAll?type="+type,
        async: false,
        success : function (data) {
            //                $("#dynamicContent").html(data);
            result = data;
        }
    });
    return eval("(" + result + ")");
}

function waiting(status){
    $body = $("body");
    if(status)
        $body.addClass("loading");
    else
        $body.removeClass("loading");
}

function highlightDynamicRoutes(){
    if(undefined == routeRowColor || routeRowColor == null)
        routeRowColor = "#81DAF5";
    var patt = new RegExp("D[0-9]")
    var table = document.getElementById('jsonTable');
    if ( table.getElementsByTagName('tr').length > 1 ){
        var tbody = table.getElementsByTagName('tbody')[0];
        var items = tbody.getElementsByTagName('tr');
        var tds = null;

        for (var j = 0; j < items.length; j++) {
            tds = items[j].getElementsByTagName('td');
            for (var i = 0; i < tds.length-1; i++) {
                if( tds[i].innerHTML.match(patt))
                    table.getElementsByTagName('tr')[j+1].style.background = routeRowColor;
            }
        }
    }
}
