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

        <div class="layui-inline">
            <label class="layui-form-label">手自动位号</label>
            <div class="layui-input-inline">
                <input type="text" name="autoTag"  autocomplete="off" class="layui-input"
                       value="${modle.autoEnbalePin.modleOpcTag}">
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">auto来源</label>
            <div class="layui-input-inline">
                <select name="autoresource">
                    <option value="">请选择来源</option>
                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                        <c:choose>
                            <c:when test="${modle.autoEnbalePin.resource!=null&&modle.autoEnbalePin.resource.equals(opcres)}">
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


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>PV设置</legend>
    </fieldset>
    <div class="layui-form-item layui-collapse" lay-accordion>
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">PV属性设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <c:forEach var="pv" items="${pvpinlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">pv${Count.count}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="pv${Count.count}" autocomplete="off" class="layui-input"
                                       placeholder="opc位号" value="${pv.modleOpcTag==null?'':pv.modleOpcTag}">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">pv${Count.count}中文注释</label>
                            <div class="layui-input-inline">
                                <input type="text" name="pv${Count.count}comment" autocomplete="off" class="layui-input"
                                       placeholder="pv${Count.count}中文注释" value="${pv.opcTagName==null?'':pv.opcTagName}">
                            </div>
                        </div>


                        <div class="layui-inline">
                            <label class="layui-form-label">pv${Count.count}位号来源</label>
                            <div class="layui-input-inline">
                                <select name="pv${Count.count}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <c:choose>
                                            <c:when test="${pv.resource!=null&&pv.resource.equals(opcres)}">
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
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">PV死区设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <c:forEach var="pv" items="${pvDeadZones}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">pv${Count.count}死区</label>
                            <div class="layui-input-inline">
                                <input type="number" name="pv${Count.count}DeadZone" autocomplete="off"
                                       class="layui-input"
                                       value="${pv}" placeholder="pv${Count.count}的死区" onmousewheel='scrollFunc()'>
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
                    <c:forEach var="pv" items="${pvFunelInitValues}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">pv${Count.count}漏斗初始值</label>
                            <div class="layui-input-inline">
                                <input type="number" name="pv${Count.count}FunelInitValue" autocomplete="off"
                                       class="layui-input"
                                       value="${pv}" placeholder="pv${Count.count}的漏斗值" onmousewheel='scrollFunc()'>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">PV漏斗类型选择</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">全漏斗(区间控制)，上漏斗(保留上漏斗线，下漏斗线为负无穷)，下漏斗(保留下漏斗线，上漏斗线为正无穷)</p>
                <div class="layui-form-item">
                    <c:forEach var="pvfuneltype" items="${pvpinlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">pv${Count.count}漏斗类型</label>
                            <div class="layui-input-inline">
                                <select name="funneltype${Count.count}">
                                    <option value="">滤波选择</option>
                                    <c:choose>
                                        <c:when test="${(pvfuneltype.funneltype!=null)&&( pvfuneltype.funneltype eq 'fullfunnel')}">
                                            <option value="fullfunnel" selected>全漏斗</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="fullfunnel">全漏斗</option>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:choose>
                                        <c:when test="${(pvfuneltype.funneltype!=null)&&( pvfuneltype.funneltype eq 'upfunnel')}">
                                            <option value="upfunnel" selected>上漏斗</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="upfunnel">上漏斗</option>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:choose>
                                        <c:when test="${(pvfuneltype.funneltype!=null)&&( pvfuneltype.funneltype eq 'downfunnel')}">
                                            <option value="downfunnel" selected>下漏斗</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="downfunnel">下漏斗</option>
                                        </c:otherwise>
                                    </c:choose>

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
                    <c:forEach var="q" items="${qlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">q${Count.count}</label>
                            <div class="layui-input-inline">
                                <input type="number" name="q${Count.count}" autocomplete="off" class="layui-input"
                                       placeholder="pv${Count.count}的Q" value="${q}" onmousewheel='scrollFunc()'>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">柔化系数(0&ltalphe&lt1)</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">PV值与SP值有差值时，沿着平稳的参考轨迹接近sp还是陡峭的参考轨迹接近sp。柔化系数越大，路线越平稳</p>
                <div class="layui-form-item">
                    <c:forEach var="pv" items="${alphelist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">pv${Count.count}的柔化系数</label>
                            <div class="layui-input-inline">
                                <input type="number" name="tracoef${Count.count}" autocomplete="off" class="layui-input"
                                       value="${pv}"
                                       placeholder="pv${Count.count}的柔化系数" onmousewheel='scrollFunc()'>
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
                    <c:forEach var="pv" items="${filterpvlist}" varStatus="Count">

                        <div class="layui-inline">
                            <label class="layui-form-label">pv${Count.count}滤波算法选择</label>
                            <div class="layui-input-inline">
                                <select name="filternamepv${Count.count}">
                                    <option value="">滤波选择</option>

                                    <c:choose>
                                        <c:when test="${pv.filtername eq 'mvav'}">
                                            <option value="mvav" selected>移动平均</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="mvav">移动平均</option>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:choose>
                                        <c:when test="${pv.filtername eq 'fodl'}">
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
                            <label class="layui-form-label">pv${Count.count}滤波系数</label>
                            <div class="layui-input-inline">
                                <input type="number" name="filtercoefpv${Count.count}" autocomplete="off"
                                       class="layui-input"
                                       value="${pv.getcoeff()}" placeholder="pv${Count.count}滤波系数" onmousewheel='scrollFunc()'>
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">pv${Count.count}滤波输出OPC位号</label>
                            <div class="layui-input-inline">
                                <input type="text" name="filteropctagpv${Count.count}" autocomplete="off"
                                       class="layui-input"
                                       value="${pv.backToDCSTag}" placeholder="pv${Count.count}滤波输出opc位号">
                            </div>
                        </div>


                        <div class="layui-inline">
                            <label class="layui-form-label">pv${Count.count}滤波输出OPC来源</label>
                            <div class="layui-input-inline">
                                <select name="filterpv${Count.count}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <c:choose>
                                            <c:when test="${(pv.opcresource!=null) && (pv.opcresource.equals(opcres))}">
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

                    </c:forEach>
                </div>
            </div>
        </div>





        <div class="layui-form-item layui-colla-item">
            <h2 class="layui-colla-title">PV独立投切OPC位号设置</h2>
            <div class="layui-colla-content layui-show">
                <c:forEach var="pv" items="${pvpinlist}" varStatus="Count">

                    <div class="layui-inline">
                        <label class="layui-form-label">pv${Count.count}</label>
                        <div class="layui-input-inline">
                            <input type="text" name="pvenable${Count.count}" autocomplete="off" class="layui-input"
                                   placeholder="opc位号" value="${pv.dcsEnabePin.modleOpcTag}">
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">pv${Count.count}来源</label>
                        <div class="layui-input-inline">
                            <select name="pvenable${Count.count}resource">
                                <option value="">请选择来源</option>
                                <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <c:choose>
                                        <c:when test="${(pv.dcsEnabePin!=null) && (pv.dcsEnabePin.modleOpcTag!=null)&&(pv.dcsEnabePin.resource.equals(opcres))}">
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
                </c:forEach>

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
                            <c:forEach var="shock" items="${shockDetectorPVlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${Count.count}监测时间</label>
                                    <div class="layui-input-inline">
                                        <input type="number" name="detectwindowstimepv${Count.count}" autocomplete="off"
                                               class="layui-input" value="${shock.windowstime}"
                                               placeholder="pv${Count.count}震荡监视时间" onmousewheel='scrollFunc()'>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">阻尼系数</h2>
                        <div class="layui-colla-content">
                            <c:forEach var="shock" items="${shockDetectorPVlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${Count.count}阻尼系数</label>
                                    <div class="layui-input-inline">
                                        <input type="number" name="detectdampcoepv${Count.count}" autocomplete="off"
                                               class="layui-input" value="${shock.dampcoeff}"
                                               placeholder="pv${Count.count}阻尼系数" onmousewheel='scrollFunc()'>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">一阶滤波系数(0&ltalphe&lt;=1)</h2>
                        <div class="layui-colla-content">
                            <p  style="font-weight: bolder;font-size: 1.1rem">系数越小滤波效果越好,为1则无滤波效果</p>
                            <c:forEach var="shock" items="${shockDetectorPVlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${Count.count}一阶滤波系数</label>
                                    <div class="layui-input-inline">
                                        <input type="number" name="detectfiltercoepv${Count.count}" autocomplete="off"
                                               class="layui-input" value="${shock.filtercoeff}"
                                               placeholder="pv${Count.count}滤波系数" onmousewheel='scrollFunc()'>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">PV滤波输出OPC位号</h2>
                        <div class="layui-colla-content">
                            <c:forEach var="shock" items="${shockDetectorPVlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${Count.count}滤波输出位号</label>
                                    <div class="layui-input-inline">
                                        <input type="text" name="detectfilteroutopctagpv${Count.count}"
                                               autocomplete="off"
                                               class="layui-input" value="${shock.filterbacktodcstag}"
                                               placeholder="pv${Count.count}滤波输出位号">
                                    </div>
                                </div>


                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${Count.count}滤波输出位号来源</label>
                                    <div class="layui-input-inline">
                                        <select name="detectfilteroutopctagpv${Count.count}resource">
                                            <option value="">请选择来源</option>
                                            <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                                <c:choose>
                                                    <c:when test="${(shock.filteropcresource!=null) && (shock.filteropcresource.equals(opcres))}">
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

                            </c:forEach>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">PV滤波后幅值结果输出OPC位号</h2>
                        <div class="layui-colla-content layui-show">
                            <c:forEach var="shock" items="${shockDetectorPVlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${Count.count}幅值输出位号</label>
                                    <div class="layui-input-inline">
                                        <input type="text" name="detectamplitudeoutopctagpv${Count.count}"
                                               autocomplete="off"
                                               class="layui-input" value="${shock.backToDCSTag}"
                                               placeholder="pv${Count.count}幅值输出位号">
                                    </div>
                                </div>


                                <div class="layui-inline">
                                    <label class="layui-form-label">pv${Count.count}幅值输出位号来源</label>
                                    <div class="layui-input-inline">
                                        <select name="detectamplitudeoutopctagpv${Count.count}resource">
                                            <option value="">请选择来源</option>
                                            <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                                <c:choose>
                                                    <c:when test="${(shock.opcresource!=null) && (shock.opcresource.equals(opcres))}">
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
                    <c:forEach var="sp" items="${sppinlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">sp${Count.count}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="sp${Count.count}" autocomplete="off" class="layui-input"
                                       placeholder="opc位号" value="${sp.modleOpcTag==null?'':sp.modleOpcTag}">
                            </div>
                        </div>


                        <div class="layui-inline">
                            <label class="layui-form-label">sp${Count.count}中文注释</label>
                            <div class="layui-input-inline">
                                <input type="text" name="sp${Count.count}comment" autocomplete="off" class="layui-input"
                                       placeholder="sp${Count.count}中文注释" value="${sp.opcTagName==null?'':sp.opcTagName}">
                            </div>
                        </div>



                        <div class="layui-inline">
                            <label class="layui-form-label">sp${Count.count}位号来源</label>
                            <div class="layui-input-inline">
                                <select name="sp${Count.count}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <c:choose>
                                            <c:when test="${(sp.resource!=null) && (sp.resource.equals(opcres))}">
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
                    <c:forEach var="mv" items="${mvpinlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">mv${Count.count}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="mv${Count.count}" autocomplete="off" class="layui-input"
                                       placeholder="opc位号" value="${mv.modleOpcTag==null?'':mv.modleOpcTag}">
                            </div>
                        </div>



                        <div class="layui-inline">
                            <label class="layui-form-label">mv${Count.count}中文注释</label>
                            <div class="layui-input-inline">
                                <input type="text" name="mv${Count.count}comment" autocomplete="off" class="layui-input"
                                       placeholder="mv${Count.count}中文注释" value="${mv.opcTagName==null?'':mv.opcTagName}">
                            </div>
                        </div>


                        <div class="layui-inline">
                            <label class="layui-form-label">mv${Count.count}opc位号来源</label>
                            <div class="layui-input-inline">
                                <select name="mv${Count.count}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <c:choose>
                                            <c:when test="${(mv.resource!=null) && (mv.resource.equals(opcres))}">
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

                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">R设置</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">影响MV调节幅度，R越大算法则要求每次的deltaMV越小</p>

                <div class="layui-form-item">
                    <c:forEach var="r" items="${rlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">r${Count.count}</label>
                            <div class="layui-input-inline">
                                <input type="number" name="r${Count.count}" autocomplete="off" class="layui-input"
                                       placeholder="mv${Count.count}的R" value="${r}" onmousewheel='scrollFunc()'>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">dmv高限</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">mv增量绝对值不能超过该值</p>
                <div class="layui-form-item">
                    <c:forEach var="dmv" items="${dmvHighlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">dmv${Count.count}High</label>
                            <div class="layui-input-inline">
                                <input type="number" name="dmv${Count.count}High" autocomplete="off" class="layui-input"
                                       placeholder="dmv${Count.count}High" value="${dmv}" onmousewheel='scrollFunc()'>
                            </div>
                        </div>
                    </c:forEach>
                </div>

            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">dmv低限</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">低于此不进行调节</p>
                <div class="layui-form-item">
                    <c:forEach var="dmv" items="${dmvLowlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">dmv${Count.count}Low</label>
                            <div class="layui-input-inline">
                                <input type="number" name="dmv${Count.count}Low" autocomplete="off" class="layui-input"
                                       placeholder="dmv${Count.count}Low" value="${dmv}" onmousewheel='scrollFunc()'>
                            </div>
                        </div>
                    </c:forEach>
                </div>

            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV上限设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <c:forEach var="mvup" items="${mvuplist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">mvup${Count.count}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="mvup${Count.count}" autocomplete="off" class="layui-input"
                                       placeholder="mv${Count.count}上限"
                                       value="${mvup.modleOpcTag==null?"":mvup.modleOpcTag}">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">mv${Count.count}上限来源</label>
                            <div class="layui-input-inline">
                                <select name="mvup${Count.count}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <c:choose>
                                            <c:when test="${mvup.resource!=null&&mvup.resource.equals(opcres)}">
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
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV下限设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <c:forEach var="mvdown" items="${mvdownlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">mvdown${Count.count}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="mvdown${Count.count}" autocomplete="off" class="layui-input"
                                       placeholder="mv${Count.count}下限"
                                       value="${mvdown.modleOpcTag==null?"":mvdown.modleOpcTag}">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">mv${Count.count}下限来源</label>
                            <div class="layui-input-inline">
                                <select name="mvdown${Count.count}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <c:choose>
                                            <c:when test="${mvdown.resource!=null&&mvdown.resource.equals(opcres)}">
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
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV反馈设置</h2>
            <div class="layui-colla-content layui-show">

                <div class="layui-form-item">
                    <c:forEach var="mvfb" items="${mvfbpinlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">mvfb${Count.count}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="mvfb${Count.count}" autocomplete="off" class="layui-input"
                                       placeholder="mv${Count.count}反馈位号"
                                       value="${mvfb.modleOpcTag==null?'':mvfb.modleOpcTag}">
                            </div>
                        </div>


                        <div class="layui-inline">
                            <label class="layui-form-label">mvfb${Count.count}中文注释</label>
                            <div class="layui-input-inline">
                                <input type="text" name="mvfb${Count.count}comment" autocomplete="off" class="layui-input"
                                       placeholder="mvfb${Count.count}中文注释" value="${mvfb.opcTagName==null?'':mvfb.opcTagName}">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">mvfb${Count.count}反馈opc位号来源</label>
                            <div class="layui-input-inline">
                                <select name="mvfb${Count.count}resource">
                                    <option value="">请选择来源</option>
                                    <<c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <c:choose>
                                        <c:when test="${mvfb.resource!=null&&mvfb.resource.equals(opcres)}">
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


                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV反馈滤波器设置</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">一阶滤波(滤波系数0&ltalphe&lt1，数值越小滤波越强)和移动平均滤波(滤波系数0&ltalphe的整数，数值越大滤波越强)</p>
                <div class="layui-form-item">
                    <c:forEach var="mv" items="${filtermvfblist}" varStatus="Count">

                        <div class="layui-inline">
                            <label class="layui-form-label">mvfb${Count.count}滤波算法选择</label>
                            <div class="layui-input-inline">
                                <select name="filternamemv${Count.count}">
                                    <option value="">滤波选择</option>

                                    <c:choose>
                                        <c:when test="${mv.filtername eq 'mvav'}">
                                            <option value="mvav" selected>移动平均</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="mvav">移动平均</option>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:choose>
                                        <c:when test="${mv.filtername eq 'fodl'}">
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
                            <label class="layui-form-label">mvfb${Count.count}滤波系数</label>
                            <div class="layui-input-inline">
                                <input type="number" name="filtercoefmv${Count.count}" autocomplete="off"
                                       class="layui-input"
                                       value="${mv.getcoeff()}" placeholder="mvfb${Count.count}滤波系数" onmousewheel='scrollFunc()'>
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">mvfb${Count.count}滤波输出OPC位号</label>
                            <div class="layui-input-inline">
                                <input type="text" name="filteropctagmv${Count.count}" autocomplete="off"
                                       class="layui-input"
                                       value="${mv.backToDCSTag}" placeholder="mvfb${Count.count}滤波输出opc位号">
                            </div>
                        </div>


                        <div class="layui-inline">
                            <label class="layui-form-label">mvfb${Count.count}滤波输出OPC位号来源</label>
                            <div class="layui-input-inline">
                                <select name="filtermvfb${Count.count}resource">
                                    <option value="">请选择来源</option>
                                    <<c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                    <c:choose>
                                        <c:when test="${mv.opcresource!=null&&mv.opcresource.equals(opcres)}">
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
                            <c:forEach var="shock" items="${shockDetectorMVlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${Count.count}监测时间</label>
                                    <div class="layui-input-inline">
                                        <input type="number" name="detectwindowstimemv${Count.count}" autocomplete="off"
                                               class="layui-input" value="${shock.windowstime}"
                                               placeholder="mv${Count.count}震荡监视时间" onmousewheel='scrollFunc()'>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">阻尼系数</h2>
                        <div class="layui-colla-content">
                            <c:forEach var="shock" items="${shockDetectorMVlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${Count.count}阻尼系数</label>
                                    <div class="layui-input-inline">
                                        <input type="number" name="detectdampcoemv${Count.count}" autocomplete="off"
                                               class="layui-input" value="${shock.dampcoeff}"
                                               placeholder="mv${Count.count}阻尼系数" onmousewheel='scrollFunc()'>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">一阶滤波系数(0&ltalphe&lt;=1)</h2>
                        <div class="layui-colla-content">
                            <p  style="font-weight: bolder;font-size: 1.1rem">系数越小滤波效果越好,为1则无滤波效果</p>
                            <c:forEach var="shock" items="${shockDetectorMVlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${Count.count}一阶滤波系数</label>
                                    <div class="layui-input-inline">
                                        <input type="number" name="detectfiltercoemv${Count.count}" autocomplete="off"
                                               class="layui-input" value="${shock.filtercoeff}"
                                               placeholder="mv${Count.count}滤波系数" onmousewheel='scrollFunc()'>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">MV滤波输出OPC位号</h2>
                        <div class="layui-colla-content">
                            <c:forEach var="shock" items="${shockDetectorMVlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${Count.count}滤波输出位号</label>
                                    <div class="layui-input-inline">
                                        <input type="text" name="detectfilteroutopctagmv${Count.count}"
                                               autocomplete="off"
                                               class="layui-input" value="${shock.filterbacktodcstag}"
                                               placeholder="mv${Count.count}滤波输出位号">
                                    </div>
                                </div>


                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${Count.count}滤波输出位号来源</label>
                                    <div class="layui-input-inline">
                                        <select name="detectfilteroutopctagmv${Count.count}resource">
                                            <option value="">请选择来源</option>
                                            <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                                <c:choose>
                                                    <c:when test="${shock.filteropcresource!=null&&shock.filteropcresource.equals(opcres)}">
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

                            </c:forEach>
                        </div>
                    </div>


                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">MV滤波后幅值结果输出OPC位号</h2>
                        <div class="layui-colla-content">
                            <c:forEach var="shock" items="${shockDetectorMVlist}" varStatus="Count">
                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${Count.count}幅值输出位号</label>
                                    <div class="layui-input-inline">
                                        <input type="text" name="detectamplitudeoutopctagmv${Count.count}"
                                               autocomplete="off"
                                               class="layui-input" value="${shock.backToDCSTag}"
                                               placeholder="mv${Count.count}幅值输出位号">
                                    </div>
                                </div>


                                <div class="layui-inline">
                                    <label class="layui-form-label">mv${Count.count}幅值输出位号来源</label>
                                    <div class="layui-input-inline">
                                        <select name="detectamplitudeoutopctagmv${Count.count}resource">
                                            <option value="">请选择来源</option>
                                            <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                                <c:choose>
                                                    <c:when test="${shock.opcresource!=null&&shock.opcresource.equals(opcres)}">
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
            <h2 class="layui-colla-title">FF前馈属性设置</h2>
            <div class="layui-colla-content">
                <div class="layui-form-item">
                    <c:forEach var="ff" items="${ffpinlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">ff${Count.count}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="ff${Count.count}" autocomplete="off" class="layui-input"
                                       placeholder="ff${Count.count}位号"
                                       value="${ff.modleOpcTag==null?'':ff.modleOpcTag}">
                            </div>
                        </div>


                        <div class="layui-inline">
                            <label class="layui-form-label">ff${Count.count}中文注释</label>
                            <div class="layui-input-inline">
                                <input type="text" name="ff${Count.count}comment" autocomplete="off" class="layui-input"
                                       placeholder="ff${Count.count}中文注释" value="${ff.opcTagName==null?'':ff.opcTagName}">
                            </div>
                        </div>


                        <div class="layui-inline">
                            <label class="layui-form-label">ff${Count.count}opc位号来源</label>
                            <div class="layui-input-inline">
                                <select name="ff${Count.count}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <c:choose>
                                            <c:when test="${ff.resource!=null&&ff.resource.equals(opcres)}">
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


                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF前馈滤波器设置</h2>
            <div class="layui-colla-content">
                <p style="font-weight: bolder;font-size: 1.1rem">一阶滤波(滤波系数0&ltalphe&lt1，数值越小滤波越强)和移动平均滤波(滤波系数0&ltalphe的整数，数值越大滤波越强)</p>

                <div class="layui-form-item">
                    <c:forEach var="ff" items="${filterfflist}" varStatus="Count">

                        <div class="layui-inline">
                            <label class="layui-form-label">ff${Count.count}滤波算法选择</label>
                            <div class="layui-input-inline">
                                <select name="filternameff${Count.count}">
                                    <option value="">滤波选择</option>

                                    <c:choose>
                                        <c:when test="${ff.filtername eq 'mvav'}">
                                            <option value="mvav" selected>移动平均</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="mvav">移动平均</option>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:choose>
                                        <c:when test="${ff.filtername eq 'fodl'}">
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
                            <label class="layui-form-label">ff${Count.count}滤波系数</label>
                            <div class="layui-input-inline">
                                <input type="number" name="filtercoefff${Count.count}" autocomplete="off"
                                       class="layui-input"
                                       value="${ff.getcoeff()}" placeholder="ff${Count.count}滤波系数" onmousewheel='scrollFunc()'>
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">ff${Count.count}滤波输出OPC位号</label>
                            <div class="layui-input-inline">
                                <input type="text" name="filteropctagff${Count.count}" autocomplete="off"
                                       class="layui-input"
                                       value="${ff.backToDCSTag}" placeholder="ff${Count.count}滤波输出opc位号">
                            </div>
                        </div>


                        <div class="layui-inline">
                            <label class="layui-form-label">ff${Count.count}滤波输出OPC位号来源</label>
                            <div class="layui-input-inline">
                                <select name="filterff${Count.count}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <c:choose>
                                            <c:when test="${ff.opcresource!=null&&ff.opcresource.equals(opcres)}">
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

                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF(前馈)上限设置</h2>
            <div class="layui-colla-content">
                <div class="layui-form-item">
                    <c:forEach var="ffup" items="${ffuplist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">ffup${Count.count}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="ffup${Count.count}" autocomplete="off" class="layui-input"
                                       placeholder="ff${Count.count}位号"
                                       value="${ffup.modleOpcTag==null?'':ffup.modleOpcTag}">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">ffup${Count.count}上限来源</label>
                            <div class="layui-input-inline">
                                <select name="ffup${Count.count}resource">
                                    <option value="">请选择来源</option>
                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <c:choose>
                                            <c:when test="${ffup.resource!=null&&ffup.resource.equals(opcres)}">
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
                    </c:forEach>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF(前馈)下限设置</h2>
            <div class="layui-colla-content">
                <div class="layui-form-item">
                    <c:forEach var="ffdown" items="${ffdownlist}" varStatus="Count">
                        <div class="layui-inline">
                            <label class="layui-form-label">ffdown${Count.count}</label>
                            <div class="layui-input-inline">
                                <input type="text" name="ffdown${Count.count}" autocomplete="off"
                                       class="layui-input"
                                       placeholder="ffdown${Count.count}位号/值"
                                       value="${ffdown.modleOpcTag==null?'':ffdown.modleOpcTag}">
                            </div>
                        </div>

                        <div class="layui-inline">
                            <label class="layui-form-label">ffdown${Count.count}下限来源</label>
                            <div class="layui-input-inline">
                                <select name="ffdown${Count.count}resource">
                                    <option value="">请选择来源</option>

                                    <c:forEach var="opcres" items="${opcresources}" varStatus="Count">
                                        <c:choose>
                                            <c:when test="${ffdown.resource!=null&&ffdown.resource.equals(opcres)}">
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
                    </c:forEach>
                </div>
            </div>
        </div>

    </div>


    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>MV对PV的响应设置 形如{k:10,t:180,tao:200} 英文模式输入!</legend>
    </fieldset>
    <div class="layui-form-item layui-collapse" lay-accordion>
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV对PV的响应设置</h2>
            <div class="layui-colla-content layui-show">
                <table class="layui-table" lay-data='{"data":${mvresp}, "id":"mvresp"}' lay-filter="mvresp">
                    <thead>
                    <tr>
                        <th lay-data="{field:'mv', width:80, }">MV</th>
                        <c:forEach var="pv" items="${pvlist}" varStatus="Count">
                            <th lay-data="{field:'pv${Count.count}', width:180,  edit: 'text'}">
                                pv${Count.count}</th>
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
    <div class="layui-form-item layui-collapse" lay-accordion>
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF对PV的响应设置</h2>
            <div class="layui-colla-content">
                <table class="layui-table" lay-data='{"data":${ffresp}, "id":"ffresp"}' lay-filter="ffresp">
                    <thead>
                    <tr>
                        <th lay-data="{field:'ff', width:80, }">FF</th>
                        <c:forEach var="pv" items="${pvlist}" varStatus="Count">
                            <th lay-data="{field:'pv${Count.count}', width:180,  edit: 'text'}">
                                pv${Count.count}</th>
                        </c:forEach>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>

    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" lay-filter="motifymodlesubmit">立即提交</button>
        <button type="reset" class="layui-btn layui-btn-primary">重置</button>
    </div>

</form>

<script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
<script>
    var table;

    layui.use(['element', 'form', 'layer'], function () {
        var element = layui.element;
        var form = layui.form,
            layer = parent.layer === undefined ? layui.layer : parent.layer;
        form.render(); //更新全部
        form.render('select'); //刷新select选择框渲染

        //各种基于事件的操作，下面会有进一步介绍

        //监听提交
        form.on('submit(motifymodlesubmit)', function (data) {
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


            let index = layer.msg('修改中，请稍候', {icon: 16, time: false, shade: 0.8});
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
                    layer.close(index);
                    let json = JSON.parse(result);
                    if (json['msg'] == "error") {
                        layer.msg("修改失败！");
                    } else {
                        layer.msg("修改成功！");
                        location.href = '${pageContext.request.contextPath}' + json['go'];
                        // newleft(json['modleName'],json['modleId'])
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
            //alert("function in " + param);
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

