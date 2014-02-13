/*
 Topology definition. Nodes and links. Images path.
 */

var sw_x = 90, sw_y = 80;
var node_x = 30, node_y = 200;

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

var dataTopology = readTextFile("/nfv-gui-demo-vrf/resources/js/topology/topo.json");
dataTopology = eval("(" +dataTopology+ ')');
if( dataTopology ){
    createTopology(dataTopology);
}
var nodes, links, controllers, controllersLinks;
    
function createTopology(dataTopology){
    createJSVars(dataTopology);
}

function createJSVars(dataTopology){
    nodes = dataTopology.nodes;
    //            console.log(dataTopology);
    for (i= 0; i<dataTopology.links.length; i++){
        dataTopology.links[i].source = nodes[dataTopology.links[i].source];
        dataTopology.links[i].target = nodes[dataTopology.links[i].target];
    }
    links = dataTopology.links;
    controllers = dataTopology.controllers;
    for (i= 0; i<dataTopology.controllersLinks.length; i++){
        dataTopology.controllersLinks[i].source = nodes[dataTopology.controllersLinks[i].source];
        dataTopology.controllersLinks[i].target = controllers[dataTopology.controllersLinks[i].target];
    }
    controllersLinks = dataTopology.controllersLinks;
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
        f[links[i].source.id_num][links[i].target.id_num] = 1;
        f[links[i].target.id_num][links[i].source.id_num] = 1;
    }
    return f;
}

function getNonZeroRandomNumber(){
    var random = Math.floor(Math.random()*80) - 60;
    if(random==0) return getNonZeroRandomNumber();
    return random;
}