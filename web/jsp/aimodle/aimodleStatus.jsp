<%@ page import="hs.Bean.ControlModle" %><%--
  Created by IntelliJ IDEA.
  User: zaixz
  Date: 2020/5/7
  Time: 15:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="shortcut icon"
          href="../../img/favicon.ico" type="image/x-icon"/>
    <title>${aimodle.algorithmName}</title>
    <meta charset="utf-8">
    <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
    <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/layui/layui.js"></script>

    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
</head>
<body class="layui-layout-body" onload=" table_flush()">
<%--<div id="container" style="height: 100%"></div>--%>
<%----%>
<div style="position: absolute;left: 0;top: 0;width: 100%;height:100%;overflow: auto;overflow-x: hidden">

    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>${aimodle.algorithmName}</legend>
    </fieldset>
    <div class="layui-fluid">
        <div class="layui-row layui-col-space15"><%--layui-col-space15 列间距15--%>
            <c:forEach var="apt" items="${aimodle.algorithmProperties}" varStatus="Count">
                <c:choose>
                    <c:when test="${apt.datatype=='image'}">
                        <div class="layui-col-md4">
                            <div class="layui-card">
                                <div class="layui-card-header">${apt.propertyName}</div>
                                <div class="layui-card-body">
                                    <div id="imagebox${Count.count}" style="height:300px;">
                                        <img id="imagecard${Count.count}"
                                             src="${pageContext.request.contextPath}/aimodle/aimodlepropertypic/${apt.refrencealgorithmid}/${apt.property}.do" width="300px" height="300px"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:when>
                </c:choose>
            </c:forEach>
        </div>


        <div class="layui-row layui-col-space15">
            <div class="layui-btn-group">
                <a href="" class="layui-btn"
                   onclick="isdelete('${pageContext.request.contextPath}/aimodle/deletaimodle.do?modleid='+${aimodle.modleid})">删除</a>
                <a href="" class="layui-btn"
                   onclick="window.location.reload()">刷新</a>
                <a href=""
                   class="layui-btn"
                   onclick="newTab('编辑${aimodle.algorithmName}','${pageContext.request.contextPath}/aimodle/modifyaimodle/${aimodle.modleid}.do')">编辑</a>
            </div>
        </div>
    </div>
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>属性参数信息</legend>
    </fieldset>
    <table class="layui-hide" id="algorithmProperty" width="900px" height="900px"></table>

</div>


<script>
    var table;
    var form;

    let table_flush_t;

    // let table_width = $("#modlereadDatatab").width();
    // console.log("table_width:",table_width)

    layui.use('table', function () {
        let w = document
        table = layui.table;
        table.render({
            elem: '#algorithmProperty'
            , data: []
            , jump: function (obj, first) {
                if (!first) {
                    //do something
                    console.log("not first time")
                }
            }
            , "cellMinWidth": 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , "cols": [[
                {field: 'propertyname', title: '属性名', width: '10%'}
                , {field: 'propertyvalue', title: '值', width: '10%'}
            ]]
        });

    });
</script>

<script>
    function table_flush() {
        clearInterval(table_flush_t);
        try {
            valuepropertyfill();
            imageepropertyfill();
        } catch (e) {
            console.log(e);
        }

        table_flush_t = setInterval(table_flush, 3 * 1000);
        <%--            ${modle.controlAPCOutCycle.intValue()*1000}--%>
    }

    function valuepropertyfill() {
        $.ajax({
            url: "${pageContext.request.contextPath}/aimodle/aimodlepropertyvalue/${aimodle.modleid}.do" + "?" + Math.random(),
            async: true,
            type: "POST",
            success: function (result) {
                // console.log(result);
                var json = JSON.parse(result);
                // console.log(json["funneltype"][0][0]);
                // console.log(typeof (json["funneltype"][0][0]));


                let table_width = $("#algorithmProperty").width();
                // console.log("table_width:", table_width)
                $("#algorithmProperty").width($(document).width());


                if (json["msg"] == "success") {
                    table.reload('algorithmProperty', {
                        "data": json['data'],
                        "width": $(document).width()
                    });
                }

            }
        });

    }



    function imageepropertyfill() {
        $("img[id^=imagecard]").each(function () {
            let oldsrc=$(this).attr('src').split('?')[0];
            // console.log(oldsrc);
            $(this).attr('src',oldsrc+'?'+Math.random());
        });
    }



</script>

<script>


    function isdelete(url) {
        var r = confirm("确定删除?");

        if (r === true) {
            $.ajax({
                url: url + "&" + Math.random(),
                async: true,
                type: "POST",
                success: function (result) {
                    console.log(result);

                }
            });

            $(".layui-nav-tree", parent.document).find("li:eq(1)").find("dl").find("dd:has(a[lay-href='${pageContext.request.contextPath}/aimodle/aimodlestatus/${aimodle.modleid}.do'])").remove();
            $("#bt_flush_nav", parent.document).trigger("click");
            $("#bt_close_tab", parent.document).attr("lay-id", '${pageContext.request.contextPath}/aimodle/aimodlestatus/${aimodle.modleid}.do');
            $("#bt_close_tab", parent.document).trigger("click");
            //parent.location.reload();
        } else {

        }
    }


    function stopOrrun(url, msg) {
        var r = confirm(msg);
        if (r == true) {
            $.ajax({
                url: url + "&" + Math.random(),
                async: true,
                type: "POST",
                success: function (result) {
                    // console.log(result);

                }
            });
            window.location.reload();
        }
    }


    function newTab(title, url) {
        let element11 = parent.layui.element;
        element11.tabAdd(
            'pagetabs', {
                title: title
                ,
                content: '<iframe src="' + url + '" class="layui-admin-iframe" scrolling="auto" frameborder="0"></iframe>'
                ,
                id: url
            }
        );
        // window.location.reload()
        // var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        // parent.layer.close(index); //再执行关闭
        // sleep(1000);
        element11.tabChange('pagetabs', url)


        //newleft();


    }


    var sleep = function (time) {
        var startTime = new Date().getTime() + parseInt(time, 10);
        while (new Date().getTime() < startTime) {
        }
    };

</script>

<%--<script>--%>
<%--    --%>
<%--    function newTab(url) {--%>
<%--        console.log(url)--%>
<%--           addTab(url);--%>
<%--    }--%>
<%--</script>--%>


<%--<script>--%>
<%--    $(document).ready(function(){--%>
<%--        table_flush();--%>
<%--    });--%>

<%--</script>--%>


</body>
</html>
