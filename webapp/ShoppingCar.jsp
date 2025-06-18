<%@ page import="Model.ShoppingCar" %>
<%@ page import="Model.secondary.ShoppingCarProduct" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.DecimalFormat" %><%--
  Created by IntelliJ IDEA.
  User: Melody1
  Date: 2025/6/14
  Time: 11:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8" />
    <title>title</title>
    <link rel="stylesheet" type="text/css" href="CSS/ShoppingCar.css">
    <link rel="stylesheet" href="icon/iconfont.css">
</head>
<body>
<jsp:include page="Header.jsp">
    <jsp:param name="tiaozhuan" value="Head.jsp"/>
</jsp:include>

<%--shoppingCarProductList--%>
<% ArrayList<ShoppingCarProduct> shoppingCarProducts=(ArrayList<ShoppingCarProduct>) request.getAttribute("shoppingCarProductList");
    if(shoppingCarProducts.size()!=0){
%>
<div class="product_list_contanier">
    <%
        for(ShoppingCarProduct tmp:shoppingCarProducts){
            System.out.println(tmp.getProduct().getProductName()+"  "+tmp.getNumber());
    %>
    <div class="order_info">
        <div class="img_container"  style="background-image: url(<%=tmp.getProduct().getPicture()%>)"></div>
        <div class="product_name_container">
            <div class="product_name"><%=tmp.getProduct().getProductName()%></div>
        </div>
        <div class="product_amount"><%=tmp.getNumber()%></div>
        <div class="product_price"><span class="iconfont icon-fl-renminbi"></span>单价:<%=tmp.getProduct().getPrice()%></div>
        <% double sum=tmp.getProduct().getPrice()*tmp.getNumber();
           String sumstring=new DecimalFormat("#.00").format(sum);
        %>
        <div class="product_sum_price"><span class="iconfont icon-fl-renminbi"></span>总价:<%=sumstring%></div>
    </div>
    <% }%>
    <!-- 算总价 扩展-->
    <div class="price"><a href="servletOrderGenerate" class="price_a">生成订单</a></div>
</div>
<%}%>

<jsp:include page="Footer.jsp">
    <jsp:param name="tiaozhuan" value="Head.jsp"/>
</jsp:include>
</body>
</html>
