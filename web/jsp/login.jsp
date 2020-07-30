<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
	<head>
		<link rel="shortcut icon"
			  href="../img/favicon.ico" type="image/x-icon" />
		<meta charset="UTF-8">
		<meta http-equiv="X-UA-Compatible" content="ie=edge">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css" />
		<title>${companyName}过程控制系统</title>
	</head>
	<body class="layui-layout-body" onkeydown="onEnter();">
		<div class="layui-fluid" style="padding: 0px;">
			<canvas id="particle"></canvas>
			<div class="layui-form layui-form-pane layui-admin-login">
				<div class="layui-admin-login-header">
					<h1>${companyName}过程控制系统</h1>
<%--					<p>Power by 智能制作研究所</p>--%>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label"><i class="layui-icon layui-icon-username"></i></label>
					<div class="layui-input-block">
						<input name="username" class="layui-input" type="text" placeholder="请输入账号" autocomplete="off" />
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label"><i class="layui-icon layui-icon-password"></i></label>
					<div class="layui-input-block">
						<input name="userpassword" class="layui-input" type="password" placeholder="请输入密码" autocomplete="off"/>
					</div>
				</div>
				<div class="layui-form-item">
					<input type="checkbox" pane name="remember" title="记住密码" lay-skin="primary">
					<a href="javascript:;" style="margin-top: 10px;float: right;color: #009688;">忘记密码?</a>
				</div>
				<div class="layui-form-item">
					<div class="layui-btn-container">
						<button class="layui-btn layui-btn-fluid" lay-submit lay-filter="login">
							<i class="layui-icon layui-icon-ok-circle"></i>登陆
						</button>
					</div>
					<div class="layui-btn-container">
						<button class="layui-btn layui-btn-fluid" lay-submit lay-filter="register">
							<i class="layui-icon layui-icon-add-circle"></i>注册
						</button>
					</div>
				</div>
				<!--底部-->
				<div class="layui-footer">© 2020 Power by 智能制造研究所</div>
			</div>
		</div>
		<script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/particle.min.js" ></script>
		<script>
			layui.use(['layer','form'],function(){
				var layer = layui.layer
				,form = layui.form
				,$ = layui.jquery;
				//监控表单提交
				form.on('submit(login)', function(data){
					var obj = data.field;
					if(obj.username=="admin"&&obj.userpassword=="123456"){
						window.location.href="${pageContext.request.contextPath}/login/index.do";
					}else{
						layer.msg('账号或密码不正确', {icon: 5});
					}
				});
				//监控注册
				form.on('submit(register)', function(data){
					layer.open({
						title:"用户注册",
						type: 2,
						content:"${pageContext.request.contextPath}/jsp/register.jsp",
						area: ['400px', '250px']
					});
				});
			})
			//登陆按钮绑定回车
			function onEnter(){
				if(window.event.keyCode == 13){
					document.getElementsByTagName("button")[0].click();
				}
			}
			var particle = new Particle('#particle', {
				effect: 'line'
			});
		</script>
	</body>
</html>
