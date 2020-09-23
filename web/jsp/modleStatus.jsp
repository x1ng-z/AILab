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

            <c:forEach var="pv" items="${enablePVPins}" varStatus="Count">
                <%--                <c:choose>--%>
                <%--                    <c:when test="${pv==1}">--%>
                <div class="layui-col-md4">
                    <div class="layui-card">
                        <div class="layui-card-header">${pv.modlePinName}(${((pv.opcTagName==null)||pv.opcTagName.equals(""))?pv.modleOpcTag:pv.opcTagName})</div>
                        <div class="layui-card-body">
                            <div id="container${Count.count}" style="height:300px;"></div>
                        </div>
                    </div>
                </div>
                <%--                    </c:when>--%>

                <%--                </c:choose>--%>

            </c:forEach>
        </div>

        <%--        <c:choose>--%>
        <%--            <c:when test="${modle.categoryPVmodletag.size()>4}">--%>
        <%--                <div class="layui-row layui-col-space15">&lt;%&ndash;layui-col-space15 列间距15&ndash;%&gt;--%>
        <%--                    <c:forEach var="pv" items="${modle.categoryPVmodletag}" begin="4" varStatus="Count">--%>
        <%--                        <div class="layui-col-md4">--%>
        <%--                            <div class="layui-card">--%>
        <%--                                <div class="layui-card-header">${pv.modlePinName}(${pv.modleOpcTag})</div>--%>
        <%--                                <div class="layui-card-body">--%>
        <%--                                    <div id="container${Count.count+4}" pinname="${pv.modlePinName}"--%>
        <%--                                         style="height:300px;"></div>--%>
        <%--                                </div>--%>
        <%--                            </div>--%>
        <%--                        </div>--%>
        <%--                    </c:forEach>--%>
        <%--                </div>--%>
        <%--            </c:when>--%>
        <%--        </c:choose>--%>

    </div>

    <div class="layui-form-item">

        <div class="layui-btn-group">
            <c:choose>
                <c:when test="${modle.modleEnable eq 0}">
                    <button type="button"
                            class="layui-btn layui-btn-primary layui-btn-danger"
                            id="modlestatus">${modle.modleName}</button>
                </c:when>
                <c:otherwise>
                    <button type="button" class="layui-btn layui-btn-primary" id="modlestatus"
                    >${modle.modleName}</button>
                </c:otherwise>
            </c:choose>

            <a href="" class="layui-btn"
               onclick="isdelete('${pageContext.request.contextPath}/modle/deleteModle.do?modleid=${modle.modleId}')">删除</a>
            <a href="" class="layui-btn"
               onclick="window.location.reload()">刷新</a>
            <c:choose>
                <c:when test="${modle.simulatControlModle.issimulation==true}">
                    <a href=""
                       class="layui-btn "
                       onclick="stopOrrun('${pageContext.request.contextPath}/modle/stopSimulateModle.do?modleid=${modle.modleId}','确定停止仿真？')">仿真</a>
                </c:when>
                <c:otherwise>
                    <a href=""
                       class="layui-btn layui-btn-danger"
                       onclick="stopOrrun('${pageContext.request.contextPath}/modle/runSimulateModle.do?modleid=${modle.modleId}','确定运行仿真？')">仿真</a>
                </c:otherwise>
            </c:choose>


            <c:choose>
                <c:when test="${modle.modleEnable eq 1}">
                    <a href=""
                       class="layui-btn"
                       onclick="stopOrrun('${pageContext.request.contextPath}/modle/stopModle.do?modleid=${modle.modleId}','确定停止？')">停止</a>
                </c:when>
                <c:otherwise>
                    <a href=""
                       class="layui-btn layui-btn-danger"
                       onclick="stopOrrun('${pageContext.request.contextPath}/modle/runModle.do?modleid=${modle.modleId}','确定运行？')">运行</a>
                </c:otherwise>
            </c:choose>

            <a href=""
               class="layui-btn"
               onclick="newTab('编辑${modle.modleName}','${pageContext.request.contextPath}/modle/modifymodle.do?modleid=${modle.modleId}')">编辑</a>
        </div>

    </div>
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>主控制参数信息</legend>
    </fieldset>
    <table class="layui-hide" id="modlereadDatatab" width="900px" height="900px"></table>
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>仿真参数信息</legend>
    </fieldset>
    <table class="layui-hide" id="sdmvDatatab" width="900px" height="900px"></table>
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>前馈信息</legend>
    </fieldset>
    <table class="layui-hide" id="ffDatatab" width="900px" height="900px"></table>

