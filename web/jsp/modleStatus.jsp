<%@ page import="hs.Bean.ControlModle" %><%--
  Created by IntelliJ IDEA.
  User: zaixz
  Date: 2020/5/7
  Time: 15:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="shortcut icon"
          href="../img/favicon.ico" type="image/x-icon"/>
    <title>modlestatus</title>
    <meta charset="utf-8">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/echarts.js"></script>
    <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
    <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/layui/layui.js"></script>

    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
</head>
<body class="layui-layout-body" onload=" table_flush()">
<%--<div id="container" style="height: 100%"></div>--%>
<%----%>
<div style="position: absolute;left: 0;top: 0;width: 100%;height:100%;overflow: auto;overflow-x: hidden">

    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>${modle.modleName}</legend>
    </fieldset>
    <div class="layui-fluid">

        <div class="layui-row layui-col-space15"><%--layui-col-space15 列间距15--%>

            <c:forEach var="pv" items="${modle.categoryPVmodletag}" varStatus="Count">
                <c:choose>
                    <c:when test="${Count.count<=4}">
                        <div class="layui-col-md4">
                            <div class="layui-card">
                                <div class="layui-card-header">${pv.modlePinName}(${pv.modleOpcTag})</div>
                                <div class="layui-card-body">
                                    <div id="container${Count.count}" style="height:300px;"></div>
                                </div>
                            </div>
                        </div>
                    </c:when>

                </c:choose>

            </c:forEach>
        </div>

        <c:choose>
            <c:when test="${modle.categoryPVmodletag.size()>4}">
                <div class="layui-row layui-col-space15"><%--layui-col-space15 列间距15--%>
                    <c:forEach var="pv" items="${modle.categoryPVmodletag}" begin="4" varStatus="Count">
                        <div class="layui-col-md4">
                            <div class="layui-card">
                                <div class="layui-card-header">${pv.modlePinName}(${pv.modleOpcTag})</div>
                                <div class="layui-card-body">
                                    <div id="container${Count.count+4}" pinname="${pv.modlePinName}"
                                         style="height:300px;"></div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
        </c:choose>

    </div>

    <table class="layui-hide" id="modlereadDatatab" width="900px" height="900px"></table>

    <div class="layui-form-item">

        <div class="layui-btn-group">
            <c:choose>
                <c:when test="${modle.modleEnable eq 0}">
                    <button type="button"
                            class="layui-btn layui-btn-primary layui-btn-danger">${modle.modleName}</button>
                </c:when>
                <c:otherwise>
                    <button type="button" class="layui-btn layui-btn-primary">${modle.modleName}</button>
                </c:otherwise>
            </c:choose>

            <a href="" class="layui-btn"
               onclick="isdelete('${pageContext.request.contextPath}/modle/deleteModle.do?modleid=${modle.modleId}')">删除</a>
            <a href=""
               class="layui-btn"
               onclick="newTab('编辑${modle.modleName}','${pageContext.request.contextPath}/modle/modifymodle.do?modleid=${modle.modleId}')">编辑</a>


            <c:choose>
                <c:when test="${modle.modleEnable eq 1}">
                    <a href=""
                       class="layui-btn"
                       onclick="stopOrrun('${pageContext.request.contextPath}/modle/stopModle.do?modleid=${modle.modleId}','确定停止？')">停止</a>
                </c:when>
                <c:otherwise>
                    <a href=""
                       class="layui-btn"
                       onclick="stopOrrun('${pageContext.request.contextPath}/modle/runModle.do?modleid=${modle.modleId}','确定运行？')">运行</a>
                </c:otherwise>
            </c:choose>
        </div>

    </div>

</div>


<script type="text/html" id="switchTpl1">
    <input type="checkbox" disabled name="auto" value="{{d.auto}}" lay-skin="switch" lay-text="自动|手动"
           lay-filter="sexDemo" {{ d.auto=== "自动" ? 'checked' : '' }}/>
</script>
<script>
    function isdelete(url) {
        var r = confirm("确定删除?");
        if (r == true) {
            $.ajax({
                url: url + "&" + Math.random(),
                async: true,
                type: "POST",
                success: function (result) {
                    console.log(result);

                }
            });
            parent.location.reload();
        } else {

        }
    }


    function stopOrrun(url, msg) {
        var r = confirm(msg);
        if (r == true) {
            $.ajax({
                url: url + "&" + Math.random(),
                async: true,
                type: "POST",
                success: function (result) {
                    console.log(result);

                }
            });
            parent.location.reload();
        } else {

        }
    }

