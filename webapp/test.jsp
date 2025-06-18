<%--
  Created by IntelliJ IDEA.
  User: 13515
  Date: 2025/6/13
  Time: 14:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
  <script>
    const test={
      name:"melody",
      password:"12345"
    }
    fetch('mika', { // 替换为实际 API 地址
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(test) // 发送 test 对象
    });
  </script>
  <div id="test"></div>
</body>
</html>
