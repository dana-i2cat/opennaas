/*
 * Config Route page. Show the topology and the Routing Table.
 *
 */
var file = "config";
document.getElementById("ui-id-1").className += " ui-state-highlight";
document.getElementById("ui-id-4").className += " ui-state-highlight";

function runtime(node, links) {

    node
        .on('mouseover', mouseOverImage)
        .on('mousedown', function (d) {
            if (d3.event.ctrlKey) return;
            // select node
            mousedown_node = d;
            if (mousedown_node === selected_node) selected_node = null;
            else selected_node = mousedown_node;
            selected_link = null;
            $(document).on("dragstart", function () {
                return false;
            }); //disable drag in Firefox 3.0 and later
            // reposition drag line
            drag_line
                .style('marker-end', 'url(#end-arrow)')
                .classed('hidden', false)
                .attr('d', 'M' + mousedown_node.x + ',' + mousedown_node.y + 'L' + mousedown_node.x + ',' + mousedown_node.y);
            //restart();
        })
        .on('mouseup', function (d) {
            if (!mousedown_node) return;
            if (d.type === "switch") {
                d3.selectAll('.link2').attr('d', 'M0,0L0,0');
                //show a warning message in order to inform that the connection to a destination switch is not allowed.
                //http request to OpenNaaS
                return;
            }
console.log("Click on node " + d.id);
            // needed by FF
            drag_line
                .classed('hidden', true)
                .style('marker-end', '');
            // check for drag-to-self
            mouseup_node = d;
            if (mouseup_node === mousedown_node) {
                resetMouseVars();
                return;
            }
            // add link to graph (update if exists)
            // NB: links are strictly source < target; arrows separately specified by booleans
            var source, target;
            source = mousedown_node;
            target = mouseup_node;

console.log("Source " + source.id + " to Dest " + target.id);
            var link;
            link = links.filter(function (l) { return (l.source === source && l.target === target); })[0];

            //request to OpenNaaS
            swNode = nodes.filter(function (n) {return n.id === source.SW; });
            var returnedRoutes = eval('(' + getRoute(source.ip, target.ip, swNode[0].dpid, source.port) + ')');
//             returnedRoutes = [{dpid: '00:00:00:00:00:00:00:01'}, {dpid: '00:00:00:00:00:00:00:03'}, {dpid: '00:00:00:00:00:00:00:04'},{dpid: '00:00:00:00:00:00:00:06'}, {dpid: '00:00:00:00:00:00:00:07'}, {dpid: '00:00:00:00:00:00:00:08'}, {ip: '192.168.2.51'}];
            if( typeof returnedRoutes !== 'undefined' ){
                for(var i=0;i<returnedRoutes.length;i++){//i=1 because the first position is the source
                    var obj = returnedRoutes[i];
                     for(var key in obj){
console.log("Json key: "+key+" Json value: "+obj[key]);
                        dest1 = nodes.filter(function (n) {return n.dpid === obj[key];})[0];
                        if(key === "dpid"){
                            dest1 = nodes.filter(function (n) {return n.dpid === obj[key];})[0];
                        }else if (key === "ip"){
                            dest1 = nodes.filter(function (n) {return n.ip === obj[key];})[0];
                            highlight(dest1.ip);
                        }
                        link = {source: source, target: dest1, left: false, right: false, type: "new_link"};
console.log(link);
                        links.push(link);
                        source = dest1;
                    }
                }
        }
            d3.selectAll('.link2').attr('d', 'M0,0L0,0'); //Remove the requested path

            // select new link
            selected_link = link;
            selected_node = null;
            updateLinks();
        });
}

/**
 * Show the black line
 * @returns {undefined}
 */
function mousemove() {
    if (!mousedown_node) return;
    // update drag line
    drag_line.attr('d', 'M' + mousedown_node.x + ',' + mousedown_node.y + 'L' + d3.mouse(this)[0] + ',' + d3.mouse(this)[1]);

}
// app starts here
svg.on('mousedown', mousedown)
    .on('mousemove', mousemove)
    .on('mouseup', mouseup);

/**
 * Call OpenNaaS get Route Table
 * @param {type} dpid
 * @returns {undefined}
 */
function getRouteTable(dpid) {
    $.ajax({
        type: "GET",
        url: "getInfoSw/" + dpid,
        success: function (data) {
            $('#ajaxUpdate').html(data);
        }
    });
}
/**
 * Get Route to OpenNaaS
 * @param {type} ipSrc
 * @param {type} ipDst
 * @param {type} dpid
 * @param {type} inPort
 * @returns {data}
 */
function getRoute(ipSrc, ipDst, dpid, inPort) {
    var response;
    $.ajax({
        type: "GET",
        async:false,
        url: "getRoute/" + ipSrc+"/"+ipDst+"/"+dpid+"/"+inPort,
        success: function (data) {
            response = data;
        }
    });
    return response;
}

/**
 * Highlight the rows according to the requested route
 * @param {type} word to search in the route table
 * @returns {undefined}
 */
function highlight(word){
    var table = document.getElementById('jsonTable');
    if ( table.getElementsByTagName('tr').length > 1 ){
        var tbody = table.getElementsByTagName('tbody')[0];
        var items = tbody.getElementsByTagName('tr');
        var tds = null;

        for (var j = 0; j < items.length; j++) {
            tds = items[j].getElementsByTagName('td');
            for (var i = 0; i < tds.length-1; i++) {
                if(tds[i].innerHTML === word){
                    table.getElementsByTagName('tr')[j+1].style.background = 'yellow';
                }else if(inSubNet(word, tds[i].innerHTML)){
                    table.getElementsByTagName('tr')[j+1].style.background = 'yellow';
                }
            }
        }
    }
}

function mouseOverImage(){
    $('svg image').tipsy({
        fade: false,
        html: true, 
        gravity: 'w', 
        title: function() {
            var d = this.__data__;
            var info ="<b>Id:</b> "+d.id+"<br>";
            if(d.type !== "host"){
                info +="<b>DPID:</b> "+d.dpid+"<br>";
                info +="<b>Controller:</b> "+d.controller+"<br>";
            } else {
                info +="<b>IP:</b> "+d.ip+"<br>";
                info +="<b>Connected with:</b> "+d.SW+"<br>";
            }
            return info;
        }
    });
}