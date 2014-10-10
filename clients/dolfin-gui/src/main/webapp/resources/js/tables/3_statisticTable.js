function ConvertJsonToStatisticTable(parsedJson, tableId, tableClassName) {
    
     waiting(true);
//showHidePreloader(true);    
     //Pattern for table                          
    var idMarkup = tableId ? ' id="' + tableId + '"' : '';
    var classMarkup = tableClassName ? ' class="' + tableClassName + '"' : '';
    var tbl = '<table border="1" cellpadding="1" cellspacing="1"' + idMarkup + classMarkup + '>{0}{1}</table>';

    //Patterns for table content
    var th = '<thead>{0}</thead>';
    var tb = '<tbody>{0}</tbody>';
    var tr = '<tr>{0}</tr>';
    var thRow = '<th>{0}</th>';
    var tdRow = '<td>{0}</td>';
    var thCon = '';
    var tbCon = '';
    var trCon = '';
console.log(parsedJson);
    if (parsedJson) {
        var isStringArray = typeof (parsedJson[0]) === 'string';
        var headers;

        headers = array_keys(parsedJson[0]);
//        headers[0] = "Name";
        headers[0] = "Switch Id";
        headers[1] = "Port Id";
        headers[2] = "Throughput";
        headers[3] = "Packet Loss";

        for (i = 0; i < headers.length; i++)
            thCon += thRow.format(headers[i]);

        var arr_size;
        
        try{    
//            headers = array_keys(parsedJson.TimedPortStatistics.TimedStatistics[0]);
            arr_size = parsedJson.TimedPortStatistics.TimedStatistics.length;
        }catch (e){
            headers = [];
            arr_size = 0;
        }
        th = th.format(tr.format(thCon));

        // Create table rows from Json data
        if (isStringArray) {
/*            for (i = 0; i < parsedJson.length; i++) {
                tbCon += tdRow.format(parsedJson[i]);
                trCon += tr.format(tbCon);
                tbCon = '';
            }
*/
        } else {
            if (headers) {
                for (i = 0; i < arr_size; i++) {
                    tbCon += tdRow.format(parsedJson.TimedPortStatistics.TimedStatistics[i].switchId);
                    tbCon += tdRow.format(parsedJson.TimedPortStatistics.TimedStatistics[i].portId);
                    tbCon += tdRow.format(parsedJson.TimedPortStatistics.TimedStatistics[i].throughput);
                    tbCon += tdRow.format(parsedJson.TimedPortStatistics.TimedStatistics[i].packetLoss);
                    trCon += tr.format(tbCon);
                    tbCon = '';
                }
            }
        }
        tb = tb.format(trCon);
        tbl = tbl.format(th, tb);
setTimeout( 'waiting(false)', 1000);
//showHidePreloader(false);
        return tbl;
    }
    return null;
}

function ConvertJsonToCircuitStatisticTable(parsedJson, tableId, tableClassName) {
    
     waiting(true);
//showHidePreloader(true);    
     //Pattern for table                          
    var idMarkup = tableId ? ' id="' + tableId + '"' : '';
    var classMarkup = tableClassName ? ' class="' + tableClassName + '"' : '';
    var tbl = '<table border="1" cellpadding="1" cellspacing="1"' + idMarkup + classMarkup + '>{0}{1}</table>';

    //Patterns for table content
    var th = '<thead>{0}</thead>';
    var tb = '<tbody>{0}</tbody>';
    var tr = '<tr>{0}</tr>';
    var thRow = '<th>{0}</th>';
    var tdRow = '<td>{0}</td>';
    var thCon = '';
    var tbCon = '';
    var trCon = '';
console.log(parsedJson);
    if (parsedJson) {
        var isStringArray = typeof (parsedJson[0]) === 'string';
        var headers;
        //slaFlowId", "throughput", "packetLoss", "delay", "jitter", "flowData"];

//        headers = array_keys(parsedJson[0]);
        headers = [];
        headers[0] = "SLA Flow Id";
        headers[1] = "Throughput";
        headers[2] = "Packet Loss";
        headers[3] = "Delay";
        headers[4] = "Jitter";
        headers[5] = "Flow Data";

        for (i = 0; i < headers.length; i++)
            thCon += thRow.format(headers[i]);

        var arr_size;
        try{    
//            headers = array_keys(parsedJson.floodlightOFFlows.floodlightOFFlow[0]);
            arr_size = parsedJson.length;
        }catch (e){
            headers = [];
            arr_size = 0;
        }
        th = th.format(tr.format(thCon));

        // Create table rows from Json data
        if (isStringArray) {
/*            for (i = 0; i < parsedJson.length; i++) {
                tbCon += tdRow.format(parsedJson[i]);
                trCon += tr.format(tbCon);
                tbCon = '';
            }
*/
        } else {
            if (headers) {
                console.log();
                for (i = 0; i < arr_size; i++) {
                    tbCon += tdRow.format(parsedJson[i].slaFlowId);
                    tbCon += tdRow.format(parsedJson[i].throughput);
                    tbCon += tdRow.format(parsedJson[i].packetLoss);
                    tbCon += tdRow.format(parsedJson[i].delay);
                    tbCon += tdRow.format(parsedJson[i].jitter);
                    tbCon += tdRow.format(parsedJson[i].flowData);
                    trCon += tr.format(tbCon);
                    tbCon = '';
                }
            }
        }
        tb = tb.format(trCon);
        tbl = tbl.format(th, tb);
setTimeout( 'waiting(false)', 1000);
//showHidePreloader(false);
        return tbl;
    }
    return null;
}

