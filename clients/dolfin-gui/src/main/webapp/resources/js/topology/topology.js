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

var dataTopology = readTextFile("/ofertie/resources/js/topology/topo.json");
//dataTopology = '{"nodes":[{id:0,name:"SW1",type:"switch",controller:"Ctrl1",fixed:true,x:50,y:10,dpid:"00:00:00:00:00:00:00:01"},{id:1,name "SW2",type:"switch",controller:"Ctrl1",fixed:true,x:30,y:200,dpid:"00:00:00:00:00:00:00:02"},{id:2,name "SW3",type:"switch",controller:"Ctrl1",fixed:true,x:160,y:200,dpid:"00:00:00:00:00:00:00:03"},{id:3,name "h1",type:"host",fixed:true,x:10,y:10,ip:"192.168.1.1",SW:"SW1",port:3},{id:4,name "h2",type:"host",fixed:true,x:10,y:200,ip:"192.168.2.51",SW:"SW8",port:2},{id:5,name "h3",type:"host",fixed:true,x:400,y:10,ip:"192.168.3.3",SW:"SW1",port:4},{id:6,name "h4",type:"host",fixed:true,x:400,y:200,ip:"192.168.4.4",SW:"SW4",port:4}],"links":[{id:"path01",source:0,target:1,srcPort:1,dstPort:1,type:"static"},{id:"path02",source:0,target:2,srcPort:2,dstPort:1,type:"static"},{id:"path12",source:1,target:2,srcPort:2,dstPort:2,type:"static"},{id:"path23",source:2,target:3,srcPort:3,dstPort:1,type:"static"},{id:"path34",source:3,target:4,srcPort:2,dstPort:1,type:"static"},{id:"path35",source:3,target:5,srcPort:3,dstPort:1,type:"static"},{id:"path45",source:4,target:5,srcPort:3,dstPort:2,type:"static"},{id:"path56",source:5,target:6,srcPort:3,dstPort:1,type:"static"},{id:"path67",source:6,target:7,srcPort:2,dstPort:1,type:"static"},{id:"path80",source:8,target:0,srcPort:3,dstPort:3,type:"static"},{id:"path97",source:9,target:7,srcPort:2,dstPort:2,type:"static"},{id:"path100",source:10,target:0,srcPort:4,dstPort:4,type:"static"},{id:"path113",source:11,target:3,srcPort:4,dstPort:4,type:"static"},{id:"path124",source:12,target:4,srcPort:3,dstPort:3,type:"static"},{id:"path134",source:13,target:4,srcPort:4,dstPort:4,type:"static"},{id:"path146",source:14,target:6,srcPort:3,dstPort:3,type:"static"}],"controllers":[{id:0,name "Ctrl1",type:"controller",controller:"controllersVM:8191",fixed:true,x:sw_x*2,y:10},{id:1,name "Ctrl2",type:"controller",controller:"controllersVM:8192",fixed:true,x:sw_x*3+50,y:150},{id:2,name "Ctrl3",type:"controller",controller:"controllersVM2:8193",fixed:true,x:sw_x*5+60,y:10}],"controllersLinks":[{id:"00",source:0,target:0,type:"static"},{id:"10",source:1,target:0,type:"static"},{id:"20",source:2,target:0,type:"static"},{id:"31",source:3,target:1,type:"static"},{id:"41",source:4,target:1,type:"static"},{id:"51",source:5,target:1,type:"static"},{id:"62",source:6,target:2,type:"static"},{id:"72",source:7,target:2,type:"static"}],"cloud":[{id:0,name "cloud",type:"cloud",fixed:true,x:330,y:15}],"cloudLinks":[{id:0,source:0,target:0,type:"static"},{id:1,source:0,target:1,type:"static"},{id:2,source:0,target:2,type:"static"}]}';
dataTopology = eval("(" +dataTopology+ ")");
console.log(dataTopology);
if( dataTopology ){
    console.log("Create topology vars");
    createJSVars(dataTopology);
}
var nodes, links, controllers, controllersLinks, cloud, cloudLinks;
    
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
    cloud = dataTopology.cloud;
    for (i= 0; i < dataTopology.cloudLinks.length; i++){
        dataTopology.cloudLinks[i].source = cloud[dataTopology.cloudLinks[i].source];
        dataTopology.cloudLinks[i].target = controllers[dataTopology.cloudLinks[i].target];
    }
    cloudLinks = dataTopology.cloudLinks;
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