</script>

<script>
    var table;
    var form;

    let table_flush_t;
    let tableSerise = [];//各个pv的图标的3条曲线
    let chartNames = [];
    let xData = [];
    let allchars = [];

    layui.use('table', function () {
        table = layui.table;
        table.render({
            elem: '#modlereadDatatab'
            // , page: true
            // , method: 'POST'
            //, "hight": "full-20"
            //, "width": 900
            // , url: '/status/findhostory.do'
            , data: []
            // , request: {
            //     pageNum: 'currPage',//页码的参数名称，默认：page
            //     limitName: 'pageSize'//每页显示数据条数的参数名，默认：limit
            // }
            // , where: {
            //     "date": $('#sltdate').val(),
            //     "time": $('#slttime').val(),
            //     "region": $("#selProvince").val(),
            //     "company": $("#selCompany").val()
            // }
            // , limit: 10
            , jump: function (obj, first) {
                //obj包含了当前分页的所有参数，比如：
                console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                console.log(obj.limit); //得到每页显示的条数
                console.log(obj)
                //首次不执行
                if (!first) {
                    //do something
                    console.log("not first time")
                }
            }
            <%--, "data":${data}//[{"id":10000,"username":"user-0","sex":"女","city":"城市-0","sign":"签名-0","experience":255,"logins":24,"wealth":82830700,"classify":"作家","score":57}]--%>
            , "cellMinWidth": 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , "cols": [[
                {field: 'modleName', title: '引脚', sort: true}
                , {field: 'pvName', title: '主控点'}
                , {field: 'pvValue', title: '实际值'}
                , {field: 'spValue', title: '目标值'}
                , {field: 'e', title: '预测误差'}
                , {field: 'dmv', title: 'mv增量'}
                , {field: 'mvvalue', title: '给定值'}
                , {field: 'mvFeedBack', title: '反馈'}
                , {field: 'mvDownLmt', title: '下限'}
                , {field: 'mvUpLmt', title: '上限'}
                , {field: 'shockmv', title: 'mv幅值'}
                , {field: 'shockpv', title: 'pv幅值'}
                , {field: 'auto', title: '手自动', templet: '#switchTpl1', unresize: true}
            ]]
        });
        //监听性别操作
        // form.on('switch(sexDemo)', function (obj) {
        //     layer.tips(this.value + ' ' + this.name + '：' + obj.elem.checked, obj.othis);
        //
        //     var index = layer.msg('修改中，请稍候', {icon: 16, time: false, shade: 0.8});
        //     setTimeout(function () {
        //         layer.close(index);
        //         layer.msg(this.value + ' ' + this.name + '：' + obj.elem.checked, obj.othis + "展示状态修改成功！");
        //     }, 2000);
        // });

    });
</script>

