<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <!--表单的用户信息传到哪里?-->
    <title>Title</title>
    <link rel="stylesheet" type="text/css" href="CSS/LoginRegister.css">
    <link rel="stylesheet" href="icon/iconfont.css">
</head>
<body>
<div id="container_form">
    <div id="container_left">
        <div id="title_market">通通超市</div>
        <span class="iconfont icon-siyecao-copy" id="jhy" style="font-size: 40px;"></span>
        <span class="iconfont icon-siyecao" id="zwk" style="font-size: 40px;"></span>
        <span class="iconfont icon-siyecao1" id="ctj" style="font-size: 40px;"></span>
        <span class="iconfont icon-xingxing" style="font-size: 22px;color: yellow;" id="star"></span>
    </div>
    <hr id="xian2">
    <div id="container_right">
        <div id="login_name">密码登录</div>
        <hr id="xian"></hr>
        <a id="register_name" href="Register.jsp">账户注册</a>
        <div id="container_form_login">
            <form action="servletLogin" method="post" autocomplete="on">
                <ul id="form_login_list">
                    <li id="container_login_name">
                        <label for="login_name_input" id="label1">用户名:</label>
                        <input id="login_name_input" type="text" name="username">
                    </li>
                    <li id="container_login_password">
                        <label for="login_password_input" id="label2">密码:</label>
                        <input id="login_password_input" type="password" name="password">
                    </li>
                    <li id="container_login_button">
                        <button type="submit" id="button1">登录</button>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div id="copyright">由陈组长,简小队员,张小队员共同完成</div>
</div>


<script type="text/javascript" src="JavaScript/LoginRegister.js"></script>
</body>
</html>