function getSwitchStatistic(switchId){
    console.log("Get Statistic");
    $.ajax({
        type: "GET",
        url: "ajax/portStatistics/"+switchId,
        success: function(data) {
            $('#ajaxUpdate').html(data);    
            var json = convertXml2JSon(data);
            data = "";
            json = eval("("+json+")");
            console.log(json);
            var jsonHtmlTable = ConvertJsonToStatisticTable(json, 'jsonStatisticTable', null);
            console.log(jsonHtmlTable);
            document.getElementById("jsonStatisticTable").innerHTML = jsonHtmlTable;
        }
    });
}

function getPortStatistic(switchId, portName){
    console.log("Get Statistic");

    $.ajax({
        type: "GET",
        url: "ajax/portStatistics/"+switchId,
        success: function(data) {
            $('#ajaxUpdate').html(data);    
            var json = convertXml2JSon(data);
            data = "";                        
            json = eval("("+json+")");
            console.log(json);
            var json = jsonStatisticsGivenPort(json.TimedPortStatistics, portName);
            var jsonHtmlTable = ConvertJsonToStatisticTable(json, 'jsonStatisticTable', null);
            console.log(jsonHtmlTable);
            document.getElementById("jsonStatisticTable").innerHTML = jsonHtmlTable;
        }
    });
}

function jsonStatisticsGivenPort(json, portName){
    var TimedPortStatistics = new Object;
    var TimedStatistics = [];
    console.log(json);
    console.log(json.TimedStatistics);
    for(i=0; i<json.TimedStatistics.length; i++){
        console.log(json.TimedStatistics[i].portId);
        if(json.TimedStatistics[i].portId === portName){
            TimedStatistics.push(json.TimedStatistics[i]);
        }
    }
    var newJson = new Object;
    TimedPortStatistics.TimedStatistics = TimedStatistics;
    newJson.TimedPortStatistics = TimedPortStatistics;
    console.log(newJson);
    return newJson;
}

function getCircuitStatistic(){
    console.log("Get Circuit Statistic");

    $.ajax({
        type: "GET",
        url: "ajax/circuitStatistics",
        success: function(data) {
            console.log(data);
            json = csvJSON(data);
//            json = eval("("+json+")");
//            $('#ajaxUpdate').html(data);
            data = "";
            var jsonHtmlTable = ConvertJsonToCircuitStatisticTable(json, 'jsonCircuitStatisticTable', null);
            console.log(jsonHtmlTable);
            document.getElementById("jsonCircuitStatisticTable").innerHTML = jsonHtmlTable;
//            
//            var jsonHtmlTable = ConvertJsonToStatisticTable(json, 'jsonStatisticTable', null);
//            console.log(jsonHtmlTable);
//            document.getElementById("jsonFlowTable").innerHTML = jsonHtmlTable;
        }
    });
}

function csvJSON(csv){
    var lines=csv.split("\n");
    var result = [];
//    var headers=lines[0].split(",");
    var headers = ["timestamp", "slaFlowId", "throughput", "packetLoss", "delay", "jitter", "flowData"];
    for(var i=0;i<lines.length;i++){
        var obj = {};
        var currentline=lines[i].split(",");
        for(var j=0;j<headers.length;j++){
            obj[headers[j]] = currentline[j];
        }
        result.push(obj);
    }
    console.log(result);
    console.log(JSON.stringify(result));
  return result; //JavaScript object
  //return JSON.stringify(result); //JSON
}