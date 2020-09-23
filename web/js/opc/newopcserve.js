
var uploadstatus=0;
var newopcservewindowsindex;
var newopcservewindowslayer;
var opcserveinfojspelement;
var opcserveinfojspdocument;


function newopcwindows(element,layer,url) {
    let indexlayer=layer.open(
        {
            type: 2
            ,title: false //不显示标题栏
            // ,id:'idLAY_layuipro'
            // ,closeBtn: false
            , area: ['500', '300px'] //['300px', '260px']
            ,content: [url,'no']//[$(dom).attr("lay-href"),'no']//不要滚动条
            ,zIndex: layer.zIndex //重点1
            ,shade: 0.3
            ,id: 'newopcwindows'
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


function successAndCloseopcserceWD() {
    // layer.newopcservewindowsindex
    console.log("创建成功！!")
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
    $("#bt_flush_opctab",parent.document).trigger("click");
}


