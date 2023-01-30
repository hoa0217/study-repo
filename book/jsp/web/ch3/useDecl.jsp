<%--
  Created by IntelliJ IDEA.
  User: heojeonghwa
  Date: 2023/01/31
  Time: 3:43 오전
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    public int multiply(int a, int b){
        return a * b;
    }
%>
<html>
<head>
    <title>선언부를 사용한 두 정수값의 곱</title>
</head>
<body>
10 * 25 = <%=multiply(10, 25)%>
</body>
</html>
