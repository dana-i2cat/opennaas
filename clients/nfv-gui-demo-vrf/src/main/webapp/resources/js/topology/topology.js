/*
 Topology definition. Nodes and links. Images path.
 */

var sw_x = 90, sw_y = 80;
var node_x = 30, node_y = 200;

var nodes = [
    {id_num: 0, id: "SW1", type: "switch", controller: "Ctrl1", reflexive: false, fixed: true, x: sw_x, y: sw_y, dpid: "00:00:00:00:00:00:00:01"},
    {id_num: 1, id: "SW2", type: "switch", controller: "Ctrl1", reflexive: true, fixed: true, x: sw_x*2, y: sw_y*2, dpid: "00:00:00:00:00:00:00:02"},
    {id_num: 2, id: "SW3", type: "switch", controller: "Ctrl1", reflexive: false, fixed: true, x: sw_x*3, y: sw_y, dpid: "00:00:00:00:00:00:00:03"},
    {id_num: 3, id: "SW4", type: "switch", controller: "Ctrl2", reflexive: false, fixed: true, x: sw_x*2+50 , y: sw_y*3+50, dpid: "00:00:00:00:00:00:00:04"},
    {id_num: 4, id: "SW5", type: "switch", controller: "Ctrl2", reflexive: false, fixed: true, x: sw_x*3+50, y: sw_y*2+50, dpid: "00:00:00:00:00:00:00:05"},
    {id_num: 5, id: "SW6", type: "switch", controller: "Ctrl2", reflexive: false, fixed: true, x: sw_x*4+50, y: sw_y*3+50, dpid: "00:00:00:00:00:00:00:06"},
    {id_num: 6, id: "SW7", type: "switch", controller: "Ctrl3", reflexive: false, fixed: true, x: sw_x*5, y: sw_y, dpid: "00:00:00:00:00:00:00:07"},
    {id_num: 7, id: "SW8", type: "switch", controller: "Ctrl3", reflexive: false, fixed: true, x: sw_x*6+30, y: sw_y, dpid: "00:00:00:00:00:00:00:08"},

    {id_num: 8, id: "h1", type: "host", reflexive: false, fixed: true, x: node_x, y: node_y, ip: "192.168.1.1", SW: "SW1", port: 3},
    {id_num: 9, id: "h2", type: "host", reflexive: false, fixed: true, x: node_x*19+10, y: node_y, ip: "192.168.2.51", SW: "SW8", port: 2},

    {id_num: 10, id: "h3", type: "host", reflexive: false, fixed: true, x: node_x*3+10, y: node_y, ip: "192.168.3.3", SW: "SW1", port: 4},
    {id_num: 11, id: "h4", type: "host", reflexive: false, fixed: true, x: node_x*6+20, y: node_y+180, ip: "192.168.4.4", SW: "SW4", port: 4},
    {id_num: 12, id: "h5", type: "host", reflexive: false, fixed: true, x: node_x*9+20, y: node_y+180, ip: "192.168.5.5", SW: "SW5", port: 3},
    {id_num: 13, id: "h6", type: "host", reflexive: false, fixed: true, x: node_x*12+10, y: node_y+180, ip: "192.168.6.6", SW: "SW5", port: 4},
    {id_num: 14, id: "h7", type: "host", reflexive: false, fixed: true, x: node_x*16, y: node_y, ip: "192.168.7.7", SW: "SW7", port: 3}],
lastNodeId = 2,
vertexNum = nodes.length,
links = [
    {id: "path01", source: nodes[0], target: nodes[1], srcPort: 1, dstPort: 1, type: "static"},
    {id: "path02", source: nodes[0], target: nodes[2], srcPort: 2, dstPort: 1, type: "static"},
    {id: "path12", source: nodes[1], target: nodes[2], srcPort: 2, dstPort: 2, type: "static"},
    {id: "path23", source: nodes[2], target: nodes[3], srcPort: 3, dstPort: 1, type: "static"},
    {id: "path34", source: nodes[3], target: nodes[4], srcPort: 2, dstPort: 1, type: "static"},
    {id: "path35", source: nodes[3], target: nodes[5], srcPort: 3, dstPort: 1, type: "static"},
    {id: "path45", source: nodes[4], target: nodes[5], srcPort: 3, dstPort: 2, type: "static"},
    {id: "path56", source: nodes[5], target: nodes[6], srcPort: 3, dstPort: 1, type: "static"},
    {id: "path67", source: nodes[6], target: nodes[7], srcPort: 2, dstPort: 1, type: "static"},
    {id: "path80", source: nodes[8], target: nodes[0], srcPort: 3, dstPort: 3, type: "static"},
    {id: "path97", source: nodes[9], target: nodes[7], srcPort: 2, dstPort: 2, type: "static"},
    {id: "path100", source: nodes[10], target: nodes[0], srcPort: nodes[10].port, dstPort: nodes[10].port, type: "static"},
    {id: "path113", source: nodes[11], target: nodes[3], srcPort: nodes[11].port, dstPort: nodes[11].port, type: "static"},
    {id: "path124", source: nodes[12], target: nodes[4], srcPort: nodes[12].port, dstPort: nodes[12].port, type: "static"},
    {id: "path134", source: nodes[13], target: nodes[4], srcPort: nodes[13].port, dstPort: nodes[13].port, type: "static"},
    {id: "path146", source: nodes[14], target: nodes[6], srcPort: nodes[14].port, dstPort: nodes[14].port, type: "static"}],
controllers = [
    {id_num: 0, id: "Ctrl1", type: "controller", controller: "controllersVM:8191", reflexive: false, fixed: true, x: sw_x*2, y: 10},
    {id_num: 1, id: "Ctrl2", type: "controller", controller: "controllersVM:8192", reflexive: false, fixed: true, x: sw_x*3+50, y: 150},
    {id_num: 2, id: "Ctrl3", type: "controller", controller: "controllersVM2:8193", reflexive: false, fixed: true, x: sw_x*5+60, y: 10}],
controllersLinks = [
    {id: "00", source: nodes[0], target: controllers[0], type: "static"},
    {id: "10", source: nodes[1], target: controllers[0], type: "static"},
    {id: "20", source: nodes[2], target: controllers[0], type: "static"},
    {id: "31", source: nodes[3], target: controllers[1], type: "static"},
    {id: "41", source: nodes[4], target: controllers[1], type: "static"},
    {id: "51", source: nodes[5], target: controllers[1], type: "static"},
    {id: "62", source: nodes[6], target: controllers[2], type: "static"},
    {id: "72", source: nodes[7], target: controllers[2], type: "static"}
];

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