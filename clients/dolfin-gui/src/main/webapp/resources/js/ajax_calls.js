
//ajax calls
var map = {};
map["getCircuitSwitches"] = {"path": "getCircuitSwitches", "params": ""};
map["getAllocatedFlows"] = {"path": "getAllocatedFlows", "params": ""};
map["getStatistic"] = {"path": "getStatistic", "params": ""};
map["getCircuits"] = {"path": "getCircuits", "params": ""};

function ajax_Request(objectRequest, method, others) {
    var url = createUrl(objectRequest.path, objectRequest.params );
    var response;
    if(method === "GET"){
        response = ajax_GET(url);
    } else if(method === "POST"){
        response = ajax_POST(url, others);
    } else if(method === "PUT"){
        response = ajax_PUT(url, others);
    }
   return response;
}

function ajax_GET(url){
    var response;
    $.ajax({
        type: "GET",
        async: false,
        url: url,
        success: function(data) {
            response = data;
        }
    });
    return response;
}
function ajax_POST(url, data){
    var response;
    $.ajax({
        type: "POST",
        async: false,
        data: data,
        url: url,
        success: function(data) {
            response = data;
        }
    });
    return response;
}
function ajax_PUT(url, data){
    var response;
    $.ajax({
        type: "PUT",
        async: false,
        data: data,
        url: url,
        success: function(data) {
            response = data;
        }
    });
    return response;
}

function createUrl(path, params){
    var url = "ajax/"+path;
    params.forEach(function(entry){
        url = url + "/"+entry;
    });
    return url;
}