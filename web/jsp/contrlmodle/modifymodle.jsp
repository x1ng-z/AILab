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
    <title>modifymodle</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
    <script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script src="${pageContext.request.contextPath}/js/contrlmodle/contrlmodle.js"></script>
</head>


<body>

<div id="bt_flush_pvtab" onclick="flush_pvtab()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>
<div id="bt_flush_sptab" onclick="flush_sptab()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>
<div id="bt_flush_mvtab" onclick="flush_mvtab()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>
<div id="bt_flush_fftab" onclick="flush_fftab()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>
<div id="bt_flush_resppvmvtab" onclick="flush_resppvmvtab()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>
<div id="bt_flush_resppvfftab" onclick="flush_resppvfftab()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>


<form class="layui-form" action="" method="post">
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>基础参数设置</legend>
    </fieldset>

    <div class="layui-form-item">
        <div class="layui-inline">
            <div class="layui-input-inline">
                <input type="text" name="modleid" value="${modle.modleId}" hidden/>
            </div>
        </div>
    </div>

    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">模型名称</label>
            <div class="layui-input-inline">
                <input type="text" name="modleName" lay-verify="required" autocomplete="off" class="layui-input"
                       value="${modle.modleName}">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">序列数量</label>
            <div class="layui-input-inline">
                <input type="number" name="N" lay-verify="required" autocomplete="off" placeholder="响应序列的数目"
                       class="layui-input" value="${modle.timeserise_N}" onmousewheel='scrollFunc()'>
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">预测步数</label>
            <div class="layui-input-inline">
                <input type="number" name="P" lay-verify="required|number" autocomplete="off" class="layui-input"
                       value="${modle.predicttime_P}" onmousewheel='scrollFunc()'>
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">输出步数</label>
            <div class="layui-input-inline">
                <input type="number" name="M" lay-verify="required|number" autocomplete="off" placeholder="计算后续多少步的输出"
                       class="layui-input" value="${modle.controltime_M}" onmousewheel='scrollFunc()'>
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">输出间隔(秒)</label>
            <div class="layui-input-inline">
                <input type="number" name="O" lay-verify="required|number" autocomplete="off" class="layui-input"
                       value="${modle.controlAPCOutCycle}" onmousewheel='scrollFunc()'>
            </div>
        </div>

        <div class="layui-form-item">
            <div class="layui-inline">
                <div class="layui-input-inline">
                    <input type="text" name="modlepinsId" value="${autopin.modlepinsId}" hidden/>
                </div>
            </div>
        </div>


        <div class="layui-inline">
            <label class="layui-form-label">手自动位号</label>
            <div class="layui-input-inline">
                <input type="text" name="autoTag" autocomplete="off" class="layui-input"
                       value="${autopin.modleOpcTag}">
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">auto来源</label>
            <div class="layui-input-inline">
                <select name="autoresource">
                    <option value="">请选择来源</option>
                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                        <c:choose>
                            <c:when test="${autopin.resource!=null&&autopin.resource.equals(opcres)}">
                                <option value="${opcres}" selected>${opcres}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${opcres}">${opcres}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                </select>
            </div>
        </div>
    </div>


    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" lay-filter="motifymodlesubmit">更新模型</button>
        <button type="reset" class="layui-btn layui-btn-primary"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">重置
        </button>
    </div>
</form>


<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>PV设置</legend>
</fieldset>

<script type="text/html" id="toolbarpv">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" id="addpvpinbt" lay-event="addpvpin"
                lay-href="${pageContext.request.contextPath}/contrlmodle/newmodelpvpin.do?pintype=pv&modleid=${modle.modleId}">
            添加pv&sp
        </button>
    </div>
</script>

<script type="text/html" id="toolbarmv">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" id="addmvpinbt" lay-event="addmvpin"
                lay-href="${pageContext.request.contextPath}/contrlmodle/newmodelmvpin.do?pintype=mv&modleid=${modle.modleId}">
            添加mv
        </button>
    </div>
</script>

<script type="text/html" id="toolbarff">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" id="addffpinbt" lay-event="addffpin"
                lay-href="${pageContext.request.contextPath}/contrlmodle/newmodelffpin.do?pintype=ff&modleid=${modle.modleId}">
            添加ff
        </button>
    </div>
</script>


<script type="text/html" id="toolbarrespon">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" id="addresponbt" lay-event="addrespon"
                lay-href="${pageContext.request.contextPath}/contrlmodle/newrespon.do?modleid=${modle.modleId}">
            添加响应
        </button>
    </div>
