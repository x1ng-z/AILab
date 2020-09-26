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
          href="../img/favicon.ico" type="image/x-icon"/>
    <title>${modle.modleName}</title>
    <meta charset="utf-8">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/echarts.js"></script>
    <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
    <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/contrlmodle/contrlmodle.js"></script>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
</head>
<body class="layui-layout-body">
<%--<div id="container" style="height: 100%"></div>--%>
<%----%>
<div id="bt_flush_modlestructtab" onclick="flush_modlestructtab()"
     style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>
<div style="position: absolute;left: 0;top: 0;width: 100%;height:100%;overflow: auto;overflow-x: hidden">


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>控制模型结构</legend>
    </fieldset>
    <table class="layui-hide" id="modlestructtb" width="900px" height="900px"></table>


</div>


<script type="text/html" id="switchIsenable">
    <%--    input 添加 就不能进行编辑 disabled--%>
    <input type="checkbox" name="checkIO" value="{{d.pinStatus}}" lay-skin="switch" lay-text="启用|停用"
           lay-filter="flagpinStatus" {{
           d.pinStatus===undefined? '': (d.pinStatus.split('_')[2]== '1' ? 'checked' : '') }}>
</script>


<script type="text/html" id="switchBound">
    <%--    input 添加 就不能进行编辑 disabled--%>
    <input type="checkbox" name="checkIO2" value="{{d.pinBound}}" lay-skin="switch" lay-text="在线|越界"
           lay-filter="flagpinStatus" {{
           d.pinBound===undefined? '': (d.pinBound== '1' ? 'checked' : '') }} disabled>
</script>


<script>
    var table;
    var form;
    var mylayer;


    layui.use(['table', 'layer', 'table'], function () {
        let w = document
        table = layui.table;
        form = layui.form;
        mylayer = layui.layer;
        table.render({
            elem: '#modlestructtb'
            , url: '${pageContext.request.contextPath}/contrlmodle/pagemodlestruct.do'
            , method: 'post'
            // , request: {
            //     pageName: 'page' //页码的参数名称，默认：page
            //     , limitName: 'pagesize' //每页数据量的参数名，默认：limit
            // }
            , where: {
                modleid: '${modleid}'
            }
            , limit: 100
            , jump: function (obj, first) {
                //obj包含了当前分页的所有参数，比如：
                // console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                // console.log(obj.limit); //得到每页显示的条数
                // console.log(obj)
                //首次不执行
                if (!first) {
                    //do something
                    console.log("not first time")
                }
            }
            <%--, "data":${data}//[{"id":10000,"username":"user-0","sex":"女","city":"城市-0","sign":"签名-0","experience":255,"logins":24,"wealth":82830700,"classify":"作家","score":57}]--%>
            , "cellMinWidth": 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , "cols": [[
                {field: 'pinName', title: '引脚', width: 150}
                , {field: 'pinNote', title: '引脚注释', width: 150}
                , {field: 'pinStatus', title: '是否启用', width: 150, templet: '#switchIsenable', unresize: true}
                , {field: 'pinBound', title: '是否在线', width: 150, templet: '#switchBound', unresize: true}
            ]]
        });


        //监听操作
        form.on('switch(flagpinStatus)', function (obj) {
            // layer.tips(this.value + '_' + this.name + '：' + obj.elem.checked, obj.othis);
            if (this.value === 'undefined') {
                //console.log('value is undefine')
                return;
            }
            let index4modlepin = this.value.split("_");

            layer.confirm((index4modlepin[2] == 1 ? '停用引脚' : '启用引脚'), function (index) {
                var index = layer.msg('修改中，请稍候', {icon: 16, time: false, shade: 0.8});
                $.ajax({
                    url: "${pageContext.request.contextPath}/contrlmodle/savemodlestruct.do"
                    , async: true
                    , type: "POST"
                    , data: {
                        "modleid": index4modlepin[0],
                        "pinid": index4modlepin[1],
                        "pinstatus": index4modlepin[2]
                    }
                    , success: function (result) {
                        console.log(result);
                        layer.close(index);
                        let json = JSON.parse(result);
                        if (json['msg'] == "error") {
                            layer.msg("修改失败！");
                        } else {
                            layer.msg("修改成功！");
                            flush_modlestructtab();
                        }
                    }
                });
            });


        });
    });


</script>


<script>

    function flush_modlestructtab() {
        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;
            table.reload('modlestructtb', {

                elem: '#modlestructtb'
                , url: '${pageContext.request.contextPath}/contrlmodle/pagemodlestruct.do'
                , method: 'post'
                // , request: {
                //     pageName: 'page' //页码的参数名称，默认：page
                //     , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                // }
                , where: {
                    modleid: '${modleid}'
                }


            });
        });
    }


</script>


</body>
</html>
