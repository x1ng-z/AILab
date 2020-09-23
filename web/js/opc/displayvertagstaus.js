

function newopcservevertagstatuswindows(element,layer,root,opcserveid) {
    let indexlayer=layer.open(
        {
            type: 2
            ,title: false //不显示标题栏
            // ,id:'idLAY_layuipro'
            // ,closeBtn: false
            , area: ['700px', '450px'] //['300px', '260px']
            ,content: root+'/opc/opcvertagstatus.do?opcserveid='+opcserveid//[$(dom).attr("lay-href"),'no']//不要滚动条
            ,zIndex: layer.zIndex //重点1
            ,shade: 0.3
            ,id: 'newopcservevertagstatuswindows'
            ,btn: ['关闭']//['确定', '取消']
            ,yes: function(index, layero){
                displayjsplayer.close(index);

                //按钮【按钮一】的回调
                // layer.getChildFrame()
                //var iframeWin = window[layero.find('iframe')[0]['name']];
                // iframeWin.document.getElementById("opcservesubmit").click();
                //$("#opcservesubmit",iframeWin.document).trigger("click");
            }
            // ,btn2: function(index, layero){
            //     //按钮【按钮二】的回调 do nothing
            //     //return false 开启该代码可禁止点击该按钮关闭
            // }
            ,btnAlign: 'c'
            ,success: function(layero){
                layer.setTop(layero); //重点2
            }

            // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
        }
    );

    displayjsplayer=layer;
    console.log(displayjsplayer);
    return indexlayer;
}




function newvertagwindows(element,root,opcserveid) {
    let indexlayer= parent.layer.open(
        {
            type: 2
            ,title: false //不显示标题栏
            // ,id:'idLAY_layuipro'
            // ,closeBtn: false
            , area: ['500px', '250px'] //['300px', '260px']
            ,content: [root+'/opc/newopcserveVerTag.do?opcserveid='+opcserveid,'no']//[$(dom).attr("lay-href"),'no']//不要滚动条
            ,zIndex: parent.layer.zIndex //重点1
            ,shade: 0.3
            ,id: 'newvertagwindows'
            ,btn: ['确定', '取消']
            ,yes: function(index, layero){
                //按钮【按钮一】的回调
                // console.log(body)
                // console.log(layero.find('iframe'))

                let iframeWin = window[layero.find('iframe')[0]['name']];
                // iframeWin.document.getElementById("opcservesubmit").click();
                // console.log(iframeWin)
                $("#opcservevertagsubmit",iframeWin.document).trigger("click");//send data to serve

            }
            ,btn2: function(index, layero){
                //按钮【按钮二】的回调 do nothing
                //return false 开启该代码可禁止点击该按钮关闭
            }
            ,btnAlign: 'c'
            ,success: function(layero){
                parent.layer.setTop(layero); //重点2
            }
        }
    );
    return indexlayer;
}



function modifyopcvertagwindows(element,root,tagid) {
    let indexlayer= parent.layer.open(
        {
            type: 2
            ,title: false //不显示标题栏
            // ,id:'idLAY_layuipro'
            // ,closeBtn: false
            , area: ['500px', '250px'] //['300px', '260px']
            ,content: [root+'/opc/modifyopcservevertag.do?tagid='+tagid,'no']//[$(dom).attr("lay-href"),'no']//不要滚动条
            ,zIndex: parent.layer.zIndex //重点1
            ,shade: 0.3
            ,id: 'modifyopcvertagwindows'
            ,btn: ['确定', '取消']
            ,yes: function(index, layero){
                //按钮【按钮一】的回调
                // console.log(body)
                // console.log(layero.find('iframe'))

                let iframeWin = window[layero.find('iframe')[0]['name']];
                // iframeWin.document.getElementById("opcservesubmit").click();
                // console.log(iframeWin)
                $("#opcservevertagsubmit",iframeWin.document).trigger("click");//send data to serve

            }
            ,btn2: function(index, layero){
                //按钮【按钮二】的回调 do nothing
                //return false 开启该代码可禁止点击该按钮关闭
            }
            ,btnAlign: 'c'
            ,success: function(layero){
                parent.layer.setTop(layero); //重点2
            }
        }
    );
    return indexlayer;
}



function successAndCloseopcvertagWD() {
    // layer.newopcservewindowsindex
    console.log("创建成功！!")
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
    $("#bt_flush_opcvertagtab",parent.document).trigger("click");
}



function successAndClosemodifyopcvertagWD() {
    // layer.newopcservewindowsindex
    console.log("创建成功！!")
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
    $("#bt_flush_opcvertagtab",parent.document).trigger("click");
}


function deleteopcvertag(tagid,root){
    $.ajax({
        url: root+"/opc/deleteopcservevertag.do",
        async: false,
        data: {
            "tagid": tagid
        },
        type: "POST",
        success: function (result) {
            console.log(result);
            let json = JSON.parse(result);
            if (json['msg'] == "error") {
                layer.msg("验证位号删除失败！");
            } else {
                layer.msg("o验证位号删除成功！");
                // <%--location.href = '${pageContext.request.contextPath}/' + json['go'];--%>
                //newleft(json['modleName'],json['modleId'])
                // parent.location.reload();
            }


            //window.location.href("result")
            // var json = JSON.parse(result);
        }
    });

}

