/*
 * Insert Route page. Show the topology and allows to insert Routes.
 *
 *  Two modes: Automatic and manual.
 *  Automatic uses dijkstra in order to find the route.
 *  Manual allows to insert the routes switch-to-switch. Then, its possible to define this routes point-to-point or with all the path
 *      -point-to-point: From each route, the user inserts the information
 *      -end-to-end: at the final of the definiton of the path, the user inserts the information
 * 
 */
var file = "insertRoute";
var adjacencyMatrix = createAdjacencyMatrix();//calculate adjacent matrix of the paths
document.getElementById("ui-id-2").className += " ui-state-highlight";

var selectedNode = false;
var mode = auto;//man - auto
var manualType = "Point-to-point";//Point-to-point or End-to-end
var originNode = null;
var activeNode = null;
var sourceIp, destinationIp;
var stackRoute = new Array();
var manualPath = [];//manual path end-to-end

function runtime(node) {
    node
        .on('mouseover', function (d) {
            if(mode === auto && activeNode !== null)
                showPath(d.id);
        })
        .on('mousedown', function (d) {
            if (d3.event.ctrlKey) return;
            if (selectedNode) {//nodeActive is NOT empty
                selectedNode = false;
                destinationIp = d.ip;
                if(mode === auto)
                    setPath(d.id);//insert path in OpenNaaS
            } else {
                selectedNode = true;
                setActive(d.id);
                if ( originNode === null )
                    originNode = d.id;
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
            //updateLinks();
        })
        .on('mouseup', function (d) {    
            if ( d.id === activeNode) return;
            if (!mousedown_node) return;
            // needed by FF
            drag_line
                .classed('hidden', true)
                .style('marker-end', '');
            // check for drag-to-self
            mouseup_node = d;

            var source, target, dest1;
            source = mousedown_node;
            target = mouseup_node;
         
console.log("MouseUp on node " + d.name+" Source h " + source.name+" Dest h " + target.name);
            var originLink;//Link that match with source <-> target
            var newLink;//New defined link (this should includes CSS changes)

            originLink = links.filter(function (l) {return (l.source === source && l.target === target); })[0];
            if ( typeof originLink === 'undefined' ) {
                originLink = links.filter(function (l) {return (l.source === target && l.target === source); })[0];
            }
var routeExists = false;
            //Adding new link in Manual Mode
            if ( mode === man ){
console.log("New Link. Manual mode. ");
                dest1 = nodes.filter(function(n) {return n.name === target.name; })[0];
                newLink = {id: links.length, source: source, target: dest1, type: "new_link"};
                                
                //this link exists? It is possible to make this connection?
                for (var i = 0; i < links.length; ++i) {
                    if( (newLink.source === links[i].source && newLink.target === links[i].target ) || 
                        newLink.source === links[i].target && newLink.target === links[i].source){

                        manualPath.push(newLink.target.id);
                        links.push(newLink);
                        if ( manualType === "Point-to-point" ){
                            insertIpDialog(newLink, originLink).done(function (answer) {
//console.log("Destination IP " + answer);//TRUE
                            });
                        } else if( manualType === "End-to-end" ){
                            insertIpDiv(newLink);
                        }
                        routeExists = true;
                        break;
                    }
                }
                if(!routeExists){
                    d3.selectAll('.dragline').attr('d', 'M0,0L0,0');
                }
            }

            selected_node = null;
            updateLinks();
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
}

// app starts here
svg.on('mousemove', mousemove)
    .on('mouseup', mouseup);

/**
 * Show the actual path calculated by dijkstra algorithm
 * @param {type} to
 * @returns {undefined}
 */
function showPath(to) {
    clearPath();
    var id, l;
    if (activeNode !== to) {
        var path = constructPath(shortestPathInfo, to);
        var prev = activeNode;
        for (var i = 0; i < path.length; i++) {
            id = (document.getElementById("path" + path[i] + prev) !== null) ? "path" + path[i] + prev : "path" + prev + path[i];
            l = document.getElementById(id);
            l.setAttributeNS(null, "class", "link2 dragline hidden");
            prev = path[i];
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
    var orgLink, nextLink;
    if (activeNode !== to) {
        var path = constructPath(shortestPathInfo, to);
console.log(path);
        var prev = activeNode;
        var ipSrc = sourceIp;
        var ipDst = destinationIp;
        var sourceNode = nodes.filter(function (nodes) {return (nodes.ip === sourceIp);})[0];
        var targetNode;
        for (var i = 0; i < path.length -1; i++) {
            var id, nextId;
            id = (document.getElementById("path" + path[i] + prev) !== null) ? "path" + path[i] + prev : "path" + prev + path[i];
            nextId = (document.getElementById("path" + path[i+1] + path[i]) !== null) ? "path" + path[i+1] + path[i] : "path" + path[i] +path[i+1];

            prev = path[i];
console.log("Prev"+prev+" Id: "+id+" NextId: "+nextId);
            orgLink = links.filter(function (link) {return (link.id === id);})[0];
            nextLink = links.filter(function (link) {return (link.id === nextId);})[0];
            source1 = orgLink.source;
            dest1 = orgLink.target;
            link = {source: source1, target: dest1, type:"new_link"};
            links.push(link);
console.log(orgLink);
console.log(nextLink);
console.log("i:"+i+" src: "+ipSrc+" "+ipDst+" "+orgLink.target.dpid+" "+orgLink.dstPort+" "+nextLink.srcPort);

/*search the source dpid -> the dpid common in path and nextPath */
            srcPort = orgLink.dstPort;
            dstPort = nextLink.srcPort;
            if ( orgLink.source === sourceNode ){
                targetNode = orgLink.target;
                srcPort = orgLink.dstPort;
                if( nextLink.source === orgLink.target ){
                    dstPort = nextLink.srcPort;
                }else{
                    dstPort = nextLink.dstPort;
                }
            } else if ( orgLink.target === sourceNode ){
                targetNode = orgLink.source;
                srcPort = orgLink.srcPort;
                if( nextLink.source === orgLink.target ){
                    dstPort = nextLink.srcPort;
                }else{
                    dstPort = nextLink.dstPort;
                }
            }
console.log("i:"+i+" src: "+ipSrc+" "+ipDst+" "+targetNode.dpid+" "+srcPort+" "+dstPort);
            insertRoute(ipSrc, ipDst, targetNode.dpid, srcPort, dstPort);
            //opposite direction
            insertRoute(ipDst, ipSrc, targetNode.dpid, dstPort, srcPort);
            
            sourceNode = targetNode;
        }
    }
console.log("...................:Send to OpenNaaS:.........................");
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
        }, 4000);
    }
    return response;
}


function insertPath(to) {
    var orgLink;
    var path = manualPath;
    if (activeNode !== to) {
        var prev = originNode;
        var ipSrc = sourceIp;
        var ipDst = destinationIp;
        for (var i = 0; i < path.length -1; i++) {
            var id, nextId;
            id = (document.getElementById("path" + path[i] + prev) !== null) ? "path" + path[i] + prev : "path" + prev + path[i];
            nextId = (document.getElementById("path" + path[i+1] + path[i]) !== null) ? "path" + path[i+1] + path[i] : "path" + path[i] +path[i+1];
//            var l = document.getElementById(id);
//            var l2 = document.getElementById(nextId);
            
            prev = path[i];
console.log("Prev"+prev+" Id: "+id+" NextId: "+nextId);
            orgLink = links.filter(function (link) {return (link.id === id);})[0];
            nextLink = links.filter(function (link) {return (link.id === nextId);})[0];

            source1 = orgLink.source;
            dest1 = orgLink.target;
            link = {source: source1, target: dest1, type:"new_link"};
            links.push(link);

console.log("i:"+i+" src: "+ipSrc+" "+ipDst+" "+orgLink.target.dpid+" "+orgLink.dstPort+" "+nextLink.srcPort);
            insertRoute(ipSrc, ipDst, orgLink.target.dpid, orgLink.dstPort, nextLink.srcPort);
            //contrary direction
            insertRoute(ipDst, ipSrc, orgLink.target.dpid, nextLink.srcPort, orgLink.dstPort);
        }
    }
    originNode = null;
}


/**
 * Choose a new active node
 * @param {type} to
 * @returns {undefined}
 */
function setActive(to) {
console.log("Set Active > " + to);
    activeNode = to;
    if( mode === auto )
        shortestPathInfo = shortestPath(adjacencyMatrix, vertexNum, to);
}
/**
 * Remove the black links. Return to the original topology
 * @returns {undefined}
 */
function clearPath() {
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
 * Remove the last link inserted in the stack. Also the dragged line is removed
 * @returns {undefined}
 */
function removeLastLink(){
    var lastLink = links.pop();
console.log(lastLink);
	try{
	    var element = document.getElementById(lastLink.id);
	    element.parentNode.removeChild(element);
	}catch(e){
	}
}

/**
 * Cahnge the insert routes mode. Automatic or manual (auto-man)
 * @param {type} mode
 * @returns {undefined}
 */
function change(mode) {
    this.mode = mode;
     if(this.mode === auto){
        mode = man;
        document.getElementById('manualType').style.display = 'none';
        if( getManualType() === "End-to-end" ){
            document.getElementById('insertRouteInfo').style.display = 'inline-block';
        }
     }else{
        mode = man;
        clearPath();
        document.getElementById('manualType').style.display = 'inline-block';
        document.getElementById('insertRouteInfo').style.display = 'none';
    }    
//    ref.value= mode;
    setActive(null);
}

/**
 * Manual type selection
 * @param {type} ref
 * @returns {undefined}
 */
function toggleManualType(ref){
    if ( manualType === "Point-to-point" ){
        manualType = "End-to-end";
        document.getElementById('insertRouteInfo').style.display = 'inline-block';
    } else {
        manualType = "Point-to-point";
        document.getElementById('insertRouteInfo').style.display = 'none';
    }
    ref.value= manualType;
}

function getManualType(){
console.log("Get Manual type: "+manualType);
    return manualType;
}

function updateTips( t ) {
    tips.text( t ).addClass( "ui-state-highlight" );
    setTimeout(function() {
        tips.removeClass( "ui-state-highlight", 1500 );
    }, 500 );
}
    
/* Move to a centralized file? */
function checkLength( o, n, min, max ) {
    var newO;
    newO = (typeof o === 'string' || o instanceof String ) ? o : o.val();
    if ( newO.length > max || newO.length < min ) {
        o.addClass( "ui-state-error" );
        updateTips( "Length of " + n + " must be between " + min + " and " + max + "." );
        return false;
    } else {
        return true;
    }
}

function checkRegexp( o, regexp, n ) {
    var newO;
    newO = (typeof o === 'string' || o instanceof String ) ? o : o.val();
    if ( !( regexp.test( newO ) ) ) {
        o.addClass( "ui-state-error" );
        updateTips( n );
        return false;
    } else {
        return true;
    }
}

function checkIp( ip ){
    if( ip ){
        var validIp = true;
        validIp = validIp && checkLength( ip, "ip", 8, 16 );
        validIp = validIp && checkRegexp( ip, /^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$/, "Insert an IP address with the following format: www.xxx.yyy.zzz" );
        return validIp;
    }
    return false;
}
