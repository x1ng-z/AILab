<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core_1_1" %>
<%--
  Created by IntelliJ IDEA.
  User: zaixz
  Date: 2020/9/21
  Time: 9:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="shortcut icon"
          href="../img/favicon.ico" type="image/x-icon"/>
    <meta charset="utf-8">
    <title>new pv pin</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
</head>
<body>

<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>RESP设置</legend>
</fieldset>

<form class="layui-form" action="" method="post">
    <div class="layui-form-item layui-collapse" lay-accordion>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">响应属性设置</h2>
            <div class="layui-colla-content  layui-show">
                <div class="layui-form-item">

                    <input type="text" name="modleid" value="${modleid}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <input type="text" name="responid" value="" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">输入引脚选择</label>
                        <div class="layui-input-inline">
                            <select name="inputpinName" lay-verify="required" lay-search>
                                <option value="">请选择或搜索输入引脚</option>
                                <c:forEach var="pinindex" items="${userinputpinscope}" varStatus="Count">
                                    <option value="${pinindex.modlePinName}">${pinindex.modlePinName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">输出引脚选择</label>
                        <div class="layui-input-inline">
                            <select name="outputpinName" lay-verify="required" lay-search>
                                <option value="">请选择或搜索输出引脚</option>
                                <c:forEach var="pinindex" items="${useroutputpinscope}" varStatus="Count">
                                    <option value="${pinindex.modlePinName}">${pinindex.modlePinName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">K</label>
                        <div class="layui-input-inline">
                            <input type="number" name="K" autocomplete="off" class="layui-input"
                                   placeholder="K值" lay-verify="required" onmousewheel='scrollFunc()'>
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">T</label>
                        <div class="layui-input-inline">
                            <input type="number" name="T" autocomplete="off" class="layui-input"
                                   placeholder="T值" lay-verify="required" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">Tau</label>
                        <div class="layui-input-inline">
                            <input type="number" name="Tau" autocomplete="off" class="layui-input"
                                   placeholder="Tau值" lay-verify="required" onmousewheel='scrollFunc()'>
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">作用比例</label>
                        <div class="layui-input-inline">
                            <input type="number" name="effectRatio" autocomplete="off" class="layui-input"
                                   placeholder="pv对mv的输出占比" lay-verify="required" onmousewheel='scrollFunc()'>
                        </div>
                    </div>


                </div>
            </div>
        </div>

    </div>


    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" id="newresponsubmitbt"
                lay-filter="newresponsubmit"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">立即提交
        </button>
        <button type="reset" class="layui-btn layui-btn-primary"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">重置
        </button>
    </div>
</form>

</body>

<script>

    function scrollFunc(evt) {
        evt = evt || window.event;
        if(evt.preventDefault) {
            // Firefox
            evt.preventDefault();
            evt.stopPropagation();
        } else {
            // IE
            evt.cancelBubble=true;
            evt.returnValue = false;
        }
        return false;
    }

    layui.use(['element', 'form', 'layer'], function () {
        var element = layui.element;
        var form = layui.form,
            layer = layui.layer; //parent.layer === undefined ? layui.layer : parent.layer;
        form.render(); //更新全部
        form.render('select'); //刷新select选择框渲染
        //监听提交
        form.on('submit(newresponsubmit)', function (data) {
            let index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});
            $.ajax({
                url: "${pageContext.request.contextPath}/contrlmodle/savenewrespon.do",
                async: true,
                data: {
                    "responcontext": JSON.stringify(data.field),
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
                        let index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                        parent.layer.close(index); //再执行关闭
                        $("#bt_flush_fftab", parent.document).trigger('click')
                    }

                }
            });


            console.log(JSON.stringify(data.field))
            return false;
        });
    });

</script>


<script>

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
</html>
