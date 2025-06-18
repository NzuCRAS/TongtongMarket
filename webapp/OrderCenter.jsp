<%@ page import="java.util.ArrayList" %>
<%@ page import="Model.secondary.OrderDetail" %>
<%@ page import="Model.Product" %>
<%@ page import="Model.OrderInfo" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Collections" %><%--
  Created by IntelliJ IDEA.
  User: Melody1
  Date: 2025/6/11
  Time: 15:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8" />
    <title>title</title>
    <link rel="stylesheet" type="text/css" href="CSS/OrderCenter.css">
    <link rel="stylesheet" href="icon/iconfont.css">
</head>
<body>
<jsp:include page="Header.jsp">
    <jsp:param name="tiaozhuan" value="Head.jsp"/>
</jsp:include>

<!-- --需要拉订单数据-- -->
<%  ArrayList<OrderDetail> orderDetails=(ArrayList<OrderDetail>) request.getAttribute("orderDetailList");
    ArrayList<OrderDetail> reversedList = new ArrayList<>(orderDetails);
    Collections.reverse(reversedList);
    for(OrderDetail order:reversedList){
%>
<div class="product_list_contanier">
    <!-- 需要拉订单信息 -->
    <div class="order_head">
        <div class="time">订单时间:<%=order.getOrder().getDate()%></div>
        <div class="order_id">订单编号:<%=order.getOrder().getOrderId()%></div>
        <% String orderstatus;
            if(order.getOrder().isPay())orderstatus=new String("已支付");
            else orderstatus=new String("未支付");
        %>
        <div class="order_status">订单状态:<%=orderstatus%></div>
    </div>
    <!-- 需要拉具体订单信息 -->
    <% ArrayList<OrderInfo> orderInfos=order.getOrderInfoList();
        for(OrderInfo tmp:orderInfos){
    %>
    <div class="order_info">
        <div class="img_container"></div>
        <div class="product_name_container">
            <div class="product_name"><%=tmp.getProductName()%></div>
        </div>
        <div class="product_amount"><%=tmp.getProductAmount()%>件</div>
        <div class="product_price"><span class="iconfont icon-fl-renminbi"></span>单价:<%=tmp.getPrice()%></div>
        <% double sum=tmp.getProductAmount()*tmp.getPrice();
            String sumString=new DecimalFormat("#.00").format(sum);
        %>
        <div class="product_sum_price"><span class="iconfont icon-fl-renminbi"></span>总价:<%=sumString%></div>
    </div>
    <%}%>

    <!-- 算总价 扩展-->
    <% if(orderstatus.equals("未支付")){%>
    <div class="price">
        <a href="servletPayOrder?orderID=<%=order.getOrder().getOrderId()%>" class="buy">总价:<span class="iconfont icon-fl-renminbi"></span><%=order.getOrder().getSumPrice()%>&nbsp;&nbsp;&nbsp;点击支付</a>
    </div>
    <%}%>
    <% if(orderstatus.equals("已支付")){%>
    <div class="price">
        <div class="buy">总价:<span class="iconfont icon-fl-renminbi"></span><%=order.getOrder().getSumPrice()%>&nbsp</div>
    </div>
    <%}%>
</div>
<%}%>

<jsp:include page="Footer.jsp">
    <jsp:param name="tiaozhuan" value="Head.jsp"/>
</jsp:include>
</body>
</html>
