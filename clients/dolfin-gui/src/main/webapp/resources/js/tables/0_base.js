String.prototype.format = function() {
    var args = arguments;

    return this.replace(/{(\d+)}/g, function(match, number) {
        return typeof args[number] != 'undefined' ? args[number] : '{' + number + '}';
    });
};

function array_keys(input, search_value, argStrict) {
    var search = typeof search_value !== 'undefined', tmp_arr = [], strict = !!argStrict, include = true, key = '';

    if (input && typeof input === 'object' && input.change_key_case) { // Duck-type check for our own array()-created PHPJS_Array
        return input.keys(search_value, argStrict);
    }

    for (key in input) {
        if (input.hasOwnProperty(key)) {
            include = true;
            if (search) {
                if (strict && input[key] !== search_value)
                    include = false;
                else if (input[key] != search_value)
                    include = false;
            }
            if (include)
                tmp_arr[tmp_arr.length] = key;
        }
    }
    return tmp_arr;
}

function convertXml2JSon(xml) {
    var x2js = new X2JS();
    return JSON.stringify(x2js.xml_str2json(xml));
}

function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [, ""])[1].replace(/\+/g, '%20')) || null;
}
function waiting(status) {
    $body = $("body");
    if (status)
        $body.addClass("loading");
    else
        $body.removeClass("loading");
}

function showHidePreloader(show){
     if(show)
         document.getElementById('preloader').style.display='block';
     else
         document.getElementById('preloader').style.display='none';
}