<%@ page import="hs.Bean.ControlModle" %><%--
  Created by IntelliJ IDEA.
  User: zaixz
  Date: 2020/4/1
  Time: 22:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="shortcut icon"
          href="../img/favicon.ico" type="image/x-icon"/>
    <meta charset="utf-8">
    <title>opc serve info</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
    <script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script src="${pageContext.request.contextPath}/js/opc/displayvertagstaus.js"></script>


</head>


<body style="width:700px;height: 450px">

<div id="bt_flush_opcvertab" onclick="tablereflush()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>
<%--    属性表格--%>
<table class="layui-hide" id="vertagtable" lay-filter="vertagtable" width="700"></table>

<script type="text/html" id="toolbarDemo">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" id="addopcservevertagbt" lay-event="addopcservevertag">
            添加校验位号
        </button>
    </div>
</script>

<script type="text/html" id="barDemo">
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
</script>


<%--<script src="../js/layui/layui.js" charset="utf-8"></script>--%>
<!-- 注意：如果你直接复制所有代码到本地，上述js路径需要改成你本地的 -->

<script>
    var table;
    var element;
    var form;
    var layer;
    layui.use(['table', 'element', 'form', 'layer'], function () {
        table = layui.table;

        element = layui.element;
        form = layui.form;
        layer = layui.layer;
        element.render();
        table.render({
            elem: '#vertagtable'
            // ,data:
            , url: '${pageContext.request.contextPath}/opc/pageopcvertagstatus.do'
            , method: 'post'
            , request: {
                pageName: 'page' //页码的参数名称，默认：page
                , limitName: 'pagesize' //每页数据量的参数名，默认：limit
            }
            , where: {
                opcserveid: '${opcserveid}'
            }
            , toolbar: '#toolbarDemo' //开启头部工具栏，并为其绑定左侧模板
            , defaultToolbar: ['filter', 'exports', 'print', { //自定义头部工具栏右侧图标。如无需自定义，去除该参数即可
                title: '提示'
                , layEvent: 'LAYTABLE_TIPS'
                , icon: 'layui-icon-tips'
            }]
            , title: '模型属性'
            , limit: 10
            , loading: true
            ,minWidth:80
            , cols: [[
                {type: 'checkbox', fixed: 'left',}
                , {field: 'tagid', title: 'ID', width: 150, fixed: 'left', sort: true, hide: true}
                , {field: 'tagName', title: '注释', width: 200}
                , {field: 'tag', title: '位号', width: 200}
                , {field: 'opcserveid', title: 'serveip', width: 100,hide: true}
                , {fixed: 'right', title: '操作', toolbar: '#barDemo', width: 200}
            ]]
            , page: true
        });

        //头工具栏事件
        table.on('toolbar(vertagtable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addopcservevertag':
                    // var data = checkStatus.data;
                    // alert("sss"+$("#addopcservevertagvertagbt").attr('lay-href'));
                    newvertagwindows(element,'${pageContext.request.contextPath}',${opcserveid});
                    break;
                case 'getCheckLength':
                    // var data = checkStatus.data;
                    // layer.msg('选中了：'+ data.length + ' 个');
                    break;
                case 'isAll':
                    // layer.msg(checkStatus.isAll ? '全选': '未全选');
                    break;

                //自定义头工具栏右侧图标 - 提示
                case 'LAYTABLE_TIPS':
                    // layer.alert('这是工具栏右侧自定义的一个图标按钮');
                    break;
            }
        });

        //监听行工具事件
        table.on('tool(vertagtable)', function (obj) {
            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                layer.confirm('是否删除', function (index) {
                    deleteopcvertag(data['tagid'],'${pageContext.request.contextPath}');
                    tablereflush();
                    // obj.del();
                    layer.close(index);
                });
            } else if (obj.event === 'edit') {
                modifyopcvertagwindows(element,'${pageContext.request.contextPath}',data['tagid']);
            }
        });
    });
</script>


<script>

    function tablereflush() {
        // alert("vertagtable")
        layui.use(['element', 'layer', 'table'], function () {
            // table.render();

            table.reload('vertagtable', {
                url: '${pageContext.request.contextPath}/opc/pageopcvertagstatus.do'
                , method: 'post'
                , request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                }
                , where: {
                    opcserveid: '${opcserveid}'
                }
            });
        });
    }
</script>


</body>
</html>


