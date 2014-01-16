/*
 Topology definition. Nodes and links. Images path.
 */

var switchImage = "/nfv-gui-demo-vrf/resources/images/topology/switch.png";
var hostImage = "/nfv-gui-demo-vrf/resources/images/topology/host.png";
var controllerImage = "/nfv-gui-demo-vrf/resources/images/topology/controller.png";
var packetImage = "/nfv-gui-demo-vrf/resources/images/topology/movie_tape.gif";
var linkImage = "/nfv-gui-demo-vrf/resources/images/topology/link.png";

var nodes = [
    {id_num: 0, id: "SW1", type: "switch", controller: "controllersVM:8191", reflexive: false, fixed: true, x: 150, y: 160, dpid: "00:00:00:00:00:00:00:01"},
    {id_num: 1, id: "SW2", type: "switch", controller: "controllersVM:8192", reflexive: true, fixed: true, x: 450, y: 160, dpid: "00:00:00:00:00:00:00:02"},
    {id_num: 2, id: "SW3", type: "switch", controller: "controllersVM2:8193", reflexive: false, fixed: true, x: 300, y: 60, dpid: "00:00:00:00:00:00:00:03"},
    {id_num: 3, id: "SW4", type: "switch", controller: "controllersVM2:8194", reflexive: false, fixed: true, x: 430, y: 340, dpid: "00:00:00:00:00:00:00:04"},
    {id_num: 4, id: "SW5", type: "switch", controller: "controllersVM2:8195", reflexive: false, fixed: true, x: 170, y: 340, dpid: "00:00:00:00:00:00:00:05"},
    {id_num: 5, id: "h1", type: "host", reflexive: false, fixed: true, x: 50, y: 100, ip: "192.168.1.1", SW: "SW1", port: 5},
    {id_num: 6, id: "h2", type: "host", reflexive: false, fixed: true, x: 550, y: 300, ip: "192.168.2.51", SW: "SW2", port: 5}
],
        lastNodeId = 2,
links = [
    {id: "path01", source: nodes[0], target: nodes[1], left: false, right: false, srcPort: 1, dstPort: 1, type: "static"},
    {id: "path02", source: nodes[0], target: nodes[2], left: false, right: false, srcPort: 2, dstPort: 1, type: "static"},
    {id: "path03", source: nodes[0], target: nodes[3], left: false, right: false, srcPort: 3, dstPort: 1, type: "static"},
    {id: "path04", source: nodes[0], target: nodes[4], left: false, right: false, srcPort: 4, dstPort: 1, type: "static"},
    {id: "path12", source: nodes[1], target: nodes[2], left: false, right: false, srcPort: 2, dstPort: 2, type: "static"},
    {id: "path13", source: nodes[1], target: nodes[3], left: false, right: false, srcPort: 3, dstPort: 2, type: "static"},
    {id: "path14", source: nodes[1], target: nodes[4], left: false, right: false, srcPort: 4, dstPort: 2, type: "static"},
    {id: "path23", source: nodes[2], target: nodes[3], left: false, right: false, srcPort: 3, dstPort: 3, type: "static"},
    {id: "path24", source: nodes[2], target: nodes[4], left: false, right: false, srcPort: 4, dstPort: 3, type: "static"},
    {id: "path34", source: nodes[3], target: nodes[4], left: false, right: false, srcPort: 4, dstPort: 4, type: "static"},
    {id: "path50", source: nodes[5], target: nodes[0], left: false, right: false, srcPort: 5, dstPort: 5, type: "static"},
    {id: "path61", source: nodes[6], target: nodes[1], left: false, right: false, srcPort: 5, dstPort: 5, type: "static"}
];

/**
 * Given the number of nodes and the connection between them, create an adjacent matrix used by the dijkstra algorithm.
 * @returns {Array}
 */
function createAdjacencyMatrix(){
    var f = new Array();
    for (i = 0;i < nodes.length; i++) {
        f[i]=new Array();
        for (j = 0; j < nodes.length; j++) {
            f[i][j]= Infinity;
        }
    }
    for (i = 0; i < links.length; i++){
        f[links[i].source.id_num][links[i].target.id_num] = 1;
        f[links[i].target.id_num][links[i].source.id_num] = 1;
    }
    return f;
}