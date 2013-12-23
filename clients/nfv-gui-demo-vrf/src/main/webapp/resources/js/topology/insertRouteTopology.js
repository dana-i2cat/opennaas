/*
 * Insert Route page. Show the topology and allows to insert Routes.
 *
 */
var file = "insertRoute";
var adjacencyMatrix = createAdjacencyMatrix();//calculate adjacent matrix of the paths
console.log(adjacencyMatrix);
var selectedNode = false;
var originNode;
var mode = auto;//manual
var activeNode = null;
var sourceIp;
var destinationIp;

// line displayed when dragging new nodes
var drag_line = svg.append('svg:path')
    .attr('class', 'link2 dragline hidden')
    .attr('d', 'M0,0L0,0');

// handles to link and node element groups
var path = svg.append('svg:g').selectAll('path'),
    circle = svg.append('svg:g').selectAll('g');

// update graph (called when needed)
function restart() {
     // path (link) group
    path = path.data(links);

    // update existing links
    path.classed('selected', function (d) {return d === selected_link; })
        .style('marker-end', function (d) {return d.right ? 'url(#end-arrow)' : ''; });

    // add new links
    path.enter().append('svg:path')
        .attr('id', function (d) {return d.id;})
        .attr('class', function (d) {
            if (d.type === "static") {
                return 'link';
            } else {
                return 'link2';
            }
        })
        .classed('selected', function (d) {return d === selected_link;})
        .style('marker-start', function (d) {return d.left ? 'url(#start-arrow)' : ''; })
        .style('marker-end', function (d) {return d.right ? 'url(#end-arrow)' : ''; })
        .on('mousedown', function (d) {
            if (d3.event.ctrlKey) return;
            // select link
            mousedown_link = d;
console.log("Click on Link " + d.type);
            if (mousedown_link === selected_link) selected_link = null;
            else selected_link = mousedown_link;
            selected_node = null;
            restart();
        });

    // remove old links
    path.exit().remove();

    // circle (node) group
    // NB: the function arg is crucial here! nodes are known by id, not by index!
    circle = circle.data(nodes, function (d) {return d.id; });

    // update existing nodes (reflexive & selected visual states)
    circle.selectAll('circle')
        .style('fill', function (d) {
            return (d === selected_node) ? d3.rgb(colors(d.id)).brighter().toString() : colors(d.id);
        })
        .classed('reflexive', function (d) {return d.reflexive; });

    // add new nodes
    var g = circle.enter().append('svg:g');

    g.append('svg:image')
        .attr('class', function (d) {return d.type; })
        .attr('x', "-40")
        .attr('y', "-40")
        .attr('width', "75")
        .attr('height', "75")
    //  .attr("onclick", function(d) { setActive(d.id_num) })
        .attr('xlink:href', function (d) {
            if (d.type === "switch")
                return switchImage;
            else
                return hostImage;
        })
        .on('mouseover', function (d) {
console.log("Mouseover Id num " + d.id_num + "node: " + d.id + ". SelectNode: " + selectedNode);
            if(mode === auto && activeNode != null)
                showPath(d.id_num);
        })
        .on('mouseout', function (d) {
            if (!mousedown_node || d === mousedown_node) return;
            // unenlarge target node
            d3.select(this).attr('transform', '');
        })
        .on('mousedown', function (d) {
            if (d3.event.ctrlKey) return;
console.log("Mousedown;" + d.id + " selectNode: " + selectedNode);
            if (selectedNode) {
                selectedNode = false;
                destinationIp = d.ip;
                if(mode === auto)
                    setPath(d.id_num);
//                clearPath();
            } else {
                selectedNode = true;
                originNode = d;
                setActive(d.id_num);
                sourceIp = d.ip;
            }
            // select node
            mousedown_node = d;
            if (mousedown_node === selected_node) selected_node = null;
            else selected_node = mousedown_node;
            selected_link = null;
            $(document).on("dragstart", function () {
                return false;
            }); //disable drag in Firefox 3.0 and later
            // reposition drag line
            if (mode === man){
            drag_line
                .style('marker-end', 'url(#end-arrow)')
                .classed('hidden', false)
                .attr('d', 'M' + mousedown_node.x + ',' + mousedown_node.y + 'L' + mousedown_node.x + ',' + mousedown_node.y);
            }
            restart();
        })
        .on('mouseup', function (d) {
            d3.selectAll(".switch").attr("width", 75); //image big
            d3.selectAll(".host").attr("width", 75); //image big
            d3.selectAll(".id_txt_sw").attr("x", "-20").attr("y", "9");
            d3.selectAll(".id_txt_host").attr("x", "-20").attr("y", "30");
            d3.select(this).attr("width", 100); //image big
            if (d.type === "switch")
                d3.select("#" + d.id).attr("x", "-9").attr("y", "12"); //move text
            else if (d.type === "host")
                d3.select("#" + d.id).attr("x", "-9").attr("y", "35"); //move text
            if (!mousedown_node) return;
console.log("Click on node " + d.id);
            // needed by FF
            drag_line
                .classed('hidden', true)
                .style('marker-end', '');
            // check for drag-to-self
            mouseup_node = d;

            // unenlarge target node
            d3.select(this).attr('transform', '');
            // add link to graph (update if exists)
            // NB: links are strictly source < target; arrows separately specified by booleans
            var source, target, direction;
            if (mousedown_node.id < mouseup_node.id) {
                source = mousedown_node;
                target = mouseup_node;
                direction = 'right';
            } else {
                source = mouseup_node;
                target = mousedown_node;
                direction = 'left';
            }
console.log("Source h " + source.id+" Dest h " + target.id);
            var originLink;//match link
            var newLink;//new defined link (CSS changes)
            originLink = links.filter(function (l) {return (l.source === source && l.target === target); })[0];

            if (originLink) {
                originLink[direction] = true;
            }
            //new link
            if ( mode === man){
                
console.log("New Link. Manual mode. ");
//                var newLink = false;
                if(target.type === "switch"){
                    $( "#insertRouteIp" ).dialog( "open" );
                }else{
                    dest1 = nodes.filter(function(n) {return n.id === target.id; })[0];
    console.log(dest1);
                    newLink = {source: source, target: dest1, left: false, right: false, type: "new_link"};
                    //this link exists? It is possible to make this connection?
                    for (var i = 0; i < links.length; ++i) {
                        if( (newLink.source === links[i].source && newLink.target === links[i].target ) || 
                            newLink.source === links[i].target && newLink.target === links[i].source){
                            newLink[direction] = true;
    console.log(newLink);
                        links.push(newLink);
    //                    var response = findPortsGivenLinks(originLink);
                        insertRoute(ipSrc, ipDst, dpid, originLink.srcPort, originLink.dstPort);
    //console.log(response);
    //                        console.log(link);
    console.log(links);
                            break;
                        }
                    }
                }
            }
            // select new link
//            selected_link = link;
            selected_node = null;
            restart();
            mousedown();
            mouseup();
            cleanDrag();//remove the visual connection
        });

    // show node IDs
    g.append('svg:text')
        .attr('x', "-20")
        .attr('y', function (d) {
            if (d.type === "switch") {
                return "9";
            } else {
                return "30";
            }
        })
        .attr('class', function (d) {
            if (d.type === "switch") {
                return 'id_txt_sw';
            } else {
                return "id_txt_host";
            }
        })
        .attr('id', function (d) {return d.id;})
        .style('fill', 'red')
        .text(function (d) {return d.id;});

    g.append('svg:image')
        .attr('class', "controller")
        .attr('x', "-20")
        .attr('y', "-60")
        .attr('width', "40")
        .attr('height', "40")
        .attr('xlink:href', controllerImage);
    // remove old nodes
    circle.exit().remove();

    // set the graph in motion
    force.start();
}

