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
    <title>new opc serve</title>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
    <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
    <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script src="${pageContext.request.contextPath}/js/opc/modifyopcserve.js"></script>
</head>
<body style=" width:100%; height:100% ;padding-top:50px;text-align: center;background-color: #393D49; color: #fff;">
<form class="layui-form" action="">

    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">用户名</label>
            <div class="layui-input-inline">
                <input type="text" name="opcuser" lay-verify="required" value="${opcserve.opcuser}" autocomplete="off"
                       class="layui-input" placeholder="Windows用户名">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">密码</label>
            <div class="layui-input-inline">
                <input type="text" name="opcpassword" lay-verify="required" value="${opcserve.opcpassword}"
                       autocomplete="off" class="layui-input" placeholder="Windows密码">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">服务器ip</label>
            <div class="layui-input-inline">
                <input type="text" name="opcip" lay-verify="required" value="${opcserve.opcip}" autocomplete="off"
                       class="layui-input" placeholder="opc服务器ip">
            </div>
        </div>


        <div class="layui-inline">
            <label class="layui-form-label">opcclsid</label>
            <div class="layui-input-inline">
                <input type="text" name="opcclsid" lay-verify="required" value="${opcserve.opcclsid}" autocomplete="off"
                       class="layui-input" placeholder="opc class id">
            </div>
        </div>


        <div class="layui-inline">
            <label class="layui-form-label">引脚ip退换为新ip</label>
            <div class="layui-input-inline">
                <input type="checkbox" name="like[repacle]" title="替换">
                <%--                <input type="checkbox" name="like[read]" title="阅读" checked="">--%>
                <%--                <input type="checkbox" name="like[game]" title="游戏">--%>
            </div>
        </div>


        <div class="layui-inline" style="visibility: hidden">
            <div class="layui-input-inline">
                <input type="text" name="opcserveid" autocomplete="off" class="layui-input"
                       value="${opcserve.opcserveid}" style="visibility: hidden">
            </div>
        </div>
    </div>


    <div class="layui-form-item">
        <div class="layui-input-block">
            <div class="layui-row">

                <div class="layui-col-md6">
                    <button type="submit" class="layui-btn" lay-submit="" lay-filter="modifyopcservesb"
                            id="opcservesubmit"
                            style="visibility: hidden">更新数据
                    </button>
                </div>
                <div class="layui-col-md6">
                    <button type="reset" class="layui-btn layui-btn-primary" style="visibility: hidden">重置</button>
                </div>
            </div>
        </div>
    </div>
    <p style="position:fixed; bottom:0px; left:25%;text-align:center;">注：如有修改，需要重启服务器</p>
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
        form.on('submit(modifyopcservesb)', function (data) {
            console.log(JSON.stringify(data.field));

            var index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});

            $.ajax({
                url: "${pageContext.request.contextPath}/opc/updateopcserve.do" + "?" + Math.random(),
                async: true,
                data: {
                    "opccontext": JSON.stringify(data.field)
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
                        successAndClosemodifyopcserceWD('${pageContext.request.contextPath}');
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
