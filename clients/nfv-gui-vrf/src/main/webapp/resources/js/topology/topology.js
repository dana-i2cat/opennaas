/*
 Topology definition. Nodes and links. Images path.
 */
console.log("Loading topology.js");
var sw_x = 40, sw_y = 160;
var node_x = 30, node_y = 300;
//console.log(sessvars);
//console.log("Sess vars: "+sessvars.nodes[2].x);
if(sessvars.nodes){
    nodes = sessvars.nodes;
    links = sessvars.links;
}
/*
console.log(nodes);
console.log("Pos x, node 0, h2: "+nodes[2].x);
*/
if(nodes === undefined){
    var nodes, links, controllers, controllersLinks, cloud, cloudLinks;
     console.log("Executin...");
    var dataTopology = readTextFile("/nfv-gui-vrf/resources/js/topology/topology.json");
    dataTopology = eval("(" +dataTopology+ ")");
    if( dataTopology ){
        console.log("Create topology vars");
        createJSVars(dataTopology);
        updateTopology(nodes, links);
    }
}
    
function createJSVars(dataTopology){
    console.log("Creating JSVars taking into account the json file");
    nodes = dataTopology.nodes;
    //            console.log(dataTopology);
    for (i= 0; i<dataTopology.links.length; i++){
        dataTopology.links[i].source = nodes[dataTopology.links[i].source];
        dataTopology.links[i].target = nodes[dataTopology.links[i].target];
    }
    links = dataTopology.links;
    /*
    controllers = dataTopology.controllers;
    if(controllers === undefined){
        //insert default controller without data
        var ctrl = new Object;
        ctrl.id = 0;
        ctrl.name = "Ctrl1";
        ctrl.type = "controller";
        ctrl.controller = "";
        ctrl.ctrlType = "fdl";
        ctrl.fixed = true;
        ctrl.x = 140;
        ctrl.y = 20;
        controllers = [];
        controllers.push(ctrl);
    }
    else{
        for (i= 0;i<dataTopology.controllersLinks.length; i++){
            dataTopology.controllersLinks[i].source = nodes[dataTopology.controllersLinks[i].source];
            dataTopology.controllersLinks[i].target = controllers[dataTopology.controllersLinks[i].target];
        }
    }
    console.log(controllers);
    console.log(links);
    console.log(nodes);
    /*
    controllersLinks = dataTopology.controllersLinks;
    if( controllersLinks === undefined){
        controllersLinks = [];
        j = 0;
        for(i= 0;i<nodes.length; i++){
            if( nodes[i].type === "switch" ){
                var ctrlLink = new Object;
                ctrlLink.id = nodes[i].id+""+0;
                ctrlLink.source = nodes[i];
                ctrlLink.target = 0;
                ctrlLink.type = "static";
                controllersLinks[j] = ctrlLink;
                j++;
            }
        }
    }
    cloud = dataTopology.cloud;
    if(cloud === undefined){
        //insert default controller without data
        var cl = new Object;
        cl.id = 0;
        cl.name = "cloud";
        cl.type = "cloud";
        cl.fixed = true;
        cl.x = 270;
        cl.y = 17;
        cloud = [];
        cloud.push(cl);
    }else{
        for (i= 0; i < dataTopology.cloudLinks.length; i++){
            dataTopology.cloudLinks[i].source = cloud[dataTopology.cloudLinks[i].source];
            dataTopology.cloudLinks[i].target = controllers[dataTopology.cloudLinks[i].target];
        }
    }
    cloudLinks = dataTopology.cloudLinks;
    if( cloudLinks === undefined){
        cloudLinks = [];
        var clLink = new Object;
        clLink.id = 0;
        clLink.source = 0;
        clLink.target = 0;
        clLink.type = "static";
        cloudLinks[0] = clLink;
    }
    */
//    domains = dataTopology.domains;  
}
var lastNodeId = 2,
    vertexNum = nodes.length;

function createAdjacencyMatrix() {
    var f = new Array();
    for (i = 0; i < nodes.length; i++) {
        f[i] = new Array();
        for (j = 0; j < nodes.length; j++) {
            f[i][j] = Infinity;
        }
    }
    for (i = 0; i < links.length; i++) {
        f[links[i].source.id][links[i].target.id] = 1;
        f[links[i].target.id][links[i].source.id] = 1;
    }
    return f;
}

function getNonZeroRandomNumber(){
    var random = Math.floor(Math.random()*80) - 60;
    if(random == 0) return getNonZeroRandomNumber();
    return random;
}

function readTextFile(url) {
    if (window.XMLHttpRequest) {              
        AJAX=new XMLHttpRequest();              
    } else {                                  
        AJAX=new ActiveXObject("Microsoft.XMLHTTP");
    }
    if (AJAX) {
        AJAX.open("GET", url, false);                             
        AJAX.send(null);
        return AJAX.responseText;                                         
    } else {
        return false;
    }                                             
}