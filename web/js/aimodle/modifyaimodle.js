var modifymodlewindowsindex;
var modifymodlewindowslayer;
var indexjspelementinmodifyjs;
var indexjspdocumentinmodifyjs;


function newmodlewapropindows(element, layer, url) {


    let indexlayer = layer.open(
        {
            type: 2
            , title: '新建属性'
            // ,title: false //不显示标题栏
            // ,id:'idLAY_layuipro'
            // ,closeBtn: false
            , area: ['850px', '500px'] //['300px', '260px']
            , content: url //[dom.attr("lay-href"),'no']//不要滚动条
            , zIndex: layer.zIndex //重点1
            , shade: 0
            , id: 'newmodlewapropindows'
            , btn: ['确定', '取消']
            , yes: function (index, layero) {
                //按钮【按钮一】的回调
                // layer.getChildFrame()
                var iframeWin = window[layero.find('iframe')[0]['name']];
                iframeWin.document.getElementById("newmodleprosubmit").click();
            }
            , btn2: function (index, layero) {
                //按钮【按钮二】的回调 do nothing
                //return false 开启该代码可禁止点击该按钮关闭
            }
            , btnAlign: 'c'
            , success: function (layero) {
                layer.setTop(layero); //重点2
            }

            // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
        }
    );
    modifymodlewindowsindex = indexlayer;
    modifymodlewindowslayer = layer;
    indexjspelementinmodifyjs = element;
    return indexlayer;
}


function modifymodlewpropindows(element, layer, modleid, propertyid,root) {
    let indexlayer = layer.open(
        {
            type: 2
            , title: '修改属性'
            // ,title: false //不显示标题栏
            // ,id:'idLAY_layuipro'
            // ,closeBtn: false
            , area: ['850px', '500px'] //['300px', '260px']
            , content: root+'/aimodle/modifyaimodleproperty.do?modleid=' + modleid + '&propertyid=' + propertyid
            //[dom.attr("lay-href"),'no']//不要滚动条
            , zIndex: layer.zIndex //重点1
            , shade: 0
            , id: 'modifymodlewpropindows'
            , btn: ['确定', '取消']
            , yes: function (index, layero) {
                //按钮【按钮一】的回调
                // layer.getChildFrame()
                var iframeWin = window[layero.find('iframe')[0]['name']];
                iframeWin.document.getElementById("newmodleprosubmit").click();
            }
            , btn2: function (index, layero) {
                //按钮【按钮二】的回调 do nothing
                //return false 开启该代码可禁止点击该按钮关闭
            }
            , btnAlign: 'c'
            , success: function (layero) {
                layer.setTop(layero); //重点2
            }

            // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
        }
    );
    modifymodlewindowsindex = indexlayer;
    modifymodlewindowslayer = layer;
    indexjspelementinmodifyjs = element;
    return indexlayer;
}


function successAndClosemodifyaimodlepropertyWD() {
    // layer.newmodlewindowsindex
    console.log("属性修改层关闭！!")
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
    console.log("修改属性成功成功！!")
    //属性列表
    $("#bt_flush_tab", parent.document).trigger("click");
    //newleft(modlename,modleid);
}


function successAndClosenewaimodlepropertyWD() {
    // layer.newmodlewindowsindex
    console.log("属性创层关闭！!")
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
    console.log("创建属性成功成功！!")
    //属性列表
    $("#bt_flush_tab", parent.document).trigger("click");
    //newleft(modlename,modleid);
}


function deletemodleproperty(modleid, propertyid,root) {

    $.ajax({
        url: root+"/aimodle/deleteaimodleproperty.do" + "?" + Math.random(),
        async: true,
        data: {
            "modleid": modleid
            , "propertyid": propertyid
        },
        type: "POST",
        success: function (result) {
            console.log(result);
            let json = JSON.parse(result);
            if (json['msg'] == "error") {
                layer.msg("属性删除失败！");
            } else {
                layer.msg("属性删除成功！");
                // <%--location.href = '${pageContext.request.contextPath}/' + json['go'];--%>
                //newleft(json['modleName'],json['modleId'])
                // parent.location.reload();
            }


            //window.location.href("result")
            // var json = JSON.parse(result);
        }
    });

}