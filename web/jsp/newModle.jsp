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
</head>


<body>
<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>模型新建</legend>
</fieldset>

<form class="layui-form" action="" method="post">
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>基础参数设置</legend>
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
                <input type="number" name="P" lay-verify="required|number" autocomplete="off" class="layui-input" onmousewheel='scrollFunc()'>
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
                <input type="number" name="O" lay-verify="required|number" autocomplete="off" class="layui-input" onmousewheel='scrollFunc()'>
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">手自动位号</label>
            <div class="layui-input-inline">
                <input type="text" name="autoTag"  autocomplete="off" class="layui-input"
                       placeholder="opc位号">
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">auto位号来源</label>
            <div class="layui-input-inline">
                <select name="autoresource">
                    <option value="">请选择来源</option>
                    <<c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                    <option value="${opcres}">${opcres}</option>
                </c:forEach>
                </select>
            </div>
        </div>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>PV设置</legend>
    </fieldset>
    <div class="layui-form-item layui-collapse" lay-accordion>
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">PV属性设置</h2>
            <div class="layui-colla-content layui-show">
                <c:forEach var="pv" items="${pvlist}" varStatus="movieLoopCount">
                    <div class="layui-inline">
                        <label class="layui-form-label">pv${pv}</label>
                        <div class="layui-input-inline">
                            <input type="text" name="pv${pv}" autocomplete="off" class="layui-input"
                                   placeholder="opc位号">
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv${pv}来源</label>
                        <div class="layui-input-inline">
                            <select name="pv${pv}resource">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </c:forEach>

            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">PV死区设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <c:forEach var="pv" items="${pvlist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">pv${pv}死区</label>
                            <div class="layui-input-inline">
                                <input type="number" name="pv${pv}DeadZone" autocomplete="off" class="layui-input"
                                       placeholder="pv${pv}死区" onmousewheel='scrollFunc()'>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">PV漏斗初始值设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <c:forEach var="pv" items="${pvlist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">pv${pv}漏斗初始值</label>
                            <div class="layui-input-inline">
                                <input type="number" name="pv${pv}FunelInitValue" autocomplete="off" class="layui-input"
                                       placeholder="pv${pv}漏斗初始值设置值" onmousewheel='scrollFunc()'>
                            </div>
                        </div>
                    </c:forEach>
                </div>

            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">PV漏斗类型选择</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem"> 全漏斗(区间控制)，上漏斗(保留上漏斗线，下漏斗线为负无穷)，下漏斗(保留下漏斗线，上漏斗线为正无穷)</p>
                <div class="layui-form-item">
                    <c:forEach var="pv" items="${pvlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">pv${pv}漏斗类型</label>
                            <div class="layui-input-inline">
                                <select name="funneltype${pv}">
                                    <option value="">滤波选择</option>
                                    <option value="fullfunnel" selected>全漏斗</option>
                                    <option value="upfunnel">上漏斗</option>
                                    <option value="downfunnel">下漏斗</option>
                                </select>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">Q设置</h2>

            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">影响反馈与目标值的偏差度，Q越大算法则要求PV与设定值相差越小</p>
                <div class="layui-form-item">
                    <c:forEach var="pv" items="${pvlist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">q${pv}</label>
                            <div class="layui-input-inline">
                                <input type="number" name="q${pv}" autocomplete="off" class="layui-input"
                                       placeholder="pv${pv}的Q" onmousewheel='scrollFunc()'>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">柔化系数alphe((0&ltalphe&lt1))</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">PV值与SP值有差值时，沿着平稳的参考轨迹接近sp还是陡峭的参考轨迹接近sp。柔化系数越大，路线越平稳</p>
                <div class="layui-form-item">
                    <c:forEach var="pv" items="${pvlist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">pv${pv}的柔化系数</label>
                            <div class="layui-input-inline">
                                <input type="number" name="tracoef${pv}" autocomplete="off" class="layui-input"
                                       placeholder="pv${pv}的柔化系数" onmousewheel='scrollFunc()'>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">PV滤波器设置</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">一阶滤波(滤波系数0&ltalphe&lt1，数值越小滤波越强)和移动平均滤波(滤波系数0&ltalphe的整数，数值越大滤波越强)</p>
                <div class="layui-form-item">
                    <c:forEach var="pv" items="${pvlist}" varStatus="Count">

                        <div class="layui-inline">
                            <label class="layui-form-label">pv${pv}滤波算法选择</label>
                            <div class="layui-input-inline">
                                <select name="filternamepv${pv}">
                                    <option value="">滤波选择</option>
                                    <option value="mvav">移动平均</option>
                                    <option value="fodl">一阶滤波</option>
                                </select>
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">pv${pv}滤波系数</label>
                            <div class="layui-input-inline">
                                <input type="number" name="filtercoefpv${pv}" autocomplete="off" class="layui-input"
                                       placeholder="pv${pv}滤波系数" onmousewheel='scrollFunc()'>
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">pv${pv}滤波输出OPC位号</label>
                            <div class="layui-input-inline">
                                <input type="text" name="filteropctagpv${pv}" autocomplete="off" class="layui-input"
                                       placeholder="pv${pv}滤波输出opc位号">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">pv${pv}滤波输出OPC来源</label>
                            <div class="layui-input-inline">
                                <select name="filterpv${pv}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <option value="${opcres}">${opcres}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-form-item layui-colla-item">
            <h2 class="layui-colla-title">PV震荡检测设置</h2>
            <div class="layui-colla-content">

                <div class="layui-form-item layui-collapse" lay-accordion>
                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">PV震荡检测时间设置</h2>
                        <div class="layui-colla-content">
                            <p style="font-weight: bolder;font-size: 1.1rem">震荡监视时间长度(秒)推荐(1.5~2)个PV响应时间</p>
                            <c:forEach var="pv" items="${pvlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${pv}监测时间</label>
                                    <div class="layui-input-inline">
                                        <input type="number" name="detectwindowstimepv${pv}" autocomplete="off"
                                               class="layui-input"
                                               placeholder="pv${pv}震荡监视时间" onmousewheel='scrollFunc()'>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">阻尼系数</h2>
                        <div class="layui-colla-content">
                            <c:forEach var="pv" items="${pvlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${pv}阻尼系数</label>
                                    <div class="layui-input-inline">
                                        <input type="number" name="detectdampcoepv${pv}" autocomplete="off"
                                               class="layui-input"
                                               placeholder="pv${pv}阻尼系数" onmousewheel='scrollFunc()'>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">一阶滤波系数(0&ltalphe&lt;=1)</h2>
                        <div class="layui-colla-content">
                            <p  style="font-weight: bolder;font-size: 1.1rem">系数越小滤波效果越好,为1则无滤波效果</p>
                            <c:forEach var="pv" items="${pvlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${pv}一阶滤波系数</label>
                                    <div class="layui-input-inline">
                                        <input type="number" name="detectfiltercoepv${pv}" autocomplete="off"
                                               class="layui-input"
                                               placeholder="pv${pv}滤波系数" onmousewheel='scrollFunc()'>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">PV滤波输出OPC位号</h2>
                        <div class="layui-colla-content">
                            <c:forEach var="pv" items="${pvlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${pv}滤波输出位号</label>
                                    <div class="layui-input-inline">
                                        <input type="text" name="detectfilteroutopctagpv${pv}" autocomplete="off"
                                               class="layui-input"
                                               placeholder="pv${pv}滤波输出位号">
                                    </div>
                                </div>


                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${pv}滤波输出位号来源</label>
                                    <div class="layui-input-inline">
                                        <select name="detectfilteroutopctagpv${pv}resource">
                                            <option value="">请选择来源</option>
                                            <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                                <option value="${opcres}">${opcres}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>

                            </c:forEach>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">PV滤波后幅值结果输出OPC位号</h2>
                        <div class="layui-colla-content layui-show">
                            <c:forEach var="pv" items="${pvlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${pv}幅值输出位号</label>
                                    <div class="layui-input-inline">
                                        <input type="text" name="detectamplitudeoutopctagpv${pv}" autocomplete="off"
                                               class="layui-input"
                                               placeholder="pv${pv}幅值输出位号">
                                    </div>
                                </div>


                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${pv}幅值输出位号来源</label>
                                    <div class="layui-input-inline">
                                        <select name="detectamplitudeoutopctagpv${pv}resource">
                                            <option value="">请选择来源</option>
                                            <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                                <option value="${opcres}">${opcres}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                </div>


            </div>
        </div>


    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>SP设置</legend>
    </fieldset>

    <div class="layui-form-item layui-collapse" lay-accordion>
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">SP属性设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <c:forEach var="sp" items="${pvlist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">sp${sp}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="sp${sp}" autocomplete="off" class="layui-input"
                                       placeholder="opc位号">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">sp${sp}位号来源</label>
                            <div class="layui-input-inline">
                                <select name="sp${sp}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <option value="${opcres}">${opcres}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                    </c:forEach>
                </div>
            </div>
        </div>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>MV设置</legend>
    </fieldset>
    <div class="layui-form-item layui-collapse" lay-accordion>
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV属性设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <c:forEach var="mv" items="${mvlist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">mv${mv}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="mv${mv}" autocomplete="off" class="layui-input"
                                       placeholder="opc位号">
                            </div>
                        </div>


                        <div class="layui-inline">
                            <label class="layui-form-label">mv${mv}来源</label>
                            <div class="layui-input-inline">
                                <select name="mv${mv}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <option value="${opcres}">${opcres}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">R设置</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">影响MV调节幅度，R越大算法则要求每次的deltaMV越小</p>
                <div class="layui-form-item">
                    <c:forEach var="pv" items="${mvlist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">r${pv}</label>
                            <div class="layui-input-inline">
                                <input type="number" name="r${pv}" autocomplete="off" class="layui-input"
                                       placeholder="mv${pv}的R" onmousewheel='scrollFunc()'>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">dmv高限设置</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">mv增量绝对值不能超过该值</p>
                <div class="layui-form-item">
                    <c:forEach var="mv" items="${mvlist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">dmv${mv}High</label>
                            <div class="layui-input-inline">
                                <input type="number" name="dmv${mv}High" autocomplete="off" class="layui-input"
                                       placeholder="dmv${mv}High" onmousewheel='scrollFunc()'>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">dmv低限设置</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">mv增量低于该值不行进本次调节</p>
                <div class="layui-form-item">
                    <c:forEach var="mv" items="${mvlist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">dmv${mv}Low</label>
                            <div class="layui-input-inline">
                                <input type="number" name="dmv${mv}Low" autocomplete="off" class="layui-input"
                                       placeholder="dmv${mv}Low" onmousewheel='scrollFunc()'>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV上限设置</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">MV不超过该值</p>
                <div class="layui-form-item">
                    <c:forEach var="mvup" items="${mvlist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">mvup${mvup}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="mvup${mvup}" autocomplete="off" class="layui-input"
                                       placeholder="mv${mvup}上限">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">mv${mvup}上限来源</label>
                            <div class="layui-input-inline">
                                <select name="mvup${mvup}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <option value="${opcres}">${opcres}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:forEach>
                </div>

            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV下限设置</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">MV不低于该值</p>
                <div class="layui-form-item">
                    <c:forEach var="mvdown" items="${mvlist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">mvdown${mvdown}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="mvdown${mvdown}" autocomplete="off" class="layui-input"
                                       placeholder="mv${mvdown}下限">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">mv${mvdown}下限来源</label>
                            <div class="layui-input-inline">
                                <select name="mvdown${mvdown}resource">
                                    <option value="">请选择来源</option>
                                    <<c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV反馈属性设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <c:forEach var="mvdown" items="${mvlist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">mvfb${mvdown}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="mvfb${mvdown}" autocomplete="off" class="layui-input"
                                       placeholder="mv${mvdown}反馈位号">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">mvfb${mvdown}反馈opc位号来源</label>
                            <div class="layui-input-inline">
                                <select name="mvfb${mvdown}resource">
                                    <option value="">请选择来源</option>
                                    <<c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                                </select>
                            </div>
                        </div>

                    </c:forEach>
                </div>

            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MVFB反馈滤波器设置</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">一阶滤波(滤波系数0&ltalphe&lt1，数值越小滤波越强)和移动平均(滤波系数0&ltalphe的整数，数值越大滤波越强)</p>
                <div class="layui-form-item">
                    <c:forEach var="mv" items="${mvlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">mvfb${mv}滤波算法选择</label>
                            <div class="layui-input-inline">
                                <select name="filternamemv${mv}">
                                    <option value="">滤波选择</option>
                                    <option value="mvav">移动平均</option>
                                    <option value="fodl">一阶滤波</option>
                                </select>
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">mvfb${mv}滤波系数</label>
                            <div class="layui-input-inline">
                                <input type="number" name="filtercoefmv${mv}" autocomplete="off" class="layui-input"
                                       placeholder="mvfb${mv}滤波系数" onmousewheel='scrollFunc()'>
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">mvfb${mv}滤波输出OPC位号</label>
                            <div class="layui-input-inline">
                                <input type="text" name="filteropctagmv${mv}" autocomplete="off" class="layui-input"
                                       placeholder="mvfb${mv}滤波输出opc位号">
                            </div>
                        </div>


                        <div class="layui-inline">
                            <label class="layui-form-label">mvfb${mv}滤波输出OPC位号来源</label>
                            <div class="layui-input-inline">
                                <select name="filtermvfb${mv}resource">
                                    <option value="">请选择来源</option>
                                    <<c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <option value="${opcres}">${opcres}</option>
                                </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:forEach>
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
                            <c:forEach var="mv" items="${mvlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${mv}监测时间</label>
                                    <div class="layui-input-inline">
                                        <input type="number" name="detectwindowstimemv${mv}" autocomplete="off"
                                               class="layui-input"
                                               placeholder="mv${mv}震荡监视时间" onmousewheel='scrollFunc()'>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">阻尼系数</h2>
                        <div class="layui-colla-content">
                            <c:forEach var="mv" items="${mvlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${mv}阻尼系数</label>
                                    <div class="layui-input-inline">
                                        <input type="number" name="detectdampcoemv${mv}" autocomplete="off"
                                               class="layui-input"
                                               placeholder="mv${mv}阻尼系数" onmousewheel='scrollFunc()'>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">一阶滤波系数(0&ltalphe&lt;=1)</h2>
                        <div class="layui-colla-content">
                            <p  style="font-weight: bolder;font-size: 1.1rem">系数越小滤波效果越好,为1则无滤波效果</p>
                            <c:forEach var="mv" items="${mvlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${mv}一阶滤波系数</label>
                                    <div class="layui-input-inline">
                                        <input type="number" name="detectfiltercoemv${mv}" autocomplete="off"
                                               class="layui-input"
                                               placeholder="mv${mv}滤波系数" onmousewheel='scrollFunc()'>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">MV滤波输出OPC位号</h2>
                        <div class="layui-colla-content">
                            <c:forEach var="mv" items="${mvlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${mv}滤波输出位号</label>
                                    <div class="layui-input-inline">
                                        <input type="text" name="detectfilteroutopctagmv${mv}" autocomplete="off"
                                               class="layui-input"
                                               placeholder="mv${mv}滤波输出位号">
                                    </div>
                                </div>


                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${mv}滤波输出位号来源</label>
                                    <div class="layui-input-inline">
                                        <select name="detectfilteroutopctagmv${mv}resource">
                                            <option value="">请选择来源</option>
                                            <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                                <option value="${opcres}">${opcres}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>

                            </c:forEach>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">MV滤波后幅值结果输出OPC位号</h2>
                        <div class="layui-colla-content">
                            <c:forEach var="mv" items="${mvlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${mv}幅值输出位号</label>
                                    <div class="layui-input-inline">
                                        <input type="text" name="detectamplitudeoutopctagmv${mv}" autocomplete="off"
                                               class="layui-input"
                                               placeholder="mv${mv}幅值输出位号">
                                    </div>
                                </div>


                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${mv}幅值输出位号来源</label>
                                    <div class="layui-input-inline">
                                        <select name="detectamplitudeoutopctagmv${mv}resource">
                                            <option value="">请选择来源</option>
                                            <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                                <option value="${opcres}">${opcres}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                </div>


            </div>
        </div>




    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>FF(前馈)设置</legend>
    </fieldset>
    <div class="layui-form-item layui-collapse" lay-accordion>
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF属性设置</h2>
            <div class="layui-colla-content">
                <div class="layui-form-item">
                    <c:forEach var="ff" items="${fflist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">ff${ff}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="ff${ff}" autocomplete="off" class="layui-input"
                                       placeholder="ff${ff}位号">
                            </div>
                        </div>


                        <div class="layui-inline">
                            <label class="layui-form-label">ff${ff}opc位号来源</label>
                            <div class="layui-input-inline">
                                <select name="ff${ff}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <option value="${opcres}">${opcres}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF(前馈)滤波器设置</h2>
            <div class="layui-colla-content">
                <p style="font-weight: bolder;font-size: 1.1rem">一阶滤波(滤波系数0&ltalphe&lt1，数值越小滤波越强)和移动平均(滤波系数0&ltalphe的整数，数值越大滤波越强)</p>
                <div class="layui-form-item">
                    <c:forEach var="ff" items="${fflist}" varStatus="Count">

                        <div class="layui-inline">
                            <label class="layui-form-label">ff${ff}滤波算法选择</label>
                            <div class="layui-input-inline">
                                <select name="filternameff${ff}">
                                    <option value="">滤波选择</option>
                                    <option value="mvav">移动平均</option>
                                    <option value="fodl">一阶滤波</option>
                                </select>
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">ff${ff}滤波系数</label>
                            <div class="layui-input-inline">
                                <input type="number" name="filtercoefff${ff}" autocomplete="off" class="layui-input"
                                       placeholder="ff${ff}滤波系数" onmousewheel='scrollFunc()'>
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">ff${ff}滤波输出OPC位号</label>
                            <div class="layui-input-inline">
                                <input type="text" name="filteropctagff${ff}" autocomplete="off" class="layui-input"
                                       placeholder="ff${ff}滤波输出opc位号">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">ff${ff}滤波输出OPC位号来源</label>
                            <div class="layui-input-inline">
                                <select name="filterff${ff}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <option value="${opcres}">${opcres}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF(前馈)上限设置</h2>
            <div class="layui-colla-content">
                <p style="font-weight: bolder;font-size: 1.1rem">FF值的置信区间上限</p>
                <div class="layui-form-item">
                    <c:forEach var="ff" items="${fflist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">ffup${ff}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="ffup${ff}" autocomplete="off" class="layui-input"
                                       placeholder="ff${ff}位号">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">ffup${ff}上限来源</label>
                            <div class="layui-input-inline">
                                <select name="ffup${ff}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <option value="${opcres}">${opcres}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF(前馈)下限设置</h2>
            <div class="layui-colla-content">
                <p style="font-weight: bolder;font-size: 1.1rem">FF值的置信区间下限</p>
                <div class="layui-form-item">
                    <c:forEach var="ffdown" items="${fflist}" varStatus="movieLoopCount">
                        <div class="layui-inline">
                            <label class="layui-form-label">ffdown${ffdown}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="ffdown${ffdown}" autocomplete="off" class="layui-input"
                                       placeholder="ffdown${ffdown}位号/值">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">ffdown${ffdown}上限来源</label>
                            <div class="layui-input-inline">
                                <select name="ffdown${ffdown}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <option value="${opcres}">${opcres}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>MV对PV的响应设置 形如{k:10,t:180,tao:200} 英文模式输入!</legend>
    </fieldset>

    <div class="layui-collapse" lay-accordion>
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV对PV的响应设置</h2>
            <div class="layui-colla-content layui-show">
                <table class="layui-table" lay-data='{"data":${mvresp}, "id":"mvresp"}' lay-filter="mvresp">
                    <thead>
                    <tr>
                        <th lay-data="{field:'mv', width:80, }">MV</th>
                        <c:forEach var="pv" items="${pvlist}" varStatus="movieLoopCount">
                            <th lay-data="{field:'pv${pv}', width:180,  edit: 'text'}">pv${pv}</th>
                        </c:forEach>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>前馈(FF)对PV的响应设置 形如{k:10,t:180,tao:200} 英文模式输入!</legend>
    </fieldset>
    <div class="layui-collapse" lay-accordion>
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF对PV的响应设置</h2>
            <div class="layui-colla-content">
                <table class="layui-table" lay-data='{"data":${ffresp}, "id":"ffresp"}' lay-filter="ffresp">
                    <thead>
                    <tr>
                        <th lay-data="{field:'ff', width:80, }">FF</th>
                        <c:forEach var="pv" items="${pvlist}" varStatus="movieLoopCount">
                            <th lay-data="{field:'pv${pv}', width:180,  edit: 'text'}">pv${pv}</th>
                        </c:forEach>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>

    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" lay-filter="newmodlesubmit">立即提交</button>
        <button type="reset" class="layui-btn layui-btn-primary">重置</button>
    </div>

</form>

<script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
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
            // layer.alert(JSON.stringify(data.field), {
            //     title: '最终的提交信息'
            // })
            let jsontypemvresp = table.cache.mvresp;
            let jsontypeffresp = table.cache.ffresp;
            console.log("jsonmvresp", jsontypemvresp)
            for (let indexmr = 0; indexmr < jsontypemvresp.length; indexmr++) {
                for (let indexpvi = 1; indexpvi <=${pvlist.size()}; indexpvi++) {
                    if (!verifyrespon(jsontypemvresp[indexmr]["pv" + indexpvi])) {
                        layer.msg("创建失败！请检查:" + "mv" + (indexmr + 1) + "对pv" + indexpvi + "的响应格式是否填写正确");
                        return false;
                    }
                }

            }

            for (let indexfr = 0; indexfr < jsontypeffresp.length; indexfr++) {
                for (let indexpvi = 1; indexpvi <=${pvlist.size()}; indexpvi++) {
                    if (!verifyrespon(jsontypeffresp[indexfr]["pv" + indexpvi])) {
                        layer.msg("创建失败！请检查:" + "ff" + (indexfr + 1) + "对pv" + indexpvi + "的响应格式是否填写正确");
                        return false;
                    }
                }
            }


            console.log(table.cache);
            console.log(typeof table.cache);
            var index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});
            $.ajax({
                url: "${pageContext.request.contextPath}/modle/savemodle.do" + "?" + Math.random(),
                async: true,
                data: {
                    "modle": JSON.stringify(data.field),
                    "mvresp": JSON.stringify(table.cache.mvresp),
                    "ffresp": JSON.stringify(table.cache.ffresp)
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
                        location.href = '${pageContext.request.contextPath}/' + json['go'];
                        //newleft(json['modleName'],json['modleId'])
                        parent.location.reload();
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

    function newleft(modleName, modleId) {

        console.log($(".layui-nav-tree", parent.document).find("li").find("dl")[0])

        mydd = document.createElement("dd");
        mya = document.createElement("a");
        mya.setAttribute("href", "/modle/modlestatus/" + modleId + ".do");
        mya.innerHTML = modleName;
        mydd.appendChild(mya);
        let parentli
        $(".layui-nav-tree", parent.document).find("li").find("dl")[0].append(mydd);

    }

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
</script>


</body>
</html>

