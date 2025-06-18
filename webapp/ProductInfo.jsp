<%@ page import="Model.Product" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<html>
<head>
    <meta charset="UTF-8" />
    <title>title</title>
    <link rel="stylesheet" type="text/css" href="CSS/ProductInfo.css">
    <link rel="stylesheet" href="icon/iconfont.css">
</head>
<body>
<jsp:include page="Header.jsp">
    <jsp:param name="tiaozhuan" value="Head.jsp"/>
</jsp:include>

<!-- 需要拉具体某个商品的数据,有照片，名字，简介，单价，库存，销量 -->
<div class="product_container">
    <div id="product_id">${product.productId}</div>
    <div class="product_img" style="background-image: url('${product.picture}'); "></div>
    <div class="product_name">${product.productName}</div>
    <div class="jianjie">介绍:</div>
    <div class="product_introduction">${product.introduction}</div>
    <div class="jiage">价格:</div>
    <div class="product_price"><span class="iconfont icon-fl-renminbi" style="color:black"></span>${product.price}</div>
    <div class="shuliang">数量:</div>
    <div class="amount">
        <span class="iconfont icon-jiahao" id="jia" style="color: #ff6699;font-size: 22px;"></span>
        <span id="true_amount">1</span>
        <span class="iconfont icon-jianhao" id="jian" style="color: #ff6699;font-size: 22px;"></span>
    </div>
    <div class="stock">库存:<span id="stock_amount">${product.stock}</span>&nbsp;件</div>
    <div id="buy">加入购物车</div>
</div>

<jsp:include page="Footer.jsp">
    <jsp:param name="tiaozhuan" value="Head.jsp"/>
</jsp:include>
<script type="text/javascript" src="JavaScript/ProductInfo.js"></script>
</body>
</html>
