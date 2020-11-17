<%--
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
    <title>newmodle</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">

    <script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script src="${pageContext.request.contextPath}/js/contrlmodle/contrlmodle.js"></script>
</head>

<%--position:absolute;left:0;top:0;  line-height: 22px; text-align:left;background-color: #393D49; color: #fff; font-weight: 300;--%>
<body style="width:100%; height:100%;">

<form class="layui-form" action="" method="post">
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>模型基础参数设置</legend>
    </fieldset>
    <input type="text" name="modleid" value="" hidden/>
    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">模型名称</label>
            <div class="layui-input-inline">
                <input type="text" name="modleName" lay-verify="required|text" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">序列数量</label>
            <div class="layui-input-inline">
                <input type="number" name="N" lay-verify="required|number" autocomplete="off" placeholder="响应序列的数目"
                       class="layui-input" onmousewheel='scrollFunc()'>
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">预测步数</label>
            <div class="layui-input-inline">
                <input type="number" name="P" lay-verify="required|number" autocomplete="off" class="layui-input"
                       onmousewheel='scrollFunc()'>
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">输出步数</label>
            <div class="layui-input-inline">
                <input type="number" name="M" lay-verify="required|number" autocomplete="off" placeholder="计算后续多少步的输出"
                       class="layui-input" onmousewheel='scrollFunc()'>
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">输出间隔(秒)</label>
            <div class="layui-input-inline">
                <input type="number" name="O" lay-verify="required|number" autocomplete="off" class="layui-input"
                       onmousewheel='scrollFunc()'>
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">手自动位号</label>
            <div class="layui-input-inline">
                <input type="text" name="autoTag" autocomplete="off" class="layui-input"
                       placeholder="opc位号">
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">auto位号来源</label>
            <div class="layui-input-inline">
                <select name="autoresource">
                    <option value="">请选择来源</option>
                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                        <option value="${opcres}">${opcres}</option>
                    </c:forEach>
                </select>
            </div>
        </div>


        <div class="layui-inline">
            <label class="layui-form-label">PV对Dmv作用分配方式</label>
            <div class="layui-input-inline">
                <select name="runstyle" lay-verify="required">
                    <option value="">请选择分配方式</option>
                        <option value="0">最小误差</option>
                        <option value="1">手动分配</option>
                </select>
            </div>
        </div>


    </div>

    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" id="newmodlesubmitbt" lay-filter="newmodlesubmit"   style="visibility: hidden">立即提交</button>
        <button type="reset" class="layui-btn layui-btn-primary"   style="visibility: hidden">重置</button>
    </div>

</form>

<script>
    var table;
    var layer;
    layui.use(['element', 'layer', 'form'], function () {
        var element = layui.element;
        var form = layui.form,
            layer = parent.layer === undefined ? layui.layer : parent.layer;
        form.render(); //更新全部
        form.render('select'); //刷新select选择框渲染
        //各种基于事件的操作，下面会有进一步介绍

        //监听提交
        form.on('submit(newmodlesubmit)', function (data) {

            var index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});
            $.ajax({
                url: "${pageContext.request.contextPath}/contrlmodle/savenewmodle.do" + "?" + Math.random(),
                async: true,
                data: {
                    "modlecontext": JSON.stringify(data.field),
                },
                type: "POST",
                success: function (result) {
                    console.log(result);
                    layer.close(index);
                    let json = JSON.parse(result);
                    if (json['msg'] == "error") {
                        layer.msg("创建失败！");
                    } else {
                        layer.msg("创建成功！");
                        //newleft(json['modleName'],json['modleId'])
                        console.log('${pageContext.request.contextPath}');
                        successAndClosenewcontrlmodleWD(json['modlename'],json['modleid'],'${pageContext.request.contextPath}');
                    }


                    //window.location.href("result")
                    // var json = JSON.parse(result);
                }
            });


            console.log(JSON.stringify(data.field))
            return false;
        });
    });


    layui.use('table', function () {
        table = layui.table;

        //监听单元格编辑
        table.on('edit(mvresp)', function (obj) {
            var value = obj.value //得到修改后的值
                , data = obj.data //得到所在行所有键值
                , field = obj.field; //得到字段
            // layer.msg('[ID: '+ data.mv +'] ' + field + ' 字段更改为：'+ value);
            console.log('[ID: ' + data.mv + '] ' + field + ' 字段更改为：' + value);
            console.log(table.cache);

        });

        table.on('edit(ffresp)', function (obj) {
            var value = obj.value //得到修改后的值
                , data = obj.data //得到所在行所有键值
                , field = obj.field; //得到字段
            // layer.msg('[ID: '+ data.ff +'] ' + field + ' 字段更改为：'+ value);
            console.log('[ID: ' + data.ff + '] ' + field + ' 字段更改为：' + value);
            console.log(table.cache);

        });
    });


</script>


<script>


    function verifyrespon(param) {
        if (param == "") {
            return true;
        } else {
            //alert("function in " + param)
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
</script>


</body>
</html>