function mousemove() {
    if (!mousedown_node) return;

    // update drag line
    if (mode === man){
    drag_line.attr('d', 'M' + mousedown_node.x + ',' + mousedown_node.y + 'L' + d3.mouse(this)[0] + ',' + d3.mouse(this)[1]);
    }
    restart();
}

// app starts here
svg.on('mousedown', mousedown)
    .on('mousemove', mousemove)
    .on('mouseup', mouseup);
d3.select(window)
//    .on('keydown', keydown)
    .on('keyup', keyup);
restart();

// Choose a new active node
function setActive(to) {
console.log("Set Active < " + to);
    //      document.getElementById("c" + activeNode).removeAttributeNS(null, "class");
    //      clearPath();
    activeNode = to;
    if( mode === auto)
        shortestPathInfo = shortestPath(adjacencyMatrix, 7, to);
}
/**
 * Show the actual path calculated by dijkstra algorithm
 * @param {type} to
 * @returns {undefined}
 */
function showPath(to) {
    clearPath();
    if (activeNode != to) {
console.log("ShowPath " + to+"  from: " + activeNode);
        var path = constructPath(shortestPathInfo, to);
        var prev = activeNode;
console.log("To :" + to+" path: " + path+" prev: " + prev);
console.log(shortestPathInfo);
        for (var i = 0; i < path.length; i++) {
            var id;
            id = "path" + path[i] + prev;
            var l = document.getElementById(id);
            if (l === null) {
                id = "path" + prev + path[i];
            }
            var l = document.getElementById(id);
            l.setAttributeNS(null, "class", "link2 dragline hidden");
            prev = path[i];
        }
    }
}