</div>


<script type="text/html" id="switchTpl1">
    <%--    input 添加 就不能进行编辑 disabled--%>
    <input type="checkbox" name="checkIO" value="{{d.checkIO}}" lay-skin="switch" lay-text="on|off"
           lay-filter="icheckIO" {{ d.checkIO===undefined? '': (d.checkIO.split('_')[2]== '1' ? 'checked' : '') }}/>
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
                    // console.log(result);

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
                    // console.log(result);

                }
            });
            window.location.reload();
        }
    }


</script>

<script>
    var table;
    var form;

    let table_flush_t;
    let mychartSerise = [];//各个pv的图标的3条曲线
    let chartNames = [];
    let xData = [];
    let allchars = [];

    // let table_width = $("#modlereadDatatab").width();
    // console.log("table_width:",table_width)

    layui.use('table', function () {
        let w = document
        table = layui.table;
        form = layui.form;
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
                // console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                // console.log(obj.limit); //得到每页显示的条数
                // console.log(obj)
                //首次不执行
                if (!first) {
                    //do something
                    console.log("not first time")
                }
            }
            <%--, "data":${data}//[{"id":10000,"username":"user-0","sex":"女","city":"城市-0","sign":"签名-0","experience":255,"logins":24,"wealth":82830700,"classify":"作家","score":57}]--%>
            , "cellMinWidth": 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , "cols": [[
                {field: 'pinName', title: '引脚', width: '8.3%', sort: true}
                , {field: 'pvValue', title: '实际值', width: '8.3%'}
                , {field: 'spValue', title: '目标值', width: '8.3%'}
                , {field: 'e', title: '预测误差', width: '8.3%'}
                , {field: 'dmv', title: 'dmv', width: '8.3%'}
                , {field: 'mvvalue', title: '给定值', width: '8.3%'}
                , {field: 'mvFeedBack', title: '反馈', width: '8.3%'}
                , {field: 'mvDownLmt', title: '下限', width: '8.3%'}
                , {field: 'mvUpLmt', title: '上限', width: '8.3%'}
                , {field: 'shockmv', title: 'mv幅值', width: '8.3%'}
                , {field: 'shockpv', title: 'pv幅值', width: '8.3%'}
                , {field: 'checkIO', title: '手自动', width: '8.3%', templet: '#switchTpl1', unresize: true}
            ]]
        });


        table.render({
            elem: '#sdmvDatatab'
            , data: []
            , jump: function (obj, first) {
                //obj包含了当前分页的所有参数，比如：
                // console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                // console.log(obj.limit); //得到每页显示的条数
                // console.log(obj)
                //首次不执行
                if (!first) {
                    //do something
                    console.log("not first time")
                }
            }
            <%--, "data":${data}//[{"id":10000,"username":"user-0","sex":"女","city":"城市-0","sign":"签名-0","experience":255,"logins":24,"wealth":82830700,"classify":"作家","score":57}]--%>
            , "cellMinWidth": 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , "cols": [[
                {field: 'pinName', title: '引脚', sort: true, width: '8.3%'}
                <c:forEach var="mv" items="${enableMVPins}" varStatus="Count">
                , {field: '${mv.modlePinName}', title: 'd${mv.modlePinName}', width: '8.3%'}
                </c:forEach>
            ]]
        });


        table.render({
            elem: '#ffDatatab'
            , data: []
            , jump: function (obj, first) {
                //obj包含了当前分页的所有参数，比如：
                // console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                // console.log(obj.limit); //得到每页显示的条数
                // console.log(obj)
                //首次不执行
                if (!first) {
                    //do something
                    console.log("not first time")
                }
            }
            <%--, "data":${data}//[{"id":10000,"username":"user-0","sex":"女","city":"城市-0","sign":"签名-0","experience":255,"logins":24,"wealth":82830700,"classify":"作家","score":57}]--%>
            , "cellMinWidth": 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , "cols": [[
                {field: 'pinName', title: '引脚', sort: true, width: '8.3%'}
                <c:forEach var="ff" items="${enableFFPins}" varStatus="Count">
                , {field: '${ff.modlePinName}', title: '${ff.modlePinName}', width: '8.3%'}
                , {field: 'd${ff.modlePinName}', title: 'd${ff.modlePinName}', width: '8.3%'}
                </c:forEach>
            ]]
        });


        //监听操作
        form.on('switch(icheckIO)', function (obj) {
            // console.log("checkIO");
            // layer.tips(this.value + '_' + this.name + '：' + obj.elem.checked, obj.othis);
            if(this.value==='undefined'){
                //console.log('value is undefine')
                return;
            }
            console.log('value is undefine out'+typeof (this.value))
            let index4modlepin = this.value.split("_");
            var r = confirm(index4modlepin[2] == 1 ? '切除出模型控制' : '切入到模型控制');
            if (r == true) {
                var index = layer.msg('修改中，请稍候', {icon: 16, time: false, shade: 0.8});
                $.ajax({
                    url: "${pageContext.request.contextPath}/modle/modlepvcheckout/" + index4modlepin[0] + "/" + index4modlepin[1] + "/" + index4modlepin[2] + ".do" + "?" + Math.random(),
                    async: true,
                    type: "POST",
                    success: function (result) {
                        console.log(result);
                        layer.close(index);
                        let json = JSON.parse(result);
                        if (json['msg'] == "error") {
                            layer.msg("修改失败！");
                        } else {
                            layer.msg("修改成功！");
                        }
                    }
                });
                window.location.reload()
            }


        });

    });
