<%--
  Created by IntelliJ IDEA.
  User: heojeonghwa
  Date: 2023/01/31
  Time: 3:26 오전
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>클래스 사용</title>
</head>
<body>
<%
    java.util.Calendar cal = java.util.Calendar.getInstance();
%>
오늘은
<%=cal.get(java.util.Calendar.YEAR)%>년
<%=cal.get(java.util.Calendar.MONTH+1)%>월
<%=cal.get(java.util.Calendar.DATE)%>일
입니다.
</body>
</html>