</script>

<script type="text/html" id="barpv">
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
</script>

<script type="text/html" id="barmv">
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
</script>


<script type="text/html" id="barff">
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
</script>

<script type="text/html" id="barrespon">
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
</script>


<table class="layui-hide" id="pvtable" lay-filter="pvtable"></table>



<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>MV设置</legend>
</fieldset>
<table class="layui-hide" id="mvtable" lay-filter="mvtable"></table>


<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>FF(前馈)设置</legend>
</fieldset>

<table class="layui-hide" id="fftable" lay-filter="fftable"></table>


<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>响应设置</legend>
</fieldset>

<table class="layui-hide" id="respontable" lay-filter="respontable"></table>


<script>
    var table;
    var element;
    var form;
    var layer;
    var opcvertaglayerindex;
    layui.use(['table', 'element', 'form', 'layer'], function () {
        table = layui.table;

        element = layui.element;
        form = layui.form;
        layer = layui.layer;//parent.layer === undefined ? layui.layer : parent.layer;
        form.render(); //更新全部
        form.render('select'); //刷新select选择框渲染
        form.on('submit(motifymodlesubmit)', function (data) {

            let index = layer.msg('修改中，请稍候', {icon: 16, time: false, shade: 0.8});
            $.ajax({
                url: "${pageContext.request.contextPath}/contrlmodle/savemodifymodle.do",
                async: true,
                data: {
                    "modlecontxt": JSON.stringify(data.field),
                },
                type: "POST",
                success: function (result) {
                    console.log(result);
                    layer.close(index);
                    let json = JSON.parse(result);
                    if (json['msg'] == "error") {
                        layer.msg("修改失败！");
                    } else {
                        layer.msg("修改成功！");
                        // newleft(json['modleName'],json['modleId'])
                        window.location.reload();
                    }
                    //window.location.href("result")
                    // var json = JSON.parse(result);
                }
            });

            console.log(JSON.stringify(data.field))
            return false;
        });

        pvtabrender(table);

        mvtabrender(table);

        fftabrender(table);
        respontabtabrender(table);


    });


</script>


