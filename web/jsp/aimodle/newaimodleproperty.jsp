<%--
  Created by IntelliJ IDEA.
  User: zaixz
  Date: 2020/9/8
  Time: 18:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>new ai modle property</title>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
    <script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script src="${pageContext.request.contextPath}/js/aimodle/modifyaimodle.js"></script>
    <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
    <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
</head>
<body style="width:850px;">
<form class="layui-form" action="">
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>属性设置</legend>
    </fieldset>
    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">属性</label>
            <div class="layui-input-inline">
                <input type="text" name="property" lay-verify="required" autocomplete="off" placeholder="属性，如面积:area"
                       class="layui-input">
            </div>
        </div>


        <div class="layui-inline">
            <label class="layui-form-label">属性名称</label>
            <div class="layui-input-block">
                <input type="text" name="propertyname" lay-verify="required" placeholder="请输入" autocomplete="off"
                       class="layui-input">
            </div>
        </div>


        <div class="layui-inline">
            <label class="layui-form-label">属性类型</label>
            <div class="layui-input-inline">
                <select name="datatype">
                    <option value="">请选择类型</option>
                    <c:forEach var="type" items="${datatype}" varStatus="Count">
                        <option value="${type}">${type}</option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">反写位号</label>
            <div class="layui-input-inline">
                <input type="text" name="opctag" autocomplete="off" class="layui-input"
                       placeholder="opc位号">
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">反写位号来源</label>
            <div class="layui-input-inline">
                <select name="source">
                    <option value="">请选择来源</option>
                    <c:forEach var="opcres" items="${resource}" varStatus="Count">
                        <option value="${opcres}">${opcres}</option>
                    </c:forEach>
                </select>
            </div>
        </div>


        <div class="layui-inline">
            <div class="layui-input-inline">
                <input type="text" name="modleid" value="${modleid}" autocomplete="off" style="visibility: hidden"
                       class="layui-input">
            </div>
        </div>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>属性滤波:一阶滤波(滤波系数0&ltalphe&lt1，数值越小滤波越强)和移动平均滤波(滤波系数0&ltalphe的整数，数值越大滤波越强)</legend>
    </fieldset>
    <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">滤波算法选择</label>
                <div class="layui-input-inline">
                    <select name="filtername">
                        <option value="">滤波选择</option>
                        <option value="mvav">移动平均</option>
                        <option value="fodl">一阶滤波</option>
                    </select>
                </div>
            </div>

            <div class="layui-inline">
                <label class="layui-form-label">滤波系数</label>
                <div class="layui-input-inline">
                    <input type="number" name="filtercoef" autocomplete="off" class="layui-input"
                           placeholder="滤波系数" onmousewheel='scrollFunc()'>
                </div>
            </div>

            <div class="layui-inline">
                <label class="layui-form-label">滤波输出OPC位号</label>
                <div class="layui-input-inline">
                    <input type="text" name="filteropctag" autocomplete="off" class="layui-input"
                           placeholder="滤波输出opc位号">
                </div>
            </div>

            <div class="layui-inline">
                <label class="layui-form-label">滤波输出OPC来源</label>
                <div class="layui-input-inline">
                    <select name="filterresource">
                        <option value="">请选择来源</option>
                        <c:forEach var="opcres" items="${resource}" varStatus="Count">
                            <option value="${opcres}">${opcres}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
    </div>


    <div class="layui-form-item">
        <div class="layui-input-block">
            <button type="submit" class="layui-btn" lay-submit="" lay-filter="newmodleprosb" id="newmodleprosubmit"
                    style="visibility: hidden">
                立即提交
            </button>
            <button type="reset" class="layui-btn layui-btn-primary" style="visibility: hidden">重置</button>
        </div>
    </div>


</form>

</body>
</html>

<script>
    layui.use(['form'], function () {
        let form = layui.form
            , layer = layui.layer
        form.render();

        form.on('submit(newmodleprosb)', function (data) {
            console.log(JSON.stringify(data.field));

            let index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});

            $.ajax({
                url: "${pageContext.request.contextPath}/aimodle/saveaimodleproperty.do" + "?" + Math.random(),
                async: true,
                data: {
                    "aimodlepropertycontext": JSON.stringify(data.field)
                },
                type: "POST",
                success: function (result) {
                    console.log(result);
                    layer.close(index);
                    let json = JSON.parse(result);
                    if (json['msg'] == "error") {
                        layer.msg("创建属性失败！");
                    } else {
                        layer.msg("创建属性成功！");
                        console.log("创建属性成功！")
                        successAndClosenewaimodlepropertyWD();
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


    });










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
