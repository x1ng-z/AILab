
var uploadstatus=0;
var newmodlewindowsindex;
var newmodlewindowslayer;
var indexjspelement;
var indexjspdocument;


function newaimodlewindows(document, element, layer, dom) {
    let indexlayer=layer.open(
        {
            type: 2
            ,title: false //不显示标题栏
            // ,id:'idLAY_layuipro'
            // ,closeBtn: false
            ,area: '500px' //['300px', '260px']
            ,content: [$(dom).attr("lay-href"),'no']//不要滚动条
            ,zIndex: layer.zIndex //重点1
            ,shade: 0.3
            ,id: 'LAY_layuipro'
            ,btn: ['确定', '取消']
            ,yes: function(index, layero){
                //按钮【按钮一】的回调
                // layer.getChildFrame()
                var iframeWin = window[layero.find('iframe')[0]['name']];
                iframeWin.document.getElementById("modlesubmit").click();
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
    newmodlewindowsindex=indexlayer;
    newmodlewindowslayer=layer;
    indexjspelement=element;
    indexjspdocument=document;
    return indexlayer;
}


function successAndClosenewaimodleWD(modlename,modleid,root) {
    // layer.newmodlewindowsindex
    console.log("创建成功！!"+modlename+modleid)
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
    console.log("创建成功！!")
    newleft(modlename,modleid,root);
}


function newleft(modleName, modleId,root) {

    console.log($(".layui-nav-tree", indexjspdocument).find("li:eq(1)").find("dl:last"))

    mydd = document.createElement("dd");
    mya = document.createElement("a");
    mya.setAttribute("lay-href", root+"/aimodle/aimodlestatus/" + modleId + ".do");
    mya.setAttribute("href","javascript:;");
    mya.innerHTML = modleName;
    mydd.appendChild(mya);
    let parentli
    $(".layui-nav-tree", parent.document).find("li:eq(1)").find("dl:last").append(mydd);
    //indexjspelement.render('nav', 'navtree');
    $("#bt_flush_nav",parent.document).trigger("click")
    // indexjspelement.init();
}