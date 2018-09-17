<%--
  Created by IntelliJ IDEA.
  User: sunhuazhong
  Date: 2015/5/9
  Time: 17:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>车车管理系统登录</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="Robots" content="none">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/reset.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/home.css"/>
    <script src="<%=request.getContextPath()%>/libs/jquery-1.11.2/jquery-1.11.2.min.js"></script>
    <script src="<%=request.getContextPath()%>/js/home_validation.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            $('#form').validation();
            $('#email').focus();
        });
    </script>
</head>
<body>
<div class="container">
    <div class="main">
        <h1 class="text-center">车车管理系统</h1>
        <div>
            <form action="/admin/login" method='POST' id="form">
                <div class="row">
                    <span>邮 箱:</span>
                    <div class="input_box">
                        <input type="text" id="email" name="email" validation="email">
                        <p node-type="请输入有效的邮件地址"></p>
                    </div>
                </div>
                <div class="row">
                    <span>密 码:</span>
                    <div class="input_box">
                        <input type="password" id="password" name="password" validation="password">
                        <p node-type="由6到12位字母数字下划线组成"></p>
                    </div>
                </div>
                <div class="btn">
                    <button type="submit" value="登陆" class="submit">登录</button>
                </div>
            </form>
        </div>
        <span style="color:red;margin-bottom: 10px;">${SPRING_SECURITY_LAST_EXCEPTION.message}</span>
    </div>
</div>
</body>
</html>
