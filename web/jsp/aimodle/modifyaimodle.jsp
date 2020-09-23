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
          href="../../img/favicon.ico" type="image/x-icon"/>
    <meta charset="utf-8">
    <title>edit ${modle.algorithmName}</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
    <script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script src="${pageContext.request.contextPath}/js/aimodle/modifyaimodle.js"></script>
</head>


<body>

<div id="bt_flush_tab" onclick="tablereflush()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>
<form class="layui-form" action="" method="post">
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>基础参数设置</legend>
    </fieldset>

    <div class="layui-form-item">
        <div class="layui-inline">
            <div class="layui-input-inline">
                <input type="number" name="modleid" value="${modle.modleid}" hidden/>
            </div>
        </div>
    </div>

    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">模型名称</label>
            <div class="layui-input-inline">
                <input type="text" name="modlename" lay-verify="required" autocomplete="off" class="layui-input"
                       value="${modle.algorithmName}">
            </div>
        </div>
    </div>


    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" lay-filter="motifymodlesubmit">更新模型</button>
<%--        <button type="reset" class="layui-btn layui-btn-primary">重置</button>--%>
    </div>

</form>


<%--    属性表格--%>
<table class="layui-hide" id="propertytable" lay-filter="propertytable"></table>

<script type="text/html" id="toolbarDemo">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" id="addpropertybt" lay-event="addproperty"
                lay-href="${pageContext.request.contextPath}/aimodle/newaimodleproperty.do?modleid=${modle.modleid}">
            添加属性
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

        //监听提交
        form.on('submit(motifymodlesubmit)', function(data){
            console.log(JSON.stringify(data.field));
            let index = layer.msg('修改中，请稍候', {icon: 16, time: false, shade: 0.8});
            $.ajax({
                url: "${pageContext.request.contextPath}/aimodle/updateaimodle.do" + "?" + Math.random(),
                async: true,
                data: {
                    "aimodlecontext": JSON.stringify(data.field)
                },
                type: "POST",
                success: function (result) {
                    console.log(result);
                    layer.close(index);
                    let json = JSON.parse(result);
                    if (json['msg'] == "error") {
                        layer.msg("属性修改失败！");
                    } else {
                        layer.msg("属性修改成功！");
                        window.location.reload()

                        // successAndClosemodifyaimodlepropertyWD();
                        <%--location.href = '${pageContext.request.contextPath}/' + json['go'];--%>
                        //newleft(json['modleName'],json['modleId'])
                        // parent.location.reload();
                    }


                    //window.location.href("result")
                    // var json = JSON.parse(result);
                }
            });
            return false;

        });





        table.render({
            elem: '#propertytable'
            // ,data:
            , url: '${pageContext.request.contextPath}/aimodle/pageformodleproperty.do'
            , method: 'post'
            , request: {
                pageName: 'page' //页码的参数名称，默认：page
                , limitName: 'pagesize' //每页数据量的参数名，默认：limit
            }
            , where: {
                modleid: '${modle.modleid}'
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
            , cols: [[
                {type: 'checkbox', fixed: 'left',}
                , {field: 'propertyid', title: 'ID', width: 100, fixed: 'left', sort: true, hide: true}
                , {field: 'propertyName', title: '属性名', width: 100}
                , {field: 'property', title: 'property', width: 100}
                , {field: 'refrencealgorithmid', title: '引用模型id', width: 100, hide: true}
                , {field: 'resource', title: 'opcserve', width: 100}
                , {field: 'opctag', title: 'opc位号', width: 120}
                , {field: 'datatype', title: '类型', width: 100}
                , {fixed: 'right', title: '操作', toolbar: '#barDemo', width: 150}
            ]]
            , page: true
        });

        //头工具栏事件
        table.on('toolbar(propertytable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addproperty':
                    // var data = checkStatus.data;
                    // alert("sss"+$("#addpropertybt").attr('lay-href'));
                    newmodlewapropindows(element, layer, $("#addpropertybt").attr('lay-href'));
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
        table.on('tool(propertytable)', function (obj) {
            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                layer.confirm('是否删除', function (index) {
                    deletemodleproperty(data['refrencealgorithmid'], data['propertyid'],'${pageContext.request.contextPath}');
                    tablereflush();
                    // obj.del();
                    layer.close(index);
                });
            } else if (obj.event === 'edit') {
                modifymodlewpropindows(element,layer,data['refrencealgorithmid'], data['propertyid'],'${pageContext.request.contextPath}');
            }
        });
    });
</script>


<script>

    function tablereflush() {
        // alert("tablereflush")
        layui.use(['element', 'layer', 'table'], function () {
            // table.render();

            table.reload('propertytable', {
                url: '${pageContext.request.contextPath}/aimodle/pageformodleproperty.do'
                , method: 'post'
                , request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                }
                , where: {
                    modleid: '${modle.modleid}'
                }
            });
        });
    }
</script>


</body>
</html>


<%--table.render({--%>
<%--elem: '#propertytable'--%>
<%--// ,data:--%>
<%--, url: '${pageContext.request.contextPath}/aimodle/pageformodleproperty.do'--%>
<%--, method: 'post'--%>
<%--, request: {--%>
<%--pageName: 'page' //页码的参数名称，默认：page--%>
<%--, limitName: 'pagesize' //每页数据量的参数名，默认：limit--%>
<%--}--%>
<%--, where: {--%>
<%--modleid: '${modle.modleid}'--%>
<%--}--%>

<%--, toolbar: '#toolbarDemo' //开启头部工具栏，并为其绑定左侧模板--%>
<%--, defaultToolbar: ['filter', 'exports', 'print', { //自定义头部工具栏右侧图标。如无需自定义，去除该参数即可--%>
<%--title: '提示'--%>
<%--, layEvent: 'LAYTABLE_TIPS'--%>
<%--, icon: 'layui-icon-tips'--%>
<%--}]--%>
<%--, title: '模型属性'--%>
<%--, limit: 10--%>
<%--, loading: true--%>

<%--, cols: [[--%>
<%--{type: 'checkbox', fixed: 'left',}--%>
<%--, {field: 'propertyid', title: 'ID', width: 80, fixed: 'left', sort: true, hide: true}--%>
<%--, {field: 'propertyName', title: '属性名', width: 80}--%>
<%--, {field: 'property', title: 'property', width: 100}--%>
<%--, {field: 'refrencealgorithmid', title: '引用模型id', width: 100, hide: true}--%>
<%--, {field: 'resource', title: 'opcserve', width: 80}--%>
<%--, {field: 'opctag', title: 'opc位号', width: 120}--%>
<%--, {field: 'datatype', title: '类型', width: 100}--%>
<%--, {fixed: 'right', title: '操作', toolbar: '#barDemo', width: 150}--%>

<%--]]--%>
<%--, page: true--%>
<%--});--%>

