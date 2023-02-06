<%--
  Created by IntelliJ IDEA.
  User: heojeonghwa
  Date: 2023/02/07
  Time: 12:38 오전
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>쿠키 목록</title>
</head>
<body>
쿠키 목록<br/>
<%
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
%>
<%=cookie.getName()%> : <%=cookie.getValue()%> <br/>
<%
    }
%>
</body>
</html>
