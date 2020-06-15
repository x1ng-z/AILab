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
			<div class="layui-card">
				<div class="layui-card-header">网站设置</div>
				<div class="layui-card-body">
				    <form class="layui-form" action="">
						<div class="layui-form-item">
							<label class="layui-form-label">新增区域</label>
							 <div class="layui-input-block">
							 	<input type="text" name="title" required  lay-verify="required" placeholder="请输入标题" autocomplete="off" class="layui-input">
							 </div>
						</div>
						<div class="layui-form-item">
							<label class="layui-form-label">新增子公司</label>
							 <div class="layui-input-block">
							 	<input type="text" name="weburl" required  lay-verify="required" placeholder="请输入标题" autocomplete="off" class="layui-input">
							 </div>
						</div>
						<div class="layui-form-item">
							<label class="layui-form-label">截图周期</label>
							 <div class="layui-input-inline" style="width: 80px;">
							 	<input type="text" name="weburl" required  lay-verify="required" placeholder="0" autocomplete="off" class="layui-input">
							 </div>
							 <div class="layui-form-mid layui-word-aux">
							 	分钟 本地开发一般推荐设置为 60，线上环境建议设置为 60。
							 </div>
						</div>

						<div class="layui-inline">
							<label class="layui-form-label">新增摄像头</label>
							<div class="layui-input-inline">
								<input type="text" name="P" lay-verify="required|number" autocomplete="off" class="layui-input">
							</div>
						</div>
						<div class="layui-form-item">
							<label class="layui-form-label">新增摄像头描述</label>
							 <div class="layui-input-block">
							 	<textarea name="title" class="layui-textarea"></textarea>
							 </div>
						</div>
						<div class="layui-form-item">
							 <div class="layui-input-block">
							 	<button class="layui-btn" lay-filter="set_website" lay-submit="">确认保存</button>
							 </div>
						</div>
					</form>
				</div>
			</div>
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
