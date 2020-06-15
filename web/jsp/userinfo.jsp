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
			<form class="layui-form">
				<div class="layui-form-item">	
				</div>
				<div class="layui-form-item">	
					<label class="layui-form-label">ID</label>
					<div class="layui-input-block">
						<input name="id" class="layui-input" type="text" placeholder="100000" autocomplete="off"/>
					</div>
				</div>
				<div class="layui-form-item">	
					<label class="layui-form-label">用户名</label>
					<div class="layui-input-block">
						<input name="id" class="layui-input" type="text" placeholder="心的远行" autocomplete="off"/>
					</div>
				</div>
				<div class="layui-form-item">	
					<label class="layui-form-label">邮箱</label>
					<div class="layui-input-block">
						<input name="id" class="layui-input" type="text" placeholder="zzc0505@qq.com" autocomplete="off"/>
					</div>
				</div>
				<div class="layui-form-item">	
					<label class="layui-form-label">性别</label>
					<div class="layui-input-block">
				    	<input type="radio" name="sex" value="男" title="男" checked />
				    	<input type="radio" name="sex" value="女" title="女" />
				    </div>
				</div>
				<div class="layui-form-item layui-form-text">
				    <label class="layui-form-label">文本域</label>
				    <div class="layui-input-block">
				      <textarea name="desc" placeholder="请输入内容" class="layui-textarea"></textarea>
				    </div>
				</div>
				<div class="layui-form-item">
				    <div class="layui-input-block">
				    	<button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
				    	<button type="reset" class="layui-btn layui-btn-primary">重置</button>
				    </div>
				</div>
			</form>	
		</div>
		<script src="../js/layui/layui.js"></script>
		<script>
			layui.use(['element','layer','form'],function(){
				var element = layui.element
				,layer = layui.layer
				,form = layui.form
				,$ = layui.jquery;
			})	
		</script>		
	</body>
</html>
