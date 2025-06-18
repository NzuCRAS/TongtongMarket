<%@ page import="Model.Product" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: Melody1
  Date: 2025/6/14
  Time: 23:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" type="text/css" href="CSS/Search.css">
    <link rel="stylesheet" href="icon/iconfont.css">
</head>
<body>
    <jsp:include page="Header.jsp">
  <jsp:param name="tiaozhuan" value="Head.jsp"/>
</jsp:include>

<%--    需要拉查询出来的数据--%>
    <div id="result">为您查询到${size}条结果</div>
    <div id="item_list">
        <% ArrayList<Product> productArrayList=(ArrayList<Product>)request.getAttribute("productList");
            for(Product tmp:productArrayList){
        %>
        <a href="servletProductInfo?id=<%=tmp.getProductId()%>">
            <div class="item_container">
                <div class="item_background"></div>
                <div class="img_container" style="background-image: url(<%=tmp.getPicture()%>)"></div>
                <div class="product_name"><%=tmp.getProductName()%></div>
                <div class="sold"><span style="color: pink;" class="iconfont icon-yishouchu" ></span>销量:<%=tmp.getSales()%>件</div>
                <div class="stock"><span style="color: pink;" class="iconfont icon-kucun"></span>库存:<%=tmp.getStock()%>件</div>
                <div class="price"><span class="iconfont icon-fl-renminbi" style="font-size:16px;"></span><%=tmp.getPrice()%></div>
            </div>
        </a>
        <%}%>
    </div>

    <jsp:include page="Footer.jsp">
        <jsp:param name="tiaozhuan" value="Head.jsp"/>
    </jsp:include>
</body>
</html>
