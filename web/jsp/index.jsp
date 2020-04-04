<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: zaixz
  Date: 2020/2/21
  Time: 11:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${basedata.commenName}先进过程控制系统</title>

    <meta charset="utf-8">
    <title>Layui</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/layui/css/layui.css" media="all">

</head>
<body>
<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>模型概要</legend>
</fieldset>

<c:forEach var="modle" items="${modles}" varStatus="Count">

<div class="layui-form-item">
    <%--<label class="layui-form-label">模型名称：${modle.modleName}</label>--%>
    <div class="layui-btn-group">
        <c:choose>
            <c:when test="${modle.enable eq 0}">
                <button type="button" class="layui-btn layui-btn-primary layui-btn-danger">${modle.modleName}</button>
            </c:when>
            <c:otherwise>
                <button type="button" class="layui-btn layui-btn-primary">${modle.modleName}</button>
            </c:otherwise>
        </c:choose>

        <a href="${pageContext.request.contextPath}/modle/deleteModle.do?modleid=${modle.modleId}" class="layui-btn">删除</a>
        <a href="${pageContext.request.contextPath}/modle/modifymodle.do?modleid=${modle.modleId}" class="layui-btn">编辑</a>
        <c:choose>
            <c:when test="${modle.enable eq 1}">
                <a href="${pageContext.request.contextPath}/modle/stopModle.do?modleid=${modle.modleId}" class="layui-btn">停止</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/modle/runModle.do?modleid=${modle.modleId}" class="layui-btn">运行</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

</c:forEach>


<div class="layui-form-item">
    <a href="${pageContext.request.contextPath}/modle/newmodle.do" class="layui-btn">模型新建</a>
</div>

</body>
</html>

