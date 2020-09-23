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
    <title>new opc serve vertag</title>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
    <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
    <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script src="${pageContext.request.contextPath}/js/opc/displayvertagstaus.js"></script>
</head>
<body style="position:absolute; width:500px; height:250px ;left:0;top:0; padding: 50px;line-height: 22px; text-align:left;">
<form class="layui-form" action="">

    <div class="layui-form-item">

        <div class="layui-inline">
            <label class="layui-form-label">位号注释</label>
            <div class="layui-input-inline">
                <input type="text" name="tagName" autocomplete="off" class="layui-input" placeholder="位号注释">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">位号</label>
            <div class="layui-input-inline">
                <input type="text" name="tag" lay-verify="required" autocomplete="off" class="layui-input"
                       placeholder="opc位号">
            </div>
        </div>

        <div class="layui-inline" style="visibility: hidden">
            <div class="layui-input-inline">
                <input type="text" name="opcserveid" autocomplete="off" class="layui-input" value="${opcserveid}"
                       style="visibility: hidden">
            </div>
        </div>

        <div class="layui-inline" style="visibility: hidden">
            <div class="layui-input-inline">
                <div class="layui-input-inline">
                    <input type="text" name="tagid" autocomplete="off" class="layui-input" value="">
                </div>
            </div>
        </div>

    </div>


    <div class="layui-form-item">
        <div class="layui-input-block">
            <div class="layui-row">

                <div class="layui-col-md6">
                    <button type="submit" class="layui-btn" lay-submit="" lay-filter="newopcservevertagsb" id="opcservevertagsubmit"
                            style="visibility: hidden">立即提交
                    </button>
                </div>
                <div class="layui-col-md6">
                    <button type="reset" class="layui-btn layui-btn-primary" style="visibility: hidden">重置</button>
                </div>
            </div>
        </div>
    </div>


</form>
</body>
</html>


<script>
    layui.use(['form'], function () {
        let form = layui.form

            , layer = layui.layer
        form.render();
        //自定义验证规则
        // form.verify({
        //         aimodlename: function (value) {
        //             console.log(value);
        //             if (value=='s') {
        //                 return 'name';
        //             }
        //         }
        //         // , pass: [
        //         //     /^[\S]{6,12}$/
        //         //     , '密码必须6到12位，且不能出现空格'
        //         // ]
        //         // , content: function (value) {
        //         //     layedit.sync(editIndex);
        //         // }
        //     }
        // );
        form.on('submit(newopcservevertagsb)', function (data) {
            console.log(JSON.stringify(data.field));

            var index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});

            $.ajax({
                url: "${pageContext.request.contextPath}/opc/savenewopcserveVertag.do" + "?" + Math.random(),
                async: true,
                data: {
                    "opcvertagcontext": JSON.stringify(data.field)
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
                        successAndCloseopcvertagWD();

                    }
                }
            });
            return false;
        });


    });


</script>
