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
    <legend>MV设置</legend>
</fieldset>

<form class="layui-form" action="" method="post">
    <div class="layui-form-item layui-collapse" lay-accordion>
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV属性设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">

                    <input type="text" name="modleid" value="${modleid}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">


                    <input type="text" name="mvpinid" autocomplete="off" class="layui-input" value=""
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">


                    <div class="layui-inline">
                        <label class="layui-form-label">MV引脚选择</label>
                        <div class="layui-input-inline">
                            <select name="pinName" lay-verify="required">
                                <option value="">请选择引脚名称</option>
                                <c:forEach var="pinindex" items="${unuserpinscope}" varStatus="Count">
                                    <option value="${pintype}${pinindex}">${pintype}${pinindex}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">mv</label>
                        <div class="layui-input-inline">
                            <input type="text" name="mv" autocomplete="off" class="layui-input" lay-verify="required"
                                   placeholder="opc位号">
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">mv中文注释</label>
                        <div class="layui-input-inline">
                            <input type="text" name="mvcomment" autocomplete="off" class="layui-input"
                                   placeholder="mv中文注释">
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">mvopc位号来源</label>
                        <div class="layui-input-inline">
                            <select name="mvresource" lay-verify="required">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">R设置</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">影响MV调节幅度，R越大算法则要求每次的deltaMV越小</p>

                <div class="layui-form-item">
                    <div class="layui-inline">
                        <label class="layui-form-label">r</label>
                        <div class="layui-input-inline">
                            <input type="number" name="r" autocomplete="off" class="layui-input" lay-verify="required"
                                   placeholder="mv的R" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">mv独立投切</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">mvenable位号</p>
                <div class="layui-form-item">
                    <input type="text" name="mvenableid" value="" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">


                    <div class="layui-inline">
                        <label class="layui-form-label">mv</label>
                        <div class="layui-input-inline">
                            <input type="text" name="mvenable" autocomplete="off" class="layui-input"
                                   placeholder="opc位号">
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">mvopc位号来源</label>
                        <div class="layui-input-inline">
                            <select name="mvenableresource">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">dmv高限</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">mv增量绝对值不能超过该值</p>
                <div class="layui-form-item">

                    <div class="layui-inline">
                        <label class="layui-form-label">dmvHigh</label>
                        <div class="layui-input-inline">
                            <input type="number" name="dmvHigh" autocomplete="off" class="layui-input"
                                   lay-verify="required"
                                   placeholder="dmvHigh" onmousewheel='scrollFunc()'>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">dmv低限</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">低于此不进行调节</p>
                <div class="layui-form-item">

                    <div class="layui-inline">
                        <label class="layui-form-label">dmvLow</label>
                        <div class="layui-input-inline">
                            <input type="number" name="dmvLow" autocomplete="off" class="layui-input"
                                   lay-verify="required"
                                   placeholder="dmvLow" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                </div>

            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV上限设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">

                    <input type="text" name="mvuppinid" autocomplete="off" class="layui-input" value=""
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">mvup</label>
                        <div class="layui-input-inline">
                            <input type="text" name="mvup" autocomplete="off" class="layui-input" lay-verify="required"
                                   placeholder="mv上限">
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">mv上限来源</label>
                        <div class="layui-input-inline">
                            <select name="mvupresource" lay-verify="required">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV下限设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">

                    <input type="text" name="mvdownpinid" autocomplete="off" class="layui-input" value=""
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">mvdown</label>
                        <div class="layui-input-inline">
                            <input type="text" name="mvdown" autocomplete="off" class="layui-input"
                                   lay-verify="required"
                                   placeholder="mv下限"
                            >
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">mv下限来源</label>
                        <div class="layui-input-inline">
                            <select name="mvdownresource" lay-verify="required">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV反馈设置</h2>
            <div class="layui-colla-content layui-show">

                <div class="layui-form-item">

                    <input type="text" name="mvfbpinid" autocomplete="off" class="layui-input" value=""
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
                    <div class="layui-inline">
                        <label class="layui-form-label">mvfb</label>
                        <div class="layui-input-inline">
                            <input type="text" name="mvfb" autocomplete="off" class="layui-input" lay-verify="required"
                                   placeholder="mv反馈位号"
                            >
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">mvfb中文注释</label>
                        <div class="layui-input-inline">
                            <input type="text" name="mvfbcomment" autocomplete="off" class="layui-input"
                                   placeholder="mvfb中文注释">
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">mvfb反馈opc位号来源</label>
                        <div class="layui-input-inline">
                            <select name="mvfbresource" lay-verify="required">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV反馈滤波器设置</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">
                    一阶滤波(滤波系数0&ltalphe&lt1，数值越小滤波越强)和移动平均滤波(滤波系数0&ltalphe的整数，数值越大滤波越强)</p>
                <div class="layui-form-item">

                    <input type="text" name="mvfbfilterpinid" autocomplete="off" class="layui-input" value=""
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">mvfb滤波算法选择</label>
                        <div class="layui-input-inline">
                            <select name="filternamemv">
                                <option value="">滤波选择</option>
                                <option value="mvav">移动平均</option>
                                <option value="fodl">一阶滤波</option>
                            </select>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">mvfb滤波系数</label>
                        <div class="layui-input-inline">
                            <input type="number" name="filtercoefmv" autocomplete="off"
                                   class="layui-input"
                                   onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">mvfb滤波输出OPC位号</label>
                        <div class="layui-input-inline">
                            <input type="text" name="filteropctagmv" autocomplete="off"
                                   class="layui-input">
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">mvfb滤波输出OPC位号来源</label>
                        <div class="layui-input-inline">
                            <select name="filtermvfbresource">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                </div>
            </div>
        </div>

        <div class="layui-form-item layui-colla-item">
            <h2 class="layui-colla-title">MV震荡检测设置</h2>
            <div class="layui-colla-content">
                <div class="layui-form-item layui-collapse" lay-accordion>
                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">MV震荡检测时间设置</h2>
                        <div class="layui-colla-content">
                            <p style="font-weight: bolder;font-size: 1.1rem">震荡监视时间长度(秒)推荐(1.5~2)个PV响应时间</p>


                            <input type="text" name="mvfbshockerid" autocomplete="off" class="layui-input" value=""
                                   style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                            <div class="layui-inline">
                                <label class="layui-form-label">mv监测时间</label>
                                <div class="layui-input-inline">
                                    <input type="number" name="detectwindowstimemv" autocomplete="off"
                                           class="layui-input"
                                           placeholder="mv震荡监视时间" onmousewheel='scrollFunc()'>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">阻尼系数</h2>
                        <div class="layui-colla-content">
                            <div class="layui-inline">
                                <label class="layui-form-label">mv阻尼系数</label>
                                <div class="layui-input-inline">
                                    <input type="number" name="detectdampcoemv" autocomplete="off"
                                           class="layui-input"
                                           placeholder="mv阻尼系数" onmousewheel='scrollFunc()'>
                                </div>
                            </div>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">一阶滤波系数(0&ltalphe&lt;=1)</h2>
                        <div class="layui-colla-content">
                            <p style="font-weight: bolder;font-size: 1.1rem">系数越小滤波效果越好,为1则无滤波效果</p>
                            <div class="layui-inline">
                                <label class="layui-form-label">mv一阶滤波系数</label>
                                <div class="layui-input-inline">
                                    <input type="number" name="detectfiltercoemv" autocomplete="off"
                                           class="layui-input"
                                           placeholder="mv滤波系数" onmousewheel='scrollFunc()'>
                                </div>
                            </div>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">MV滤波输出OPC位号</h2>
                        <div class="layui-colla-content">

                            <div class="layui-inline">
                                <label class="layui-form-label">mv滤波输出位号</label>
                                <div class="layui-input-inline">
                                    <input type="text" name="detectfilteroutopctagmv"
                                           autocomplete="off"
                                           class="layui-input"
                                           placeholder="mv滤波输出位号">
                                </div>
                            </div>


                            <div class="layui-inline">
                                <label class="layui-form-label">mv滤波输出位号来源</label>
                                <div class="layui-input-inline">
                                    <select name="detectfilteroutopctagmvresource">
                                        <option value="">请选择来源</option>
                                        <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                            <option value="${opcres}">${opcres}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">MV滤波后幅值结果输出OPC位号</h2>
                        <div class="layui-colla-content">

                            <div class="layui-inline">
                                <label class="layui-form-label">mv幅值输出位号</label>
                                <div class="layui-input-inline">
                                    <input type="text" name="detectamplitudeoutopctagmv"
                                           autocomplete="off"
                                           class="layui-input"
                                           placeholder="mv幅值输出位号">
                                </div>
                            </div>


                            <div class="layui-inline">
                                <label class="layui-form-label">mv幅值输出位号来源</label>
                                <div class="layui-input-inline">
                                    <select name="detectamplitudeoutopctagmvresource">
                                        <option value="">请选择来源</option>
                                        <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                            <option value="${opcres}">${opcres}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>


            </div>
        </div>

    </div>


    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" id="newcontrlmodlepinsubmitbt"
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
                url: "${pageContext.request.contextPath}/contrlmodle/savenewmodelmvpin.do",
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

                        console.log($("#bt_flush_mvtab", parent.document))
                        $("#bt_flush_mvtab", parent.document).trigger('click')

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
