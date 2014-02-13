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
                    tbCon += tdRow.format(parsedJson.routeTable[i]['id']);
                    tbCon += tdRow.format(parsedJson.routeTable[i]['sourceAddress']);
                    tbCon += tdRow.format(parsedJson.routeTable[i]['destinationAddress']);
//                    tbCon += tdRow.format(parsedJson.routeTable[i]['switchInfo'].inputPort);
//                    tbCon += tdRow.format(parsedJson.routeTable[i]['switchInfo'].dpid.substr(21));
                    var dpid = parsedJson.routeTable[i]['switchInfo'].dpid;
                    var switchId = nodes.filter(function (d) {return (d.dpid === dpid ); })[0].id;
                    tbCon += tdRow.format(switchId);
//                    tbCon += tdRow.format(parsedJson.routeTable[i]['switchInfo'].outputPort);
                    tbCon += tdRowHide.format(parsedJson.routeTable[i]['switchInfo'].dpid);
                    tbCon += tdRow.format("<input onclick='del(" + rowId + "); return false;' class='deleteButton' type='button' value='Delete'/>");
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
}

function del(id){
    waiting(true);
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
            $('.success').slideUp("slow", function() { $('.success').remove();});
            //$('.success').fadeOut(300, function(){ $(this).remove();});
        }, 3000);
    }
    setTimeout( 'waiting(false)' ,2000);
    return result;
}

function waiting(status){
    $body = $("body");
    if(status)
        $body.addClass("loading");
    else
        $body.removeClass("loading");
}

function getRouteList(jsonReceived){
    var possibleRoutes = [];
console.log(jsonReceived.routeTable);
    var listRoutes = jsonReceived.routeTable;
    var listNodes = new Array();

    listRoutes.forEach(function(entry){
        var found = jQuery.inArray(entry.destinationAddress, listNodes);
        if (found == -1 && entry.destinationAddress.indexOf("/") == -1) {
            listNodes.push(entry.destinationAddress);
        }
    });

console.log(listNodes);
    var routeObject = new Object();
    var initial = listNodes[0];
    console.log(listNodes.length);
    for ( var i = 0; i <= listNodes.length -1; i++){
    //getRoute(initial, listNode[i+1]);
        routeObject = new Object();
        routeObject.id = "id"+i;
        console.log(initial);
        routeObject.node = initial;
        possibleRoutes.push(routeObject);
        initial = listNodes[i+1];
        
    }
    return possibleRoutes;
}
function getSpecificRoute(src, dst){
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
    document.getElementById("jsonTable").innerHTML = jsonHtmlTable;
}