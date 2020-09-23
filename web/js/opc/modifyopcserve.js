
var uploadstatus=0;
var newopcservewindowsindex;
var newopcservewindowslayer;
var opcserveinfojspelement;
var opcserveinfojspdocument;


function modifyopcwindows(element,layer,opcserveid,root) {
    let indexlayer=layer.open(
        {
            type: 2
            ,title: false //不显示标题栏
            // ,id:'idLAY_layuipro'
            // ,closeBtn: false
            , area:['500px','350px'] //['300px', '260px']
            ,content: [''+root+'/opc/modifyopcserve.do?opcserveid='+opcserveid,'no']//[$(dom).attr("lay-href"),'no']//不要滚动条
            ,zIndex: layer.zIndex //重点1
            ,shade: 0.3
            ,id: 'modifyopcwindows'
            ,btn: ['确定', '取消']
            ,yes: function(index, layero){
                //按钮【按钮一】的回调
                // layer.getChildFrame()
                var iframeWin = window[layero.find('iframe')[0]['name']];
                // iframeWin.document.getElementById("opcservesubmit").click();
                $("#opcservesubmit",iframeWin.document).trigger("click");
            }
            ,btn2: function(index, layero){
                //按钮【按钮二】的回调 do nothing
                //return false 开启该代码可禁止点击该按钮关闭
            }
            ,btnAlign: 'c'
            ,success: function(layero){
                layer.setTop(layero); //重点2
            }

            // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
        }
    );
    newopcservewindowsindex=indexlayer;
    newopcservewindowslayer=layer;
    opcserveinfojspelement=element;
    return indexlayer;
}


function successAndClosemodifyopcserceWD(root) {
    // layer.newopcservewindowsindex
    console.log("创建成功！!")
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
    $("#bt_flush_opctab",parent.document).trigger("click");
}


function deleteopcserve(serveid,root) {

    $.ajax({
        url: root+"/opc/deleteopcserve.do",
        async: false,
        data: {
            "opcserveid": serveid
        },
        type: "POST",
        success: function (result) {
            console.log(result);
            let json = JSON.parse(result);
            if (json['msg'] == "error") {
                layer.msg("opc服务器删除失败！");
            } else {
                layer.msg("opc服务器删除成功！");
                // <%--location.href = '${pageContext.request.contextPath}/' + json['go'];--%>
                //newleft(json['modleName'],json['modleId'])
                // parent.location.reload();
            }


            //window.location.href("result")
            // var json = JSON.parse(result);
        }
    });

}