</script>

<script>

    findChartNum(${modle.numOfRunnablePVPins_pp});

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

    function findChartNum(pvsize) {
        for (let loop = 1; loop <= pvsize; ++loop) {
            var dom = document.getElementById("container" + loop);
            if (dom === undefined) {
                continue;
            } else {
                let chartserise = [];
                let curvename = [];
                mychartSerise.push(chartserise);
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

            charttheme(allchars[loop - 1], mychartSerise[loop - 1], chartNames[loop - 1]);
            // }
        }


    }

    function charttheme(myChart, chartserise, charname) {
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
                // console.log(result);

                var json = JSON.parse(result);
                // console.log(json["funneltype"][0][0]);
                // console.log(typeof (json["funneltype"][0][0]));
                for (let loop = 0; loop < mychartSerise.length; ++loop) {
                    mychartSerise[loop] = [
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

                let table_width = $("#modlereadDatatab").width();
                // console.log("table_width:", table_width)
                $("#modlereadDatatab").width($(document).width());


                table.reload('modlereadDatatab', {
                    "data": json['modleRealData'],
                    "width": $(document).width()
                });


                table.reload('sdmvDatatab', {
                    "data": json['sdmvData'],
                    "width": $(document).width()
                });

                table.reload('ffDatatab', {
                    "data": json['ffData'],
                    "width": $(document).width()
                });
                if (json["modlestatus"] == 0) {
                    $("#modlestatus").addClass("layui-btn-danger");
                } else {
                    $("#modlestatus").removeClass("layui-btn-danger");
                }

                // $("#modlereadDatatab").width($(document).width());

                // let table_width = $("#modlereadDatatab").width();
                // console.log("table_width:",table_width)

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
        // window.location.reload()
        // var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        // parent.layer.close(index); //再执行关闭
        // sleep(1000);
        // element11.tabChange('pagetabs', url)


        //newleft();


    }


    var sleep = function (time) {
        var startTime = new Date().getTime() + parseInt(time, 10);
        while (new Date().getTime() < startTime) {
        }
    };

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
