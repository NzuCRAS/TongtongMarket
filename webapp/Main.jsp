<%@ page import="java.util.ArrayList" %>
<%@ page import="Model.Product" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" type="text/css" href="CSS/Main.css">
    <link rel="stylesheet" href="icon/iconfont.css">
</head>
<body>

<jsp:include page="Header.jsp">
    <jsp:param name="tiaozhuan" value="Head.jsp"/>
</jsp:include>

<div id="filter">
    <div id="response_type">${type}</div>
    <div id="response_flag">${flag}</div>
    <ul id="filter1">
        <p id="p1">类型:</p>
        <li class="sort_name"><a href="servletMain?type=全部">全部</a></li>
        <li class="sort_name"><a href="servletMain?type=数码产品">数码产品</a></li>
        <li class="sort_name"><a href="servletMain?type=家电">家电</a></li>
        <li class="sort_name"><a href="servletMain?type=日用百货">日用百货</a></li>
        <li class="sort_name"><a href="servletMain?type=男装">男装</a></li>
        <li class="sort_name"><a href="servletMain?type=书本读物">书本读物</a></li>
        <li class="sort_name"><a href="servletMain?type=零食">零食</a></li>
        <li class="sort_name"><a href="servletMain?type=女装">女装</a></li>
        <li class="sort_name"><a href="servletMain?type=家具">家具</a></li>
        <li class="sort_name"><a href="servletMain?type=玩具">玩具</a></li>
        <li class="sort_name"><a href="servletMain?type=酒水">酒水</a></li>
        <li class="sort_name"><a href="servletMain?type=蔬果类">蔬果类</a></li>
    </ul>
    <hr id="xian1"></hr>
    <ul id="filter2">
        <p id="p2">筛选:</p>
        <li class="flag"><a href="servletMain?flag=1">默认</a></li>
        <li class="flag"><a href="servletMain?flag=2">销量</a></li>
        <li class="flag"><a href="servletMain?flag=3">价格</a></li>
    </ul>
</div>

<!--需要拉数据-->

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

<%int current_page=(int)request.getAttribute("page");
%>
<!--需要改页面数字-->
<div id="fenye_container">
    <% int tmp_page=current_page-1;
    if(tmp_page==0)tmp_page=1;
    %>
    <a href="servletMain?page=<%=tmp_page%>"><span class="iconfont icon-zuojiantou"></span></a>
    <a href="servletMain?page=<%=current_page%>"><span style="color: pink"><%=current_page%></span></a>
    <a href="servletMain?page=<%=current_page+1%>"><span><%=current_page+1%></span></a>
    <a href="servletMain?page=<%=current_page+2%>"><span><%=current_page+2%></span></a>
    <a href="servletMain?page=<%=current_page+1%>"><span class="iconfont icon-youjiantou"></span></a>
</div>

<jsp:include page="Footer.jsp">
    <jsp:param name="tiaozhuan" value="Head.jsp"/>
</jsp:include>
<script type="text/javascript" src="JavaScript/Main.js"></script>
</body>
</html>