function clearPath() {
console.log("..................CLEAR PATH....................");
    for (var i = 0; i < nodes.length; i++) {
        for (var j = i + 1; j < nodes.length; j++) {
            if (adjacencyMatrix[i][j] != Infinity) {
                if (document.getElementById("path" + i + j) === null)
                    document.getElementById("path" + j + i).setAttributeNS(null, "class", "link");
                else
                    document.getElementById("path" + i + j).setAttributeNS(null, "class", "link");
            }
        }
    }
}

/**
 * Set the path in automatic mode
 * 
 * @param {type} to
 * @returns {undefined}
 */
function setPath(to) {
console.log("SetPath");
    var orgLink;
    if (activeNode != to) {
        var path = constructPath(shortestPathInfo, to);
console.log(path);        
        var prev = activeNode;
        var ipSrc = sourceIp;
        var ipDst = destinationIp;
        for (var i = 0; i < path.length -1; i++) {
            var id, nextId;
            id = "path" + path[i] + prev;
            nextId = "path" + path[i+1] + path[i];//next path id, we need to extract the dstPort of the next link
            var l = document.getElementById(id);
            var l2 = document.getElementById(nextId);
            if (l === null) {
                id = "path" + prev + path[i];
            }if (l2 === null) {
                nextId = "path" + path[i] +path[i+1];
            }
            var l = document.getElementById(id);

            prev = path[i];
console.log("Prev"+prev);
console.log("Id: "+id+" NextId: "+nextId);
            orgLink = links.filter(function (link) {return (link.id === id);})[0];
            nextLink = links.filter(function (link) {return (link.id === nextId);})[0];
            source1 = orgLink.source;
            dest1 = orgLink.target;
            link = {source: source1, target: dest1, left: false, right: false, type:"new_link"};
            links.push(link);
console.log(orgLink);
//            if(orgLink.target.type == "switch"){//i si està al revés?????????? la definició del link...
                   
console.log("Insert link num "+i);
console.log(orgLink);
console.log(nextLink);
console.log("src: "+ipSrc+" "+ipDst+" "+orgLink.target.dpid+" "+orgLink.dstPort+" "+nextLink.dstPort);
            insertRoute(ipSrc, ipDst, orgLink.target.dpid, orgLink.dstPort, nextLink.dstPort);
            //}
            
        }
    }
console.log("...................:Send to OpenNaaS:.........................");
}

function hidePath(to) {
    clearPath();
    if (activeNode != to) {
        var c = document.getElementById("c" + to);
        c.removeAttributeNS(null, "class");
    }
}

function change(ref) {
     if(mode === auto){
        mode = man;
         clearPath();
     }else
        mode = auto;
    ref.value= mode;
    setActive(null);
}

function insertRoute(ipSrc, ipDst, dpid, inPort, outPort) {
    var response;
    $.ajax({
        type: "GET",
        async:false,
        url: "insertRoute/" + ipSrc+"/"+ipDst+"/"+dpid+"/"+inPort+"/"+outPort,
        success: function (data) {
            response = data;
//            $('#ajaxUpdate').html(data);
        }
    });
            console.log("!aaaaaaaaaaaaaaaaaaaaaa"+response);
    if(document.getElementById("insertRoute") === null){
        if(response === "Already exist"){
            $("<div id='insertRoute' class='error'>"+response+"</div>" ).insertAfter( "#header_menu").before("<br>");
        }else{
            $("<div id='insertRoute' class='success'>Inserted correctly.</div>" ).insertAfter( "#header_menu").before("<br>");
        }
            $('#insertRoute').next('br').remove();
            setTimeout(function() {
                //$('.success').remove();
                $('#insertRoute').slideUp("slow", function() { $('#insertRoute').remove();});
                //$('.success').fadeOut(300, function(){ $(this).remove();});
            }, 3000);
        
    }
        
    return response;
}
/**
 * Source and target are nodes(json object).
 * 
 * @param {type} reqLink
 * @returns {Array|findPortsGivenLinks.ports}
 */
function findPortsGivenLinks(reqLink){
    var ports = [];
    try{
        link = links.filter(function (l) {return (l.source === reqLink.source && l.target === reqLink.target);})[0];
        console.log(link);
    } catch(e){
        link = links.filter(function (l) {return (l.source === reqLink.target && l.target === reqLink.source);})[0];
        console.log(link);
    }
    ports.push({source: link.srcPort, target: link.dstPort});
    console.log(ports);
    return ports;
}
    
    