<script>
    
    
    
    function pvtabrender(table) {

        table.render({
            elem: '#pvtable'
            // ,data:
            , url: '${pageContext.request.contextPath}/contrlmodle/pagemodelpin.do'
            , method: 'post'
            , request: {
                pageName: 'page' //页码的参数名称，默认：page
                , limitName: 'pagesize' //每页数据量的参数名，默认：limit
            }
            , where: {
                modleid: '${modle.modleId}',
                pintype: 'pv'
            }
            , toolbar: '#toolbarpv' //开启头部工具栏，并为其绑定左侧模板
            , defaultToolbar: ['filter', 'exports', 'print', { //自定义头部工具栏右侧图标。如无需自定义，去除该参数即可
                title: '提示'
                , layEvent: 'LAYTABLE_TIPS'
                , icon: 'layui-icon-tips'
            }]
            , title: '模型属性'
            , limit: 10
            , loading: true
            , minWidth: 150
            , cols: [[
                {type: 'checkbox', fixed: 'left',}
                , {field: 'modleid', title: 'modleid', width: 150, fixed: 'left', hide: true}
                , {field: 'pinid', title: 'pinid', width: 150, fixed: 'left', hide: true}
                , {field: 'modlePinName', title: '引脚名', width: 150}
                , {field: 'modleOpcTag', title: 'opc位号', width: 100}
                , {field: 'opcTagName', title: '注释', width: 100}
                , {fixed: 'right', title: '操作', toolbar: '#barpv', width: 300}
            ]]
            , page: true
        });

        //头工具栏事件
        table.on('toolbar(pvtable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addpvpin':
                    // var data = checkStatus.data;
                    // alert("sss"+$("#addopcservebt").attr('lay-href'));
                    newmodlepinwindow(layer, $("#addpvpinbt").attr('lay-href'),'pv',document);
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
        table.on('tool(pvtable)', function (obj) {
            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                layer.confirm('是否删除', function (index) {
                    deletecontrlpin(document,layer,data['pinid'],'pv','deletemodelpvpin','${pageContext.request.contextPath}');
                    // obj.del();
                    layer.close(index);
                });
            } else if (obj.event === 'edit') {
                modifymodlepinwindow(document,layer,data['modleid'],data['pinid'],'pv','modofymodelpvpin','${pageContext.request.contextPath}');
            }
        });

    }


    function mvtabrender(table) {
        table.render({
            elem: '#mvtable'
            // ,data:
            , url: '${pageContext.request.contextPath}/contrlmodle/pagemodelpin.do'
            , method: 'post'
            , request: {
                pageName: 'page' //页码的参数名称，默认：page
                , limitName: 'pagesize' //每页数据量的参数名，默认：limit
            }
            , where: {
                modleid: '${modle.modleId}',
                pintype: 'mv'
            }
            , toolbar: '#toolbarmv' //开启头部工具栏，并为其绑定左侧模板
            , defaultToolbar: ['filter', 'exports', 'print', { //自定义头部工具栏右侧图标。如无需自定义，去除该参数即可
                title: '提示'
                , layEvent: 'LAYTABLE_TIPS'
                , icon: 'layui-icon-tips'
            }]
            , title: '模型属性'
            , limit: 10
            , loading: true
            , minWidth: 150
            , cols: [[
                {type: 'checkbox', fixed: 'left',}
                , {field: 'modleid', title: 'modleid', width: 150, fixed: 'left', hide: true}
                , {field: 'pinid', title: 'pinid', width: 150, fixed: 'left', hide: true}
                , {field: 'modlePinName', title: '引脚名', width: 150}
                , {field: 'modleOpcTag', title: 'opc位号', width: 100}
                , {field: 'opcTagName', title: '注释', width: 100}
                , {fixed: 'right', title: '操作', toolbar: '#barmv', width: 300}
            ]]
            , page: true
        });

        //头工具栏事件
        table.on('toolbar(mvtable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addmvpin':
                    // var data = checkStatus.data;
                    // alert("sss"+$("#addopcservebt").attr('lay-href'));
                    newmodlepinwindow(layer, $("#addmvpinbt").attr('lay-href'),'mv',document);
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
        table.on('tool(mvtable)', function (obj) {
            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                layer.confirm('是否删除', function (index) {
                    deletecontrlpin(document,layer,data['pinid'],'mv','deletemodelmvpin','${pageContext.request.contextPath}');
                    // obj.del();
                    layer.close(index);
                });
            } else if (obj.event === 'edit') {
                modifymodlepinwindow(document,layer,data['modleid'],data['pinid'],'mv','modofymodelmvpin','${pageContext.request.contextPath}');
            }
        });

    }



    function fftabrender(table) {
        table.render({
            elem: '#fftable'
            // ,data:
            , url: '${pageContext.request.contextPath}/contrlmodle/pagemodelpin.do'
            , method: 'post'
            , request: {
                pageName: 'page' //页码的参数名称，默认：page
                , limitName: 'pagesize' //每页数据量的参数名，默认：limit
            }
            , where: {
                modleid: '${modle.modleId}',
                pintype: 'ff'
            }
            , toolbar: '#toolbarff' //开启头部工具栏，并为其绑定左侧模板
            , defaultToolbar: ['filter', 'exports', 'print', { //自定义头部工具栏右侧图标。如无需自定义，去除该参数即可
                title: '提示'
                , layEvent: 'LAYTABLE_TIPS'
                , icon: 'layui-icon-tips'
            }]
            , title: '模型属性'
            , limit: 10
            , loading: true
            , minWidth: 150
            , cols: [[
                {type: 'checkbox', fixed: 'left',}
                , {field: 'modleid', title: 'modleid', width: 150, fixed: 'left', hide: true}
                , {field: 'pinid', title: 'pinid', width: 150, fixed: 'left', hide: true}
                , {field: 'modlePinName', title: '引脚名', width: 150}
                , {field: 'modleOpcTag', title: 'opc位号', width: 100}
                , {field: 'opcTagName', title: '注释', width: 100}
                , {fixed: 'right', title: '操作', toolbar: '#barff', width: 300}
            ]]
            , page: true
        });

        //头工具栏事件
        table.on('toolbar(fftable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addffpin':
                    // var data = checkStatus.data;
                    // alert("sss"+$("#addopcservebt").attr('lay-href'));
                    newmodlepinwindow(layer, $("#addffpinbt").attr('lay-href'),'ff',document);
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
        table.on('tool(fftable)', function (obj) {
            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                layer.confirm('是否删除', function (index) {
                    deletecontrlpin(document,layer,data['pinid'],'ff','deletemodelffpin','${pageContext.request.contextPath}');
                    // obj.del();
                    layer.close(index);
                });
            } else if (obj.event === 'edit') {
                modifymodlepinwindow(document,layer,data['modleid'],data['pinid'],'ff','modofymodelffpin','${pageContext.request.contextPath}');
            }
        });


    }


    function respontabtabrender(table) {
        table.render({
            elem: '#respontable'
            // ,data:
            , url: '${pageContext.request.contextPath}/contrlmodle/pagerespon.do'
            , method: 'post'
            , request: {
                pageName: 'page' //页码的参数名称，默认：page
                , limitName: 'pagesize' //每页数据量的参数名，默认：limit
            }
            , where: {
                modleid: '${modle.modleId}'
            }
            , toolbar: '#toolbarrespon' //开启头部工具栏，并为其绑定左侧模板
            , defaultToolbar: ['filter', 'exports', 'print', { //自定义头部工具栏右侧图标。如无需自定义，去除该参数即可
                title: '提示'
                , layEvent: 'LAYTABLE_TIPS'
                , icon: 'layui-icon-tips'
            }]
            , title: '模型属性'
            , limit: 10
            , loading: true
            , minWidth: 150
            , cols: [[
                {type: 'checkbox', fixed: 'left',}
                , {field: 'modleid', title: 'modleid', width: 150, fixed: 'left', hide: true}
                , {field: 'responid', title: 'responid', width: 150, fixed: 'left', hide: true}
                , {field: 'input', title: 'input', width: 150}
                , {field: 'output', title: 'output', width: 150}
                , {field: 'K', title: 'K', width: 150}
                , {field: 'T', title: 'T', width: 150}
                , {field: 'Tau', title: 'Tau', width: 150}
                , {fixed: 'right', title: '操作', toolbar: '#barrespon', width: 300}
            ]]
            , page: true
        });

        //头工具栏事件
        table.on('toolbar(respontable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addrespon':
                    // var data = checkStatus.data;
                    // alert("sss"+$("#addopcservebt").attr('lay-href'));
                    newmresponwindow(layer, $("#addresponbt").attr('lay-href'),document);
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
        table.on('tool(respontable)', function (obj) {
            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                layer.confirm('是否删除', function (index) {
                    deleterespon(document,layer,data['responid'],'deletemodelrespon','${pageContext.request.contextPath}');
                    // obj.del();
                    layer.close(index);
                });
            } else if (obj.event === 'edit') {
                modifyresponwindow(document,layer,data['modleid'],data['responid'],'modofyrespon','${pageContext.request.contextPath}');
            }
        });


    }








    function verifyrespon(param) {
        if (param == "") {
            return true;
        } else {
            //alert("function in " + param);
            let pattern = new RegExp("^{k:[\\d|.|-]+,t:[\\d|.]+,tao:[\\d|.]+}$");
            return pattern.test(param);
        }
    }

    function scrollFunc(evt) {
        evt = evt || window.event;
        if (evt.preventDefault) {
            // Firefox
            evt.preventDefault();
            evt.stopPropagation();
        } else {
            // IE
            evt.cancelBubble = true;
            evt.returnValue = false;
        }
        return false;
    }

    function flush_pvtab() {
        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;
            table.reload('pvtable', {
                elem: '#pvtable'
                // ,data:
                , url: '${pageContext.request.contextPath}/contrlmodle/pagemodelpin.do'
                , method: 'post'
                , request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                }
                , where: {
                    modleid: '${modle.modleId}',
                    pintype: 'pv'
                }
            });

        });
    }

    function flush_sptab() {

    }

    function flush_mvtab() {

        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;
            table.reload('mvtable', {
                elem: '#mvtable'
                // ,data:
                , url: '${pageContext.request.contextPath}/contrlmodle/pagemodelpin.do'
                , method: 'post'
                , request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                }
                , where: {
                    modleid: '${modle.modleId}',
                    pintype: 'mv'
                }
            });

        });

    }




    function flush_fftab() {

        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;
            table.reload('fftable', {
                elem: '#fftable'
                // ,data:
                , url: '${pageContext.request.contextPath}/contrlmodle/pagemodelpin.do'
                , method: 'post'
                , request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                }
                , where: {
                    modleid: '${modle.modleId}',
                    pintype: 'ff'
                }
            });

        });

    }

    function flush_resppvmvtab() {
        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;
            table.reload('respontable', {
                elem: '#respontable'
                // ,data:
                , url: '${pageContext.request.contextPath}/contrlmodle/pagerespon.do'
                , method: 'post'
                , request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                }
                , where: {
                    modleid: '${modle.modleId}'
                }
            });

        });







    }

    function flush_resppvfftab() {

    }

</script>
</body>
</html>
