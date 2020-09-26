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
    <legend>FF(前馈)设置</legend>
</fieldset>

<form class="layui-form" action="" method="post">
    <div class="layui-form-item layui-collapse" lay-accordion>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF属性设置</h2>
            <div class="layui-colla-content  layui-show">
                <div class="layui-form-item">

                    <input type="text" name="modleid" value="${ffpin.reference_modleId}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <input type="text" name="ffpinid" value="${ffpin.modlepinsId}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">FF引脚选择</label>
                        <div class="layui-input-inline">
                            <select name="pinName" lay-verify="required">
                                <option value="">请选择引脚名称</option>
                                <c:forEach var="pinindex" items="${unuserpinscope}" varStatus="Count">
                                    <c:choose>
                                        <c:when test="${(Count.count==1) }">
                                            <option value="${pintype}${pinindex}"
                                                    selected>${pintype}${pinindex}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${pintype}${pinindex}">${pintype}${pinindex}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">ff位号</label>
                        <div class="layui-input-inline">
                            <input type="text" name="ff" autocomplete="off" class="layui-input"
                                   placeholder="ff位号" lay-verify="required" value="${ffpin.modleOpcTag}">
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">ff中文注释</label>
                        <div class="layui-input-inline">
                            <input type="text" name="ffcomment" autocomplete="off" class="layui-input"
                                   placeholder="ff中文注释" value="${ffpin.opcTagName}">
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">ffopc位号来源</label>
                        <div class="layui-input-inline">
                            <select name="ffresource" lay-verify="required">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <c:choose>
                                        <c:when test="${(ffpin.resource!=null) && (ffpin.resource.equals(opcres))}">
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
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF(前馈)滤波器设置</h2>
            <div class="layui-colla-content  layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">
                    一阶滤波(滤波系数0&ltalphe&lt1，数值越小滤波越强)和移动平均(滤波系数0&ltalphe的整数，数值越大滤波越强)</p>
                <div class="layui-form-item">

                    <input type="text" name="ffpinfilterid" value="${ffpin.filter.pk_filterid}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
                    <div class="layui-inline">
                        <label class="layui-form-label">ff滤波算法选择</label>
                        <div class="layui-input-inline">
                            <select name="filternameff">
                                <option value="">滤波选择</option>
                                <c:choose>
                                    <c:when test="${ffpin.filter.filtername eq 'mvav'}">
                                        <option value="mvav" selected>移动平均</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="mvav">移动平均</option>
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <c:when test="${ffpin.filter.filtername eq 'fodl'}">
                                        <option value="fodl" selected>一阶滤波</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="fodl">一阶滤波</option>
                                    </c:otherwise>
                                </c:choose>
                            </select>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">ff滤波系数</label>
                        <div class="layui-input-inline">
                            <input type="number" name="filtercoefff" autocomplete="off" class="layui-input"
                                   placeholder="ff滤波系数" onmousewheel='scrollFunc()' value="${ffpin.filter.getcoeff()}">
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">ff滤波输出OPC位号</label>
                        <div class="layui-input-inline">
                            <input type="text" name="filteropctagff" autocomplete="off" class="layui-input"
                                   placeholder="ff滤波输出opc位号" value="${ffpin.filter.backToDCSTag}">
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">ff滤波输出OPC位号来源</label>
                        <div class="layui-input-inline">
                            <select name="filterffresource">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <c:choose>
                                        <c:when test="${ffpin.filter.opcresource!=null&&ffpin.filter.opcresource.equals(opcres)}">
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
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF(前馈)上限设置</h2>
            <div class="layui-colla-content  layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">FF值的置信区间上限</p>
                <div class="layui-form-item">

                    <input type="text" name="ffuppinid" value="${ffuppin.modlepinsId}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
                    <div class="layui-inline">
                        <label class="layui-form-label">ffup</label>
                        <div class="layui-input-inline">
                            <input type="text" name="ffup" autocomplete="off" class="layui-input"
                                   placeholder="ff位号" lay-verify="required" value="${ffuppin.modleOpcTag}">
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">ffup上限来源</label>
                        <div class="layui-input-inline">
                            <select name="ffupresource" lay-verify="required">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <c:choose>
                                        <c:when test="${ffuppin.resource!=null&&ffuppin.resource.equals(opcres)}">
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
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF(前馈)下限设置</h2>
            <div class="layui-colla-content  layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">FF值的置信区间下限</p>
                <div class="layui-form-item">

                    <input type="text" name="ffdownpinid" value="${ffdownpin.modlepinsId}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
                    <div class="layui-inline">
                        <label class="layui-form-label">ffdown</label>
                        <div class="layui-input-inline">
                            <input type="text" name="ffdown" autocomplete="off" class="layui-input"
                                   placeholder="ffdown位号/值" lay-verify="required" value="${ffdownpin.modleOpcTag}">
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">ffdown上限来源</label>
                        <div class="layui-input-inline">
                            <select name="ffdownresource" lay-verify="required">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <c:choose>
                                        <c:when test="${ffdownpin.resource!=null&&ffdownpin.resource.equals(opcres)}">
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
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">ff独立投切</h2>
            <div class="layui-colla-content  layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">ffenable位号</p>
                <div class="layui-form-item">
                    <input type="text" name="ffenableid" value="${ffpinenable.modlepinsId}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">


                    <div class="layui-inline">
                        <label class="layui-form-label">ffenable</label>
                        <div class="layui-input-inline">
                            <input type="text" name="ffenable" autocomplete="off" class="layui-input"
                                   placeholder="opc位号" value="${ffpinenable.modleOpcTag}" >
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">ffopc位号来源</label>
                        <div class="layui-input-inline">
                            <select name="ffenableresource">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <c:choose>
                                        <c:when test="${ffpinenable.resource!=null&&ffpinenable.resource.equals(opcres)}">
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
            </div>
        </div>

    </div>


    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" id="modifycontrlmodlepinsubmitbt"
                lay-filter="newcontrlmodlepinsubmit"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">立即提交
        </button>
        <button type="reset" class="layui-btn layui-btn-primary"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">重置
        </button>
    </div>
</form>

</body>

<script>

    layui.use(['element', 'form', 'layer'], function () {
        var element = layui.element;
        var form = layui.form,
            layer = layui.layer; //parent.layer === undefined ? layui.layer : parent.layer;
        form.render(); //更新全部
        form.render('select'); //刷新select选择框渲染
        //监听提交
        form.on('submit(newcontrlmodlepinsubmit)', function (data) {
            let index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});
            $.ajax({
                url: "${pageContext.request.contextPath}/contrlmodle/savemodifymodelffpin.do",
                async: true,
                data: {
                    "modlepincontext": JSON.stringify(data.field),
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
