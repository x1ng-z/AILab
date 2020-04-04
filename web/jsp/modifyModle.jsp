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
    <meta charset="utf-8">
    <title>Layui</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/layui/css/layui.css" media="all">
</head>


<body>
<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>模型修改</legend>
</fieldset>

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
                <input type="text" name="N" lay-verify="required" autocomplete="off" placeholder="响应序列的数目"
                       class="layui-input" value="${modle.timeserise_N}">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">预测步数</label>
            <div class="layui-input-inline">
                <input type="text" name="P" lay-verify="required|number" autocomplete="off" class="layui-input"
                       value="${modle.predicttime_P}">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">输出步数</label>
            <div class="layui-input-inline">
                <input type="text" name="M" lay-verify="required|number" autocomplete="off" placeholder="计算后续多少步的输出"
                       class="layui-input" value="${modle.controltime_M}">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">输出间隔(秒)</label>
            <div class="layui-input-inline">
                <input type="text" name="O" lay-verify="required|number" autocomplete="off" class="layui-input"
                       value="${modle.controlAPCOutCycle}">
            </div>
        </div>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>PV设置</legend>
    </fieldset>
    <div class="layui-form-item">
        <c:forEach var="pv" items="${pvlist}" varStatus="Count">
            <div class="layui-inline">
                <label class="layui-form-label">pv${Count.count}</label>
                <div class="layui-input-inline">
                    <input type="text" name="pv${Count.count}" autocomplete="off" class="layui-input"
                           placeholder="opc位号" value="${pv}">
                </div>
            </div>
        </c:forEach>
    </div>

    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>Q设置:影响反馈与目标值的偏差度，Q越大算法则要求PV与设定值相差越小</legend>
    </fieldset>
    <div class="layui-form-item">
        <c:forEach var="q" items="${qlist}" varStatus="Count">
            <div class="layui-inline">
                <label class="layui-form-label">q${Count.count}</label>
                <div class="layui-input-inline">
                    <input type="text" name="q${Count.count}" autocomplete="off" class="layui-input"
                           placeholder="pv${Count.count}的Q" value="${q}">
                </div>
            </div>
        </c:forEach>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>SP设置</legend>
    </fieldset>
    <div class="layui-form-item">
        <c:forEach var="sp" items="${splist}" varStatus="Count">
            <div class="layui-inline">
                <label class="layui-form-label">sp${Count.count}</label>
                <div class="layui-input-inline">
                    <input type="text" name="sp${Count.count}" autocomplete="off" class="layui-input"
                           placeholder="opc位号" value="${sp}">
                </div>
            </div>

        </c:forEach>
    </div>

    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>MV设置</legend>
    </fieldset>
    <div class="layui-form-item">
        <c:forEach var="mv" items="${mvlist}" varStatus="Count">
            <div class="layui-inline">
                <label class="layui-form-label">mv${Count.count}</label>
                <div class="layui-input-inline">
                    <input type="text" name="mv${Count.count}" autocomplete="off" class="layui-input"
                           placeholder="opc位号" value="${mv}">
                </div>
            </div>
        </c:forEach>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>R设置:影响MV调节幅度，R越大算法则要求每次的deltaMV越小</legend>
    </fieldset>
    <div class="layui-form-item">
        <c:forEach var="r" items="${rlist}" varStatus="Count">
            <div class="layui-inline">
                <label class="layui-form-label">r${Count.count}</label>
                <div class="layui-input-inline">
                    <input type="text" name="r${Count.count}" autocomplete="off" class="layui-input"
                           placeholder="mv${Count.count}的R" value="${r}">
                </div>
            </div>
        </c:forEach>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>MV上限设置</legend>
    </fieldset>
    <div class="layui-form-item">
        <c:forEach var="mvup" items="${mvuplist}" varStatus="Count">
            <div class="layui-inline">
                <label class="layui-form-label">mvup${Count.count}</label>
                <div class="layui-input-inline">
                    <input type="text" name="mvup${Count.count}" autocomplete="off" class="layui-input"
                           placeholder="mv${Count.count}上限" value="${mvup.modleOpcTag}">
                </div>
            </div>

            <div class="layui-inline">
                <label class="layui-form-label">mv${Count.count}上限来源</label>
                <div class="layui-input-inline">
                    <select name="mvup${Count.count}resource">
                        <option value="">请选择来源</option>
                        <c:choose>
                            <c:when test="${mvup.resource eq 'opc'}">
                                <option value="opc" selected>opc</option>
                            </c:when>
                            <c:otherwise>
                                <option value="opc">opc</option>
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                            <c:when test="${mvup.resource eq 'constant'}">
                                <option value="constant" selected>常量</option>
                            </c:when>
                            <c:otherwise>
                                <option value="constant">常量</option>
                            </c:otherwise>
                        </c:choose>

                    </select>
                </div>
            </div>
        </c:forEach>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>MV下限设置</legend>
    </fieldset>
    <div class="layui-form-item">
        <c:forEach var="mvdown" items="${mvdownlist}" varStatus="Count">
            <div class="layui-inline">
                <label class="layui-form-label">mvdown${Count.count}</label>
                <div class="layui-input-inline">
                    <input type="text" name="mvdown${Count.count}" autocomplete="off" class="layui-input"
                           placeholder="mv${Count.count}下限" value="${mvdown.modleOpcTag}">
                </div>
            </div>

            <div class="layui-inline">
                <label class="layui-form-label">mv${Count.count}下限来源</label>
                <div class="layui-input-inline">
                    <select name="mvdown${Count.count}resource">
                        <option value="">请选择来源</option>
                        <c:choose>
                            <c:when test="${mvdown.resource eq 'opc'}">
                                <option value="opc" selected>opc</option>
                            </c:when>
                            <c:otherwise>
                                <option value="opc">opc</option>
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                            <c:when test="${mvdown.resource eq 'constant'}">
                                <option value="constant" selected>常量</option>
                            </c:when>
                            <c:otherwise>
                                <option value="constant">常量</option>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>
            </div>
        </c:forEach>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>MV反馈设置</legend>
    </fieldset>
    <div class="layui-form-item">
        <c:forEach var="mvfb" items="${mvfblist}" varStatus="Count">
            <div class="layui-inline">
                <label class="layui-form-label">mvfb${Count.count}</label>
                <div class="layui-input-inline">
                    <input type="text" name="mvfb${Count.count}" autocomplete="off" class="layui-input"
                           placeholder="mv${Count.count}反馈位号" value="${mvfb}">
                </div>
            </div>
        </c:forEach>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>FF(前馈)设置</legend>
    </fieldset>
    <div class="layui-form-item">
        <c:forEach var="ff" items="${fflist}" varStatus="Count">
            <div class="layui-inline">
                <label class="layui-form-label">ff${Count.count}</label>
                <div class="layui-input-inline">
                    <input type="text" name="ff${Count.count}" autocomplete="off" class="layui-input"
                           placeholder="ff${Count.count}位号" value="${ff}">
                </div>
            </div>
        </c:forEach>
    </div>

    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>FF(前馈)上限设置</legend>
    </fieldset>
    <div class="layui-form-item">
        <c:forEach var="ffup" items="${ffuplist}" varStatus="Count">
            <div class="layui-inline">
                <label class="layui-form-label">ffup${Count.count}</label>
                <div class="layui-input-inline">
                    <input type="text" name="ffup${Count.count}" autocomplete="off" class="layui-input"
                           placeholder="ff${Count.count}位号" value="${ffup.modleOpcTag}">
                </div>
            </div>

            <div class="layui-inline">
                <label class="layui-form-label">ffup${Count.count}上限来源</label>
                <div class="layui-input-inline">
                    <select name="ffup${Count.count}resource">
                        <option value="">请选择来源</option>
                        <c:choose>
                            <c:when test="${ffup.resource eq 'opc'}">
                                <option value="opc" selected>opc</option>
                            </c:when>
                            <c:otherwise>
                                <option value="opc">opc</option>
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                            <c:when test="${ffup.resource eq 'constant'}">
                                <option value="constant" selected>常量</option>
                            </c:when>
                            <c:otherwise>
                                <option value="constant">常量</option>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>
            </div>
        </c:forEach>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>FF(前馈)下限设置</legend>
    </fieldset>
    <div class="layui-form-item">
        <c:forEach var="ffdown" items="${ffdownlist}" varStatus="Count">
            <div class="layui-inline">
                <label class="layui-form-label">ffdown${Count.count}</label>
                <div class="layui-input-inline">
                    <input type="text" name="ffdown${Count.count}" autocomplete="off" class="layui-input"
                           placeholder="ffdown${Count.count}位号/值" value="${ffdown.modleOpcTag}">
                </div>
            </div>

            <div class="layui-inline">
                <label class="layui-form-label">ffdown${Count.count}下限来源</label>
                <div class="layui-input-inline">
                    <select name="ffdown${Count.count}resource">
                        <option value="">请选择来源</option>
                        <c:choose>
                            <c:when test="${ffdown.resource eq 'opc'}">
                                <option value="opc" selected>opc</option>
                            </c:when>
                            <c:otherwise>
                                <option value="opc">opc</option>
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                            <c:when test="${ffdown.resource eq 'constant'}">
                                <option value="constant" selected>常量</option>
                            </c:when>
                            <c:otherwise>
                                <option value="constant">常量</option>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>
            </div>
        </c:forEach>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>MV对PV的响应设置 形如{k:10,t:180,tao:200}  英文模式输入!</legend>
    </fieldset>

    <table class="layui-table" lay-data='{"data":${mvresp}, "id":"mvresp"}' lay-filter="mvresp">
        <thead>
        <tr>
            <th lay-data="{field:'mv', width:80, }">MV</th>
            <c:forEach var="pv" items="${pvlist}" varStatus="Count">
                <th lay-data="{field:'pv${Count.count}', width:120,  edit: 'text'}">pv${Count.count}</th>
            </c:forEach>
        </tr>
        </thead>
    </table>

    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>前馈(FF)对PV的响应设置 形如{k:10,t:180,tao:200}  英文模式输入!</legend>
    </fieldset>
    <table class="layui-table" lay-data='{"data":${ffresp}, "id":"ffresp"}' lay-filter="ffresp">
        <thead>
        <tr>
            <th lay-data="{field:'ff', width:80, }">FF</th>
            <c:forEach var="pv" items="${pvlist}" varStatus="Count">
                <th lay-data="{field:'pv${Count.count}', width:120,  edit: 'text'}">pv${Count.count}</th>
            </c:forEach>
        </tr>
        </thead>
    </table>

    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" lay-filter="demo1">立即提交</button>
        <button type="reset" class="layui-btn layui-btn-primary">重置</button>
    </div>

</form>

<script src="${pageContext.request.contextPath}/layui/layui.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
<script>
    var table;

    layui.use('form', function () {
        var form = layui.form;
        form.render(); //更新全部
        form.render('select'); //刷新select选择框渲染

        //各种基于事件的操作，下面会有进一步介绍

        //监听提交
        form.on('submit(demo1)', function (data) {
            // layer.alert(JSON.stringify(data.field), {
            //     title: '最终的提交信息'
            // })

            console.log(table.cache);
            console.log(typeof table.cache);

            $.ajax({
                url: "${pageContext.request.contextPath}/modle/savemodle.do" + "?" + Math.random(),
                async: true,
                data: {
                    "modle": JSON.stringify(data.field),
                    "mvresp": JSON.stringify(table.cache.mvresp),
                    "ffresp": JSON.stringify(table.cache.ffresp),
                },
                type: "POST",
                success: function (result) {
                    console.log(result);
                    location.href=result;
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


</body>
</html>

