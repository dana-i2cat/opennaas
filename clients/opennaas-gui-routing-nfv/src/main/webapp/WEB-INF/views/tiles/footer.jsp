<div id="log" align="center" style="font-size:20px;background:black;color:white;height:70px;width:640px;"></div>

<script>
    setInterval(function()
{ 
    $.ajax({
            type: 'GET',
            url : "getLog/",
            async: false,
            success : function (data) {
                if(data != ""){
                    var image = '<img src="/opennaas-routing-nfv/resources/images/arrow_icon.svg.png" width="20px"/>';
                    document.getElementById('log').innerHTML = image+data;
                }else{
                    document.getElementById('log').innerHTML = data;
                }
                result = data;                 
            }
        });
}, 500000);//5000
    
        
</script>
<div>&nbsp;</div>
<div align="center">&copy; opennaas.org</div>
<%@include file="../progressBar.jsp"%>
<%@include file="../dialog.jsp"%>


	