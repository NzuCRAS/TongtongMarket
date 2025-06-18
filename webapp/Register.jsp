<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" type="text/css" href="CSS/Register.css">
    <link rel="stylesheet" href="icon/iconfont.css">
</head>
<body>
<div id="register_form_container">
    <form action="servletRegister">
        <ul>
            <li>用户名:<input type="text" name="username"></li>
            <li>密码:<input type="password" name="password"></li>
            <li>姓名:<input type="text" name="realname"></li>
            <li>身份证号:<input type="text" name="idNumber"></li>
            <li>手机号:<input type="text" name="phone" ></li>
            <li>地址:<input type="text" name="address"></li>
            <button type="submit" id="register_button">注册</button>
        </ul>
    </form>
</div>
</body>
</html>
