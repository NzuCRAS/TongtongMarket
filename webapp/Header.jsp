<%--
  Created by IntelliJ IDEA.
  User: Melody1
  Date: 2025/6/14
  Time: 08:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" type="text/css" href="CSS/Header.css">
    <link rel="stylesheet" href="icon/iconfont.css">
</head>
<body>

<% String name;
    if(request.getAttribute("name")==null){
    name="";
    }
    else name=(String)request.getAttribute("name");
%>
<div id="response_name"><%=name%></div>
<div id="nav">
    <div id="logo"><span class="iconfont icon-sanyecaokongxin" style="font-size: 24px;color: greenyellow;"></span>&nbsp;&nbsp;通通超市</div>
    <div id="main"><a href="servletMain"><span class="iconfont icon-shouye1" style="color:pink;font-size: 22px;"></span>商店主页</a></div>
    <div id="search">
        <input id="search_input" type="text" placeholder="商品名称">
        <div id="search_icon"><span class="iconfont icon-sousuo" style="font-size:20px;color:black;"></span></div>
    </div>

    <div id="user_center">
        <a href="" id="user_center_a"><span class="iconfont icon-yonghu"></span>用户中心</a>
        <ul id="xiala">
            <li class="lis"><a href="">个人信息</a></li>
            <li class="lis"><a href="">会员中心</a></li>
            <li class="lis"><a href="">联系客服</a></li>
        </ul>
    </div>

    <div id="shopping_car"><a href="servletShoppingCar"><span class="iconfont icon-gouwuchekong"></span>购物车</a></div>
    <div id="order_center"><a href="servletOrderList"><span class="iconfont icon-shouye"></span>订单中心</a></div>
</div>

<script type="text/javascript" src="JavaScript/Header.js"></script>
</body>
</html>
