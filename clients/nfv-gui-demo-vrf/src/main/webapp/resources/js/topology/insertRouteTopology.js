/*
 * Insert Route page. Show the topology and allows to insert Routes.
 *
 */
var file = "insertRoute";
var adjacencyMatrix = createAdjacencyMatrix();//calculate adjacent matrix of the paths

var selectedNode = false;
var originNode;
var mode = auto;//man - auto
var activeNode = null;
var sourceIp;
var destinationIp;

var drag_line = svg.append('svg:path')
    .attr('class', 'link2 dragline hidden')
    .attr('d', 'M0,0L0,0');

function runtime(node) {
    node
        .on('mouseover', function (d) {
//console.log("Mouseover Id num " + d.id_num + "node: " + d.id + ". SelectNode: " + selectedNode);
            if(mode === auto && activeNode != null)
                showPath(d.id_num);
        })
        .on('mouseout', function (d) {
            if (!mousedown_node || d === mousedown_node) return;
            // unenlarge target node
//            d3.select(this).attr('transform', '');
        })
        .on('mousedown', function (d) {
            if (d3.event.ctrlKey) return;
console.log("Mousedown;" + d.id + " selectNode: " + selectedNode+" mode: "+mode);
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
            updateLinks();
        })
        .on('mouseup', function (d) {
/*            d3.selectAll(".switch").attr("width", 75); //image big
            d3.selectAll(".host").attr("width", 75); //image big
            d3.selectAll(".id_txt_sw").attr("x", "-20").attr("y", "9");
            d3.selectAll(".id_txt_host").attr("x", "-20").attr("y", "30");
            d3.select(this).attr("width", 100); //image big
*/            if (!mousedown_node) return;
console.log("Click on node " + d.id);
            // needed by FF
            drag_line
                .classed('hidden', true)
                .style('marker-end', '');
            // check for drag-to-self
            mouseup_node = d;

            // unenlarge target node
//            d3.select(this).attr('transform', '');
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
                
                dest1 = nodes.filter(function(n) {return n.id === target.id; })[0];
console.log(dest1);
                newLink = {source: source, target: dest1, left: false, right: false, type: "new_link"};
                //this link exists? It is possible to make this connection?
                for (var i = 0; i < links.length; ++i) {
//console.log(links[i]);
                    if( (newLink.source === links[i].source && newLink.target === links[i].target ) || 
                        newLink.source === links[i].target && newLink.target === links[i].source){
                        newLink[direction] = true;
console.log(newLink);
                    links.push(newLink);
//                    var response = findPortsGivenLinks(originLink);
//                    insertRoute(ipSrc, ipDst, dpid, originLink.srcPort, originLink.dstPort);
//console.log(response);
                        console.log(link);
console.log(links);
                        break;
                    }
                }
            }
            // select new link
//            selected_link = link;
            selected_node = null;
//            restart();
            updateLinks();
            mousedown();
            mouseup();
            cleanDrag();//remove the visual connection
        });
}

function mousemove() {
    if (!mousedown_node) return;

    // update drag line
    if (mode === man){
    drag_line.attr('d', 'M' + mousedown_node.x + ',' + mousedown_node.y + 'L' + d3.mouse(this)[0] + ',' + d3.mouse(this)[1]);
    }
//    restart();
}

// app starts here
svg.on('mousedown', mousedown)
    .on('mousemove', mousemove)
    .on('mouseup', mouseup);
d3.select(window)
//    .on('keydown', keydown)
    .on('keyup', keyup);
//restart();

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
    if (activeNode !== to) {
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
            l = document.getElementById(id);
            l.setAttributeNS(null, "class", "link2 dragline hidden");
            prev = path[i];
        }
    }
}

function clearPath() {
//console.log("..................CLEAR PATH....................");
    for (var i = 0; i < nodes.length; i++) {
        for (var j = i + 1; j < nodes.length; j++) {
            if (adjacencyMatrix[i][j] !== Infinity) {
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
    if (activeNode !== to) {
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
            l = document.getElementById(id);

            prev = path[i];
console.log("Prev"+prev+" Id: "+id+" NextId: "+nextId);
            orgLink = links.filter(function (link) {return (link.id === id);})[0];
            nextLink = links.filter(function (link) {return (link.id === nextId);})[0];
            source1 = orgLink.source;
            dest1 = orgLink.target;
            link = {source: source1, target: dest1, left: false, right: false, type:"new_link"};
            links.push(link);
console.log(orgLink);
//            if(orgLink.target.type == "switch"){//i si està al revés?????????? la definició del link...
                   
console.log(nextLink);
console.log("i:"+i+" src: "+ipSrc+" "+ipDst+" "+orgLink.target.dpid+" "+orgLink.dstPort+" "+nextLink.dstPort);
            insertRoute(ipSrc, ipDst, orgLink.target.dpid, orgLink.dstPort, nextLink.dstPort);
            //contrary direction
            insertRoute(ipDst, ipSrc, orgLink.target.dpid, nextLink.dstPort, orgLink.dstPort);
            //}
            
        }
    }
console.log("...................:Send to OpenNaaS:.........................");
}

/**
 * Hide a path
 * @param {type} to
 * @returns {undefined}
 */
function hidePath(to) {
    clearPath();
    if (activeNode !== to) {
        var c = document.getElementById("c" + to);
        c.removeAttributeNS(null, "class");
    }
}
/**
 * Cahnge the insert routes mode. Automatic or manual (auto-man)
 * @param {type} ref
 * @returns {undefined}
 */
function change(ref) {
     if(mode === auto){
        mode = man;
         clearPath();
     }else
        mode = auto;
    ref.value= mode;
    setActive(null);
}

/**
 * Allows to insert new routes
 * @param {type} ipSrc
 * @param {type} ipDst
 * @param {type} dpid
 * @param {type} inPort
 * @param {type} outPort
 * @returns {data}
 */
function insertRoute(ipSrc, ipDst, dpid, inPort, outPort) {
console.log("Insert Route request: "+ipSrc+" "+ipDst+" "+dpid+" "+inPort+" "+outPort);
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
    } catch(e){
        link = links.filter(function (l) {return (l.source === reqLink.target && l.target === reqLink.source);})[0];

    }
console.log(link);
    ports.push({source: link.srcPort, target: link.dstPort});
    return ports;
}

/**
 * Remove the last link inserted in the stack. Also the dragged line is removed
 * @returns {undefined}
 */
function removeLastLink(){
    links.pop();
    restart();
    cleanDrag();
}