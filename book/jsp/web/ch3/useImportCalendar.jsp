<%--
  Created by IntelliJ IDEA.
  User: heojeonghwa
  Date: 2023/01/31
  Time: 3:26 오전
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Calendar" %>
<html>
<head>
    <title>클래스 사용</title>
</head>
<body>
<%
    Calendar cal = Calendar.getInstance();
%>
오늘은
<%=cal.get(Calendar.YEAR)%>년
<%=cal.get(Calendar.MONTH+1)%>월
<%=cal.get(Calendar.DATE)%>일
입니다.
</body>
</html>
