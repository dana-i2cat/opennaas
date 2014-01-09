/*
 * Animation page. Show the topology and the Routing Table.
 *
 */
var file = 'animation';//this javascript file corresponds to a animation page
var counterStream = 0;//counter used in the animation of video streaming
// line displayed when dragging new nodes
var drag_line = svg.append('svg:path')
    .attr('class', 'link2 dragline hidden')
    .attr('d', 'M0,0L0,0');

// handles to link and node element groups
var path = svg.append('svg:g').selectAll('path'),
    circle = svg.append('svg:g').selectAll('g'),
    packet = svg.selectAll('.packet').data([0]);

packet.enter().append('image')
        .attr('class', 'packet')
        .attr('x', '0')
        .attr('y', '110')
        .attr('width', '30')
        .attr('height', '30')
        .attr('xlink:href', packetImage);

// update graph (called when needed)
function restart() {
    // path (link) group
    path = path.data(links);

    // update existing links
    path.classed('selected', function (d) {return d === selected_link;})
    .style('marker-end', function (d) {return d.right ? 'url(#end-arrow)' : '';});

    // add new links
    path.enter().append('svg:path')
        .attr('class', function (d) {
            if (d.type === 'static') {
                return 'link';
            } else {
                return 'link2';
            }
        })
        .classed('selected', function (d) {return d === selected_link;})
        .style('marker-start', function (d) {return d.left ? 'url(#start-arrow)' : '';})
        .style('marker-end', function (d) {return d.right ? 'url(#end-arrow)' : '';})
        .on('mousedown', function (d) {
            if (d3.event.ctrlKey) return;
            // select link
            mousedown_link = d;
console.log('Adding new link. Click on Link ' + d.type);
            if (mousedown_link === selected_link) selected_link = null;
            else selected_link = mousedown_link;
            selected_node = null;
            restart();
        });

    // remove old links
    path.exit().remove();

    // circle (node) group
    // NB: the function arg is crucial here! nodes are known by id, not by index!
    circle = circle.data(nodes, function (d) {return d.id;});

    // update existing nodes (reflexive & selected visual states)
    circle.selectAll('circle')
        .style('fill', function (d) {
            return (d === selected_node) ? d3.rgb(colors(d.id)).brighter().toString() : colors(d.id);
        })
        .classed('reflexive', function (d) {return d.reflexive;});

    // add new nodes
    var g = circle.enter().append('svg:g');

    g.append('svg:image')
        .attr('class', function (d) {return d.type;})
        .attr('x', '-40')
        .attr('y', '-40')
        .attr('width', '75')
        .attr('height', '75')
        .attr('xlink:href', function (d) {
            if (d.type === 'switch')
                return switchImage;
            else
                return hostImage;
        })
        .on('mouseout', function (d) {
            if (!mousedown_node || d === mousedown_node) return;
            // unenlarge target node
            d3.select(this).attr('transform', '');
        });
        
    // show node IDs
    g.append('svg:text')
        .attr('x', '-20')
        .attr('y', function (d) {
            if (d.type === 'switch') {
                return '9';
            } else {
                return '30';
            }
        })
        .attr('class', function (d) {
            if (d.type === 'switch') {
                return 'id_txt_sw';
            } else {
                return 'id_txt_host';
            }
        })
        .attr('id', function (d) {
            return d.id;
        })
        .style('fill', 'red')
        .text(function (d) {
            return d.id;
        });

    g.append('svg:image')
        .attr('class', 'controller')
        .attr('x', '-20')
        .attr('y', '-60')
        .attr('width', '40')
        .attr('height', '40')
        .attr('xlink:href', controllerImage);
    // remove old nodes
    circle.exit().remove();

    
    // set the graph in motion
    force.start();
}

// app starts here
svg.on('mousedown', mousedown)
    .on('mouseup', mouseup);
d3.select(window)
    .on('keydown', keydown)
    .on('keyup', keyup);
restart();

/**
 * Move the packet
 */
function move(){
//    packet.transition().delay(1000).duration(1000).attr('x', nodes[0].px).attr('y', nodes[0].py);
    streamPacket();
}

function streamPacket(returnedRoutes){
//    var returnedRoutes = [{ip:'192.168.1.1'},{dpid:'00:00:00:00:00:00:00:01'},{dpid:'00:00:00:00:00:00:00:02'},{ip:'192.168.2.51'}];
console.log(returnedRoutes);
var initial = nodes.filter(function (n) {return n.ip === returnedRoutes[0]['ip'];})[0];
    setTimeout(function () {
        var obj = returnedRoutes[counterStream];
        for(var key in obj){
            if(key === "dpid"){
                dest1 = nodes.filter(function (n) {return n.dpid === obj[key];})[0];
            }else if (key === "ip"){
                dest1 = nodes.filter(function (n) {return n.ip === obj[key];})[0];
            }
            packet.style('opacity', 1);
            packet.transition().duration(1000).attr('x', dest1.px).attr('y', dest1.py);
        }

        counterStream++;
        if (counterStream < returnedRoutes.length) {
            streamPacket(returnedRoutes);
        }else if( packet.attr('x') == dest1.px && packet.attr('y') == dest1.py){
            packet.transition().duration(500).style('opacity', 0);
            packet.transition().delay(500).attr('x', initial.px).attr('y', initial.py);
            counterStream = 0;
            streamPacket(returnedRoutes);
        }else{
            streamPacket(returnedRoutes);
        }
    }, 1000);  
}