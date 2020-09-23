<%--
  Created by IntelliJ IDEA.
  User: zaixz
  Date: 2020/9/6
  Time: 17:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>new ai modle</title>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
    <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
    <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
</head>
<body style="position:absolute; width:500px; height:300px ;left:0;top:0; padding: 50px;line-height: 22px; text-align:left;background-color: #393D49; color: #fff; font-weight: 300;">
<%--style="padding: 50px; line-height: 300px; background-color: #393D49; color: #fff; font-weight: 300;"--%>

<form class="layui-form" action="">
    <div class="layui-container">
        <input type="number" name="modleid" value="" hidden/>

        <div class="layui-form-item">
            <div class="layui-row">
                <div class="layui-col-md3">
                    <label class="layui-form-label">算法名称</label>
                </div>

                <div class="layui-col-md9">
                    <div class="layui-input-inline">
                        <input type="text" name="algorithmName" lay-verify="required|aimodlename" autocomplete="off"
                               placeholder="请输入模型名称" class="layui-input" lay-verType="tips">
                    </div>
                </div>

            </div>


        </div>


        <div class="layui-form-item">
            <div class="layui-input-block">
                <div class="layui-row">

                    <div class="layui-col-md6">
                        <button type="submit" class="layui-btn" lay-submit="" lay-filter="newmodlesb" id="modlesubmit"
                                style="visibility: hidden">立即提交
                        </button>
                    </div>
                    <div class="layui-col-md6">
                        <button type="reset" class="layui-btn layui-btn-primary" style="visibility: hidden">重置</button>
                    </div>
                </div>
            </div>
        </div>


    </div>


</form>
</body>
</html>

<script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
<script src="${pageContext.request.contextPath}/js/aimodle/newaimodle.js"></script>

<script>
    layui.use(['form'], function () {
        let form = layui.form

        , layer = layui.layer
        form.render();
        //自定义验证规则
        form.verify({
                aimodlename: function (value) {
                    console.log(value);
                    if (value=='s') {
                        return 'name';
                    }
                }
                // , pass: [
                //     /^[\S]{6,12}$/
                //     , '密码必须6到12位，且不能出现空格'
                // ]
                // , content: function (value) {
                //     layedit.sync(editIndex);
                // }
            }
        );


        form.on('submit(newmodlesb)', function (data) {
            console.log(JSON.stringify(data.field));

            var index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});

            $.ajax({
                url: "${pageContext.request.contextPath}/aimodle/savenewaimodle.do" + "?" + Math.random(),
                async: true,
                data: {
                    "aimodlecontext": JSON.stringify(data.field)
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
                        console.log("创建成功！")
                        successAndClosenewaimodleWD(json['modlename'],json['modleid'],'${pageContext.request.contextPath}');
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


</script>
