<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
	<head>
		<link rel="shortcut icon"
			  href="../img/favicon.ico" type="image/x-icon" />
		<meta charset="UTF-8">
		<meta http-equiv="X-UA-Compatible" content="ie=edge">
		<title></title>
		<link rel="stylesheet" href="../js/layui/css/layui.css" />
	</head>
	<body class="layui-layout-body">
		<div class="layui-fluid">
			<div class="layui-form layui-form-pane">
				<div class="layui-form-item"></div>
				<div class="layui-form-item">
					<label class="layui-form-label"><i class="layui-icon layui-icon-username"></i></label>
					<div class="layui-input-block">
						<input name="username" class="layui-input" type="text" placeholder="请输入账号，默认admin" autocomplete="off" />
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label"><i class="layui-icon layui-icon-password"></i></label>
					<div class="layui-input-block">
						<input name="userpassword" class="layui-input" type="password" placeholder="请输入密码，默认123456" autocomplete="off"/>
					</div>
				</div>
				<div class="layui-form-item">
					<div class="layui-btn-container">
						<button class="layui-btn layui-btn-fluid" lay-submit lay-filter="login">
							<i class="layui-icon layui-icon-ok-circle"></i>提交
						</button>
					</div>
				</div>
			</div>
		</div>
		<script src="../js/layui/layui.js"></script>
		<script>
			layui.use(['layer','form'],function(){
				var layer = layui.layer
				,form = layui.form
				,$ = layui.jquery;
				//监控表单提交
				form.on('submit(login)', function(data){
					var obj = data.field;
					if(obj.username!=""&&obj.userpassword!=""){
						layer.msg('注册成功,3秒后关闭', {icon: 1});
						setInterval(function(){
							parent.layer.closeAll();
						},"3000")
					}else{
						layer.msg('请填写账号或密码', {icon: 5});
					}
				});
			})
			//登陆按钮绑定回车
			function onEnter(){
				if(window.event.keyCode == 13){
					document.getElementsByTagName("button")[0].click();
				}
			}
		</script>
	</body>
</html>
