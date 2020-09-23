function newcontrlmodlewindows(layer, dom) {
    let indexlayer = layer.open(
        {
            type: 2
            , title: '新建控制模型'
            // ,title: false //不显示标题栏
            // ,id:'idLAY_layuipro'
            // ,closeBtn: false
            , area: ['950px', '500px']
            , content: [$(dom).attr("lay-href"), 'yes']//不要滚动条
            , zIndex: layer.zIndex //重点1
            , shade: 0.3
            , id: 'LAY_layuipro'
            , btn: ['确定', '取消']
            , yes: function (index, layero) {
                //按钮【按钮一】的回调
                // layer.getChildFrame()
                let iframeWin = window[layero.find('iframe')[0]['name']];
                // iframeWin.document.getElementById("newmodlesubmitbt").click();
                $("#newmodlesubmitbt", iframeWin.document).trigger('click')
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
    return indexlayer;
}


function successAndClosenewcontrlmodleWD(modlename, modleid, root) {
    // layer.newmodlewindowsindex
    console.log("创建成功！!" + modlename + modleid)
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
    console.log("创建成功！!")
    newcontrlmodleleft(modlename, modleid, root);
}


function newcontrlmodleleft(modleName, modleId, root) {

    console.log(root + '')

    mydd = document.createElement("dd");
    mya = document.createElement("a");
    mya.setAttribute("lay-href", root + "/contrlmodle/modlestatus/" + modleId + ".do");
    mya.setAttribute("href", "javascript:;");
    mya.innerHTML = modleName;
    mydd.appendChild(mya);
    $(".layui-nav-tree", parent.document).find("li:eq(0)").find("dl:last").append(mydd);
    $("#bt_flush_nav", parent.document).trigger("click")
}


function deletecontrlmodle(layer, modleid, url, tabid) {
    layer.confirm('删除模型?', function (index) {
        let indexmsg = layer.msg('删除中，请稍候', {icon: 16, time: false, shade: 0.8});
        $.ajax({
            url: url,
            async: true,
            data: {
                "modleid": modleid,
            },
            type: "POST",
            success: function (result) {
                console.log(result);
                layer.close(indexmsg);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("删除失败！");
                } else {
                    layer.msg("删除成功！");
                    removecontrlmodleletf(tabid);
                }

            }
        });

    });
}


function removecontrlmodleletf(tabid) {
    $(".layui-nav-tree", parent.document).find("li:eq(0)").find("dl").find("dd:has(a[lay-href=" + '\'' + tabid + '\'' + "])").remove();
    $("#bt_flush_nav", parent.document).trigger("click");
    $("#bt_close_tab", parent.document).attr("lay-id", tabid);
    $("#bt_close_tab", parent.document).trigger("click");
}


function newmodlepinwindow(layer, url, pintype, dom) {
    let indexlayer = layer.open(
        {
            type: 2
            , title: '新建模型' + pintype + '引脚'
            // ,title: false //不显示标题栏
            // ,id:'idLAY_layuipro'
            // ,closeBtn: false
            , offset: pintype == 'pv' ? '5px' : (pintype == 'mv' ? '30px' : (pintype == 'ff' ? '60px' : '120px'))
            , area: ['950px', '500px']
            , content: [url, 'yes']//不要滚动条
            , zIndex: layer.zIndex //重点1
            , shade: 0.3
            , id: 'LAY_layuipro'
            , btn: ['确定', '取消']
            , yes: function (index, layero) {
                //按钮【按钮一】的回调
                // layer.getChildFrame()
                let iframeWin = window[layero.find('iframe')[0]['name']];
                console.log(iframeWin)
                var body = layer.getChildFrame('body', index);

                switch (pintype) {

                    case 'pv':
                        console.log(body.find("#newcontrlmodlepinsubmitbt"))
                        body.find("#newcontrlmodlepinsubmitbt").trigger('click');
                        // body.trigger('click')
                        // iframeWin.document.getElementById("newmodlesubmitbt").click();
                        //$("#newcontrlmodlepinsubmit",iframeWin.document).trigger('click');
                        $("#bt_flush_pvtab", dom).trigger('click');
                        break;
                    case 'mv':
                        console.log("select mv")

                        body.find("#newcontrlmodlepinsubmitbt").trigger('click');
                        $("#bt_flush_mvtab", dom).trigger('click');

                        break;
                    case 'ff':
                        body.find("#newcontrlmodlepinsubmitbt").trigger('click');
                        $("#bt_flush_fftab", dom).trigger('click');
                        break;
                    default:
                        break;

                }

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
    return indexlayer;
}


function modifymodlepinwindow(dom, layer, modleid, pinid, pintype, methodname,root) {
    let indexlayer = layer.open(
        {
            type: 2
            ,
            title: '修改模型' + pintype + '引脚'
            // ,title: false //不显示标题栏
            // ,id:'idLAY_layuipro'
            // ,closeBtn: false
            ,
            area: ['950px', '500px']//modofymodelpvpin
            ,
            content: [root + '/contrlmodle/'+methodname+'.do?modleid=' + modleid + '&pinid=' + pinid + '&pintype=' + pintype, 'yes']//不要滚动条
            ,
            zIndex: layer.zIndex //重点1
            ,
            shade: 0.3
            ,
            id: 'LAY_layuipro'
            ,
            btn: ['确定', '取消']
            ,
            yes: function (index, layero) {
                //按钮【按钮一】的回调
                // layer.getChildFrame()
                let iframeWin = window[layero.find('iframe')[0]['name']];
                console.log(iframeWin)
                var body = layer.getChildFrame('body', index);
                body.find("#modifycontrlmodlepinsubmitbt").trigger('click');
                // body.trigger('click')
                // iframeWin.document.getElementById("newmodlesubmitbt").click();
                //$("#newcontrlmodlepinsubmit",iframeWin.document).trigger('click');
                $("#bt_flush_pvtab", dom).trigger('click');

            }
            ,
            btn2: function (index, layero) {
                //按钮【按钮二】的回调 do nothing
                //return false 开启该代码可禁止点击该按钮关闭
            }
            ,
            btnAlign: 'c'
            ,
            success: function (layero) {
                layer.setTop(layero); //重点2
            }

            // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
        }
    );
    return indexlayer;
}


function deletecontrlpin(dom, layer, pinid, pintype,method,root) {
    console.log(root + "/contrlmodle/"+method+".do");
    var index = layer.msg('删除中，请稍候', {icon: 16, time: false, shade: 0.8});
    $.ajax({
        url: root + "/contrlmodle/"+method+".do" + "?" + Math.random(),
        async: true,
        data: {
            "pinid": pinid
        },
        type: "POST",
        success: function (result) {
            console.log(result);
            layer.close(index);
            let json = JSON.parse(result);
            if (json['msg'] == "error") {
                layer.msg("删除失败！");
            } else {
                layer.msg("删除成功！");
                layer.close(index);
                $("#bt_flush_"+pintype+"tab", dom).trigger('click');

            }
        }
    });


}