<script>

    findTableNum(${modle.categoryPVmodletag.size()});

    function table_flush() {
        clearInterval(table_flush_t);
        try {
            dataFill();
            buildtable();
        } catch (e) {
            console.log(e);
        }

        table_flush_t = setInterval(table_flush, 1000);
        <%--            ${modle.controlAPCOutCycle.intValue()*1000}--%>
    }

    function findTableNum(pvsize) {
        for (let loop = 1; loop <= pvsize; ++loop) {
            var dom = document.getElementById("container" + loop);
            if (dom === undefined) {
                continue;
            } else {
                let chartserise = [];
                let curvename = [];
                tableSerise.push(chartserise);
                chartNames.push(curvename);
                let myChart;
                try {
                    myChart = echarts.init(dom);
                } catch (e) {
                    console.log(e)
                }


                allchars.push(myChart);
            }
        }

    }

    function buildtable() {

        let loop = 0;
        for (loop = 1; loop <= allchars.length; ++loop) {
            // var dom = this.document.getElementById("container" + loop);
            // if (dom === undefined) {
            //     continue;
            // } else {

            chart(allchars[loop - 1], tableSerise[loop - 1], chartNames[loop - 1]);
            // }
        }


    }

    function chart(myChart, chartserise, charname) {
        //var myChart = echarts.init(dom);

        var app = {};
        option = null;

        option = {
            color: ['#26f609', '#f50ae5', '#F57474', '#509ae7', '#f1a019', '#cbcbcb', '#1eebf5'],
            legend: {
                data: charname,
                icon: 'rect',
                itemWidth: 6, itemHeight: 6, itemGap: 0,
                textStyle: {color: '#83c7e3', fontSize: 7}
            },
            animation: false,
            grid: {
                top: 40,
                left: 50,
                right: 40,
                bottom: 50
            },
            xAxis: {
                name: 'x',
                data: xData,
                minorTick: {
                    show: true
                },
                splitLine: {
                    lineStyle: {
                        color: '#999'
                    }
                },
                minorSplitLine: {
                    show: true,
                    lineStyle: {
                        color: '#ddd'
                    }
                }
            },
            yAxis: {
                name: 'y',
                // min: -100,
                // max: 100,
                scale: true,
                minorTick: {
                    show: true
                },
                splitLine: {
                    lineStyle: {
                        color: '#999'
                    }
                },
                minorSplitLine: {
                    show: true,
                    lineStyle: {
                        color: '#ddd'
                    }
                }
            },
            // dataZoom: [{
            //     show: true,
            //     type: 'inside',
            //     filterMode: 'none',
            //     xAxisIndex: [0],
            //     startValue: -20,
            //     endValue: 20
            // }, {
            //     show: true,
            //     type: 'inside',
            //     filterMode: 'none',
            //     yAxisIndex: [0],
            //     startValue: -20,
            //     endValue: 20
            // }],
            series: chartserise
        };

        if (option && typeof option === "object") {
            myChart.setOption(option, true);
        }
        //
        // console.log("success flush container");
        // console.log("xData")
        // console.log(xData)

    }


    function dataFill() {
        $.ajax({
            url: "${pageContext.request.contextPath}/modle/modleRealStatus/${modle.modleId}.do" + "?" + Math.random(),
            async: true,
            // data: {
            //     "date": $('#sltdate').val(),
            //     "time": $('#slttime').val(),
            // },
            type: "POST",
            success: function (result) {
                console.log(result);

                var json = JSON.parse(result);
                console.log(json["funneltype"][0][0]);
                console.log(typeof (json["funneltype"][0][0]));
                for (let loop = 0; loop < tableSerise.length; ++loop) {
                    tableSerise[loop] = [
                        {
                            type: 'line',
                            showSymbol: false,
                            clip: true,
                            data: json['funelUp'][loop],
                            name: json["curveNames4funelUp"][loop],
                            itemStyle: {
                                normal: {
                                    lineStyle: {
                                        // width:2,
                                        type: json["funneltype"][loop][0] === 0 ? "solid" : "dotted",
                                    }
                                }
                            }

                        },
                        {
                            type: 'line',
                            showSymbol: false,
                            clip: true,
                            data: json['funelDwon'][loop],
                            name: json['curveNames4funelDown'][loop],
                            itemStyle: {
                                normal: {
                                    lineStyle: {
                                        // width:2,
                                        type: json["funneltype"][loop][1] === 0 ? "solid" : "dotted",
                                    }
                                }
                            }

                        },
                        {
                            type: 'line',
                            showSymbol: false,
                            clip: true,
                            data: json['predict'][loop],//generateData(),
                            name: json['curveNames4pv'][loop]

                        },
                    ];

                    chartNames[loop] = [
                        json["curveNames4funelUp"][loop]
                        , json['curveNames4funelDown'][loop]
                        , json['curveNames4pv'][loop]];
                }

                xData = json['xaxis'];

                table.reload('modlereadDatatab', {
                    "data": json['modleRealData'],
                });

            }
        });

    }


</script>

<script>

    function newTab(title, url) {
        let element11 = parent.layui.element;
        element11.tabAdd(
            'pagetabs', {
                title: title
                ,
                content: '<iframe src="' + url + '" class="layui-admin-iframe" scrolling="auto" frameborder="0"></iframe>'
                ,
                id: url
            }
        );
        // this.window.location.reload()
        // var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        // parent.layer.close(index); //再执行关闭
        // element11.tabChange('pagetabs',url)
        //newleft();


    }


</script>

<%--<script>--%>
<%--    --%>
<%--    function newTab(url) {--%>
<%--        console.log(url)--%>
<%--           addTab(url);--%>
<%--    }--%>
<%--</script>--%>


<%--<script>--%>
<%--    $(document).ready(function(){--%>
<%--        table_flush();--%>
<%--    });--%>

<%--</script>--%>


</body>
</html>
