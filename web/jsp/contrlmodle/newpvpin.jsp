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
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/contrlmodle/contrlmodle.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
</head>
<body>

<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>PV设置</legend>
</fieldset>


<form class="layui-form" action="" method="post">

    <div class="layui-form-item layui-collapse" lay-accordion>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">PV属性设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <input type="text" name="modleid" value="${modleid}"  autocomplete="off" class="layui-input"  style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <input type="text" name="pvpinid" autocomplete="off" class="layui-input" value=""  style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">pv引脚选择</label>
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
                        <label class="layui-form-label">pv</label>
                        <div class="layui-input-inline">
                            <input type="text" name="pv" autocomplete="off" class="layui-input" lay-verify="required"
                                   placeholder="opc位号">
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv中文注释</label>
                        <div class="layui-input-inline">
                            <input type="text" name="pvcomment" autocomplete="off" class="layui-input"
                                   placeholder="pv中文注释">
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">pv位号来源</label>
                        <div class="layui-input-inline">
                            <select name="pvresource" lay-verify="required">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv死区</label>
                        <div class="layui-input-inline">
                            <input type="number" name="pvDeadZone" autocomplete="off" lay-verify="required"
                                   class="layui-input" placeholder="pv的死区" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv漏斗初始值</label>
                        <div class="layui-input-inline">
                            <input type="number" name="pvFunelInitValue" autocomplete="off" lay-verify="required"
                                   class="layui-input" placeholder="pv的漏斗值" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                </div>
            </div>
        </div>



        <div class="layui-colla-item">
            <h2 class="layui-colla-title">漏斗类型：全漏斗(区间控制)，上漏斗(保留上漏斗线，下漏斗线为负无穷)，下漏斗(保留下漏斗线，上漏斗线为正无穷)漏斗类型：全漏斗(区间控制)，上漏斗(保留上漏斗线，下漏斗线为负无穷)，下漏斗(保留下漏斗线，上漏斗线为正无穷)</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <div class="layui-inline">
                        <label class="layui-form-label">pv漏斗类型</label>
                        <div class="layui-input-inline">
                            <select name="funneltype" lay-verify="required">
                                <option value="">滤波选择</option>
                                <option value="fullfunnel" selected>全漏斗</option>
                                <option value="upfunnel">上漏斗</option>
                                <option value="downfunnel">下漏斗</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">Q值：影响反馈与目标值的偏差度，Q越大算法则要求PV与设定值相差越小</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <div class="layui-inline">
                        <label class="layui-form-label">q</label>
                        <div class="layui-input-inline">
                            <input type="number" name="q" autocomplete="off" class="layui-input" lay-verify="required"
                                   placeholder="Q" onmousewheel='scrollFunc()'>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">柔化系数(0&ltalphe&lt1)：PV值与SP值有差值时，沿着平稳的参考轨迹接近sp还是陡峭的参考轨迹接近sp。柔化系数越大，路线越平稳</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <div class="layui-inline">
                        <label class="layui-form-label">pv的柔化系数</label>
                        <div class="layui-input-inline">
                            <input type="number" name="tracoef" autocomplete="off" class="layui-input" lay-verify="required"
                                   placeholder="pv的柔化系数" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label" >柔化方式</label>
                        <div class="layui-input-inline">
                            <select name="tracoefmethod" lay-verify="required">
                                <option value="">柔化方式</option>
                                <option value="before" selected>前期</option>
                                <option value="after">后期</option>
                            </select>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label"  type="button"  id="showtracoefmethodimg" src="${pageContext.request.contextPath}/img/tracof.jpg">ltalphe柔化效果</label>
                    </div>



                </div>
            </div>
        </div>



        <div class="layui-colla-item">
            <h2 class="layui-colla-title">PV置信区间</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">


                    <input type="text" name="pvuppinid" autocomplete="off" class="layui-input" value=""  style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">


                    <div class="layui-inline">
                        <label class="layui-form-label">pv上限</label>
                        <div class="layui-input-inline">
                            <input type="text" name="pvup" autocomplete="off" class="layui-input"
                                   placeholder="pvup位号/值">
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv上限来源</label>
                        <div class="layui-input-inline">
                            <select name="pvupresource">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>



                    <input type="text" name="pvdownpinid" autocomplete="off" class="layui-input" value=""  style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">pv下限</label>
                        <div class="layui-input-inline">
                            <input type="text" name="pvdown" autocomplete="off"
                                   class="layui-input"
                                   placeholder="pvdown位号/值">
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv下限来源</label>
                        <div class="layui-input-inline">
                            <select name="pvdownresource">
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
            <h2 class="layui-colla-title">PV滤波器设置： 一阶滤波(滤波系数0&ltalphe&lt1，数值越小滤波越强)和移动平均滤波(滤波系数0&ltalphe的整数，数值越大滤波越强)</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">


                    <input name="pinfilterid" value=""  style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">


                    <div class="layui-inline">
                        <label class="layui-form-label">pv滤波算法选择</label>
                        <div class="layui-input-inline">
                            <select name="filternamepv">
                                <option value="">滤波选择</option>
                                <option value="mvav">移动平均</option>
                                <option value="fodl">一阶滤波</option>
                            </select>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv滤波系数</label>
                        <div class="layui-input-inline">
                            <input type="number" name="filtercoefpv" autocomplete="off"
                                   class="layui-input" placeholder="pv滤波系数" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv滤波输出OPC位号</label>
                        <div class="layui-input-inline">
                            <input type="text" name="filteropctagpv" autocomplete="off"
                                   class="layui-input" placeholder="pv滤波输出opc位号">
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">pv滤波输出OPC来源</label>
                        <div class="layui-input-inline">
                            <select name="filterpvresource">
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
            <h2 class="layui-colla-title">PV独立投切OPC位号设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">

                    <input type="text" name="pvenableid" autocomplete="off" class="layui-input" value=""  style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">pvenable</label>
                        <div class="layui-input-inline">
                            <input type="text" name="pvenable" autocomplete="off" class="layui-input"
                                   placeholder="opc位号">
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pvenable来源</label>
                        <div class="layui-input-inline">
                            <select name="pvenableresource">
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
            <h2 class="layui-colla-title">PV震荡检测设置</h2>
            <div class="layui-colla-content">
                <div class="layui-form-item">

                    <input type="text" name="shockerid" autocomplete="off" class="layui-input" value=""  style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">


                    <div class="layui-inline">
                        <label class="layui-form-label">震荡监视时间长度(秒)推荐(1.5~2)个PV响应时间</label>
                        <div class="layui-input-inline">
                            <input type="number" name="detectwindowstimepv" autocomplete="off"
                                   class="layui-input"
                                   placeholder="pv震荡监视时间" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv阻尼系数</label>
                        <div class="layui-input-inline">
                            <input type="number" name="detectdampcoepv" autocomplete="off"
                                   class="layui-input"
                                   placeholder="pv阻尼系数" onmousewheel='scrollFunc()'>
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">pv一阶滤波系数(一阶滤波系数(0&ltalphe&lt;=1))</label>
                        <div class="layui-input-inline">
                            <input type="number" name="detectfiltercoepv" autocomplete="off"
                                   class="layui-input"
                                   placeholder="pv滤波系数" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv滤波输出位号</label>
                        <div class="layui-input-inline">
                            <input type="text" name="detectfilteroutopctagpv"
                                   autocomplete="off"
                                   class="layui-input"
                                   placeholder="pv滤波输出位号">
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">pv滤波输出位号来源</label>
                        <div class="layui-input-inline">
                            <select name="detectfilteroutopctagpvresource">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv幅值输出位号</label>
                        <div class="layui-input-inline">
                            <input type="text" name="detectamplitudeoutopctagpv"
                                   autocomplete="off"
                                   class="layui-input"
                                   placeholder="pv幅值输出位号">
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">pv幅值输出位号来源</label>
                        <div class="layui-input-inline">
                            <select name="detectamplitudeoutopctagpvresource">
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
            <h2 class="layui-colla-title">SP设置</h2>
            <div class="layui-colla-content  layui-show">
                <div class="layui-form-item">


                    <input type="text" name="sppinid" autocomplete="off" class="layui-input" value=""  style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">sp</label>
                        <div class="layui-input-inline">
                            <input type="text" name="sp" autocomplete="off" class="layui-input" lay-verify="required"
                                   placeholder="opc位号">
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">sp中文注释</label>
                        <div class="layui-input-inline">
                            <input type="text" name="spcomment" autocomplete="off" class="layui-input"
                                   placeholder="sp中文注释">
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">sp位号来源</label>
                        <div class="layui-input-inline">
                            <select name="spresource" lay-verify="required">
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


    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" id="newcontrlmodlepinsubmitbt" lay-filter="newcontrlmodlepinsubmit"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">立即提交
        </button>
        <button type="reset" class="layui-btn layui-btn-primary"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">重置
        </button>
    </div>
</form>

</body>

<script>



</script>


<script>


    $(document).ready(function () {


        var layer;
        layui.use(['element', 'form', 'layer'], function () {
            var element = layui.element;
            var form = layui.form;
            layer =layui.layer; //parent.layer === undefined ? layui.layer : parent.layer;
            form.render(); //更新全部
            form.render('select'); //刷新select选择框渲染
            //监听提交
            form.on('submit(newcontrlmodlepinsubmit)', function (data) {
                let index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});
                $.ajax({
                    url: "${pageContext.request.contextPath}/contrlmodle/savenewmodelpvpin.do",
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

                            console.log($("#bt_flush_pvtab", parent.document))
                            $("#bt_flush_pvtab", parent.document).trigger('click')

                        }

                    }
                });


                console.log(JSON.stringify(data.field))
                return false;
            });






        });



        var tratips;
        $('#showtracoefmethodimg').on({
            mouseenter: function () {
                tratips=layer.tips('<img width="320px" height="240px" src="'+$(this).attr('src')+'">', this, {time: 0,area: ['320px', '240px']} );
            },
            mouseleave: function () {
                layer.close(tratips);
            }
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
</html>
